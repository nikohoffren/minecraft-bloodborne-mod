package dev.minecraftmods.simpleweapon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWeaponMod implements ModInitializer {
	public static final String MOD_ID = "simple_weapon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private int teleportDelay = 0;
	private ServerPlayer pendingPlayer = null;

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

			if (pendingPlayer != null) {
				teleportDelay--;

				if (teleportDelay <= 0) {
					teleportToVillage(pendingPlayer);
					pendingPlayer = null;
				}
			}

		});

		ServerPlayerEvents.JOIN.register(this::scheduleTeleport);

		LOGGER.info("Bloodborne mod loaded.");
	}

	private void scheduleTeleport(ServerPlayer player) {
		this.pendingPlayer = player;
		this.teleportDelay = 5;
	}

	private void teleportToVillage(ServerPlayer player) {

		ServerLevel level = player.serverLevel();

		BlockPos villagePos = null;

		for (int radius = 200; radius <= 2000 && villagePos == null; radius += 200) {
			villagePos = level.findNearestMapStructure(
					net.minecraft.tags.StructureTags.VILLAGE,
					player.blockPosition(),
					radius,
					false
			);
		}

		if (villagePos == null) {
			LOGGER.warn("No village found!");
			return;
		}

		BlockPos bedPos = findVillageBed(level, villagePos);

		BlockPos finalPos;

		if (bedPos != null) {
			// Spawn slightly above bed
			finalPos = bedPos.above();
			LOGGER.info("Spawning at village bed: " + bedPos);
		} else {
			LOGGER.warn("No bed found, using fallback surface");

			finalPos = level.getHeightmapPos(
					net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
					villagePos
			);
		}

		player.teleportTo(
				level,
				finalPos.getX() + 0.5,
				finalPos.getY(),
				finalPos.getZ() + 0.5,
				player.getYRot(),
				player.getXRot()
		);
	}

	private BlockPos findVillageBed(ServerLevel level, BlockPos center) {

		int searchRadius = 64;

		for (int dx = -searchRadius; dx <= searchRadius; dx++) {
			for (int dz = -searchRadius; dz <= searchRadius; dz++) {

				BlockPos pos = center.offset(dx, 0, dz);

				// Ensure chunk is loaded
				level.getChunk(pos);

				BlockPos surface = level.getHeightmapPos(
						net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
						pos
				);

				var state = level.getBlockState(surface);

				// Check if it's a bed
				if (state.is(net.minecraft.world.level.block.Blocks.WHITE_BED) ||
						state.is(net.minecraft.world.level.block.Blocks.RED_BED) ||
						state.is(net.minecraft.world.level.block.Blocks.BLUE_BED) ||
						state.is(net.minecraft.world.level.block.Blocks.BLACK_BED)) {

					return surface;
				}
			}
		}
		return null;
	}
}
