package dev.minecraftmods.bloodborne.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.minecraftmods.bloodborne.network.DodgePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class QuickstepClient {

	private static KeyMapping quickstepKey;

	public static void register() {
		quickstepKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.bloodborne.quickstep",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"key.categories.bloodborne"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.screen != null) {
				return;
			}

			while (quickstepKey.consumeClick()) {
				tryRequestQuickstep(client);
			}
		});
	}

	private static void tryRequestQuickstep(Minecraft client) {
		if (!ClientPlayNetworking.canSend(DodgePayload.TYPE)) {
			return;
		}

		float forward = 0.0F;
		float strafe = 0.0F;

		if (client.options.keyUp.isDown()) {
			forward += 1.0F;
		}
		if (client.options.keyDown.isDown()) {
			forward -= 1.0F;
		}
		if (client.options.keyLeft.isDown()) {
			strafe += 1.0F;
		}
		if (client.options.keyRight.isDown()) {
			strafe -= 1.0F;
		}

		ClientPlayNetworking.send(new DodgePayload(
				client.player.getYRot(),
				forward,
				strafe
		));
	}

	private QuickstepClient() {
	}
}
