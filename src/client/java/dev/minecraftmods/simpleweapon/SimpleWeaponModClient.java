package dev.minecraftmods.simpleweapon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class SimpleWeaponModClient implements ClientModInitializer {

	private static int fadeTicks = 0;
	private static boolean isFading = false;

	private boolean hasStartedFade = false;

	@Override
	public void onInitializeClient() {

		// ✅ Draw black screen overlay
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (isFading) {
				drawContext.fill(
						0,
						0,
						drawContext.guiWidth(),
						drawContext.guiHeight(),
						0xFF000000 // solid black
				);
			}
		});

		// ✅ Client tick logic
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (client.player == null || client.level == null) return;

			// ✅ Start fade AFTER player is fully loaded
			if (!hasStartedFade && client.player.tickCount > 20) {
				startFade();
				hasStartedFade = true;
			}

			// ✅ Handle fade timing
			if (isFading) {
				fadeTicks--;

				if (fadeTicks == 20) {
					// ✅ Show area title near end of fade
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

		// ✅ Register darker color palette
		registerColorProviders();
	}

	// ✅ Fade trigger
	public static void startFade() {
		isFading = true;
		fadeTicks = 60; // ~3 seconds
	}

	// ✅ Block color overrides (safe with null checks)
	private void registerColorProviders() {

		// Dark grass
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) return 0x2F3B2F;

			int color = BiomeColors.getAverageGrassColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.5);
			int g = (int)(((color >> 8) & 0xFF) * 0.4);
			int b = (int)((color & 0xFF) * 0.4);

			return (r << 16) | (g << 8) | b;

		}, Blocks.GRASS_BLOCK);

		// Dark leaves
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) return 0x1F2A1F;

			int color = BiomeColors.getAverageFoliageColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.4);
			int g = (int)(((color >> 8) & 0xFF) * 0.3);
			int b = (int)((color & 0xFF) * 0.3);

			return (r << 16) | (g << 8) | b;

		}, Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES);

		// Dark water
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