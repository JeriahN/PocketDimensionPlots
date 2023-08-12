package net.coolsimulations.PocketDimensionPlots;

import net.minecraft.nbt.NbtCompound;

public interface EntityAccessor {
	
	NbtCompound getPersistentData();
	
	void setPersistentData(NbtCompound tag);

}
