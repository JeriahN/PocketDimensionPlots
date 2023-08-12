package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUtils;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity {

	private ServerWorld localLevel;

	@Shadow
	ServerPlayNetworkHandler connection;

	public ServerPlayerMixin(World level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}

	@Inject(at = @At("TAIL"), method = "restoreFrom", cancellable = true)
	public void restoreFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
		NbtCompound old = ((EntityAccessor) oldPlayer).getPersistentData();
		if (old != null)
			((EntityAccessor) this).setPersistentData(old);
	}

	@Inject(at = @At("TAIL"), method = "die", cancellable = true)
	private  void die(DamageSource source, CallbackInfo info) {

		if (this.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
			PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(this);
			((EntityAccessor) this).getPersistentData().putDouble("inPlotXPos", entry.safePos.getX());
			((EntityAccessor) this).getPersistentData().putDouble("inPlotYPos", entry.safePos.getY());
			((EntityAccessor) this).getPersistentData().putDouble("inPlotZPos", entry.safePos.getZ());
			((EntityAccessor) this).getPersistentData().putInt("currentPlot", -1);
		}
	}

	@Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"))
	private void captureLevel(ServerWorld level, CallbackInfoReturnable<Entity> info) {
		if (this.method_48926() instanceof ServerWorld)
			localLevel = (ServerWorld) this.method_48926();
	}

	@ModifyArg(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
	private Packet changeDimensionPacket(Packet packet) {
		if (connection.getPlayer().method_48926().getRegistryKey() == PocketDimensionPlots.VOID || localLevel.getRegistryKey() == PocketDimensionPlots.VOID) {
			if (packet instanceof WorldEventS2CPacket) {
				WorldEventS2CPacket levelPacket = (WorldEventS2CPacket) packet;
				return new WorldEventS2CPacket(0, levelPacket.getPos(), levelPacket.getData(), levelPacket.isGlobal());
			}
		}
		return packet;
	}

	@Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
	private void changeDimension(ServerWorld level, CallbackInfoReturnable<Entity> cir) {
		if (connection.getPlayer().method_48926().getRegistryKey() == PocketDimensionPlots.VOID || localLevel.getRegistryKey() == PocketDimensionPlots.VOID)
			connection.getPlayer().method_48926().playSound(null, connection.getPlayer().getBlockPos(), PocketDimensionPlotsConfig.teleportSound, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
