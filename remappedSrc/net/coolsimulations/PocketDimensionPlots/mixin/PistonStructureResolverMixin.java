package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PistonHandler.class)
public class PistonStructureResolverMixin {

	@Final
	@Shadow
	private World level;
	@Final
	@Shadow
	private BlockPos pistonPos;

	@Inject(method = "resolve()Z", at = @At("HEAD"), cancellable = true)
	private void resolve(CallbackInfoReturnable<Boolean> info) {
		double closestPlot = Integer.MAX_VALUE;

		if (this.level.getRegistryKey() == PocketDimensionPlots.VOID && PocketDimensionPlotsDatabase.plots.size() > 0) {
			PlotEntry pistonPlot = PocketDimensionPlotsDatabase.plots.get(0);
			for (PlotEntry entry : PocketDimensionPlotsDatabase.plots) {
				double distanceFromPlot = this.pistonPos.getSquaredDistance(entry.centerPos);
				if (distanceFromPlot < closestPlot) {
					closestPlot = distanceFromPlot;
					pistonPlot = entry;
				}
			}

			if (pistonPlot.centerPos.getX() + pistonPlot.borderRadius < pistonPos.getX())
				info.setReturnValue(false);
			else if (pistonPlot.centerPos.getX() - pistonPlot.borderRadius > pistonPos.getX())
				info.setReturnValue(false);
			else if (pistonPlot.centerPos.getZ() + pistonPlot.borderRadius < pistonPos.getZ())
				info.setReturnValue(false);
			else if (pistonPlot.centerPos.getZ() - pistonPlot.borderRadius > pistonPos.getZ())
				info.setReturnValue(false);
		}
	}

}
