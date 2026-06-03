package dev.minecraftmods.bloodborne;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Set;

public final class ModMobSpawns {

	/**
	 * Vanilla hostiles we do not want in Bloodborne worlds yet. Easy to extend or trim.
	 */
	private static final Set<EntityType<?>> DISABLED = Set.of(
			EntityType.SLIME,
			EntityType.ZOMBIE,
			EntityType.ZOMBIE_VILLAGER,
			EntityType.HUSK,
			EntityType.DROWNED,
			EntityType.SKELETON,
			EntityType.STRAY,
			EntityType.BOGGED,
			EntityType.CREEPER,
			EntityType.SPIDER,
			EntityType.CAVE_SPIDER,
			EntityType.ENDERMAN,
			EntityType.WITCH,
			EntityType.PHANTOM,
			EntityType.SILVERFISH,
			EntityType.PILLAGER,
			EntityType.VINDICATOR,
			EntityType.EVOKER,
			EntityType.VEX,
			EntityType.RAVAGER
	);

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (shouldRemove(entity)) {
				entity.discard();
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				server.execute(() -> removeDisabledMobs(server))
		);
	}

	public static boolean isDisabled(EntityType<?> type) {
		return DISABLED.contains(type);
	}

	private static boolean shouldRemove(Entity entity) {
		return isDisabled(entity.getType());
	}

	private static void removeDisabledMobs(MinecraftServer server) {
		for (ServerLevel level : server.getAllLevels()) {
			for (EntityType<?> type : DISABLED) {
				for (Entity entity : level.getEntities(type, entity -> true)) {
					entity.discard();
				}
			}
		}
	}

	private ModMobSpawns() {
	}
}
