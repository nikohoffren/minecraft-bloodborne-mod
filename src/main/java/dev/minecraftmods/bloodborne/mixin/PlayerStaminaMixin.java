package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.stamina.StaminaAccess;
import dev.minecraftmods.bloodborne.stamina.StaminaCooldownAccess;
import dev.minecraftmods.bloodborne.stamina.StaminaHandler;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerStaminaMixin implements StaminaAccess, StaminaCooldownAccess {

	@Unique
	private static final EntityDataAccessor<Float> BLOODBORNE_STAMINA = SynchedEntityData.defineId(
			Player.class,
			EntityDataSerializers.FLOAT
	);

	@Unique
	private int bloodborne$staminaRegenDelay;

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void bloodborne$defineStamina(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(BLOODBORNE_STAMINA, StaminaHandler.MAX_STAMINA);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void bloodborne$tickStamina(CallbackInfo ci) {
		Player player = (Player) (Object) this;

		if (player.level().isClientSide) {
			return;
		}

		int cooldown = StaminaHandler.tickRegenCooldown(player);
		StaminaHandler.serverTick(player, cooldown);
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void bloodborne$consumeAttackStamina(Entity target, CallbackInfo ci) {
		Player player = (Player) (Object) this;

		if (player.level().isClientSide || player.isCreative()) {
			return;
		}

		if (!StaminaHandler.tryConsume(player, StaminaHandler.ATTACK_COST, StaminaHandler.REGEN_DELAY_TICKS)) {
			ci.cancel();
		}
	}

	@Override
	public float bloodborne$getStamina() {
		return ((Player) (Object) this).getEntityData().get(BLOODBORNE_STAMINA);
	}

	@Override
	public float bloodborne$getMaxStamina() {
		return StaminaHandler.MAX_STAMINA;
	}

	@Override
	public void bloodborne$setStamina(float value) {
		((Player) (Object) this).getEntityData().set(
				BLOODBORNE_STAMINA,
				Mth.clamp(value, 0.0F, StaminaHandler.MAX_STAMINA)
		);
	}

	@Override
	public boolean bloodborne$tryConsume(float amount) {
		float current = bloodborne$getStamina();

		if (current < amount) {
			return false;
		}

		bloodborne$setStamina(current - amount);
		return true;
	}

	@Override
	public int bloodborne$getStaminaRegenDelay() {
		return bloodborne$staminaRegenDelay;
	}

	@Override
	public void bloodborne$setStaminaRegenDelay(int ticks) {
		bloodborne$staminaRegenDelay = Math.max(0, ticks);
	}
}
