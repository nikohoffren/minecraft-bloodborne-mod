package dev.minecraftmods.simpleweapon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class SimpleWeaponModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		final boolean[] wasSleeping = {false};
		final boolean[] hasShownMessage = {false};
		final boolean[] hasSetTime = {false};

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (client.level == null) return;

			// Set time once after world loads
			if (!hasSetTime[0]) {
				client.level.setDayTime(13000); // fixed twilight time
				hasSetTime[0] = true;
			}

			if (client.player == null) return;

			// Show message once when joining
			if (!hasShownMessage[0]) {
				showBiomeMessage(client);
				hasShownMessage[0] = true;
			}

			// Detect waking up from bed
			boolean isSleeping = client.player.isSleeping();

			if (wasSleeping[0] && !isSleeping) {
				showBiomeMessage(client);
			}

			wasSleeping[0] = isSleeping;
		});

		// Grass darker
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {

			if (view == null || pos == null) {
				return 0x2F3B2F; // fallback dark color
			}

			int color = BiomeColors.getAverageGrassColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.5);
			int g = (int)(((color >> 8) & 0xFF) * 0.4);
			int b = (int)((color & 0xFF) * 0.4);

			return (r << 16) | (g << 8) | b;

		}, Blocks.GRASS_BLOCK);

		// Leaves darker
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {

			if (view == null || pos == null) {
				return 0x1F2A1F;
			}

			int color = BiomeColors.getAverageFoliageColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.4);
			int g = (int)(((color >> 8) & 0xFF) * 0.3);
			int b = (int)((color & 0xFF) * 0.3);

			return (r << 16) | (g << 8) | b;

		}, Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES);

		// Water darker
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {

			if (view == null || pos == null) {
				return 0x0A0A14;
			}

			int color = BiomeColors.getAverageWaterColor(view, pos);

			int r = (int)(((color >> 16) & 0xFF) * 0.3);
			int g = (int)(((color >> 8) & 0xFF) * 0.2);
			int b = (int)((color & 0xFF) * 0.2);

			return (r << 16) | (g << 8) | b;

		}, Blocks.WATER);

	}

	private void showBiomeMessage(Minecraft client) {
		client.player.displayClientMessage(
				Component.literal("Central Yharnam"),
				true // action bar
		);
	}

}