package dev.minecraftmods.bloodborne.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class HealthHudClient {

	private static final int BAR_WIDTH = 182;
	private static final int BAR_HEIGHT = 5;

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

	public static void renderBossHealthBar(GuiGraphics graphics) {
		Minecraft client = Minecraft.getInstance();
		Player player = client.player;

		if (!shouldReplaceHearts(player)) {
			return;
		}

		float maxHealth = player.getMaxHealth();
		if (maxHealth <= 0.0F) {
			return;
		}

		float health = player.getHealth();
		float absorption = player.getAbsorptionAmount();
		float displayed = Math.min(health + absorption, maxHealth);
		float progress = displayed / maxHealth;

		int screenWidth = graphics.guiWidth();
		int x = (screenWidth - BAR_WIDTH) / 2;
		int y = 12;

		graphics.blitSprite(BOSS_BAR_BACKGROUND, x, y, BAR_WIDTH, BAR_HEIGHT);

		int progressWidth = (int) (BAR_WIDTH * progress);
		if (progressWidth > 0) {
			graphics.blitSprite(
					BOSS_BAR_PROGRESS,
					BAR_WIDTH,
					BAR_HEIGHT,
					0,
					0,
					x,
					y,
					progressWidth,
					BAR_HEIGHT
			);
		}
	}
}
