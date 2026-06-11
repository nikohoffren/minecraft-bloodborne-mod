package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.echo.BloodEchoHandler;
import dev.minecraftmods.bloodborne.echo.EchoRewardConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEchoRewardMixin {

	@Inject(method = "die", at = @At("TAIL"))
	private void bloodborne$grantEchoesOnDeath(DamageSource damageSource, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;

		if (self.level().isClientSide() || EchoRewardConfig.getReward(self) <= 0) {
			return;
		}

		BloodEchoHandler.awardKillFromDamage(self, damageSource);
	}
}
