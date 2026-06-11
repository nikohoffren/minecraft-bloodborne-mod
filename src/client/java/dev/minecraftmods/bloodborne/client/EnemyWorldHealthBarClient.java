package dev.minecraftmods.bloodborne.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.minecraftmods.bloodborne.boss.ClericBeastBossConfig;
import dev.minecraftmods.bloodborne.entity.BloodborneEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class EnemyWorldHealthBarClient {

	private static final ResourceLocation WHITE_TEXTURE =
			ResourceLocation.withDefaultNamespace("textures/misc/white.png");

	private static final double MAX_DISTANCE = 32.0D;
	private static final float BAR_HALF_WIDTH = 24.0F;
	private static final float BAR_HEIGHT = 3.0F;
	private static final float NAME_GAP = 3.0F;
	private static final float LABEL_SCALE = 0.022F;

	public static void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			Minecraft client = Minecraft.getInstance();
			if (client.player == null || client.level == null) {
				return;
			}

			render(
					context.matrixStack(),
					context.consumers(),
					context.camera(),
					context.tickCounter().getGameTimeDeltaPartialTick(false)
			);
		});
	}

	private static void render(
			PoseStack poseStack,
			MultiBufferSource buffers,
			Camera camera,
			float partialTick
	) {
		Minecraft client = Minecraft.getInstance();
		AABB search = client.player.getBoundingBox().inflate(MAX_DISTANCE);
		RenderType barType = RenderType.text(WHITE_TEXTURE);

		for (LivingEntity entity : client.level.getEntitiesOfClass(LivingEntity.class, search, EnemyWorldHealthBarClient::shouldShowBar)) {
			if (client.player.distanceTo(entity) > MAX_DISTANCE) {
				continue;
			}

			renderAboveEntity(poseStack, buffers, barType, camera, entity, partialTick);
		}
	}

	private static boolean shouldShowBar(LivingEntity entity) {
		if (!entity.isAlive() || entity.isInvisible()) {
			return false;
		}

		if (entity.getTags().contains(ClericBeastBossConfig.ENTITY_TAG)) {
			return false;
		}

		return BloodborneEntities.isBloodborneEnemy(entity);
	}

	private static void renderAboveEntity(
			PoseStack poseStack,
			MultiBufferSource buffers,
			RenderType barType,
			Camera camera,
			LivingEntity entity,
			float partialTick
	) {
		double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
		double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) + entity.getBbHeight() + 0.5D;
		double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());

		Vec3 cam = camera.getPosition();
		poseStack.pushPose();
		poseStack.translate(x - cam.x, y - cam.y, z - cam.z);
		poseStack.mulPose(camera.rotation());
		poseStack.scale(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);

		Font font = Minecraft.getInstance().font;
		float progress = entity.getHealth() / entity.getMaxHealth();
		float nameOffset = entity.hasCustomName() ? -(NAME_GAP + font.lineHeight) : 0.0F;

		if (entity.hasCustomName() && entity.getCustomName() != null) {
			float nameWidth = -font.width(entity.getCustomName()) / 2.0F;
			poseStack.pushPose();
			poseStack.translate(nameWidth, nameOffset, 0.0F);
			font.drawInBatch(
					entity.getCustomName(),
					0.0F,
					0.0F,
					0xE8E8E8,
					false,
					poseStack.last().pose(),
					buffers,
					Font.DisplayMode.SEE_THROUGH,
					0,
					0xF000F0
			);
			poseStack.popPose();
		}

		float barY = nameOffset - BAR_HEIGHT - 1.0F;
		drawBarQuad(poseStack, buffers, barType, -BAR_HALF_WIDTH, barY, BAR_HALF_WIDTH * 2.0F, BAR_HEIGHT, 0xCC222222);
		drawBarQuad(
				poseStack,
				buffers,
				barType,
				-BAR_HALF_WIDTH,
				barY,
				BAR_HALF_WIDTH * 2.0F * Mth.clamp(progress, 0.0F, 1.0F),
				BAR_HEIGHT,
				0xFFDD3333
		);

		poseStack.popPose();
	}

	private static void drawBarQuad(
			PoseStack poseStack,
			MultiBufferSource buffers,
			RenderType barType,
			float x,
			float y,
			float width,
			float height,
			int color
	) {
		if (width <= 0.0F) {
			return;
		}

		float a = ((color >> 24) & 0xFF) / 255.0F;
		float r = ((color >> 16) & 0xFF) / 255.0F;
		float g = ((color >> 8) & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		Matrix4f matrix = poseStack.last().pose();
		VertexConsumer consumer = buffers.getBuffer(barType);

		consumer.addVertex(matrix, x, y + height, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F);
		consumer.addVertex(matrix, x + width, y + height, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F);
		consumer.addVertex(matrix, x + width, y, 0.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F);
		consumer.addVertex(matrix, x, y, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F);
	}

	private EnemyWorldHealthBarClient() {
	}
}
