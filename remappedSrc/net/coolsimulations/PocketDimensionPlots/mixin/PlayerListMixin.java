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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import java.util.Objects;

@Mixin(PlayerManager.class)
public class PlayerListMixin {

	@Inject(at = @At("TAIL"), method = "placeNewPlayer")
	public void placeNewPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		if (player.method_48926().getRegistryKey() == PocketDimensionPlots.VOID) {
			NbtCompound entityData = ((EntityAccessor) player).getPersistentData();
			if (entityData.getInt("currentPlot") != -1) {
				PlotEntry entry = PocketDimensionPlotsUtils.getPlotFromId(entityData.getInt("currentPlot"));
				assert entry != null;
				if (!entry.playerOwner.equals(player.getUuid()) && Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayer(entry.playerOwner) == null)
					if (!entry.getWhitelist().contains(player.getUuid()) && !player.hasPermissionLevel(player.getServer().getOpPermissionLevel()))
						PocketDimensionPlotsUtils.teleportPlayerOutOfPlot(player, "owner_not_online");
			}
		}

		if(PocketDimensionPlotsUpdateHandler.isOld && !PocketDimensionPlotsConfig.disableUpdateCheck) {
			if(Objects.requireNonNull(player.getServer()).isDedicated())
				if(player.hasPermissionLevel(player.getServer().getOpPermissionLevel()))
					messageOutdatedPDP(player);
				else
					messageOutdatedPDP(player);
		}
	}

	@Inject(at = @At("TAIL"), method = "remove")
	public void remove(ServerPlayerEntity player, CallbackInfo info) {
		PocketDimensionPlotsUtils.kickOtherPlayersOutOfPlot(player, "owner_left_game");
	}

	@Unique
	private static void messageOutdatedPDP(ServerPlayerEntity player) {
		player.sendMessage(PocketDimensionPlotsUpdateHandler.updateInfo.styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.update.display2")))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://curseforge.com/minecraft/mc-mods/pocketdimensionplots"))));
		if(PocketDimensionPlotsUpdateHandler.updateVersionInfo != null)
			player.sendMessage(PocketDimensionPlotsUpdateHandler.updateVersionInfo.styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(PDPServerLang.langTranslations(Objects.requireNonNull(player.getServer()), "pdp.update.display2")))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://curseforge.com/minecraft/mc-mods/pocketdimensionplots"))));
	}

}
