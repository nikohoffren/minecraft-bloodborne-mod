package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Digging;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Digging.class)
public class WardenDiggingMixin {

	@Inject(method = "checkExtraStartConditions", at = @At("HEAD"), cancellable = true)
	private void bloodborne$clericBeastNeverDig(
			ServerLevel level,
			Warden warden,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (warden.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "canStillUse", at = @At("HEAD"), cancellable = true)
	private void bloodborne$clericBeastStopDigging(
			ServerLevel level,
			Warden warden,
			long time,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (warden.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
			cir.setReturnValue(false);
		}
	}
}
