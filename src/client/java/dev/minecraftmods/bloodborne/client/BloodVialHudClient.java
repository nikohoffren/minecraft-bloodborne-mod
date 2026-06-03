package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class BloodVialHudClient {

	private static final int OFFHAND_SLOT_X_OFFSET = 29;

	private BloodVialHudClient() {
	}

	public static int countBloodVials(Player player) {
		int count = 0;

		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ModItems.BLOOD_VIAL)) {
				count += stack.getCount();
			}
		}

		ItemStack offhand = player.getOffhandItem();
		if (offhand.is(ModItems.BLOOD_VIAL)) {
			count += offhand.getCount();
		}

		return count;
	}

	/** Boxed stack count above offhand when a blood vial is held there (same layout as pistol ammo). */
	public static void renderOffhandHud(GuiGraphics graphics) {
		Minecraft client = Minecraft.getInstance();
		Player player = client.player;

		if (player == null || !HealthHudClient.shouldUseBloodborneHud(player)) {
			return;
		}

		ItemStack offhand = player.getOffhandItem();
		if (!offhand.is(ModItems.BLOOD_VIAL)) {
			return;
		}

		int count = offhand.getCount();
		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();

		int offhandSlotX = screenWidth / 2 - 91 - OFFHAND_SLOT_X_OFFSET;
		int offhandSlotY = screenHeight - 16;
		int centerX = offhandSlotX + 8;
		// Panel above offhand slot: icon + count (vanilla still draws the item in the slot below)
		int panelY = offhandSlotY - 36;

		HudLabelRenderer.drawBoxedItemWithCountCentered(graphics, centerX, panelY, offhand, count, 0xFFCC4444);
	}
}
