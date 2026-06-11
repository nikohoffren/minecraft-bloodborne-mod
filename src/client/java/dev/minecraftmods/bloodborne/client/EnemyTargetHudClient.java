package dev.minecraftmods.bloodborne.client;

import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import dev.minecraftmods.bloodborne.entity.BloodborneEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Fallback health readout above the crosshair when looking at a Bloodborne enemy.
 */
public final class EnemyTargetHudClient {

	private static final int BAR_WIDTH = 110;
	private static final int BAR_HEIGHT = 4;

	private static final ResourceLocation BAR_BACKGROUND =
			ResourceLocation.withDefaultNamespace("boss_bar/white_background");
	private static final ResourceLocation BAR_FILL =
			ResourceLocation.withDefaultNamespace("boss_bar/red_progress");

	public static void render(GuiGraphics graphics) {
		Minecraft client = Minecraft.getInstance();
		LivingEntity target = getLookTarget(client);

		if (target == null || !shouldShowBar(target)) {
			return;
		}

		int centerX = graphics.guiWidth() / 2;
		int barY = graphics.guiHeight() / 2 + 14;
		int barX = centerX - BAR_WIDTH / 2;

		if (target.hasCustomName() && target.getCustomName() != null) {
			int nameY = barY - 12;
			HudLabelRenderer.drawBoxedLabel(graphics, centerX, nameY, target.getCustomName(), 0xFFE8E8E8);
		}

		graphics.blitSprite(BAR_BACKGROUND, barX, barY, BAR_WIDTH, BAR_HEIGHT);
		int fillWidth = (int) (BAR_WIDTH * (target.getHealth() / target.getMaxHealth()));
		if (fillWidth > 0) {
			graphics.blitSprite(BAR_FILL, BAR_WIDTH, BAR_HEIGHT, 0, 0, barX, barY, fillWidth, BAR_HEIGHT);
		}
	}

	private static LivingEntity getLookTarget(Minecraft client) {
		if (!(client.hitResult instanceof EntityHitResult entityHit)) {
			return null;
		}

		if (entityHit.getEntity() instanceof LivingEntity living) {
			return living;
		}

		return null;
	}

	private static boolean shouldShowBar(LivingEntity entity) {
		if (!entity.isAlive()) {
			return false;
		}

		if (entity.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
			return false;
		}

		return BloodborneEntities.isBloodborneEnemy(entity);
	}

	private EnemyTargetHudClient() {
	}
}
