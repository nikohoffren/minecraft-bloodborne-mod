package dev.minecraftmods.bloodborne.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Central Yharnam — Cleric Beast arena (Warden placeholder).
 * Fog wall at the arena entrance uses nether portal blocks (see FogWallConfig).
 */
public final class ClericBeastBossConfig {

	public static final String ENTITY_TAG = "bloodborne.cleric_beast";

	/** Spawn block position in Central Yharnam (overworld). */
	public static final BlockPos SPAWN_BLOCK_POS = new BlockPos(-91, -60, -15);

	public static final Vec3 SPAWN_POS = Vec3.atCenterOf(SPAWN_BLOCK_POS);

	/** Horizontal boss arena radius (blocks from spawn center). */
	public static final double ARENA_HORIZONTAL_RADIUS = 28.0D;

	/** Vertical half-height of the arena (blocks above/below spawn Y). */
	public static final double ARENA_VERTICAL_HALF = 14.0D;

	public static final String BOSS_DISPLAY_NAME = "Cleric Beast";

	private ClericBeastBossConfig() {
	}

	public static AABB arenaBounds() {
		return new AABB(
				SPAWN_POS.x - ARENA_HORIZONTAL_RADIUS,
				SPAWN_POS.y - ARENA_VERTICAL_HALF,
				SPAWN_POS.z - ARENA_HORIZONTAL_RADIUS,
				SPAWN_POS.x + ARENA_HORIZONTAL_RADIUS,
				SPAWN_POS.y + ARENA_VERTICAL_HALF,
				SPAWN_POS.z + ARENA_HORIZONTAL_RADIUS
		);
	}

	public static boolean isInsideArena(Vec3 position) {
		double dx = position.x - SPAWN_POS.x;
		double dz = position.z - SPAWN_POS.z;
		double horizontalSq = dx * dx + dz * dz;

		if (horizontalSq > ARENA_HORIZONTAL_RADIUS * ARENA_HORIZONTAL_RADIUS) {
			return false;
		}

		return Math.abs(position.y - SPAWN_POS.y) <= ARENA_VERTICAL_HALF;
	}

	public static boolean isInsideArena(BlockPos blockPos) {
		return isInsideArena(Vec3.atCenterOf(blockPos));
	}
}
