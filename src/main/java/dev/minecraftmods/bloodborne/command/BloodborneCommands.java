package dev.minecraftmods.bloodborne.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import dev.minecraftmods.bloodborne.boss.ClericBeastBossManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public final class BloodborneCommands {

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				registerClericBeastCommands(dispatcher)
		);
	}

	private static void registerClericBeastCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("bloodborne")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("cleric_beast")
								.then(Commands.literal("reset")
										.executes(context -> {
											CommandSourceStack source = context.getSource();
											ServerLevel level = source.getLevel();

											ClericBeastBossManager.resetClericBeast(level);
											source.sendSuccess(
													() -> Component.literal(
															"Cleric Beast reset — will respawn at "
																	+ ClericBeastBossConfig.SPAWN_BLOCK_POS
																	+ " when the chunk is loaded"
													),
													true
											);
											return 1;
										}))
								.then(Commands.literal("info")
										.executes(context -> {
											CommandSourceStack source = context.getSource();
											source.sendSuccess(
													() -> Component.literal(
															"Cleric Beast arena center: "
																	+ ClericBeastBossConfig.SPAWN_BLOCK_POS
																	+ ", radius: "
																	+ (int) ClericBeastBossConfig.ARENA_HORIZONTAL_RADIUS
																	+ " blocks"
													),
													false
											);
											return 1;
										}))
						)
		);
	}

	private BloodborneCommands() {
	}
}
