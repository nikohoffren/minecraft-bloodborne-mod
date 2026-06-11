package dev.minecraftmods.bloodborne.stamina;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class QuickstepHandler {

	/** Horizontal speed boost for the dodge (blocks/tick impulse). */
	public static final double DODGE_SPEED = 1.05D;

	/** Small hop so rolls clear low obstacles. */
	public static final double DODGE_LIFT = 0.28D;

	public static final int COOLDOWN_TICKS = 8;
	public static final int IFRAME_TICKS = 8;

	private QuickstepHandler() {
	}

	public static boolean tryQuickstep(ServerPlayer player, float yaw, float forward, float strafe) {
		if (!canQuickstep(player)) {
			return false;
		}

		if (!player.isCreative()) {
			DodgeCooldownAccess cooldown = (DodgeCooldownAccess) player;
			if (cooldown.bloodborne$getDodgeCooldown() > 0) {
				return false;
			}

			if (!StaminaHandler.tryConsume(player, StaminaHandler.DODGE_COST, StaminaHandler.REGEN_DELAY_TICKS)) {
				return false;
			}

			Vec3 direction = resolveDirection(player, yaw, forward, strafe);
			applyQuickstep(player, direction);
			cooldown.bloodborne$setDodgeCooldown(COOLDOWN_TICKS);
			return true;
		}

		Vec3 direction = resolveDirection(player, yaw, forward, strafe);
		applyQuickstep(player, direction);
		return true;
	}

	private static boolean canQuickstep(ServerPlayer player) {
		if (!player.isAlive() || player.isSpectator() || player.isPassenger()) {
			return false;
		}

		if (player.isFallFlying() || player.isSleeping()) {
			return false;
		}

		return !player.isInWater() || player.onGround();
	}

	private static Vec3 resolveDirection(ServerPlayer player, float yaw, float forward, float strafe) {
		float f = Mth.clamp(forward, -1.0F, 1.0F);
		float s = Mth.clamp(strafe, -1.0F, 1.0F);

		if (f == 0.0F && s == 0.0F) {
			f = -1.0F;
		} else {
			float length = Mth.sqrt(f * f + s * s);
			f /= length;
			s /= length;
		}

		float yawRad = yaw * (float) (Math.PI / 180.0D);
		double sin = Math.sin(yawRad);
		double cos = Math.cos(yawRad);

		double worldX = s * cos - f * sin;
		double worldZ = f * cos + s * sin;

		return new Vec3(worldX, 0.0D, worldZ).normalize();
	}

	private static void applyQuickstep(ServerPlayer player, Vec3 direction) {
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(
				direction.x * DODGE_SPEED,
				Math.max(motion.y, DODGE_LIFT),
				direction.z * DODGE_SPEED
		);
		player.setSprinting(false);
		player.hurtMarked = true;
		player.invulnerableTime = Math.max(player.invulnerableTime, IFRAME_TICKS);
		player.fallDistance = 0.0F;
	}

	public static void tickCooldown(ServerPlayer player) {
		DodgeCooldownAccess cooldown = (DodgeCooldownAccess) player;
		int remaining = cooldown.bloodborne$getDodgeCooldown();

		if (remaining > 0) {
			cooldown.bloodborne$setDodgeCooldown(remaining - 1);
		}
	}
}
