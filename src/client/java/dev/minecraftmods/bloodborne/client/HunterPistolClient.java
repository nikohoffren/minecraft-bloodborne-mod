package dev.minecraftmods.bloodborne.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.minecraftmods.bloodborne.HunterPistolItem;
import dev.minecraftmods.bloodborne.ModItems;
import dev.minecraftmods.bloodborne.network.FirePistolPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public final class HunterPistolClient {

	private static final int OFFHAND_SLOT_X_OFFSET = 29;

	private static KeyMapping firePistolKey;

	public static void register() {
		firePistolKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.bloodborne.fire_pistol",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"key.categories.bloodborne"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				return;
			}

			while (firePistolKey.consumeClick()) {
				tryRequestFire(client);
			}
		});
	}

	private static void tryRequestFire(Minecraft client) {
		ItemStack offhand = client.player.getOffhandItem();
		if (!offhand.is(ModItems.HUNTER_PISTOL)) {
			return;
		}

		if (ClientPlayNetworking.canSend(FirePistolPayload.TYPE)) {
			ClientPlayNetworking.send(new FirePistolPayload());
		}
	}

	/** Called after the hotbar is drawn so the label stays on top. */
	public static void renderAmmoHud(GuiGraphics context) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}

		ItemStack offhand = client.player.getOffhandItem();
		if (!offhand.is(ModItems.HUNTER_PISTOL)) {
			return;
		}

		int ammo = HunterPistolItem.getAmmo(offhand);
		Component label = Component.translatable("hud.bloodborne.pistol_ammo", ammo);
		int labelWidth = client.font.width(label);

		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();

		// Vanilla offhand slot: left of hotbar, 16x16 at y = screenHeight - 16
		int offhandSlotX = screenWidth / 2 - 91 - OFFHAND_SLOT_X_OFFSET;
		int offhandSlotY = screenHeight - 16;

		// Draw above the offhand icon, centered on the slot
		int textX = offhandSlotX + 8 - labelWidth / 2;
		int textY = offhandSlotY - 20;

		int padding = 2;
		context.fill(
				textX - padding,
				textY - padding,
				textX + labelWidth + padding,
				textY + client.font.lineHeight + padding,
				0xC0000000
		);
		context.drawString(client.font, label, textX, textY, 0xFFFFFF, false);
	}

	private HunterPistolClient() {
	}
}
