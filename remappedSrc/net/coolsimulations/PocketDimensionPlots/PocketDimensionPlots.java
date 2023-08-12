package net.coolsimulations.PocketDimensionPlots;

import java.io.File;

import net.coolsimulations.PocketDimensionPlots.commands.CommandPDP;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PocketDimensionPlots implements ModInitializer {
	
	private static PocketDimensionPlots instance;
	public static PocketDimensionPlots getInstance()
	{
		return instance;
	}

	public static final RegistryKey<World> VOID = RegistryKey.of(RegistryKeys.WORLD, new Identifier(PDPReference.MOD_ID, "void"));

	@Override
	public void onInitialize() {
		
		PocketDimensionPlotsConfig.init(new File(FabricLoader.getInstance().getConfigDir().toFile(), PDPReference.MOD_ID + ".json"));
		ServerLifecycleEvents.SERVER_STARTING.register(PocketDimensionPlotsUpdateHandler::init);
		PocketDimensionPlotsEventHandler.registerEvents();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandPDP.register(dispatcher);
		});
	}

}
