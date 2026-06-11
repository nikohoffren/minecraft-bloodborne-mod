package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.client.BloodEchoExperienceHud;
import dev.minecraftmods.bloodborne.client.BloodVialHudClient;
import dev.minecraftmods.bloodborne.client.EnemyTargetHudClient;
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
		if (HealthHudClient.shouldUseBloodborneHud(player)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void bloodborne$hideHunger(GuiGraphics guiGraphics, Player player, int x, int y, CallbackInfo ci) {
		if (HealthHudClient.shouldUseBloodborneHud(player)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void bloodborne$hideArmor(
			GuiGraphics guiGraphics,
			Player player,
			int x,
			int y,
			int height,
			int heartRows,
			CallbackInfo ci
	) {
		if (HealthHudClient.shouldUseBloodborneHud(player)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void bloodborne$renderEchoExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (minecraft.player != null && HealthHudClient.shouldUseBloodborneHud(minecraft.player)) {
			ci.cancel();
			BloodEchoExperienceHud.renderBar(guiGraphics, x);
		}
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
	private void bloodborne$renderEchoLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (minecraft.player != null && HealthHudClient.shouldUseBloodborneHud(minecraft.player)) {
			ci.cancel();
			BloodEchoExperienceHud.renderLevel(guiGraphics);
		}
	}

	/**
	 * Renders after the hotbar so health/stamina/vials show in survival and creative
	 * (creative often skips {@code renderPlayerHealth}).
	 */
	@Inject(method = "renderItemHotbar", at = @At("RETURN"))
	private void bloodborne$renderBloodborneHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (minecraft.player != null && HealthHudClient.shouldUseBloodborneHud(minecraft.player)) {
			HealthHudClient.renderHud(guiGraphics);
		}

		HunterPistolClient.renderAmmoHud(guiGraphics);
		BloodVialHudClient.renderOffhandHud(guiGraphics);
		EnemyTargetHudClient.render(guiGraphics);
	}
}
