package dev.minecraftmods.bloodborne.stamina;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class StaminaHandler {

	public static final float MAX_STAMINA = 100.0F;

	public static final float REGEN_PER_TICK = 3.5F;
	public static final int REGEN_DELAY_TICKS = 12;

	public static final float SPRINT_DRAIN_PER_TICK = 1.25F;
	public static final float ATTACK_COST = 14.0F;
	public static final float PISTOL_COST = 10.0F;
	public static final float DODGE_COST = 22.0F;

	private StaminaHandler() {
	}

	public static StaminaAccess access(Player player) {
		return (StaminaAccess) player;
	}

	public static void serverTick(Player player, int regenCooldownTicks) {
		if (player.level().isClientSide) {
			return;
		}

		StaminaAccess stamina = access(player);

		if (player.isCreative()) {
			stamina.bloodborne$setStamina(MAX_STAMINA);
			return;
		}

		float current = stamina.bloodborne$getStamina();

		if (player.isSprinting() && current > 0.0F) {
			current = Math.max(0.0F, current - SPRINT_DRAIN_PER_TICK);
			stamina.bloodborne$setStamina(current);
			markRegenDelay(player, REGEN_DELAY_TICKS);
		}

		if (current <= 0.0F && player.isSprinting()) {
			player.setSprinting(false);
		}

		if (regenCooldownTicks > 0) {
			return;
		}

		if (current < MAX_STAMINA) {
			stamina.bloodborne$setStamina(Math.min(MAX_STAMINA, current + REGEN_PER_TICK));
		}
	}

	public static boolean tryConsume(Player player, float amount, int regenCooldownTicks) {
		if (!access(player).bloodborne$tryConsume(amount)) {
			return false;
		}

		markRegenDelay(player, regenCooldownTicks);
		return true;
	}

	public static void markRegenDelay(Player player, int ticks) {
		StaminaCooldownAccess cooldown = (StaminaCooldownAccess) player;
		cooldown.bloodborne$setStaminaRegenDelay(Math.max(cooldown.bloodborne$getStaminaRegenDelay(), ticks));
	}

	/** @return new cooldown ticks after decrementing one tick */
	public static int tickRegenCooldown(Player player) {
		StaminaCooldownAccess cooldown = (StaminaCooldownAccess) player;
		int delay = cooldown.bloodborne$getStaminaRegenDelay();

		if (delay > 0) {
			cooldown.bloodborne$setStaminaRegenDelay(delay - 1);
		}

		return cooldown.bloodborne$getStaminaRegenDelay();
	}

	public static float getRatio(Player player) {
		return Mth.clamp(access(player).bloodborne$getStamina() / MAX_STAMINA, 0.0F, 1.0F);
	}
}
