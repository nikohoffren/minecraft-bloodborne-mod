package dev.minecraftmods.simpleweapon.mixin;

import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @ModifyArgs(
            method = "setupFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"
            )
    )
    private static void modifyFogStart(Args args) {
        args.set(0, 2.0F); // fog starts very close
    }

    @ModifyArgs(
            method = "setupFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V"
            )
    )
    private static void modifyFogEnd(Args args) {
        args.set(0, 18.0F); // fog ends early
    }

}
