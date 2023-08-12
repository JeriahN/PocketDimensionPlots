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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("resource")
public class CommandPDP {

	public static HashMap<PlotEnterRequest, Integer> requests = new HashMap<>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("pdp").requires((s) -> {
			return s.hasPermission(0);
		}).executes(pdp -> pdpTeleport(pdp.getSource()
				)).then(Commands.literal("whitelist").then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((sender, builder) -> {
					PlayerList playerlist = sender.getSource().getServer().getPlayerList();
					return SharedSuggestionProvider.suggest(playerlist.getPlayers().stream().filter((player) -> {
						if(player != sender.getSource().getPlayer() && PocketDimensionPlotsUtils.playerHasPlot(sender.getSource().getPlayer())) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getSource().getPlayer());
							return !entry.getWhitelist().contains(player.getUUID());
						}
						return false;
					}).map((player) -> {
						return player.getGameProfile().getName();
					}), builder);
				}).executes((pdp) -> {
					return whitelist(pdp.getSource(), GameProfileArgument.getGameProfiles(pdp, "targets"), true);
				}))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((sender, builder) -> {
					List<String> names = new ArrayList<>();
					if(PocketDimensionPlotsUtils.playerHasPlot(sender.getSource().getPlayer())) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getSource().getPlayer());
						for(UUID player : entry.getWhitelist()) {
							names.add(sender.getSource().getServer().getProfileCache().get(player).get().getName());
						}
					}
					return SharedSuggestionProvider.suggest(names, builder);
				}).executes((pdp) -> {
					return whitelist(pdp.getSource(), GameProfileArgument.getGameProfiles(pdp, "targets"), false);
				}))))
				.then(Commands.literal("enter").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((sender, builder) -> {
					List<String> names = new ArrayList<>();
					for (PlotEntry entry : PocketDimensionPlotsDatabase.plots)
						if (entry.getWhitelist().contains(sender.getSource().getPlayer().getUUID()) || sender.getSource().hasPermission(sender.getSource().getServer().getOperatorUserPermissionLevel()))
							names.add(sender.getSource().getServer().getProfileCache().get(entry.playerOwner).get().getName());
					for (ServerPlayer player : sender.getSource().getServer().getPlayerList().getPlayers())
						if (!names.contains(player.getGameProfile().getName()) && player != sender.getSource().getPlayer())
							names.add(player.getGameProfile().getName());
					return SharedSuggestionProvider.suggest(names, builder);
				}).executes((pdp) -> {
					return enter(pdp.getSource(), GameProfileArgument.getGameProfiles(pdp, "targets"));
				}))).then(Commands.literal("kick").then(Commands.argument("targets", EntityArgument.players()).suggests((sender, builder) -> {
					PlayerList playerlist = sender.getSource().getServer().getPlayerList();
					return SharedSuggestionProvider.suggest(playerlist.getPlayers().stream().filter((player) -> {
						if (player != sender.getSource().getPlayer() && player.level().dimension() == PocketDimensionPlots.VOID) {
							CompoundTag senderData = ((EntityAccessor) sender.getSource().getPlayer()).getPersistentData();
							CompoundTag targetData = ((EntityAccessor) player).getPersistentData();
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
					return kick(pdp.getSource(), EntityArgument.getPlayers(pdp, "targets"));
				}))).then(Commands.literal("accept").then(Commands.argument("targets", EntityArgument.players()).executes((pdp) -> {
					return accept(pdp.getSource(), EntityArgument.getPlayers(pdp, "targets"));
				}))).then(Commands.literal("setspawn").executes((pdp -> setSpawn(pdp.getSource())
						))).then(Commands.literal("create").then(Commands.literal("large").executes((pdp -> createIsland(pdp.getSource(), true)
								))).then(Commands.literal("small").executes((pdp -> createIsland(pdp.getSource(), false))))));
	}

	private static int whitelist(CommandSourceStack sender, Collection<GameProfile> players, boolean addToWhitelist) {
		Iterator<GameProfile> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) sender.getEntity();
			while (var3.hasNext()) {
				GameProfile otherPlayer = (GameProfile) var3.next();

				if (otherPlayer.getId().equals(player.getUUID())) {

					throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.whitelist")));

				} else {
					if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
						PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(player);

						if (addToWhitelist) {
							entry.addPlayerToWhitelist(otherPlayer.getId());

							MutableComponent add = Component.translatable("commands.whitelist.add.success", new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherPlayer.getId())});
							add.withStyle(ChatFormatting.GREEN);
							sender.sendSuccess((Supplier<Component>) add, false);
						}
						else {
							entry.removePlayerFromWhitelist(otherPlayer.getId());

							MutableComponent add = Component.translatable("commands.whitelist.remove.success", new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherPlayer.getId())});
							add.withStyle(ChatFormatting.GREEN);
							sender.sendSuccess((Supplier<Component>) add, false);
						}
					} else {
						sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.no_plot")));
					}
				}
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int enter(CommandSourceStack sender, Collection<GameProfile> players) {
		Iterator<GameProfile> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) sender.getEntity();
			while (var3.hasNext()) {
				GameProfile otherProfile = (GameProfile) var3.next();

				if (otherProfile.getId().equals(player.getUUID())) {

					throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.enter")));

				} else {
					if (sender.getServer().getPlayerList().getPlayer(otherProfile.getId()) != null) {
						ServerPlayer otherPlayer = sender.getServer().getPlayerList().getPlayer(otherProfile.getId());
						CompoundTag otherPlayerData = ((EntityAccessor) otherPlayer).getPersistentData();
						if (otherPlayer.level().dimension() == PocketDimensionPlots.VOID && otherPlayerData.getInt("currentPlot") != -1) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(otherPlayerData.getInt("currentPlot"));

							if (entry.getWhitelist().contains(player.getUUID()) || player.hasPermissions(sender.getServer().getOperatorUserPermissionLevel())) {
								PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
							} else {
								PlotEnterRequest enter = new PlotEnterRequest(entry.plotId, player.getUUID());
								requests.put(enter, PocketDimensionPlotsConfig.teleportRequestTimeout * 20);
								MutableComponent sentRequest = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.enter.send"), new Object[] {otherPlayer.getDisplayName()});
								sentRequest.withStyle(ChatFormatting.GREEN);
								sender.sendSuccess((Supplier<Component>) sentRequest, false);

								MutableComponent sendRequest = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.enter.recieve"), new Object[] {sender.getDisplayName(), sender.getDisplayName()});
								sendRequest.withStyle(ChatFormatting.GREEN);
								otherPlayer.sendSystemMessage(sendRequest.withStyle((style) -> {return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp accept " + sender.getTextName()));}));
							}
						} else {
							if (PocketDimensionPlotsUtils.playerHasPlot(otherProfile.getId())) {
								PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(otherProfile.getId());
								if (entry.getWhitelist().contains(player.getUUID()) || player.hasPermissions(sender.getServer().getOperatorUserPermissionLevel())) {
									PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
								} else {
									sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_in_plot"), new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherProfile.getId())}));
								}
							}
						}
					} else {
						if (PocketDimensionPlotsUtils.playerHasPlot(otherProfile.getId())) {
							PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(otherProfile.getId());
							if (entry.getWhitelist().contains(player.getUUID()) || player.hasPermissions(sender.getServer().getOperatorUserPermissionLevel())) {
								PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, new Vec3(entry.safePos.getX(), entry.safePos.getY(), entry.safePos.getZ()));
							} else {
								sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.need_whitelist")));
							}
						} else {
							sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.other_no_plot"), new Object[] {PocketDimensionPlotsUtils.getPlayerDisplayName(player.getServer(), otherProfile.getId())}));
						}
					}
				}
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int accept(CommandSourceStack sender, Collection<ServerPlayer> players) {
		Iterator<ServerPlayer> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) sender.getEntity();
			while (var3.hasNext()) {
				ServerPlayer otherPlayer = (ServerPlayer) var3.next();

				if (otherPlayer == player) {

					throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.accept")));

				} else {
					PlotEnterRequest enter = null;
					for(PlotEnterRequest pair : requests.keySet()) {
						if(pair.getSender().equals(otherPlayer.getUUID())) {
							enter = pair;
						}
					}

					if (enter != null) {
						requests.remove(enter);
						PocketDimensionPlotsUtils.teleportPlayerIntoPlot(otherPlayer, enter.getPlot());
						MutableComponent accept = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.accept"), new Object[] {sender.getDisplayName(), sender.getDisplayName()});
						accept.withStyle(ChatFormatting.GREEN);
						sender.sendSuccess((Supplier<Component>) accept, false);
					} else {
						sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.accept.no_request"), new Object[] {otherPlayer.getDisplayName()}));
					}
				}
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int kick(CommandSourceStack sender, Collection<ServerPlayer> players) {
		Iterator<ServerPlayer> var3 = players.iterator();

		if (sender.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) sender.getEntity();
			CompoundTag playerData = ((EntityAccessor) player).getPersistentData();
			while (var3.hasNext()) {
				ServerPlayer otherPlayer = (ServerPlayer) var3.next();
				CompoundTag otherPlayerData = ((EntityAccessor) otherPlayer).getPersistentData();

				if (otherPlayer == player) {

					throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.sameTarget.kick")));

				} else {
					if (!otherPlayer.hasPermissions(sender.getServer().getOperatorUserPermissionLevel())) {
						if (otherPlayer.level().dimension() == PocketDimensionPlots.VOID) {

							if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
								PlotEntry playerPlot = PocketDimensionPlotsUtils.getPlayerPlot(player);
								if (otherPlayerData.getInt("currentPlot") == playerPlot.plotId) {
									PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(otherPlayer, "owner_kicked");
									MutableComponent kick = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick"), new Object[] {otherPlayer.getDisplayName()});
									kick.withStyle(ChatFormatting.GREEN);
									sender.sendSuccess((Supplier<Component>) kick, true);
									return players.size();
								}
							}

							if (player.level().dimension() == PocketDimensionPlots.VOID) {
								if (playerData.getInt("currentPlot") == otherPlayerData.getInt("currentPlot")) {
									PlotEntry commonPlot = PocketDimensionPlotsUtils.getPlotFromId(playerData.getInt("currentPlot"));
									if (commonPlot.playerOwner != otherPlayer.getUUID()) {
										if (!commonPlot.getWhitelist().contains(player.getUUID()))
											sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.not_whitelisted")));
										else if (commonPlot.getWhitelist().contains(otherPlayer.getUUID()))
											sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.whitelist")));
										else {
											MutableComponent kick = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick"), new Object[] {otherPlayer.getDisplayName()});
											kick.withStyle(ChatFormatting.GREEN);
											sender.sendSuccess((Supplier<Component>) kick, true);
											PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(otherPlayer, "owner_kicked");
										}
									}
								}
							} else {
								throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.not_in_plot")));
							}
						} else {
							sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.no_plot"), new Object[] {otherPlayer.getDisplayName()}));
						}
					} else {
						sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.kick.admin")));
					}
				}
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return players.size();
	}

	private static int setSpawn(CommandSourceStack sender) {
		if (sender.getEntity() instanceof ServerPlayer) {
			CompoundTag playerData = ((EntityAccessor) sender.getPlayer()).getPersistentData();
			if (PocketDimensionPlotsUtils.playerHasPlot(sender.getPlayer())) {
				PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(sender.getPlayer());
				if (sender.getPlayer().level().dimension() == PocketDimensionPlots.VOID && playerData.getInt("currentPlot") == entry.plotId) {
					entry.setSafePos(sender.getPlayer().blockPosition());
					PocketDimensionPlotsDatabase.save();
					MutableComponent setSafe = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.set_safe"));
					setSafe.withStyle(ChatFormatting.GREEN);
					sender.sendSuccess((Supplier<Component>) setSafe, false);
				} else {
					sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.set_safe.not_owner")));
				}
			} else {
				sender.sendFailure(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.no_plot")));
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int createIsland(CommandSourceStack sender, boolean isLargeIsland) {
		if (sender.getEntity() instanceof ServerPlayer) {
			if (!PocketDimensionPlotsUtils.playerHasPlot(sender.getPlayer())) {
				PlotEntry entry = PocketDimensionPlotsUtils.createPlotEntry(sender.getPlayer(), isLargeIsland);
				PocketDimensionPlotsUtils.teleportPlayerIntoPlot(sender.getPlayer(), entry);
			} else {
				throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.has_island")));
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int pdpTeleport(CommandSourceStack sender) {
		if (sender.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) sender.getEntity();
			CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
			if (player.level().dimension() != PocketDimensionPlots.VOID) {
				PlotEntry entry;
				if (PocketDimensionPlotsUtils.playerHasPlot(player)) {
					entry = PocketDimensionPlotsUtils.getPlayerPlot(player);
					Vec3 inCoords = new Vec3(entityData.getDouble("inPlotXPos"), entityData.getDouble("inPlotYPos"), entityData.getDouble("inPlotZPos"));
					PocketDimensionPlotsUtils.teleportPlayerIntoPlot(player, entry, inCoords);
				} else {
					MutableComponent small = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.small"));
					small.withStyle(ChatFormatting.BLUE);

					MutableComponent large = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.large"));
					large.withStyle(ChatFormatting.GOLD);

					MutableComponent createIsland = Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot"), new Object[] {small.withStyle((style -> {
						return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp create small")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.tooltip"), new Object[] {PocketDimensionPlotsConfig.smallIslandXSize, PocketDimensionPlotsConfig.smallIslandYSize, PocketDimensionPlotsConfig.smallIslandZSize, PocketDimensionPlotsConfig.smallIslandMainBlock.getName()})));
					})), large.withStyle((style -> {
						return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pdp create large")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.create_plot.tooltip"), new Object[] {PocketDimensionPlotsConfig.largeIslandXSize, PocketDimensionPlotsConfig.largeIslandYSize, PocketDimensionPlotsConfig.largeIslandZSize, PocketDimensionPlotsConfig.largeIslandMainBlock.getName()})));
					}))});
					createIsland.withStyle(ChatFormatting.GREEN);
					sender.sendSystemMessage(createIsland);
				}
			} else {
				PocketDimensionPlotsUtils.kickOtherPlayersOutOfPlot(player, "owner_left_plot");
				PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(player, "");
			}
		} else {
			throw new CommandRuntimeException(Component.translatable(PDPServerLang.langTranslations(sender.getServer(), "pdp.commands.pdp.not_player")));
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
