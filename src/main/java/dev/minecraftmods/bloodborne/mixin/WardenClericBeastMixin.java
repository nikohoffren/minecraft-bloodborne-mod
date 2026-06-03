package dev.minecraftmods.bloodborne.mixin;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public abstract class WardenClericBeastMixin {

	@Shadow
	abstract boolean isDiggingOrEmerging();

	@Inject(method = "tick", at = @At("HEAD"))
	private void bloodborne$keepClericBeastAboveGround(CallbackInfo ci) {
		Warden self = (Warden) (Object) this;

		if (!self.getTags().contains(ClericBeastBossConfig.ENTITY_TAG) || self.level().isClientSide()) {
			return;
		}

		if (!isDiggingOrEmerging()) {
			return;
		}

		Vec3 spawn = ClericBeastBossConfig.SPAWN_POS;
		self.teleportTo(spawn.x, spawn.y, spawn.z);
		self.setDeltaMovement(Vec3.ZERO);

		if (self.level() instanceof ServerLevel level) {
			for (ServerPlayer player : level.players()) {
				if (player.isAlive() && ClericBeastBossConfig.isInsideArena(player.position())) {
					self.increaseAngerAt(player, 80, false);
				}
			}
		}
	}
}
