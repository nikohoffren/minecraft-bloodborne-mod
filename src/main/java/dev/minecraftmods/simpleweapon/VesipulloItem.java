package dev.minecraftmods.simpleweapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

public class VesipulloItem extends SwordItem {

    public VesipulloItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1.5F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        return super.mineBlock(stack, world, state, pos, entity);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof Player player) {
            // If the target died as a result of this hit, show an action-bar message to the player
            if (target.isDeadOrDying() || target.getHealth() <= 0.0F) {
                Component msg = Component.literal("Killed " + target.getName().getString());
                // true = action bar, false = chat
                player.displayClientMessage(msg, true);
            }
        }

        return result;
    }
}
