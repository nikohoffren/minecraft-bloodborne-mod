package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.BloodborneMod;
import dev.minecraftmods.bloodborne.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public final class BloodborneMusicClient {

	public enum Mode {
		/** Title screen — main theme only. */
		TITLE,
		/** Exploration — no music (Bloodborne-style silence). */
		IN_WORLD,
		/** Future boss fights — boss theme only. */
		BOSS
	}

	private static final Music MENU_MUSIC = new Music(
			Holder.direct(ModSounds.MENU_MAIN_THEME),
			0,
			0,
			true
	);

	private static final Music BOSS_MUSIC = new Music(
			Holder.direct(ModSounds.BOSS_CLERIC_BEAST),
			0,
			0,
			true
	);

	private static Mode mode = Mode.TITLE;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(BloodborneMusicClient::clientTick);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			mode = Mode.IN_WORLD;
			stopAllMusic(client);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			mode = Mode.TITLE;
		});
	}

	public static Mode getMode() {
		return mode;
	}

	/** For future boss wiring (e.g. Warden as Cleric Beast). */
	public static void setMode(Mode newMode) {
		mode = newMode;
	}

	public static void startBossMusic(Minecraft client) {
		mode = Mode.BOSS;
		client.getMusicManager().startPlaying(BOSS_MUSIC);
	}

	public static void stopBossMusic(Minecraft client) {
		if (mode == Mode.BOSS) {
			mode = Mode.IN_WORLD;
		}

		stopAllMusic(client);
	}

	public static boolean shouldAllowMusic(Music music) {
		return music.getEvent().unwrapKey()
				.map(key -> BloodborneMod.MOD_ID.equals(key.location().getNamespace()))
				.orElse(false);
	}

	private static void clientTick(Minecraft client) {
		if (client.screen instanceof TitleScreen) {
			if (mode != Mode.TITLE) {
				mode = Mode.TITLE;
			}

			ensureMenuThemePlaying(client);
		}
	}

	private static void ensureMenuThemePlaying(Minecraft client) {
		MusicManager musicManager = client.getMusicManager();

		if (musicManager.isPlayingMusic(MENU_MUSIC)) {
			return;
		}

		musicManager.startPlaying(MENU_MUSIC);
	}

	private static void stopAllMusic(Minecraft client) {
		client.getMusicManager().stopPlaying();
	}

	private BloodborneMusicClient() {
	}
}
