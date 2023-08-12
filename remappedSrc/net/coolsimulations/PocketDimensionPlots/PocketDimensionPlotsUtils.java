package net.coolsimulations.PocketDimensionPlots;

import D;
import I;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class PocketDimensionPlotsUtils {

	public static boolean playerHasPlot(PlayerEntity player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots)
			if (plot.playerOwner.equals(player.getUuid()))
				return true;
		return false;
	}

	public static boolean playerHasPlot(UUID player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots)
			if (plot.playerOwner.equals(player))
				return true;
		return false;
	}

	public static PlotEntry getPlayerPlot(PlayerEntity player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.playerOwner.equals(player.getUuid()))
				return plot;
		}
		return null;
	}

	public static PlotEntry getPlayerPlot(UUID player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.playerOwner.equals(player))
				return plot;
		}
		return null;
	}

	public static PlotEntry getPlotFromId(int plotId) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.plotId == plotId)
				return plot;
		}
		return null;
	}

	public static Text getPlayerDisplayName(MinecraftServer server, UUID player) {
		Text playerName = Text.literal(Objects.requireNonNull(server.getUserCache()).getByUuid(player).get().getName());
		if (server.getPlayerManager().getPlayer(player) != null)
			playerName = Objects.requireNonNull(server.getPlayerManager().getPlayer(player)).getDisplayName();
		return playerName;
	}

	public static PlotEntry createPlotEntry(PlayerEntity player, boolean isLargeIsland) {
		ServerWorld level = Objects.requireNonNull(player.getServer()).getWorld(PocketDimensionPlots.VOID);
		int plotId = PocketDimensionPlotsDatabase.plots.size();
		PlotEntry entry = new PlotEntry(plotId, player.getUuid(), getNewSpiralPos(plotId), PocketDimensionPlotsConfig.plotBorderRadius);

		if (!isLargeIsland)
			createSmallIsland(level, entry);
		else
			createLargeIsland(level, entry);

		PocketDimensionPlotsDatabase.addPlot(entry);
		return entry;
	}

	public static void teleportPlayerIntoPlot(PlayerEntity player, PlotEntry plotToEnter) {
		teleportPlayerIntoPlot(player, plotToEnter, new Vec3d(plotToEnter.safePos.getX(), plotToEnter.safePos.getY(), plotToEnter.safePos.getZ()));
	}

	public static void teleportPlayerIntoPlot(PlayerEntity player, PlotEntry plotToEnter, Vec3d inCoords) {
		ServerWorld level = Objects.requireNonNull(player.getServer()).getWorld(PocketDimensionPlots.VOID);
		NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
		if (player.method_48926().getRegistryKey() != PocketDimensionPlots.VOID) {
			entityData.putDouble("outPlotXPos", player.getX());
			entityData.putDouble("outPlotYPos", player.getY());
			entityData.putDouble("outPlotZPos", player.getZ());
			entityData.putString("outPlotDim", player.method_48926().getRegistryKey().getValue().toString());
		}
		player.onLanding();
		FabricDimensions.teleport(player, level, new TeleportTarget(inCoords, player.getVelocity(), player.getYaw(), player.getPitch()));
		entityData.putInt("currentPlot", plotToEnter.plotId);
		if (PocketDimensionPlotsConfig.teleportEnterMessage) {
			MutableText teleport = Text.translatable(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_into_plot"));
			if (!plotToEnter.playerOwner.equals(player.getUuid()))
				teleport = Text.translatable(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_into_player_plot"), getPlayerDisplayName(player.getServer(), plotToEnter.playerOwner));
			teleport.formatted(Formatting.GREEN);
			player.sendMessage(teleport);
		}
	}

	public static void teleportPlayerOutOfPlot(PlayerEntity player, String reason) {
		NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
		RegistryKey<World> outLevel = RegistryKey.of(RegistryKeys.WORLD, new Identifier(entityData.getString("outPlotDim")));
		Vec3d outCoords = new Vec3d(entityData.getDouble("outPlotXPos"), entityData.getDouble("outPlotYPos"), entityData.getDouble("outPlotZPos"));
		teleportPlayerOutOfPlot(player, outLevel, outCoords, reason);
	}

	public static void teleportPlayerOutOfPlot(PlayerEntity player, RegistryKey<World> outLevel, Vec3d outCoords, String reason) {
		NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
		if (player.method_48926().getRegistryKey()  == PocketDimensionPlots.VOID) {
			if (playerHasPlot(player)) {
				if (entityData.getInt("currentPlot") == Objects.requireNonNull(getPlayerPlot(player)).plotId) {
					entityData.putDouble("inPlotXPos", player.getX());
					entityData.putDouble("inPlotYPos", player.getY());
					entityData.putDouble("inPlotZPos", player.getZ());
				}
			}
			entityData.putInt("currentPlot", -1);
		}
		player.onLanding();
		FabricDimensions.teleport(player, Objects.requireNonNull(player.getServer()).getWorld(outLevel), new TeleportTarget(outCoords, player.getVelocity(), player.getYaw(), player.getPitch()));
		if (PocketDimensionPlotsConfig.teleportExitMessage) {
			MutableText teleport = Text.translatable(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_outside_plot" + (!reason.isEmpty() ? "." + reason : reason)));
			teleport.formatted(Formatting.GREEN);
			player.sendMessage(teleport);
		}
	}

	public static void kickOtherPlayersOutOfPlot(PlayerEntity player, String reason) {
		NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
		if (playerHasPlot(player)) {
			PlotEntry entry = getPlayerPlot(player);
			assert entry != null;
			if (entityData.getInt("currentPlot") == entry.plotId) {
				for (ServerPlayerEntity plotPlayer : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
					if (plotPlayer != player) {
						NbtCompound plotPlayerData = ((EntityAccessor) plotPlayer).getPersistentData();
						if (plotPlayer.method_48926().getRegistryKey() == PocketDimensionPlots.VOID && plotPlayerData.getInt("currentPlot") != -1)
							if (plotPlayerData.getInt("currentPlot") == entry.plotId && !entry.getWhitelist().contains(plotPlayer.getUuid()) && !plotPlayer.hasPermissionLevel(player.getServer().getOpPermissionLevel())) {
								teleportPlayerOutOfPlot(plotPlayer, reason);
							}
					}
				}
			}
		}
	}

	public static void createSmallIsland(ServerWorld level, PlotEntry entry) {
		for (int i = (int) entry.centerPos.getX() - (int) (double) (PocketDimensionPlotsConfig.smallIslandXSize / 2); i <= entry.centerPos.getX() + (int) Math.floor((double) PocketDimensionPlotsConfig.smallIslandXSize / 2); i++) {
			for (int j = (int) entry.centerPos.getZ() - (int) (double) (PocketDimensionPlotsConfig.smallIslandZSize / 2); j <= entry.centerPos.getZ() + (int) Math.floor((double) PocketDimensionPlotsConfig.smallIslandZSize / 2); j++) {
				level.setBlockState(new BlockPos(i, entry.centerPos.getY() - 1, j), PocketDimensionPlotsConfig.smallIslandTopBlock.getDefaultState(), 3);
				for (int k = (entry.centerPos.getY() - PocketDimensionPlotsConfig.smallIslandYSize); k < (entry.centerPos.getY() - 1); k++) {
					level.setBlockState(new BlockPos(i, k, j), PocketDimensionPlotsConfig.smallIslandMainBlock.getDefaultState(), 3);
				}
			}
		}
	}

	public static void createLargeIsland(ServerWorld level, PlotEntry entry) {
		for (int i = (int) entry.centerPos.getX() - (int) Math.floor((double) PocketDimensionPlotsConfig.largeIslandXSize / 2); i <= entry.centerPos.getX() + (int) Math.floor(PocketDimensionPlotsConfig.largeIslandXSize / 2); i++) {
			for (int j = (int) entry.centerPos.getZ() - (int) Math.floor((double) PocketDimensionPlotsConfig.largeIslandZSize / 2); j <= entry.centerPos.getZ() + (int) Math.floor(PocketDimensionPlotsConfig.largeIslandZSize / 2); j++) {
				level.setBlockState(new BlockPos(i, entry.centerPos.getY() - 1, j), PocketDimensionPlotsConfig.largeIslandTopBlock.getDefaultState(), 3);
				for (int k = (entry.centerPos.getY() - PocketDimensionPlotsConfig.largeIslandYSize); k < (entry.centerPos.getY() - 1); k++) {
					level.setBlockState(new BlockPos(i, k, j), PocketDimensionPlotsConfig.largeIslandMainBlock.getDefaultState(), 3);
				}
			}
		}
	}

	public static BlockPos getNewSpiralPos(int plotId) {
		List<Integer> list = new ArrayList<Integer>();

		var n = 3;

		for (int i = 3; i < Math.pow(plotId, 2); i++) {
			if (plotId < Math.pow(i, 2)) {
				n = i;
				break;
			}
		}

		var from = -Math.floor((double) n / 2) - 1;
		var to = -from + (n % 2) - 2;

		for (var x = to; x > from; x--) {
			for (var y = to; y > from; y--) {
				var result = Math.pow((Math.abs(Math.abs(x) - Math.abs(y)) + Math.abs(x) + Math.abs(y)), 2) + Math.abs(x + y + 0.1F) / (x + y + 0.1) * (Math.abs(Math.abs(x) - Math.abs(y)) + Math.abs(x) + Math.abs(y) + x - y) + 1;
				list.add((int)result);
			}
		}

		for(int i = 0; i < list.size(); i++) {
			if (list.get(i) == (plotId + 1)) {
				return new BlockPos(getXPos(i, n), PocketDimensionPlotsConfig.plotCenterYLevel, getYPos(i, n));
			}
		}

		return new BlockPos(0, PocketDimensionPlotsConfig.plotCenterYLevel, 0);
	}

	public static int getXPos(int index, int grid) {
		int row = (int) Math.floor((double) index / grid);
		boolean isNeg = row < Math.floor((double) grid / 2);
		int offset = (int) Math.floor((double) grid / 2) * -PocketDimensionPlotsConfig.plotSpreadDistance;
		if (isNeg)
			return row * -PocketDimensionPlotsConfig.plotSpreadDistance + offset;
		else
			return row * PocketDimensionPlotsConfig.plotSpreadDistance + offset;
	}

	public static int getYPos(int index, int grid) {
		int row = (int) Math.floor((double) index / grid);
		int column = index - row * grid;
		boolean isNeg = column < (double) (grid / 2);
		int offset = (int) Math.floor((double) grid / 2) * -PocketDimensionPlotsConfig.plotSpreadDistance;
		if (isNeg)
			return column * -PocketDimensionPlotsConfig.plotSpreadDistance  + offset;
		else
			return column * PocketDimensionPlotsConfig.plotSpreadDistance  + offset;
	}
}
