package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.client.HunterPistolClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(method = "renderItemHotbar", at = @At("RETURN"))
	private void bloodborne$renderPistolAmmoHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		HunterPistolClient.renderAmmoHud(guiGraphics);
	}
}
