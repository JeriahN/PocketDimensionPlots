package net.coolsimulations.PocketDimensionPlots.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.coolsimulations.PocketDimensionPlots.PDPServerLang;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUtils;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("resource")
public class CommandPDP {

	public static HashMap<PlotEnterRequest, Integer> requests = new HashMap<>();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("pdp").requires((s) -> {
			return s.hasPermissionLevel(0);
		}).executes(pdp -> pdpTeleport(pdp.getSource()
				)).then(CommandManager.literal("whitelist").then(CommandManager.literal("add").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((sender, builder) -> {
					PlayerManager playerlist = sender.getSource().getServer().getPlayerManager();
					return CommandSource.suggestMatching(playerlist.getPlayerList().stream().filter((player) -> {
						if(player != sender.getSource().getPlayer() && PocketDimensionPlotsUtils.playerHasPlot(sender.getSource().getPlayer())) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getSource().getPlayer());
							return !entry.getWhitelist().contains(player.getUuid());
						}
						return false;
					}).map((player) -> {
						return player.getGameProfile().getName();
					}), builder);
				}).executes((pdp) -> {
					return whitelist(pdp.getSource(), GameProfileArgumentType.getProfileArgument(pdp, "targets"), true);
				}))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((sender, builder) -> {
					List<String> names = new ArrayList<>();
					if(PocketDimensionPlotsUtils.playerHasPlot(sender.getSource().getPlayer())) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getSource().getPlayer());
						for(UUID player : entry.getWhitelist()) {
							names.add(sender.getSource().getServer().getUserCache().getByUuid(player).get().getName());
						}
					}
					return CommandSource.suggestMatching(names, builder);
				}).executes((pdp) -> {
					return whitelist(pdp.getSource(), GameProfileArgumentType.getProfileArgument(pdp, "targets"), false);
				}))))
				.then(CommandManager.literal("enter").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((sender, builder) -> {
					List<String> names = new ArrayList<>();
					for (PlotEntry entry : PocketDimensionPlotsDatabase.plots)
						if (entry.getWhitelist().contains(sender.getSource().getPlayer().getUuid()) || sender.getSource().hasPermissionLevel(sender.getSource().getServer().getOpPermissionLevel()))
							names.add(sender.getSource().getServer().getUserCache().getByUuid(entry.playerOwner).get().getName());
					for (ServerPlayerEntity player : sender.getSource().getServer().getPlayerManager().getPlayerList())
						if (!names.contains(player.getGameProfile().getName()) && player != sender.getSource().getPlayer())
							names.add(player.getGameProfile().getName());
					return CommandSource.suggestMatching(names, builder);
				}).executes((pdp) -> {
					return enter(pdp.getSource(), GameProfileArgumentType.getProfileArgument(pdp, "targets"));
				}))).then(CommandManager.literal("kick").then(CommandManager.argument("targets", EntityArgumentType.players()).suggests((sender, builder) -> {
					PlayerManager playerlist = sender.getSource().getServer().getPlayerManager();
					return CommandSource.suggestMatching(playerlist.getPlayerList().stream().filter((player) -> {
						if (player != sender.getSource().getPlayer() && player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
							NbtCompound senderData = ((EntityAccessor) sender.getSource().getPlayer()).getPersistentData();
							NbtCompound targetData = ((EntityAccessor) player).getPersistentData();
							if (senderData.getInt("currentPlot") != -1)
								return senderData.getInt("currentPlot") == targetData.getInt("currentPlot");
							else if (PocketDimensionPlotsUtils.playerHasPlot(sender.getSource().getPlayer()))
								return PocketDimensionPlotsUtils.getPlayerPlot(sender.getSource().getPlayer()).plotId == targetData.getInt("currentPlot");
						}
						return false;
					}).map((player) -> {
						return player.getGameProfile().getName();
					}), builder);
				}).executes((pdp) -> {
					return kick(pdp.getSource(), EntityArgumentType.getPlayers(pdp, "targets"));
				}))).then(CommandManager.literal("accept").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((pdp) -> {
					return accept(pdp.getSource(), EntityArgumentType.getPlayers(pdp, "targets"));
				}))).then(CommandManager.literal("setspawn").executes((pdp -> setSpawn(pdp.getSource())
						))).then(CommandManager.literal("create").then(CommandManager.literal("large").executes((pdp -> createIsland(pdp.getSource(), true)
								))).then(CommandManager.literal("small").executes((pdp -> createIsland(pdp.getSource(), false))))));
	}

	private static int whitelist(ServerCommandSource sender, Collection<GameProfile> players, boolean addToWhitelist) {
		Iterator<GameProfile> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) sender.getEntity();
			while (var3.hasNext()) {
				GameProfile otherPlayer = (GameProfile) var3.next();

				if (otherPlayer.getId().equals(player.getUuid())) {

					throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.whitelist")));

				} else {
					if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(player);

						if (addToWhitelist) {
							entry.addPlayerToWhitelist(otherPlayer.getId());

							MutableText add = Text.translatable("commands.whitelist.add.success", new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherPlayer.getId())});
							add.formatted(Formatting.GREEN);
							sender.sendFeedback((Supplier<Text>) add, false);
						}
						else {
							entry.removePlayerFromWhitelist(otherPlayer.getId());

							MutableText add = Text.translatable("commands.whitelist.remove.success", new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherPlayer.getId())});
							add.formatted(Formatting.GREEN);
							sender.sendFeedback((Supplier<Text>) add, false);
						}
					} else {
						sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.no_plot")));
					}
				}
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int enter(ServerCommandSource sender, Collection<GameProfile> players) {
		Iterator<GameProfile> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) sender.getEntity();
			while (var3.hasNext()) {
				GameProfile otherProfile = (GameProfile) var3.next();

				if (otherProfile.getId().equals(player.getUuid())) {

					throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.enter")));

				} else {
					if (sender.getServer().getPlayerManager().getPlayer(otherProfile.getId()) != null) {
						ServerPlayerEntity otherPlayer = sender.getServer().getPlayerManager().getPlayer(otherProfile.getId());
						NbtCompound otherPlayerData = ((EntityAccessor) otherPlayer).getPersistentData();
						if (otherPlayer.method_48926().getRegistryKey() == PocketDimensionPlots.VOID && otherPlayerData.getInt("currentPlot") != -1) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(otherPlayerData.getInt("currentPlot"));

							if (entry.getWhitelist().contains(player.getUuid()) || player.hasPermissionLevel(sender.getServer().getOpPermissionLevel())) {
								PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3d(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
							} else {
								PlotEnterRequest enter = new PlotEnterRequest(entry.plotId, player.getUuid());
								requests.put(enter, PocketDimensionPlotsConfig.teleportRequestTimeout * 20);
								MutableText sentRequest = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.enter.send"), new Object[] {otherPlayer.getDisplayName()});
								sentRequest.formatted(Formatting.GREEN);
								sender.sendFeedback((Supplier<Text>) sentRequest, false);

								MutableText sendRequest = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.enter.recieve"), new Object[] {sender.getDisplayName(), sender.getDisplayName()});
								sendRequest.formatted(Formatting.GREEN);
								otherPlayer.sendMessage(sendRequest.styled((style) -> {return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp accept " + sender.getName()));}));
							}
						} else {
							if (PocketDimensionPlotsUtils.playerHasPlot(otherProfile.getId())) {
								PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(otherProfile.getId());
								if (entry.getWhitelist().contains(player.getUuid()) || player.hasPermissionLevel(sender.getServer().getOpPermissionLevel())) {
									PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3d(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
								} else {
									sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_in_plot"), new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherProfile.getId())}));
								}
							}
						}
					} else {
						if (PocketDimensionPlotsUtils.playerHasPlot(otherProfile.getId())) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(otherProfile.getId());
							if (entry.getWhitelist().contains(player.getUuid()) || player.hasPermissionLevel(sender.getServer().getOpPermissionLevel())) {
								PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3d(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
							} else {
								sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.need_whitelist")));
							}
						} else {
							sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.other_no_plot"), new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherProfile.getId())}));
						}
					}
				}
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int accept(ServerCommandSource sender, Collection<ServerPlayerEntity> players) {
		Iterator<ServerPlayerEntity> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) sender.getEntity();
			while (var3.hasNext()) {
				ServerPlayerEntity otherPlayer = (ServerPlayerEntity) var3.next();

				if (otherPlayer == player) {

					throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.accept")));

				} else {
					PlotEnterRequest enter = null;
					for(PlotEnterRequest pair : requests.keySet()) {
						if(pair.getSender().equals(otherPlayer.getUuid())) {
							enter = pair;
						}
					}

					if (enter != null) {
						requests.remove(enter);
						PocketDimensionPlotsUtils.teleportPlayerIntoPlot(otherPlayer, enter.getPlot());
						MutableText accept = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.accept"), new Object[] {sender.getDisplayName(), sender.getDisplayName()});
						accept.formatted(Formatting.GREEN);
						sender.sendFeedback((Supplier<Text>) accept, false);
					} else {
						sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.accept.no_request"), new Object[] {otherPlayer.getDisplayName()}));
					}
				}
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int kick(ServerCommandSource sender, Collection<ServerPlayerEntity> players) {
		Iterator<ServerPlayerEntity> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) sender.getEntity();
			NbtCompound playerData = ((EntityAccessor) player).getPersistentData();
			while (var3.hasNext()) {
				ServerPlayerEntity otherPlayer = (ServerPlayerEntity) var3.next();
				NbtCompound otherPlayerData = ((EntityAccessor) otherPlayer).getPersistentData();

				if (otherPlayer == player) {

					throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.kick")));

				} else {
					if (!otherPlayer.hasPermissionLevel(sender.getServer().getOpPermissionLevel())) {
						if (otherPlayer.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {

							if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
								PlotEntry playerPlot = PocketDimensionPlotsUtils.getPlayerPlot(player);
								if (otherPlayerData.getInt("currentPlot") == playerPlot.plotId) {
									PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(otherPlayer, "owner_kicked");
									MutableText kick = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick"), new Object[] {otherPlayer.getDisplayName()});
									kick.formatted(Formatting.GREEN);
									sender.sendFeedback((Supplier<Text>) kick, true);
									return players.size();
								}
							}

							if (player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
								if (playerData.getInt("currentPlot") == otherPlayerData.getInt("currentPlot")) {
									PlotEntry commonPlot = PocketDimensionPlotsUtils.getPlotFromId(playerData.getInt("currentPlot"));
									if (commonPlot.playerOwner != otherPlayer.getUuid()) {
										if (!commonPlot.getWhitelist().contains(player.getUuid()))
											sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.not_whitelisted")));
										else if (commonPlot.getWhitelist().contains(otherPlayer.getUuid()))
											sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.whitelist")));
										else {
											MutableText kick = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick"), new Object[] {otherPlayer.getDisplayName()});
											kick.formatted(Formatting.GREEN);
											sender.sendFeedback((Supplier<Text>) kick, true);
											PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(otherPlayer, "owner_kicked");
										}
									}
								}
							} else {
								throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.not_in_plot")));
							}
						} else {
							sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.no_plot"), new Object[] {otherPlayer.getDisplayName()}));
						}
					} else {
						sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.admin")));
					}
				}
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int setSpawn(ServerCommandSource sender) {
		if (sender.getEntity() instanceof ServerPlayerEntity) {
			NbtCompound playerData = ((EntityAccessor) sender.getPlayer()).getPersistentData();
			if (PocketDimensionPlotsUtils.playerHasPlot(sender.getPlayer())) {
				PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getPlayer());
				if (sender.getPlayer().method_48926().getRegistryKey() == PocketDimensionPlots.VOID && playerData.getInt("currentPlot") == entry.plotId) {
					entry.setSafePos(sender.getPlayer().getBlockPos());
					PocketDimensionPlotsDatabase.save();
					MutableText setSafe = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.set_safe"));
					setSafe.formatted(Formatting.GREEN);
					sender.sendFeedback((Supplier<Text>) setSafe, false);
				} else {
					sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.set_safe.not_owner")));
				}
			} else {
				sender.sendError(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.no_plot")));
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int createIsland(ServerCommandSource sender, boolean isLargeIsland) {
		if (sender.getEntity() instanceof ServerPlayerEntity) {
			if (!PocketDimensionPlotsUtils.playerHasPlot(sender.getPlayer())) {
				PlotEntry entry = PocketDimensionPlotsUtils.createPlotEntry(sender.getPlayer(), isLargeIsland);
				PocketDimensionPlotsUtils.teleportPlayerIntoPlot(sender.getPlayer(), entry);
			} else {
				throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.has_island")));
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int pdpTeleport(ServerCommandSource sender) {
		if (sender.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) sender.getEntity();
			NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
			if (player.method_48926().getRegistryKey() != PocketDimensionPlots.VOID) {
				PlotEntry entry;
				if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
					entry = PocketDimensionPlotsUtils.getPlayerPlot(player);
					Vec3d inCoords = new Vec3d(entityData.getDouble("inPlotXPos"), entityData.getDouble("inPlotYPos"), entityData.getDouble("inPlotZPos"));
					PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, inCoords);
				} else {
					MutableText small = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.small"));
					small.formatted(Formatting.BLUE);

					MutableText large = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.large"));
					large.formatted(Formatting.GOLD);

					MutableText createIsland = Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot"), new Object[] {small.styled((style -> {
						return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp create small")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.tooltip"), new Object[] {PocketDimensionPlotsConfig.smallIslandXSize, PocketDimensionPlotsConfig.smallIslandYSize, PocketDimensionPlotsConfig.smallIslandZSize, PocketDimensionPlotsConfig.smallIslandMainBlock.getName()})));
					})), large.styled((style -> {
						return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp create large")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.tooltip"), new Object[] {PocketDimensionPlotsConfig.largeIslandXSize, PocketDimensionPlotsConfig.largeIslandYSize, PocketDimensionPlotsConfig.largeIslandZSize, PocketDimensionPlotsConfig.largeIslandMainBlock.getName()})));
					}))});
					createIsland.formatted(Formatting.GREEN);
					sender.sendMessage(createIsland);
				}
			} else {
				PocketDimensionPlotsUtils.kickOtherPlayersOutOfPlot(player, "owner_left_plot");
				PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(player, "");
			}
		} else {
			throw new CommandException(Text.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return Command.SINGLE_SUCCESS;
	}

	public static class PlotEnterRequest {

		private final int plotDestination;
		private final UUID sender;

		public PlotEnterRequest(int plotDestinationIn, UUID senderIn) {
			this.plotDestination = plotDestinationIn;
			this.sender = senderIn;
		}

		public PlotEntry getPlot() {
			return PocketDimensionPlotsUtils.getPlotFromId(plotDestination);
		}

		public UUID getSender() {
			return sender;
		}
	}
}
