package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.client.BloodVialHudClient;
import dev.minecraftmods.bloodborne.client.HealthHudClient;
import dev.minecraftmods.bloodborne.client.HunterPistolClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
	private void bloodborne$hideVanillaHearts(
			GuiGraphics guiGraphics,
			Player player,
			int x,
			int y,
			int height,
			int maxHealth,
			float health,
			int displayHealth,
			int absorption,
			int armor,
			boolean blinking,
			CallbackInfo ci
	) {
		if (HealthHudClient.shouldReplaceHearts(player)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void bloodborne$hideHunger(GuiGraphics guiGraphics, Player player, int x, int y, CallbackInfo ci) {
		if (HealthHudClient.shouldReplaceHearts(player)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderPlayerHealth", at = @At("RETURN"))
	private void bloodborne$renderHealthHud(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (minecraft.player != null && HealthHudClient.shouldReplaceHearts(minecraft.player)) {
			HealthHudClient.renderHud(guiGraphics);
		}
	}

	@Inject(method = "renderItemHotbar", at = @At("RETURN"))
	private void bloodborne$renderOffhandHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		HunterPistolClient.renderAmmoHud(guiGraphics);
		BloodVialHudClient.renderOffhandHud(guiGraphics);
	}
}
