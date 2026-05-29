package dev.minecraftmods.bloodborne.mixin.client;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

	@Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
	private void bloodborne$constantSplash(CallbackInfoReturnable<SplashRenderer> cir) {
		cir.setReturnValue(new SplashRenderer("Fear the Old Blood."));
	}
}
