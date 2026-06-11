package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.echo.BloodEchoHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Replaces the vanilla XP bar with Blood Echoes (total shown as the level number).
 */
public final class BloodEchoExperienceHud {

	private static final int BAR_WIDTH = 182;
	private static final int BAR_HEIGHT = 5;

	/** Echoes per “segment” of bar fill before the meter wraps visually. */
	private static final int BAR_SEGMENT = 1_000;

	private static final NumberFormat ECHO_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

	private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND =
			ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
	private static final ResourceLocation EXPERIENCE_BAR_PROGRESS =
			ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");

	private BloodEchoExperienceHud() {
	}

	public static void renderBar(GuiGraphics graphics, int leftX) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		int echoes = BloodEchoHandler.getEchoes(player);
		float progress = barProgress(echoes);
		int y = graphics.guiHeight() - 29;

		graphics.blitSprite(EXPERIENCE_BAR_BACKGROUND, leftX, y, BAR_WIDTH, BAR_HEIGHT);

		int fillWidth = (int) (BAR_WIDTH * progress);
		if (fillWidth > 0) {
			graphics.blitSprite(
					EXPERIENCE_BAR_PROGRESS,
					BAR_WIDTH,
					BAR_HEIGHT,
					0,
					0,
					leftX,
					y,
					fillWidth,
					BAR_HEIGHT
			);
		}
	}

	public static void renderLevel(GuiGraphics graphics) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		int echoes = BloodEchoHandler.getEchoes(player);
		String text = ECHO_FORMAT.format(echoes);
		int centerX = graphics.guiWidth() / 2;
		int y = graphics.guiHeight() - 31 - Minecraft.getInstance().font.lineHeight;

		graphics.drawCenteredString(Minecraft.getInstance().font, text, centerX, y, 0xFFE8C872);
	}

	private static float barProgress(int echoes) {
		if (echoes <= 0) {
			return 0.0F;
		}

		int segment = echoes % BAR_SEGMENT;
		if (segment == 0) {
			return 1.0F;
		}

		return segment / (float) BAR_SEGMENT;
	}
}
