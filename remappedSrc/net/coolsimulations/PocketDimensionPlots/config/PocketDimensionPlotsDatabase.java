package net.coolsimulations.PocketDimensionPlots.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PocketDimensionPlotsDatabase {
	
	static File file;
	static JsonObject object;
	
	public static List<PlotEntry> plots;
	
	public static void init(File fileSrc) {
		
		plots = new ArrayList<PlotEntry>();

		JsonObject jsonObject = setJsonObject(new JsonObject());

		if(!fileSrc.exists() || fileSrc.length() <= 2) {
			save(fileSrc, jsonObject);
		} else {
			load(fileSrc);
		}
		
		file = fileSrc;
		object = jsonObject;

	}
	
	public static void save(File fileSrc, JsonObject object) {
		try {
			FileWriter file = new FileWriter(fileSrc);
			setJsonObject(object);
			file.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void save() {
		save(file, setJsonObject(new JsonObject()));
	}
	
	public static void load(File fileSrc) {
		try {
			Object obj = JsonParser.parseReader(new FileReader(fileSrc));
			JsonObject jsonObjectRead = (JsonObject) obj;
			
			JsonArray plotsArray = jsonObjectRead.get("plots").getAsJsonArray();
			
			for (JsonElement jsonObject : plotsArray) {
				JsonArray entryArray = jsonObject.getAsJsonArray();
				int plotId = entryArray.get(0).getAsInt();
				UUID playerOwner = UUID.fromString(entryArray.get(1).getAsString());
				JsonArray coords = entryArray.get(2).getAsJsonArray();
				BlockPos centerPos = new BlockPos(coords.get(0).getAsInt(), coords.get(1).getAsInt(), coords.get(2).getAsInt());
				int borderRadius = entryArray.get(3).getAsInt();
				JsonArray safeCoords = entryArray.get(4).getAsJsonArray();
				BlockPos safePos = new BlockPos(safeCoords.get(0).getAsInt(), safeCoords.get(1).getAsInt(), safeCoords.get(2).getAsInt());
				List<UUID> whitelist = new ArrayList<UUID>();
				JsonArray whitelistArray = entryArray.get(5).getAsJsonArray();
				for (JsonElement element : whitelistArray)
					whitelist.add(UUID.fromString(element.getAsString()));
				plots.add(new PlotEntry(plotId, playerOwner, centerPos, borderRadius, safePos, whitelist));
			}
		
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static JsonObject setJsonObject(JsonObject jsonObject) {
		
		JsonArray plotsArray = new JsonArray();
		
		for (PlotEntry entry : plots) {
			JsonArray entryArray = new JsonArray();
			entryArray.add(entry.plotId);
			entryArray.add(entry.playerOwner.toString());
			JsonArray coords = new JsonArray();
			coords.add(entry.centerPos.getX());
			coords.add(entry.centerPos.getY());
			coords.add(entry.centerPos.getZ());
			entryArray.add(coords);
			entryArray.add(entry.borderRadius);
			JsonArray safeCoords = new JsonArray();
			safeCoords.add(entry.safePos.getX());
			safeCoords.add(entry.safePos.getY());
			safeCoords.add(entry.safePos.getZ());
			entryArray.add(safeCoords);
			JsonArray whitelist = new JsonArray();
			for (UUID player : entry.whitelist)
				whitelist.add(player.toString());
			entryArray.add(whitelist);
			plotsArray.add(entryArray);
		}
		
		jsonObject.add("plots", plotsArray);
		
		return jsonObject;
	}
	
	public static void addPlot(PlotEntry entry) {
		
		plots.add(entry);
		save();
	}
	
	public static File getFile() {
		return file;
	}
	
	public static JsonObject getObject() {
		return object;
	}
	
	public static class PlotEntry {
		
		public final int plotId;
		public final UUID playerOwner;
		public final BlockPos centerPos;
		public final int borderRadius;
		public BlockPos safePos;
		private List<UUID> whitelist;
		
		public PlotEntry(int plotIdIn, UUID playerOwnerIn, BlockPos centerPosIn, int borderRadiusIn, BlockPos safePosIn, List<UUID> whitelistIn) {
			this.plotId = plotIdIn;
			this.playerOwner = playerOwnerIn;
			this.centerPos = centerPosIn;
			this.borderRadius = borderRadiusIn;
			this.safePos = safePosIn;
			this.whitelist = whitelistIn;
		}
		
		public PlotEntry(int plotIdIn, UUID playerOwnerIn, BlockPos centerPosIn, int borderRadiusIn) {
			this(plotIdIn, playerOwnerIn, centerPosIn, borderRadiusIn, centerPosIn, new ArrayList<UUID>());
		}
		
		public PlotEntry(int plotIdIn, UUID playerOwnerIn, BlockPos centerPosIn, int borderRadiusIn, List<UUID> whitelistIn) {
			this(plotIdIn, playerOwnerIn, centerPosIn, borderRadiusIn, centerPosIn, whitelistIn);
		}
		
		public void setSafePos(BlockPos pos) {
			this.safePos = pos;
			PocketDimensionPlotsDatabase.save();
		}
		
		public List<UUID> getWhitelist() {
			return this.whitelist;
		}
		
		public void addPlayerToWhitelist(UUID player) {
			if (!this.whitelist.contains(player)) {
				this.whitelist.add(player);
				PocketDimensionPlotsDatabase.save();
			}
		}
		
		public void removePlayerFromWhitelist(UUID player) {
			if (this.whitelist.contains(player)) {
				this.whitelist.remove(player);
				PocketDimensionPlotsDatabase.save();
			}
		}
	}
	
}
