package dev.minecraftmods.bloodborne.echo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

import java.text.NumberFormat;
import java.util.Locale;

public final class BloodEchoHandler {

	private static final NumberFormat ECHO_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

	private BloodEchoHandler() {
	}

	public static int getEchoes(Player player) {
		if (player instanceof EchoAccess access) {
			return access.bloodborne$getBloodEchoes();
		}

		return 0;
	}

	public static void setEchoes(Player player, int amount) {
		if (player instanceof EchoAccess access) {
			access.bloodborne$setBloodEchoes(amount);
		}
	}

	public static void addEchoes(Player player, int amount) {
		if (amount <= 0 || !(player instanceof EchoAccess access)) {
			return;
		}

		access.bloodborne$addBloodEchoes(amount);
	}

	public static void awardKill(ServerPlayer player, LivingEntity victim) {
		int reward = EchoRewardConfig.getReward(victim);
		if (reward <= 0) {
			return;
		}

		addEchoes(player, reward);
		player.displayClientMessage(
				Component.translatable("message.bloodborne.echoes_gained", ECHO_FORMAT.format(reward)),
				true
		);
	}

	public static void awardKillFromDamage(LivingEntity victim, DamageSource source) {
		if (victim.level().isClientSide) {
			return;
		}

		int reward = EchoRewardConfig.getReward(victim);
		if (reward <= 0) {
			return;
		}

		ServerPlayer player = resolvePlayerAttacker(source);
		if (player == null) {
			return;
		}

		awardKill(player, victim);
	}

	private static ServerPlayer resolvePlayerAttacker(DamageSource source) {
		if (source.getEntity() instanceof ServerPlayer player) {
			return player;
		}

		if (source.getDirectEntity() instanceof ServerPlayer player) {
			return player;
		}

		if (source.getDirectEntity() instanceof Projectile projectile
				&& projectile.getOwner() instanceof ServerPlayer player) {
			return player;
		}

		if (source.getEntity() instanceof Projectile projectile
				&& projectile.getOwner() instanceof ServerPlayer player) {
			return player;
		}

		return null;
	}
}
