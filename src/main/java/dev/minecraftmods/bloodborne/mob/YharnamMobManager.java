package dev.minecraftmods.bloodborne.mob;

import dev.minecraftmods.bloodborne.echo.EchoRewardConfig;
import dev.minecraftmods.bloodborne.entity.BloodborneEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class YharnamMobManager {

	private int spawnCheckCooldown;

	private YharnamMobManager() {
	}

	public static void register() {
		YharnamMobManager manager = new YharnamMobManager();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel overworld = server.getLevel(Level.OVERWORLD);
			if (overworld == null) {
				return;
			}

			manager.tick(overworld);
		});
	}

	private void tick(ServerLevel level) {
		if (spawnCheckCooldown > 0) {
			spawnCheckCooldown--;
			return;
		}

		spawnCheckCooldown = YharnamMobConfig.SPAWN_CHECK_INTERVAL_TICKS;

		if (!level.hasChunksAt(
				BlockPos.containing(YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS.minX, 0, YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS.minZ),
				BlockPos.containing(YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS.maxX, 0, YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS.maxZ)
		)) {
			return;
		}

		int current = countTaggedSkeletons(level);
		int toSpawn = YharnamMobConfig.MAX_SKELETONS - current;

		for (int i = 0; i < toSpawn; i++) {
			trySpawnSkeleton(level);
		}
	}

	private static int countTaggedSkeletons(ServerLevel level) {
		return level.getEntities(
				EntityType.SKELETON,
				YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS,
				entity -> entity.isAlive() && BloodborneEntities.isBloodborneEnemy(entity)
		).size();
	}

	private static void trySpawnSkeleton(ServerLevel level) {
		RandomSource random = level.random;
		AABB bounds = YharnamMobConfig.CENTRAL_YHARNAM_BOUNDS;

		for (int attempt = 0; attempt < 12; attempt++) {
			double x = random.nextDouble() * bounds.getXsize() + bounds.minX;
			double z = random.nextDouble() * bounds.getZsize() + bounds.minZ;
			int y = (int) bounds.maxY;

			BlockPos surface = findSurface(level, BlockPos.containing(x, y, z), (int) bounds.minY);
			if (surface == null) {
				continue;
			}

			Skeleton skeleton = EntityType.SKELETON.create(level);
			if (skeleton == null) {
				return;
			}

			Vec3 spawnPos = Vec3.atBottomCenterOf(surface.above());
			skeleton.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, random.nextFloat() * 360.0F, 0.0F);
			skeleton.finalizeSpawn(level, level.getCurrentDifficultyAt(surface), MobSpawnType.EVENT, null);
			tagEnemy(skeleton);
			skeleton.setPersistenceRequired();

			if (level.noCollision(skeleton) && level.addFreshEntity(skeleton)) {
				return;
			}

			skeleton.discard();
		}
	}

	private static BlockPos findSurface(ServerLevel level, BlockPos start, int minY) {
		BlockPos.MutableBlockPos pos = start.mutable();

		for (int y = start.getY(); y >= minY; y--) {
			pos.setY(y);
			BlockState state = level.getBlockState(pos);
			BlockState above = level.getBlockState(pos.above());
			BlockState above2 = level.getBlockState(pos.above(2));

			if (state.isFaceSturdy(level, pos, Direction.UP)
					&& above.isAir()
					&& above2.isAir()
					&& !state.is(net.minecraft.world.level.block.Blocks.NETHER_PORTAL)) {
				return pos.immutable();
			}
		}

		return null;
	}

	public static void tagEnemy(LivingEntity entity) {
		entity.addTag(BloodborneEntities.MOB_TAG);
		entity.addTag(EchoRewardConfig.echoRewardTag(EchoRewardConfig.SKELETON_REWARD_ID));
		entity.setCustomName(Component.literal(YharnamMobConfig.SKELETON_DISPLAY_NAME));
		entity.setCustomNameVisible(false);
	}
}
