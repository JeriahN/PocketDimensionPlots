package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor{

	@Shadow
	public abstract double getX();
	@Shadow
	public abstract double getY();
	@Shadow
	public abstract double getZ();
	@Shadow
	public float yRot;
	@Shadow
	public float xRot;

	private NbtCompound persistentData;

	@Inject(at = @At("TAIL"), method = "load", cancellable = true)
	public void load(NbtCompound tag, CallbackInfo info) {
		if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
			if (Double.isFinite((double)this.yRot) && Double.isFinite((double)this.xRot)) {
				if(tag.contains("PocketDimensionPlotsData", 10)) persistentData = tag.getCompound("PocketDimensionPlotsData");
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "saveWithoutId", cancellable = true)
	public void saveWithoutId(NbtCompound tag, CallbackInfoReturnable<NbtCompound> cir) {
		try {
			if(persistentData != null) tag.put("PocketDimensionPlotsData", persistentData);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.fillCrashReportCategory(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	@Override
	@Unique
	public NbtCompound getPersistentData() {
		if (persistentData == null)
			persistentData = new NbtCompound();
		return persistentData;
	}
	
	@Override
	@Unique
	public void setPersistentData(NbtCompound tag) {
		persistentData = tag;
	}

	@Shadow
	public abstract void fillCrashReportCategory(CrashReportSection section);

}
