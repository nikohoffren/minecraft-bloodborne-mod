package dev.minecraftmods.bloodborne.mixin.client;

import dev.minecraftmods.bloodborne.client.BloodborneMusicClient;
import net.minecraft.sounds.Music;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {

	@Inject(method = "startPlaying", at = @At("HEAD"), cancellable = true)
	private void bloodborne$onlyAllowModMusic(Music music, CallbackInfo ci) {
		if (!BloodborneMusicClient.shouldAllowMusic(music)) {
			ci.cancel();
		}
	}
}
