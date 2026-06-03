package dev.minecraftmods.bloodborne;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class BloodVialItem extends Item {

	/** Restores 4 hearts (8 HP). */
	public static final float HEAL_AMOUNT = 8.0F;

	/** 2 seconds between uses. */
	public static final int COOLDOWN_TICKS = 40;

	/** Drink animation length (same as potion). */
	public static final int USE_DURATION_TICKS = 32;

	public BloodVialItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.fail(stack);
		}

		if (player.getHealth() >= player.getMaxHealth()) {
			return InteractionResultHolder.fail(stack);
		}

		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			float missing = player.getMaxHealth() - player.getHealth();
			float heal = Math.min(HEAL_AMOUNT, missing);

			if (heal > 0.0F) {
				player.heal(heal);
			}

			player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
			player.awardStat(Stats.ITEM_USED.get(this));
			level.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.GENERIC_DRINK,
					player.getSoundSource(),
					0.5F,
					level.random.nextFloat() * 0.1F + 0.9F
			);
		}

		if (!entity.hasInfiniteMaterials()) {
			stack.consume(1, entity);
		}

		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return USE_DURATION_TICKS;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.bloodborne.blood_vial.desc"));
	}
}
