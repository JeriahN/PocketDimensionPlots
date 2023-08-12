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
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

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
				ItemStack stack = player.getItemInHand(hand);

				if (!world.isClientSide) {
					if (stack.getItem() == PocketDimensionPlotsConfig.teleportItem) {
						if(player.canChangeDimensions()) {
							Objects.requireNonNull(player.getServer()).getCommands().performPrefixedCommand(player.createCommandSourceStack(), "pdp");
							return InteractionResultHolder.success(stack);
						}
					}
				}
				return InteractionResultHolder.pass(stack);
			});
		}
	}

	public static void onWorldLoad() {

		ServerWorldEvents.LOAD.register((server, level) -> {
			Path worldSave = Path.of(server.getWorldPath(LevelResource.ROOT).toString(), "serverconfig");
			try {
				Files.createDirectories(worldSave);
			} catch (IOException ignored) {}
			PocketDimensionPlotsDatabase.init(new File(worldSave.toFile(), PDPReference.MOD_ID + "_database.json"));
		});
	}

	public static void onWorldTick() {

		ServerTickEvents.START_WORLD_TICK.register((level) -> {
			if(level.dimension() == PocketDimensionPlots.VOID) {
				for (Player player : level.getPlayers((player) -> { return ((EntityAccessor) player).getPersistentData().contains("currentPlot") && ((EntityAccessor) player).getPersistentData().getInt("currentPlot") != -1; })) {
					CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
					PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(entityData.getInt("currentPlot"));

					if (entry != null) {
						if (entry.centerPos.getX() + entry.borderRadius < player.blockPosition().getX()) {
							player.unRide();
							player.teleportTo(entry.centerPos.getX() + entry.borderRadius, player.getY(), player.getZ());
						} else if (entry.centerPos.getX() - entry.borderRadius > player.getX()) {
							player.unRide();
							player.teleportTo(entry.centerPos.getX() - entry.borderRadius, player.getY(), player.getZ());
						}
						else if (entry.centerPos.getZ() + entry.borderRadius < player.getZ()) {
							player.unRide();
							player.teleportTo(player.getX(), player.blockPosition().getY(), entry.centerPos.getZ() + entry.borderRadius);
						}
						else if (entry.centerPos.getZ() - entry.borderRadius > player.blockPosition().getZ()) {
							player.unRide();
							player.teleportTo(player.getX(), player.blockPosition().getY(), entry.centerPos.getZ() - entry.borderRadius);
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
					MutableComponent expired = Component.translatable(PDPServerLang.langTranslations(server, "pdp.commands.pdp.request_expired"));
					expired.withStyle(ChatFormatting.RED);

					if (server.getPlayerList().getPlayer(request.getSender()) != null)
						Objects.requireNonNull(server.getPlayerList().getPlayer(request.getSender())).sendSystemMessage(expired);
				}
			}

			for(PlotEnterRequest remove : checkRemoval) {
				CommandPDP.requests.remove(remove);
			}
		});
	}

	public static void onBedEvents() {

		EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> {

			CompoundTag playerData = ((EntityAccessor) player).getPersistentData();
			if (player.level().dimension() == PocketDimensionPlots.VOID) {
				if (PocketDimensionPlotsConfig.allowBedToSetSpawn) {
					if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(player);
						assert entry != null;
						if (playerData.getInt("currentPlot") == entry.plotId) {
							entry.setSafePos(sleepingPos);
							PocketDimensionPlotsDatabase.save();
							MutableComponent setSafe = Component.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.commands.pdp.set_safe"));
							setSafe.withStyle(ChatFormatting.GREEN);
							player.sendSystemMessage(setSafe);
						}
					}
				}
				return false;
			}
			return true;
		});

		EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {

			if (player.level().dimension() == PocketDimensionPlots.VOID) {
				ServerLevel level = Objects.requireNonNull(player.getServer()).getLevel(Level.OVERWORLD);
				if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
					assert level != null;
					if(level.isDay()) {
						return Player.BedSleepingProblem.NOT_POSSIBLE_NOW;
					}	
				} else {
					return Player.BedSleepingProblem.NOT_POSSIBLE_HERE;
				}
			}
			return null;
		});

		EntitySleepEvents.ALLOW_RESETTING_TIME.register((player) -> {	

			if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
				if (player.level() instanceof ServerLevel && player instanceof ServerPlayer && player.level().dimension() == PocketDimensionPlots.VOID) {
					ServerLevel level = Objects.requireNonNull(player.getServer()).getLevel(Level.OVERWORLD);
					int i = player.level().getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
					if (((ServerLevelAccessor) player.level()).getSleepStatus().areEnoughSleeping(i)) {
						assert level != null;
						if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
							long l = level.getDayTime() + 24000L;
							level.setDayTime(l - l % 24000L);
						}
						((ServerLevelAccessor) player.level()).wakeUpAllPlayers();
					}
				}
			}
			return true;
		});
	}
}
