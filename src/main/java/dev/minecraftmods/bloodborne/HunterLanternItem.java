package dev.minecraftmods.bloodborne;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HunterLanternItem extends Item {

    public HunterLanternItem(Properties properties) {
        super(properties);
    }

    public void inventoryTick(ItemStack stack, Level level, LivingEntity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (!level.isClientSide) return;

        if (selected) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    220,
                    0,
                    true,
                    false
            ));
        }
    }
}
