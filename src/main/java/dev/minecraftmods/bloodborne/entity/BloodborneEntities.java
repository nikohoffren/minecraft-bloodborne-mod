package dev.minecraftmods.bloodborne.entity;

import net.minecraft.world.entity.LivingEntity;

public final class BloodborneEntities {

	/** Marks mod-controlled enemies (spawned by YharnamMobManager, not vanilla). */
	public static final String MOB_TAG = "bloodborne.enemy";

	/** Echo payout id — used when entity type alone is not enough. */
	public static final String ECHO_REWARD_ID_TAG = "bloodborne.echo_reward";

	private BloodborneEntities() {
	}

	public static boolean isBloodborneEnemy(LivingEntity entity) {
		return entity.getTags().contains(MOB_TAG);
	}
}
