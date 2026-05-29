package dev.minecraftmods.bloodborne;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class BloodborneModClient implements ClientModInitializer {

	private static int fadeTicks = 0;
	private static boolean isFading = false;

	private boolean hasStartedFade = false;

	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (isFading) {
				drawContext.fill(
						0,
						0,
						drawContext.guiWidth(),
						drawContext.guiHeight(),
						0xFF000000
				);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (client.player == null || client.level == null) return;

			if (!hasStartedFade && client.player.tickCount > 20) {
				startFade();
				hasStartedFade = true;
			}

			if (isFading) {
				fadeTicks--;

				if (fadeTicks == 20) {
					client.gui.setTitle(
							Component.literal("Central Yharnam")
									.withStyle(style -> style.withBold(true))
					);
				}

				if (fadeTicks <= 0) {
					isFading = false;
				}
			}
		});

		registerColorProviders();
	}

	public static void startFade() {
		isFading = true;
		fadeTicks = 60;
	}

	private void registerColorProviders() {

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) return 0x2F3B2F;

			int color = BiomeColors.getAverageGrassColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.5);
			int g = (int)(((color >> 8) & 0xFF) * 0.4);
			int b = (int)((color & 0xFF) * 0.4);

			return (r << 16) | (g << 8) | b;

		}, Blocks.GRASS_BLOCK);

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
