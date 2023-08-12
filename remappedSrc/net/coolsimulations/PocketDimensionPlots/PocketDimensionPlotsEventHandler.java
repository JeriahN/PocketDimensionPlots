package net.coolsimulations.PocketDimensionPlots;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;

import net.coolsimulations.PocketDimensionPlots.commands.CommandPDP;
import net.coolsimulations.PocketDimensionPlots.commands.CommandPDP.PlotEnterRequest;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.coolsimulations.PocketDimensionPlots.mixin.ServerLevelAccessor;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PocketDimensionPlotsEventHandler {

	public static void registerEvents() {

		onWorldLoad();
		onWorldTick();
		onServerTick();
		onRightClick();
		onBedEvents();
	}

	public static void onRightClick() {

		if (PocketDimensionPlotsConfig.teleportItem != Items.AIR) {
			UseItemCallback.EVENT.register((player, world, hand) -> {
				ItemStack stack = player.getStackInHand(hand);

				if (!world.isClient) {
					if (stack.getItem() == PocketDimensionPlotsConfig.teleportItem) {
						if(player.canUsePortals()) {
							Objects.requireNonNull(player.getServer()).getCommandManager().executeWithPrefix(player.getCommandSource(), "pdp");
							return TypedActionResult.success(stack);
						}
					}
				}
				return TypedActionResult.pass(stack);
			});
		}
	}

	public static void onWorldLoad() {

		ServerWorldEvents.LOAD.register((server, level) -> {
			Path worldSave = Path.of(server.getSavePath(WorldSavePath.ROOT).toString(), "serverconfig");
			try {
				Files.createDirectories(worldSave);
			} catch (IOException ignored) {}
			PocketDimensionPlotsDatabase.init(new File(worldSave.toFile(), PDPReference.MOD_ID + "_database.json"));
		});
	}

	public static void onWorldTick() {

		ServerTickEvents.START_WORLD_TICK.register((level) -> {
			if(level.getRegistryKey() == PocketDimensionPlots.VOID) {
				for (PlayerEntity player : level.getPlayers((player) -> { return ((EntityAccessor) player).getPersistentData().contains("currentPlot") && ((EntityAccessor) player).getPersistentData().getInt("currentPlot") != -1; })) {
					NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
					PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(entityData.getInt("currentPlot"));

					if (entry != null) {
						if (entry.centerPos.getX() + entry.borderRadius < player.getBlockPos().getX()) {
							player.detach();
							player.requestTeleport(entry.centerPos.getX() + entry.borderRadius, player.getY(), player.getZ());
						} else if (entry.centerPos.getX() - entry.borderRadius > player.getX()) {
							player.detach();
							player.requestTeleport(entry.centerPos.getX() - entry.borderRadius, player.getY(), player.getZ());
						}
						else if (entry.centerPos.getZ() + entry.borderRadius < player.getZ()) {
							player.detach();
							player.requestTeleport(player.getX(), player.getBlockPos().getY(), entry.centerPos.getZ() + entry.borderRadius);
						}
						else if (entry.centerPos.getZ() - entry.borderRadius > player.getBlockPos().getZ()) {
							player.detach();
							player.requestTeleport(player.getX(), player.getBlockPos().getY(), entry.centerPos.getZ() - entry.borderRadius);
						}
					}
				}
			}
		});
	}

	public static void onServerTick() {

		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			HashSet<PlotEnterRequest> checkRemoval = new HashSet<>();
			for(PlotEnterRequest request : CommandPDP.requests.keySet()) {
				int time = CommandPDP.requests.get(request);
				if(time > 0) {
					time--;
					CommandPDP.requests.put(request, time);
				} else if(time <= 0) {
					checkRemoval.add(request);
					MutableText expired = Text.translatable(PDPServerLang.langTranslations(server, "pdp.commands.pdp.request_expired"));
					expired.formatted(Formatting.RED);

					if (server.getPlayerManager().getPlayer(request.getSender()) != null)
						Objects.requireNonNull(server.getPlayerManager().getPlayer(request.getSender())).sendMessage(expired);
				}
			}

			for(PlotEnterRequest remove : checkRemoval) {
				CommandPDP.requests.remove(remove);
			}
		});
	}

	public static void onBedEvents() {

		EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> {

			NbtCompound playerData = ((EntityAccessor) player).getPersistentData();
			if (player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
				if (PocketDimensionPlotsConfig.allowBedToSetSpawn) {
					if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(player);
						assert entry != null;
						if (playerData.getInt("currentPlot") == entry.plotId) {
							entry.setSafePos(sleepingPos);
							PocketDimensionPlotsDatabase.save();
							MutableText setSafe = Text.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.commands.pdp.set_safe"));
							setSafe.formatted(Formatting.GREEN);
							player.sendMessage(setSafe);
						}
					}
				}
				return false;
			}
			return true;
		});

		EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {

			if (player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
				ServerWorld level = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
				if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
					assert level != null;
					if(level.isDay()) {
						return PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW;
					}	
				} else {
					return PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE;
				}
			}
			return null;
		});

		EntitySleepEvents.ALLOW_RESETTING_TIME.register((player) -> {	

			if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
				if (player.method_48926() instanceof ServerWorld && player instanceof ServerPlayerEntity && player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
					ServerWorld level = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
					int i = player.method_48926().getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
					if (((ServerLevelAccessor) player.method_48926()).getSleepStatus().canSkipNight(i)) {
						assert level != null;
						if (level.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
							long l = level.getTimeOfDay() + 24000L;
							level.setTimeOfDay(l - l % 24000L);
						}
						((ServerLevelAccessor) player.method_48926()).wakeUpAllPlayers();
					}
				}
			}
			return true;
		});
	}
}
