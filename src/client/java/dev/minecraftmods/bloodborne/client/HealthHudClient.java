package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class HealthHudClient {

	private static final int BAR_X = 10;
	private static final int BAR_Y = 10;

	/** Smaller than vanilla boss bar (182×5) for a Bloodborne-style readout. */
	private static final int BAR_WIDTH = 110;
	private static final int BAR_HEIGHT = 4;

	private static final int VIAL_LABEL_GAP = 5;

	private static final ResourceLocation BOSS_BAR_BACKGROUND =
			ResourceLocation.withDefaultNamespace("boss_bar/white_background");
	private static final ResourceLocation BOSS_BAR_PROGRESS =
			ResourceLocation.withDefaultNamespace("boss_bar/red_progress");

	private HealthHudClient() {
	}

	public static boolean shouldReplaceHearts(Player player) {
		if (player == null) {
			return false;
		}

		return !player.isSpectator();
	}

	public static void renderHud(GuiGraphics graphics) {
		Minecraft client = Minecraft.getInstance();
		Player player = client.player;

		if (!shouldReplaceHearts(player)) {
			return;
		}

		renderHealthBar(graphics, player);
		renderInventoryVialCount(graphics, player);
	}

	private static void renderHealthBar(GuiGraphics graphics, Player player) {
		float maxHealth = player.getMaxHealth();
		if (maxHealth <= 0.0F) {
			return;
		}

		float health = player.getHealth();
		float absorption = player.getAbsorptionAmount();
		float displayed = Math.min(health + absorption, maxHealth);
		float progress = displayed / maxHealth;

		graphics.blitSprite(BOSS_BAR_BACKGROUND, BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT);

		int progressWidth = (int) (BAR_WIDTH * progress);
		if (progressWidth > 0) {
			graphics.blitSprite(
					BOSS_BAR_PROGRESS,
					BAR_WIDTH,
					BAR_HEIGHT,
					0,
					0,
					BAR_X,
					BAR_Y,
					progressWidth,
					BAR_HEIGHT
			);
		}
	}

	/** Total vials in inventory, shown under the health bar (top-left cluster). */
	private static void renderInventoryVialCount(GuiGraphics graphics, Player player) {
		int count = BloodVialHudClient.countBloodVials(player);
		if (count <= 0) {
			return;
		}

		Component label = Component.translatable("hud.bloodborne.blood_vials", count);
		int panelY = BAR_Y + BAR_HEIGHT + VIAL_LABEL_GAP;
		ItemStack iconStack = new ItemStack(ModItems.BLOOD_VIAL);

		HudLabelRenderer.drawBoxedItemWithLabel(graphics, BAR_X, panelY, iconStack, label, 0xFFCC4444);
	}
}
