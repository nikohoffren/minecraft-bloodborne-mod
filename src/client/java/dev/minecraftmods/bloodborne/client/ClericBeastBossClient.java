package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;

public final class ClericBeastBossClient {

	private static boolean bossMusicActive;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(ClericBeastBossClient::clientTick);
	}

	private static void clientTick(Minecraft client) {
		if (client.player == null || client.level == null) {
			return;
		}

		boolean shouldPlay = client.player.isAlive()
				&& ClericBeastBossConfig.isInsideArena(client.player.position())
				&& isClericBeastAliveNearby(client);

		if (shouldPlay && !bossMusicActive) {
			BloodborneMusicClient.startBossMusic(client);
			bossMusicActive = true;
		} else if (!shouldPlay && bossMusicActive) {
			BloodborneMusicClient.stopBossMusic(client);
			bossMusicActive = false;
		}
	}

	private static boolean isClericBeastAliveNearby(Minecraft client) {
		AABB search = ClericBeastBossConfig.arenaBounds();
		for (Entity entity : client.level.getEntities(client.player, search)) {
			if (entity instanceof Warden warden
					&& warden.isAlive()
					&& warden.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
				return true;
			}
		}

		return false;
	}

	private ClericBeastBossClient() {
	}
}
