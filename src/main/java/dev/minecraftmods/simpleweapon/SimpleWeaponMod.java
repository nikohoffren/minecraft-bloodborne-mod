package dev.minecraftmods.simpleweapon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWeaponMod implements ModInitializer {
	public static final String MOD_ID = "simple_weapon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerLevel level : server.getAllLevels()) {

				if (level.getGameRules().getBoolean(
						net.minecraft.world.level.GameRules.RULE_DAYLIGHT)) {

					level.getGameRules().getRule(
							net.minecraft.world.level.GameRules.RULE_DAYLIGHT
					).set(false, server);

					level.setDayTime(13000);
				}
			}
		});

		LOGGER.info("Simple Weapon mod loaded.");
	}
}
