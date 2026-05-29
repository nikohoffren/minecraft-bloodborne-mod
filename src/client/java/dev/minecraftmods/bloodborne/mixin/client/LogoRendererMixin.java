package dev.minecraftmods.bloodborne.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {

	/** Increase to make the Bloodcraft logo larger (1.0 = vanilla size). */
	private static final float LOGO_SCALE = 1.25F;

	@Inject(
			method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
			at = @At("HEAD")
	)
	private void bloodborne$scaleLogoBegin(
			GuiGraphics guiGraphics,
			int screenWidth,
			float alpha,
			int heightOffset,
			CallbackInfo ci
	) {
		PoseStack pose = guiGraphics.pose();
		pose.pushPose();

		float centerX = screenWidth / 2.0F;
		float centerY = heightOffset + LogoRenderer.LOGO_HEIGHT / 2.0F;

		pose.translate(centerX, centerY, 0.0F);
		pose.scale(LOGO_SCALE, LOGO_SCALE, 1.0F);
		pose.translate(-centerX, -centerY, 0.0F);
	}

	@Inject(
			method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
			at = @At("RETURN")
	)
	private void bloodborne$scaleLogoEnd(
			GuiGraphics guiGraphics,
			int screenWidth,
			float alpha,
			int heightOffset,
			CallbackInfo ci
	) {
		guiGraphics.pose().popPose();
	}
}
