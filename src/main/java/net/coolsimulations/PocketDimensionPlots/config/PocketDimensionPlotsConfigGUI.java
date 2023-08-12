package net.coolsimulations.PocketDimensionPlots.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.coolsimulations.PocketDimensionPlots.PDPReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
public class PocketDimensionPlotsConfigGUI {

	public static Screen getConfigScreen(Screen parent)
	{
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("pdp.configgui.title"));

		builder.setSavingRunnable(() -> {
			PocketDimensionPlotsConfig.save(PocketDimensionPlotsConfig.getFile(), PocketDimensionPlotsConfig.getObject());
			PocketDimensionPlotsConfig.load(PocketDimensionPlotsConfig.getFile());
		});

		builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/sculk_catalyst_top.png"));

		ConfigCategory plots = builder.getOrCreateCategory(Component.translatable(PDPReference.CONFIG_CATEGORY_PLOTS));
		ConfigCategory server = builder.getOrCreateCategory(Component.translatable(PDPReference.CONFIG_CATEGORY_SERVER));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ResourceLocation teleport_location = BuiltInRegistries.ITEM.getKey(PocketDimensionPlotsConfig.teleportItem);
		ResourceLocation feather = BuiltInRegistries.ITEM.getKey(Items.FEATHER);
		ResourceLocation teleport_sound_location = BuiltInRegistries.SOUND_EVENT.getKey(PocketDimensionPlotsConfig.teleportSound);
		ResourceLocation enderman = BuiltInRegistries.SOUND_EVENT.getKey(SoundEvents.ENDERMAN_TELEPORT);

		ResourceLocation small_top_location = BuiltInRegistries.BLOCK.getKey(PocketDimensionPlotsConfig.smallIslandTopBlock);
		ResourceLocation small_main_location = BuiltInRegistries.BLOCK.getKey(PocketDimensionPlotsConfig.smallIslandMainBlock);
		ResourceLocation large_top_location = BuiltInRegistries.BLOCK.getKey(PocketDimensionPlotsConfig.largeIslandTopBlock);
		ResourceLocation large_main_location = BuiltInRegistries.BLOCK.getKey(PocketDimensionPlotsConfig.largeIslandMainBlock);
		ResourceLocation grass = BuiltInRegistries.BLOCK.getKey(Blocks.GRASS_BLOCK);
		ResourceLocation dirt = BuiltInRegistries.BLOCK.getKey(Blocks.DIRT);
		
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.teleport_item"), teleport_location.getNamespace() + ":" + teleport_location.getPath()).setTooltip(Component.translatable("pdp.configgui.tooltip.disable_item")).setDefaultValue(feather.getNamespace() + ":" + feather.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setTeleportItem(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.teleport_sound"), teleport_sound_location.getNamespace() + ":" + teleport_sound_location.getPath()).setTooltip(Component.translatable("pdp.configgui.teleport_sound")).setDefaultValue(enderman.getNamespace() + ":" + enderman.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setTeleportSound(newValue)).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.teleport_request_timeout"), PocketDimensionPlotsConfig.teleportRequestTimeout).setTooltip(Component.translatable("pdp.configgui.tooltip.teleport_request_timeout")).setDefaultValue(30).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportRequestTimeout = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Component.translatable("pdp.configgui.teleport_enter_message"), PocketDimensionPlotsConfig.teleportEnterMessage).setTooltip(Component.translatable("pdp.configgui.teleport_enter_message")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportEnterMessage = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Component.translatable("pdp.configgui.teleport_exit_message"), PocketDimensionPlotsConfig.teleportExitMessage).setTooltip(Component.translatable("pdp.configgui.teleport_exit_message")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportExitMessage = newValue).build());
		
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.small_island_size_x"), PocketDimensionPlotsConfig.smallIslandXSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_x")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandXSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.small_island_size_y"), PocketDimensionPlotsConfig.smallIslandYSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_y")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandYSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.small_island_size_z"), PocketDimensionPlotsConfig.smallIslandZSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_z")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandZSize = newValue).build());
		
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.small_island_top_block"), small_top_location.getNamespace() + ":" + small_top_location.getPath()).setTooltip(Component.translatable("pdp.configgui.tooltip.island_top_block")).setDefaultValue(grass.getNamespace() + ":" + grass.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setSmallTopBlock(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.small_island_main_block"), small_main_location.getNamespace() + ":" + small_main_location.getPath()).setTooltip(Component.translatable("pdp.configgui.tooltip.island_main_block")).setDefaultValue(dirt.getNamespace() + ":" + dirt.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setSmallMainBlock(newValue)).build());
		
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.large_island_size_x"), PocketDimensionPlotsConfig.largeIslandXSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_x")).setDefaultValue(15).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandXSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.large_island_size_y"), PocketDimensionPlotsConfig.largeIslandYSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_y")).setDefaultValue(30).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandYSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.large_island_size_z"), PocketDimensionPlotsConfig.largeIslandZSize).setTooltip(Component.translatable("pdp.configgui.tooltip.island_size_z")).setDefaultValue(15).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandZSize = newValue).build());
		
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.large_island_top_block"), large_top_location.getNamespace() + ":" + large_top_location.getPath()).setTooltip(Component.translatable("pdp.configgui.tooltip.island_top_block")).setDefaultValue(grass.getNamespace() + ":" + grass.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setLargeTopBlock(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Component.translatable("pdp.configgui.large_island_main_block"), large_main_location.getNamespace() + ":" + large_main_location.getPath()).setTooltip(Component.translatable("pdp.configgui.tooltip.island_main_block")).setDefaultValue(dirt.getNamespace() + ":" + dirt.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setLargeMainBlock(newValue)).build());
		
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.plot_border_radius"), PocketDimensionPlotsConfig.plotBorderRadius).setTooltip(Component.translatable("pdp.configgui.tooltip.plot_border_radius")).setDefaultValue(250).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotBorderRadius = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.plot_center_y_level"), PocketDimensionPlotsConfig.plotCenterYLevel).setTooltip(Component.translatable("pdp.configgui.tooltip.plot_center_y_level")).setDefaultValue(63).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotCenterYLevel = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Component.translatable("pdp.configgui.plot_spread_distance"), PocketDimensionPlotsConfig.plotSpreadDistance).setTooltip(Component.translatable("pdp.configgui.tooltip.plot_spread_distance")).setDefaultValue(1000).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotSpreadDistance = newValue).build());
		
		plots.addEntry(entryBuilder.startBooleanToggle(Component.translatable("pdp.configgui.allow_sleep"), PocketDimensionPlotsConfig.allowSleepingInPlots).setTooltip(Component.translatable("pdp.configgui.tooltip.allow_sleep")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.allowSleepingInPlots = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Component.translatable("pdp.configgui.allow_bed_spawn"), PocketDimensionPlotsConfig.allowBedToSetSpawn).setTooltip(Component.translatable("pdp.configgui.tooltip.allow_bed_spawn")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.allowBedToSetSpawn = newValue).build());
		
		MutableComponent serverLang = Component.translatable("pdp.configgui.server_lang");
		server.addEntry(entryBuilder.startStrField(serverLang.withStyle((style) -> { return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.fandom.com/wiki/Language")); }), PocketDimensionPlotsConfig.serverLang).setTooltip(Component.translatable("pdp.configgui.tooltip.server_lang")).setDefaultValue("en_us").setSaveConsumer(newValue->PocketDimensionPlotsConfig.serverLang = newValue).build());
		server.addEntry(entryBuilder.startBooleanToggle(Component.translatable("pdp.configgui.update_check"), PocketDimensionPlotsConfig.disableUpdateCheck).setTooltip(Component.translatable("pdp.configgui.update_check")).setDefaultValue(false).setSaveConsumer(newValue->PocketDimensionPlotsConfig.disableUpdateCheck = newValue).build());

		return builder.build();
	}
}
