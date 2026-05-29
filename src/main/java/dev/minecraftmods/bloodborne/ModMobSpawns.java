package dev.minecraftmods.bloodborne;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;

public final class ModMobSpawns {

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity.getType() == EntityType.SLIME) {
				entity.discard();
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				server.execute(() -> removeAllSlimes(server))
		);
	}

	private static void removeAllSlimes(MinecraftServer server) {
		for (ServerLevel level : server.getAllLevels()) {
			for (Slime slime : level.getEntities(EntityType.SLIME, slime -> true)) {
				slime.discard();
			}
		}
	}

	private ModMobSpawns() {
	}
}
