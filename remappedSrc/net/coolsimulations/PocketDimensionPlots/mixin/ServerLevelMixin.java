package net.coolsimulations.PocketDimensionPlots.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ServerWorld.class)
public abstract class ServerLevelMixin extends World {

	protected ServerLevelMixin(MutableWorldProperties writableLevelData, RegistryKey<World> resourceKey,
							   RegistryEntry<DimensionType> holder, DynamicRegistryManager registryAccess, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}

	@ModifyArg(method = "updateSleepingPlayerList()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;update(Ljava/util/List;)Z"), index = 0)
	private List<ServerPlayerEntity> updateSleepingPlayerList(List<ServerPlayerEntity> players) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			List<ServerPlayerEntity> newPlayers = new ArrayList<ServerPlayerEntity>(players);
			return getServerPlayers(newPlayers);
		} else {
			return players;
		}
	}

	@Unique
	private List<ServerPlayerEntity> getServerPlayers(List<ServerPlayerEntity> newPlayers) {
		if (this.getRegistryKey() == World.OVERWORLD) {
			ServerWorld level = Objects.requireNonNull(this.getServer()).getWorld(PocketDimensionPlots.VOID);
			assert level != null;
			for (ServerPlayerEntity player : level.getPlayers())
				if (!newPlayers.contains(player))
					newPlayers.add(player);
		} else if (this.getRegistryKey() == PocketDimensionPlots.VOID) {
			ServerWorld level = Objects.requireNonNull(this.getServer()).getWorld(World.OVERWORLD);
			assert level != null;
			for (ServerPlayerEntity player : level.getPlayers())
				if (!newPlayers.contains(player))
					newPlayers.add(player);
		}
		return newPlayers;
	}

	@ModifyArg(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"), index = 1)
	private List<ServerPlayerEntity> tick(List<ServerPlayerEntity> players) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			List<ServerPlayerEntity> newPlayers = new ArrayList<ServerPlayerEntity>(players);
			return getServerPlayers(newPlayers);
		} else {
			return players;
		}
	}

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V", shift = At.Shift.AFTER))
	private void wakeUpAllPlayers(CallbackInfo info) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			if (this.getRegistryKey() == World.OVERWORLD) {
				ServerWorld level = Objects.requireNonNull(this.getServer()).getWorld(PocketDimensionPlots.VOID);
				assert level != null;
				((ServerLevelAccessor) level).wakeUpAllPlayers();
			}
		}
	}
}
