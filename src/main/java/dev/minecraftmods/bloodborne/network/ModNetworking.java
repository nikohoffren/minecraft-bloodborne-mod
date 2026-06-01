package dev.minecraftmods.bloodborne.network;

import dev.minecraftmods.bloodborne.HunterPistolItem;
import dev.minecraftmods.bloodborne.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ModNetworking {

	public static void register() {
		PayloadTypeRegistry.playC2S().register(FirePistolPayload.TYPE, FirePistolPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(FirePistolPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			context.server().execute(() -> {
				ItemStack offhand = player.getOffhandItem();
				if (offhand.is(ModItems.HUNTER_PISTOL)) {
					HunterPistolItem.tryFire(player, offhand, payload.yaw(), payload.pitch());
				}
			});
		});
	}

	private ModNetworking() {
	}
}
