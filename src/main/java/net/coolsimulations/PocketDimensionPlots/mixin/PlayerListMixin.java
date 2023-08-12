package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.coolsimulations.PocketDimensionPlots.PDPServerLang;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUpdateHandler;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUtils;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.Objects;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@Inject(at = @At("TAIL"), method = "placeNewPlayer")
	public void placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
		if (player.level().dimension() == PocketDimensionPlots.VOID) {
			CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
			if (entityData.getInt("currentPlot") != -1) {
				PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(entityData.getInt("currentPlot"));
				assert entry != null;
				if (!entry.playerOwner.equals(player.getUUID()) && Objects.requireNonNull(player.getServer()).getPlayerList().getPlayer(entry.playerOwner) == null)
					if (!entry.getWhitelist().contains(player.getUUID()) && !player.hasPermissions(player.getServer().getOperatorUserPermissionLevel()))
						PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(player, "owner_not_online");
			}
		}

		if(PocketDimensionPlotsUpdateHandler.isOld && !PocketDimensionPlotsConfig.disableUpdateCheck) {
			if(Objects.requireNonNull(player.getServer()).isDedicatedServer())
				if(player.hasPermissions(player.getServer().getOperatorUserPermissionLevel()))
					messageOutdatedPDP(player);
				else
					messageOutdatedPDP(player);
		}
	}

	@Inject(at = @At("TAIL"), method = "remove")
	public void remove(ServerPlayer player, CallbackInfo info) {
		PocketDimensionPlotsUtils.kickOtherPlayersOutOfPlot(player, "owner_left_game");
	}

	@Unique
	private static void messageOutdatedPDP(ServerPlayer player) {
		player.sendSystemMessage(PocketDimensionPlotsUpdateHandler.updateInfo.withStyle((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.update.display2")))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://curseforge.com/minecraft/mc-mods/pocketdimensionplots"))));
		if(PocketDimensionPlotsUpdateHandler.updateVersionInfo != null)
			player.sendSystemMessage(PocketDimensionPlotsUpdateHandler.updateVersionInfo.withStyle((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.update.display2")))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://curseforge.com/minecraft/mc-mods/pocketdimensionplots"))));
	}

}
