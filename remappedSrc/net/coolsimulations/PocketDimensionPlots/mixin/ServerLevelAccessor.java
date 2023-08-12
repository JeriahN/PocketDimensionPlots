package net.coolsimulations.PocketDimensionPlots.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerWorld.class)
public interface ServerLevelAccessor {

	@Accessor
	SleepManager getSleepStatus();

	@Invoker("wakeUpAllPlayers")
	void wakeUpAllPlayers();
}
