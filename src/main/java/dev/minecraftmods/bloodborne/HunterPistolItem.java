package dev.minecraftmods.bloodborne;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public class HunterPistolItem extends Item {

	public static final int MAX_AMMO = 20;
	public static final int PISTOL_DAMAGE = 8;
	public static final double MAX_RANGE = 64.0D;
	public static final int COOLDOWN_TICKS = 8;

	private static final String AMMO_TAG = "Ammo";

	public HunterPistolItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		setAmmo(stack, MAX_AMMO);
		return stack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);

		if (hand != InteractionHand.OFF_HAND || !held.is(this)) {
			return InteractionResultHolder.pass(held);
		}

		if (level.isClientSide) {
			return InteractionResultHolder.success(held);
		}

		tryFire(player, held, player.getYRot(), player.getXRot());
		return InteractionResultHolder.consume(held);
	}

	public static void tryFire(Player player, ItemStack pistolStack, float yaw, float pitch) {
		if (player.level().isClientSide || !pistolStack.is(ModItems.HUNTER_PISTOL)) {
			return;
		}

		if (player.getCooldowns().isOnCooldown(pistolStack.getItem())) {
			return;
		}

		if (getAmmo(pistolStack) <= 0) {
			player.level().playSound(
					null,
					player.blockPosition(),
					SoundEvents.DISPENSER_FAIL,
					SoundSource.PLAYERS,
					0.6F,
					1.2F
			);
			return;
		}

		setAmmo(pistolStack, getAmmo(pistolStack) - 1);
		player.getCooldowns().addCooldown(pistolStack.getItem(), COOLDOWN_TICKS);
		player.awardStat(Stats.ITEM_USED.get(pistolStack.getItem()));

		HitResult hitResult = findShotHit(player, yaw, pitch);
		if (hitResult instanceof EntityHitResult entityHit) {
			Entity target = entityHit.getEntity();
			if (target instanceof LivingEntity living && living.isAlive()) {
				DamageSource source = player.damageSources().playerAttack(player);
				living.hurt(source, PISTOL_DAMAGE);
			}
		}

		player.level().playSound(
				null,
				player.blockPosition(),
				SoundEvents.CROSSBOW_SHOOT,
				SoundSource.PLAYERS,
				0.9F,
				1.35F
		);
	}

	private static HitResult findShotHit(Player player, float yaw, float pitch) {
		Level level = player.level();
		Vec3 start = player.getEyePosition();
		Vec3 direction = getViewVector(yaw, pitch);
		Vec3 end = start.add(direction.scale(MAX_RANGE));
		double maxRangeSq = MAX_RANGE * MAX_RANGE;

		Predicate<Entity> canHit = entity ->
				!entity.isSpectator()
						&& entity.isPickable()
						&& entity != player
						&& (entity instanceof LivingEntity living && living.isAlive());

		EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
				level,
				player,
				start,
				end,
				player.getBoundingBox().expandTowards(direction.scale(MAX_RANGE)).inflate(1.0D),
				canHit
		);

		BlockHitResult blockHit = level.clip(new ClipContext(
				start,
				end,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				player
		));

		if (entityHit != null && entityHit.getLocation().distanceToSqr(start) <= maxRangeSq) {
			if (blockHit.getType() == HitResult.Type.MISS
					|| entityHit.getLocation().distanceToSqr(start) < blockHit.getLocation().distanceToSqr(start)) {
				return entityHit;
			}
		}

		return blockHit;
	}

	private static Vec3 getViewVector(float yaw, float pitch) {
		float pitchRad = pitch * Mth.DEG_TO_RAD;
		float yawRad = -yaw * Mth.DEG_TO_RAD;
		float cosPitch = Mth.cos(pitchRad);
		float sinPitch = Mth.sin(pitchRad);
		float cosYaw = Mth.cos(yawRad);
		float sinYaw = Mth.sin(yawRad);
		return new Vec3(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
	}

	public static int getAmmo(ItemStack stack) {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData == null) {
			return MAX_AMMO;
		}

		CompoundTag tag = customData.copyTag();
		if (!tag.contains(AMMO_TAG)) {
			return MAX_AMMO;
		}

		return tag.getInt(AMMO_TAG);
	}

	public static void setAmmo(ItemStack stack, int ammo) {
		int clamped = Math.max(0, Math.min(MAX_AMMO, ammo));
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(createAmmoTag(clamped)));
	}

	private static CompoundTag createAmmoTag(int ammo) {
		CompoundTag tag = new CompoundTag();
		tag.putInt(AMMO_TAG, ammo);
		return tag;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getAmmo(stack) < MAX_AMMO;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0F * getAmmo(stack) / MAX_AMMO);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xC8C8C8;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("item.bloodborne.hunter_pistol.desc"));
		tooltip.add(Component.translatable("item.bloodborne.hunter_pistol.ammo", getAmmo(stack), MAX_AMMO));
	}
}
