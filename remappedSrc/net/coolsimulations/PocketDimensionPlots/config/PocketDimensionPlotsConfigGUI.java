package net.coolsimulations.PocketDimensionPlots.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.coolsimulations.PocketDimensionPlots.PDPReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PocketDimensionPlotsConfigGUI {

	public static Screen getConfigScreen(Screen parent)
	{
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("pdp.configgui.title"));

		builder.setSavingRunnable(() -> {
			PocketDimensionPlotsConfig.save(PocketDimensionPlotsConfig.getFile(), PocketDimensionPlotsConfig.getObject());
			PocketDimensionPlotsConfig.load(PocketDimensionPlotsConfig.getFile());
		});

		builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/sculk_catalyst_top.png"));

		ConfigCategory plots = builder.getOrCreateCategory(Text.translatable(PDPReference.CONFIG_CATEGORY_PLOTS));
		ConfigCategory server = builder.getOrCreateCategory(Text.translatable(PDPReference.CONFIG_CATEGORY_SERVER));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		Identifier teleport_location = Registries.ITEM.getId(PocketDimensionPlotsConfig.teleportItem);
		Identifier feather = Registries.ITEM.getId(Items.FEATHER);
		Identifier teleport_sound_location = Registries.SOUND_EVENT.getId(PocketDimensionPlotsConfig.teleportSound);
		Identifier enderman = Registries.SOUND_EVENT.getId(SoundEvents.ENTITY_ENDERMAN_TELEPORT);

		Identifier small_top_location = Registries.BLOCK.getId(PocketDimensionPlotsConfig.smallIslandTopBlock);
		Identifier small_main_location = Registries.BLOCK.getId(PocketDimensionPlotsConfig.smallIslandMainBlock);
		Identifier large_top_location = Registries.BLOCK.getId(PocketDimensionPlotsConfig.largeIslandTopBlock);
		Identifier large_main_location = Registries.BLOCK.getId(PocketDimensionPlotsConfig.largeIslandMainBlock);
		Identifier grass = Registries.BLOCK.getId(Blocks.GRASS_BLOCK);
		Identifier dirt = Registries.BLOCK.getId(Blocks.DIRT);
		
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.teleport_item"), teleport_location.getNamespace() + ":" + teleport_location.getPath()).setTooltip(Text.translatable("pdp.configgui.tooltip.disable_item")).setDefaultValue(feather.getNamespace() + ":" + feather.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setTeleportItem(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.teleport_sound"), teleport_sound_location.getNamespace() + ":" + teleport_sound_location.getPath()).setTooltip(Text.translatable("pdp.configgui.teleport_sound")).setDefaultValue(enderman.getNamespace() + ":" + enderman.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setTeleportSound(newValue)).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.teleport_request_timeout"), PocketDimensionPlotsConfig.teleportRequestTimeout).setTooltip(Text.translatable("pdp.configgui.tooltip.teleport_request_timeout")).setDefaultValue(30).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportRequestTimeout = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Text.translatable("pdp.configgui.teleport_enter_message"), PocketDimensionPlotsConfig.teleportEnterMessage).setTooltip(Text.translatable("pdp.configgui.teleport_enter_message")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportEnterMessage = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Text.translatable("pdp.configgui.teleport_exit_message"), PocketDimensionPlotsConfig.teleportExitMessage).setTooltip(Text.translatable("pdp.configgui.teleport_exit_message")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.teleportExitMessage = newValue).build());
		
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.small_island_size_x"), PocketDimensionPlotsConfig.smallIslandXSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_x")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandXSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.small_island_size_y"), PocketDimensionPlotsConfig.smallIslandYSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_y")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandYSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.small_island_size_z"), PocketDimensionPlotsConfig.smallIslandZSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_z")).setDefaultValue(5).setSaveConsumer(newValue->PocketDimensionPlotsConfig.smallIslandZSize = newValue).build());
		
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.small_island_top_block"), small_top_location.getNamespace() + ":" + small_top_location.getPath()).setTooltip(Text.translatable("pdp.configgui.tooltip.island_top_block")).setDefaultValue(grass.getNamespace() + ":" + grass.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setSmallTopBlock(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.small_island_main_block"), small_main_location.getNamespace() + ":" + small_main_location.getPath()).setTooltip(Text.translatable("pdp.configgui.tooltip.island_main_block")).setDefaultValue(dirt.getNamespace() + ":" + dirt.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setSmallMainBlock(newValue)).build());
		
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.large_island_size_x"), PocketDimensionPlotsConfig.largeIslandXSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_x")).setDefaultValue(15).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandXSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.large_island_size_y"), PocketDimensionPlotsConfig.largeIslandYSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_y")).setDefaultValue(30).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandYSize = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.large_island_size_z"), PocketDimensionPlotsConfig.largeIslandZSize).setTooltip(Text.translatable("pdp.configgui.tooltip.island_size_z")).setDefaultValue(15).setSaveConsumer(newValue->PocketDimensionPlotsConfig.largeIslandZSize = newValue).build());
		
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.large_island_top_block"), large_top_location.getNamespace() + ":" + large_top_location.getPath()).setTooltip(Text.translatable("pdp.configgui.tooltip.island_top_block")).setDefaultValue(grass.getNamespace() + ":" + grass.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setLargeTopBlock(newValue)).build());
		plots.addEntry(entryBuilder.startStrField(Text.translatable("pdp.configgui.large_island_main_block"), large_main_location.getNamespace() + ":" + large_main_location.getPath()).setTooltip(Text.translatable("pdp.configgui.tooltip.island_main_block")).setDefaultValue(dirt.getNamespace() + ":" + dirt.getPath()).setSaveConsumer(newValue->PocketDimensionPlotsConfig.setLargeMainBlock(newValue)).build());
		
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.plot_border_radius"), PocketDimensionPlotsConfig.plotBorderRadius).setTooltip(Text.translatable("pdp.configgui.tooltip.plot_border_radius")).setDefaultValue(250).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotBorderRadius = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.plot_center_y_level"), PocketDimensionPlotsConfig.plotCenterYLevel).setTooltip(Text.translatable("pdp.configgui.tooltip.plot_center_y_level")).setDefaultValue(63).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotCenterYLevel = newValue).build());
		plots.addEntry(entryBuilder.startIntField(Text.translatable("pdp.configgui.plot_spread_distance"), PocketDimensionPlotsConfig.plotSpreadDistance).setTooltip(Text.translatable("pdp.configgui.tooltip.plot_spread_distance")).setDefaultValue(1000).setSaveConsumer(newValue->PocketDimensionPlotsConfig.plotSpreadDistance = newValue).build());
		
		plots.addEntry(entryBuilder.startBooleanToggle(Text.translatable("pdp.configgui.allow_sleep"), PocketDimensionPlotsConfig.allowSleepingInPlots).setTooltip(Text.translatable("pdp.configgui.tooltip.allow_sleep")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.allowSleepingInPlots = newValue).build());
		plots.addEntry(entryBuilder.startBooleanToggle(Text.translatable("pdp.configgui.allow_bed_spawn"), PocketDimensionPlotsConfig.allowBedToSetSpawn).setTooltip(Text.translatable("pdp.configgui.tooltip.allow_bed_spawn")).setDefaultValue(true).setSaveConsumer(newValue->PocketDimensionPlotsConfig.allowBedToSetSpawn = newValue).build());
		
		MutableText serverLang = Text.translatable("pdp.configgui.server_lang");
		server.addEntry(entryBuilder.startStrField(serverLang.styled((style) -> { return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.fandom.com/wiki/Language")); }), PocketDimensionPlotsConfig.serverLang).setTooltip(Text.translatable("pdp.configgui.tooltip.server_lang")).setDefaultValue("en_us").setSaveConsumer(newValue->PocketDimensionPlotsConfig.serverLang = newValue).build());
		server.addEntry(entryBuilder.startBooleanToggle(Text.translatable("pdp.configgui.update_check"), PocketDimensionPlotsConfig.disableUpdateCheck).setTooltip(Text.translatable("pdp.configgui.update_check")).setDefaultValue(false).setSaveConsumer(newValue->PocketDimensionPlotsConfig.disableUpdateCheck = newValue).build());

		return builder.build();
	}
}
