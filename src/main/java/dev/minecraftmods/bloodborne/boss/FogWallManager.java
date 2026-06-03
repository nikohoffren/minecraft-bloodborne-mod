package dev.minecraftmods.bloodborne.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public final class FogWallManager {

	private static final Map<BlockPos, BlockState> SAVED_BLOCKS = new HashMap<>();
	private static boolean wallActive;

	private FogWallManager() {
	}

	public static boolean isWallActive() {
		return wallActive;
	}

	public static boolean isFogWallBlock(BlockPos pos) {
		if (!wallActive) {
			return false;
		}

		return pos.getX() >= FogWallConfig.MIN_X
				&& pos.getX() <= FogWallConfig.MAX_X
				&& pos.getY() >= FogWallConfig.MIN_Y
				&& pos.getY() <= FogWallConfig.MAX_Y
				&& pos.getZ() == FogWallConfig.PLANE_Z;
	}

	public static void activate(ServerLevel level) {
		if (wallActive) {
			return;
		}

		if (!level.hasChunkAt(new BlockPos(FogWallConfig.MIN_X, FogWallConfig.MIN_Y, FogWallConfig.PLANE_Z))) {
			return;
		}

		SAVED_BLOCKS.clear();

		for (int x = FogWallConfig.MIN_X; x <= FogWallConfig.MAX_X; x++) {
			for (int y = FogWallConfig.MIN_Y; y <= FogWallConfig.MAX_Y; y++) {
				BlockPos pos = new BlockPos(x, y, FogWallConfig.PLANE_Z);
				BlockState existing = level.getBlockState(pos);

				if (!existing.isAir() && !existing.is(FogWallConfig.PORTAL_STATE.getBlock())) {
					SAVED_BLOCKS.put(pos.immutable(), existing);
				}

				level.setBlock(pos, FogWallConfig.PORTAL_STATE, 3);
			}
		}

		wallActive = true;
	}

	public static void deactivate(ServerLevel level) {
		if (!wallActive) {
			return;
		}

		for (Map.Entry<BlockPos, BlockState> entry : SAVED_BLOCKS.entrySet()) {
			level.setBlock(entry.getKey(), entry.getValue(), 3);
		}

		for (int x = FogWallConfig.MIN_X; x <= FogWallConfig.MAX_X; x++) {
			for (int y = FogWallConfig.MIN_Y; y <= FogWallConfig.MAX_Y; y++) {
				BlockPos pos = new BlockPos(x, y, FogWallConfig.PLANE_Z);

				if (!SAVED_BLOCKS.containsKey(pos)) {
					BlockState current = level.getBlockState(pos);

					if (current.is(FogWallConfig.PORTAL_STATE.getBlock())) {
						level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		}

		SAVED_BLOCKS.clear();
		wallActive = false;
	}

	public static void enforceBlocking(ServerLevel level) {
		if (!wallActive) {
			return;
		}

		double planeZ = FogWallConfig.PLANE_Z;
		/** Push far enough that the player bbox no longer crosses the portal plane. */
		double pushMargin = 1.25D;

		for (ServerPlayer player : level.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}

			AABB playerBox = player.getBoundingBox();
			boolean onArenaSide = player.position().z < planeZ;
			boolean crossingWall;

			if (onArenaSide) {
				crossingWall = playerBox.maxZ > planeZ;
			} else {
				crossingWall = playerBox.minZ < planeZ;
			}

			if (!crossingWall) {
				continue;
			}

			Vec3 pos = player.position();
			double pushZ = onArenaSide ? planeZ - pushMargin : planeZ + pushMargin;

			player.teleportTo(pos.x, pos.y, pushZ);
			Vec3 motion = player.getDeltaMovement();
			player.setDeltaMovement(motion.x, motion.y, 0.0D);
			player.hurtMarked = true;
			player.setPortalCooldown();
		}
	}
}
