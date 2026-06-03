package dev.minecraftmods.bloodborne.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/**
 * Cleric Beast doorway fog wall (nether portal sheet).
 * Corners: (-91, -60, 10) and (-86, -56, 10).
 */
public final class FogWallConfig {

	public static final BlockPos CORNER_ONE = new BlockPos(-91, -60, 10);
	public static final BlockPos CORNER_TWO = new BlockPos(-86, -56, 10);

	public static final int MIN_X = Math.min(CORNER_ONE.getX(), CORNER_TWO.getX());
	public static final int MAX_X = Math.max(CORNER_ONE.getX(), CORNER_TWO.getX());
	public static final int MIN_Y = Math.min(CORNER_ONE.getY(), CORNER_TWO.getY());
	public static final int MAX_Y = Math.max(CORNER_ONE.getY(), CORNER_TWO.getY());
	public static final int PLANE_Z = CORNER_ONE.getZ();

	/** Arena is on the lower-Z side of the wall (boss at z = -15). */
	public static final double ARENA_SIDE_Z = PLANE_Z - 0.5D;

	/**
	 * East–west portal (enter from north/south along Z). {@code Axis.Z} looks sideways for this doorway.
	 */
	public static final BlockState PORTAL_STATE = Blocks.NETHER_PORTAL.defaultBlockState()
			.setValue(NetherPortalBlock.AXIS, Direction.Axis.X);

	private FogWallConfig() {
	}

	public static AABB collisionBounds() {
		return new AABB(
				MIN_X,
				MIN_Y,
				PLANE_Z - 0.45D,
				MAX_X + 1.0D,
				MAX_Y + 1.0D,
				PLANE_Z + 0.45D
		);
	}
}
