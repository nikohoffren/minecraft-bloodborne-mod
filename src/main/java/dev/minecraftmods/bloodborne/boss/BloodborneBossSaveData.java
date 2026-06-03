package dev.minecraftmods.bloodborne.boss;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class BloodborneBossSaveData extends SavedData {

	private static final String DATA_ID = "bloodborne_boss_state";

	private static final SavedData.Factory<BloodborneBossSaveData> FACTORY = new SavedData.Factory<>(
			BloodborneBossSaveData::new,
			BloodborneBossSaveData::load,
			null
	);

	private boolean clericBeastDefeated;

	public BloodborneBossSaveData() {
	}

	private static BloodborneBossSaveData load(CompoundTag tag, HolderLookup.Provider provider) {
		BloodborneBossSaveData data = new BloodborneBossSaveData();
		data.clericBeastDefeated = tag.getBoolean("ClericBeastDefeated");
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		tag.putBoolean("ClericBeastDefeated", clericBeastDefeated);
		return tag;
	}

	public static BloodborneBossSaveData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, DATA_ID);
	}

	public boolean isClericBeastDefeated() {
		return clericBeastDefeated;
	}

	public void setClericBeastDefeated(boolean defeated) {
		this.clericBeastDefeated = defeated;
		setDirty();
	}
}
