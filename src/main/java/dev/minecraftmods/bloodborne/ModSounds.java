package dev.minecraftmods.bloodborne;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {

	/** Title screen (Bloodcraft main menu). */
	public static final SoundEvent MENU_MAIN_THEME = register("music.menu.main_theme");

	/** Reserved for Cleric Beast / Warden boss fights (not wired up yet). */
	public static final SoundEvent BOSS_CLERIC_BEAST = register("music.boss.cleric_beast");

	private static SoundEvent register(String path) {
		ResourceLocation id = BloodborneMod.id(path);
		return Registry.register(
				BuiltInRegistries.SOUND_EVENT,
				id,
				SoundEvent.createVariableRangeEvent(id)
		);
	}

	public static void initialize() {
		// Triggers static registration
	}

	private ModSounds() {
	}
}
