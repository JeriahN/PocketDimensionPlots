package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kwpugh.gobber2.items.rings.RingTeleport;

import net.coolsimulations.PocketDimensionPlots.PDPServerLang;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.TeleportTarget;

@Mixin(RingTeleport.class)
public class RingTeleportMixin {
	
	@Inject(at = @At("HEAD"), method = "doTeleport", cancellable = true, remap = false, require = 0)
	public void canEnchant(ServerPlayerEntity player, ServerWorld world, TeleportTarget target, CallbackInfo info) {
		
		if (world.getRegistryKey() == PocketDimensionPlots.VOID || player.getLevel().dimension() == PocketDimensionPlots.VOID) {
			MutableText denied = Text.translatable(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.gobber_ring"));
			denied.formatted(Formatting.RED);
			player.sendMessage(denied);
			info.cancel();
		}
	}

}
