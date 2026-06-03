package dev.minecraftmods.bloodborne.boss;

import dev.minecraftmods.bloodborne.BloodborneMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public final class ClericBeastBossManager {

	private static final int SPAWN_CHECK_INTERVAL_TICKS = 40;

	private final ServerBossEvent bossEvent = new ServerBossEvent(
			Component.literal(ClericBeastBossConfig.BOSS_DISPLAY_NAME),
			BossEvent.BossBarColor.RED,
			BossEvent.BossBarOverlay.NOTCHED_20
	);

	private UUID trackedWardenId;
	private int spawnCheckCooldown;

	private ClericBeastBossManager() {
	}

	public static void register() {
		ClericBeastBossManager manager = new ClericBeastBossManager();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel overworld = server.getLevel(Level.OVERWORLD);
			if (overworld == null) {
				return;
			}

			manager.tick(overworld);
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof Warden warden)) {
				return;
			}

			if (!warden.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
				return;
			}

			if (!(entity.level() instanceof ServerLevel level)) {
				return;
			}

			manager.onBossDefeated(level);
		});

	}

	private void tick(ServerLevel level) {
		BloodborneBossSaveData save = BloodborneBossSaveData.get(level);

		if (save.isClericBeastDefeated()) {
			bossEvent.removeAllPlayers();
			bossEvent.setVisible(false);
			trackedWardenId = null;
			return;
		}

		Warden warden = findTrackedOrNearbyWarden(level);

		if (warden == null) {
			bossEvent.removeAllPlayers();
			bossEvent.setVisible(false);
			trackedWardenId = null;
			trySpawn(level, save);
			return;
		}

		trackedWardenId = warden.getUUID();
		constrainToArena(warden);
		updateBossBar(level, warden);
	}

	private void trySpawn(ServerLevel level, BloodborneBossSaveData save) {
		if (save.isClericBeastDefeated()) {
			return;
		}

		if (spawnCheckCooldown > 0) {
			spawnCheckCooldown--;
			return;
		}

		spawnCheckCooldown = SPAWN_CHECK_INTERVAL_TICKS;

		BlockPos spawn = ClericBeastBossConfig.SPAWN_BLOCK_POS;
		if (!level.hasChunkAt(spawn)) {
			return;
		}

		Warden warden = EntityType.WARDEN.create(level);
		if (warden == null) {
			return;
		}

		Vec3 pos = ClericBeastBossConfig.SPAWN_POS;
		warden.moveTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);
		warden.addTag(ClericBeastBossConfig.ENTITY_TAG);
		warden.setCustomName(Component.literal(ClericBeastBossConfig.BOSS_DISPLAY_NAME));
		warden.setCustomNameVisible(true);
		warden.setPersistenceRequired();

		level.addFreshEntity(warden);
		trackedWardenId = warden.getUUID();

		BloodborneMod.LOGGER.info("Spawned Cleric Beast (Warden) at {}", spawn);
	}

	private Warden findTrackedOrNearbyWarden(ServerLevel level) {
		if (trackedWardenId != null) {
			Entity entity = level.getEntity(trackedWardenId);
			if (entity instanceof Warden warden && warden.isAlive()) {
				return warden;
			}
		}

		AABB search = ClericBeastBossConfig.arenaBounds().inflate(8.0D);
		List<Warden> wardens = level.getEntities(EntityType.WARDEN, search, Entity::isAlive);

		for (Warden candidate : wardens) {
			if (candidate.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
				return candidate;
			}
		}

		return null;
	}

	private void constrainToArena(Warden warden) {
		Vec3 pos = warden.position();

		if (ClericBeastBossConfig.isInsideArena(pos)) {
			return;
		}

		Vec3 center = ClericBeastBossConfig.SPAWN_POS;
		warden.teleportTo(center.x, center.y, center.z);
		warden.setDeltaMovement(Vec3.ZERO);
	}

	private void updateBossBar(ServerLevel level, Warden warden) {
		float progress = warden.getHealth() / warden.getMaxHealth();
		bossEvent.setProgress(Math.max(0.0F, Math.min(1.0F, progress)));
		bossEvent.setVisible(true);

		for (ServerPlayer player : level.players()) {
			if (ClericBeastBossConfig.isInsideArena(player.position())) {
				bossEvent.addPlayer(player);
			} else {
				bossEvent.removePlayer(player);
			}
		}
	}

	private void onBossDefeated(ServerLevel level) {
		BloodborneBossSaveData save = BloodborneBossSaveData.get(level);
		save.setClericBeastDefeated(true);
		trackedWardenId = null;
		bossEvent.setProgress(0.0F);
		bossEvent.removeAllPlayers();
		bossEvent.setVisible(false);

		BloodborneMod.LOGGER.info("Cleric Beast defeated in Central Yharnam");
	}

	/** For commands / debugging — allows the boss to spawn again. */
	public static void resetClericBeast(ServerLevel level) {
		BloodborneBossSaveData save = BloodborneBossSaveData.get(level);
		save.setClericBeastDefeated(false);

		AABB search = ClericBeastBossConfig.arenaBounds().inflate(16.0D);
		for (Warden warden : level.getEntities(EntityType.WARDEN, search, e -> true)) {
			if (warden.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
				warden.discard();
			}
		}
	}
}
