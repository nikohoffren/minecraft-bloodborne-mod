package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.ModItems;
import dev.minecraftmods.bloodborne.stamina.StaminaHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class HealthHudClient {

	private static final int BAR_X = 10;
	private static final int BAR_Y = 10;
	private static final int BAR_GAP = 2;

	/** Smaller than vanilla boss bar (182×5) for a Bloodborne-style readout. */
	private static final int BAR_WIDTH = 110;
	private static final int BAR_HEIGHT = 4;

	private static final int VIAL_LABEL_GAP = 5;

	private static final ResourceLocation BOSS_BAR_BACKGROUND =
			ResourceLocation.withDefaultNamespace("boss_bar/white_background");
	private static final ResourceLocation BOSS_BAR_HEALTH =
			ResourceLocation.withDefaultNamespace("boss_bar/red_progress");
	private static final ResourceLocation BOSS_BAR_STAMINA =
			ResourceLocation.withDefaultNamespace("boss_bar/green_progress");

	private HealthHudClient() {
	}

	/** Survival, adventure, and creative — only spectator uses vanilla HUD. */
	public static boolean shouldUseBloodborneHud(Player player) {
		if (player == null) {
			return false;
		}

		return !player.isSpectator();
	}

	public static void renderHud(GuiGraphics graphics) {
		Minecraft client = Minecraft.getInstance();
		Player player = client.player;

		if (!shouldUseBloodborneHud(player)) {
			return;
		}

		renderHealthBar(graphics, player);
		renderStaminaBar(graphics, player);
		renderInventoryVialCount(graphics, player);
	}

	private static int staminaBarY() {
		return BAR_Y + BAR_HEIGHT + BAR_GAP;
	}

	private static void renderBar(
			GuiGraphics graphics,
			int x,
			int y,
			float progress,
			ResourceLocation progressSprite
	) {
		graphics.blitSprite(BOSS_BAR_BACKGROUND, x, y, BAR_WIDTH, BAR_HEIGHT);

		int progressWidth = (int) (BAR_WIDTH * progress);
		if (progressWidth > 0) {
			graphics.blitSprite(
					progressSprite,
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

	private static void renderHealthBar(GuiGraphics graphics, Player player) {
		float maxHealth = player.getMaxHealth();
		if (maxHealth <= 0.0F) {
			return;
		}

		float health = player.getHealth();
		float absorption = player.getAbsorptionAmount();
		float displayed = Math.min(health + absorption, maxHealth);
		float progress = displayed / maxHealth;

		renderBar(graphics, BAR_X, BAR_Y, progress, BOSS_BAR_HEALTH);
	}

	private static void renderStaminaBar(GuiGraphics graphics, Player player) {
		float progress = StaminaHandler.getRatio(player);
		renderBar(graphics, BAR_X, staminaBarY(), progress, BOSS_BAR_STAMINA);
	}

	/** Total vials in inventory, shown under the stamina bar (top-left cluster). */
	private static void renderInventoryVialCount(GuiGraphics graphics, Player player) {
		int count = BloodVialHudClient.countBloodVials(player);
		if (count <= 0) {
			return;
		}

		int panelY = staminaBarY() + BAR_HEIGHT + VIAL_LABEL_GAP;
		ItemStack iconStack = new ItemStack(ModItems.BLOOD_VIAL);

		HudLabelRenderer.drawBoxedItemWithCount(graphics, BAR_X, panelY, iconStack, count, 0xFFCC4444);
	}
}
