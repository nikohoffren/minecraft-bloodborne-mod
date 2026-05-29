package dev.minecraftmods.bloodborne;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloodborneMod implements ModInitializer {

	public static final String MOD_ID = "bloodborne";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> {

			for (ServerLevel level : server.getAllLevels()) {
				if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {

					level.getGameRules()
							.getRule(GameRules.RULE_DAYLIGHT)
							.set(false, server);

					level.setDayTime(13000);
				}
			}
		});

		LOGGER.info("Bloodborne mod loaded.");
	}
}
