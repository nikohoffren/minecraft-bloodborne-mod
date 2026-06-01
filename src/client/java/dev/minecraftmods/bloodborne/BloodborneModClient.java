package dev.minecraftmods.bloodborne;

import dev.minecraftmods.bloodborne.client.HunterPistolClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class BloodborneModClient implements ClientModInitializer {

	/** Full black before the world fades in (~1.5 s). */
	private static final int HOLD_BLACK_TICKS = 30;

	/** Fade from black to clear (~3.5 s). */
	private static final int FADE_OUT_TICKS = 70;

	private static final int FADE_TOTAL_TICKS = HOLD_BLACK_TICKS + FADE_OUT_TICKS;

	private static int fadeTicks = 0;
	private static boolean isFading = false;

	private boolean titleShown = false;

	@Override
	public void onInitializeClient() {

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> beginWorldIntro());

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> endWorldIntro());

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (!isFading || fadeTicks <= 0) {
				return;
			}

			int alpha = getFadeOverlayAlpha();
			if (alpha <= 0) {
				return;
			}

			drawContext.fill(
					0,
					0,
					drawContext.guiWidth(),
					drawContext.guiHeight(),
					(alpha << 24)
			);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (client.player == null || client.level == null) {
				return;
			}

			if (isFading) {
				fadeTicks--;

				if (!titleShown && fadeTicks == FADE_OUT_TICKS) {
					showCentralYharnamTitle(client);
					titleShown = true;
				}

				if (fadeTicks <= 0) {
					isFading = false;
				}
			}
		});

		registerColorProviders();
		HunterPistolClient.register();
	}

	private void beginWorldIntro() {
		titleShown = false;
		startFade();
	}

	private void endWorldIntro() {
		isFading = false;
		fadeTicks = 0;
		titleShown = false;
	}

	public static void startFade() {
		isFading = true;
		fadeTicks = FADE_TOTAL_TICKS;
	}

	/** 255 = solid black, 0 = fully visible world. */
	private static int getFadeOverlayAlpha() {
		if (fadeTicks > FADE_OUT_TICKS) {
			return 255;
		}

		return (int) (255.0f * fadeTicks / FADE_OUT_TICKS);
	}

	private static void showCentralYharnamTitle(net.minecraft.client.Minecraft client) {
		Component title = Component.literal("Central Yharnam")
				.withStyle(style -> style.withBold(true));

		client.gui.setTitle(title);
		client.gui.setSubtitle(Component.empty());
		// fade in (ticks), stay, fade out — ~3s total visible title
		client.gui.setTimes(10, 50, 20);
	}

	private void registerColorProviders() {

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) return 0x1F2A1F;

			int color = BiomeColors.getAverageFoliageColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.4);
			int g = (int)(((color >> 8) & 0xFF) * 0.3);
			int b = (int)((color & 0xFF) * 0.3);

			return (r << 16) | (g << 8) | b;

		}, Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) return 0x0A0A14;

			int color = BiomeColors.getAverageWaterColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.3);
			int g = (int)(((color >> 8) & 0xFF) * 0.2);
			int b = (int)((color & 0xFF) * 0.2);

			return (r << 16) | (g << 8) | b;

		}, Blocks.WATER);
	}
}
