package net.coolsimulations.PocketDimensionPlots;

import net.minecraft.nbt.CompoundTag;

public interface EntityAccessor {
	
	CompoundTag getPersistentData();
	
	void setPersistentData(CompoundTag tag);

}
