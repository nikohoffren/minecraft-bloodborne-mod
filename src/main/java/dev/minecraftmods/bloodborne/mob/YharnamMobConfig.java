package dev.minecraftmods.bloodborne.mob;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import net.minecraft.world.phys.AABB;

/**
 * Central Yharnam enemy spawns (overworld).
 */
public final class YharnamMobConfig {

	public static final String SKELETON_DISPLAY_NAME = "Scourge Hunter";

	/** Street area around the Cleric Beast arena. */
	public static final AABB CENTRAL_YHARNAM_BOUNDS = ClericBeastBossConfig.arenaBounds()
			.inflate(22.0D, 6.0D, 30.0D);

	public static final int MAX_SKELETONS = 6;
	public static final int SPAWN_CHECK_INTERVAL_TICKS = 100;

	private YharnamMobConfig() {
	}
}
