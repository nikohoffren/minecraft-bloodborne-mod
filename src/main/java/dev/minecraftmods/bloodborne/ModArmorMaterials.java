package dev.minecraftmods.bloodborne;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {

	public static final ResourceLocation HUNTER_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(BloodborneMod.MOD_ID, "hunter");

	private static final ResourceKey<ArmorMaterial> HUNTER_KEY = ResourceKey.create(
			Registries.ARMOR_MATERIAL,
			HUNTER_TEXTURE
	);

	public static final Holder<ArmorMaterial> HUNTER = Registry.registerForHolder(
			BuiltInRegistries.ARMOR_MATERIAL,
			HUNTER_KEY,
			new ArmorMaterial(
					Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
						map.put(ArmorItem.Type.BOOTS, 2);
						map.put(ArmorItem.Type.LEGGINGS, 5);
						map.put(ArmorItem.Type.CHESTPLATE, 6);
						map.put(ArmorItem.Type.HELMET, 2);
					}),
					15,
					SoundEvents.ARMOR_EQUIP_LEATHER,
					() -> Ingredient.of(net.minecraft.world.item.Items.LEATHER),
					List.of(new ArmorMaterial.Layer(HUNTER_TEXTURE)),
					0.0F,
					0.0F
			)
	);

	private ModArmorMaterials() {
	}
}
