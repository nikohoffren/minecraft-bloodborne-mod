package dev.minecraftmods.bloodborne.network;

import dev.minecraftmods.bloodborne.HunterPistolItem;
import dev.minecraftmods.bloodborne.ModItems;
import dev.minecraftmods.bloodborne.stamina.QuickstepHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ModNetworking {

	public static void register() {
		PayloadTypeRegistry.playC2S().register(FirePistolPayload.TYPE, FirePistolPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DodgePayload.TYPE, DodgePayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DodgePayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			context.server().execute(() ->
					QuickstepHandler.tryQuickstep(player, payload.yaw(), payload.forward(), payload.strafe())
			);
		});

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
