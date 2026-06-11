package dev.minecraftmods.bloodborne.echo;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import dev.minecraftmods.bloodborne.entity.BloodborneEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public final class EchoRewardConfig {

	public static final String SKELETON_REWARD_ID = "skeleton";

	private static final Map<String, Integer> REWARD_BY_ID = Map.of(
			SKELETON_REWARD_ID, 120
	);

	private static final Map<EntityType<?>, Integer> REWARD_BY_TYPE = Map.of(
			EntityType.SKELETON, 120,
			EntityType.STRAY, 140,
			EntityType.BOGGED, 130
	);

	private EchoRewardConfig() {
	}

	public static int getReward(LivingEntity entity) {
		if (entity.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
			return 8_000;
		}

		for (String tag : entity.getTags()) {
			if (tag.startsWith(BloodborneEntities.ECHO_REWARD_ID_TAG + ":")) {
				String id = tag.substring((BloodborneEntities.ECHO_REWARD_ID_TAG + ":").length());
				Integer reward = REWARD_BY_ID.get(id);
				if (reward != null) {
					return reward;
				}
			}
		}

		Integer byType = REWARD_BY_TYPE.get(entity.getType());
		return byType != null ? byType : 0;
	}

	public static String echoRewardTag(String rewardId) {
		return BloodborneEntities.ECHO_REWARD_ID_TAG + ":" + rewardId;
	}
}
