package dev.minecraftmods.bloodborne;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public final class ModItems {
	public static final Tier WEAPON_MATERIAL = Tiers.IRON;

	/** Saw Cleaver — heavy, high damage. */
	public static final int SAW_CLEAVER_DAMAGE = 8;
	public static final float SAW_CLEAVER_SPEED = -2.8F;

	/** Hunter Axe — balanced trick weapon. */
	public static final int HUNTER_AXE_DAMAGE = 5;
	public static final float HUNTER_AXE_SPEED = -2.4F;

	/** Threaded Cane — fast, lighter hits. */
	public static final int THREADED_CANE_DAMAGE = 4;
	public static final float THREADED_CANE_SPEED = -1.8F;

	public static final Item SAW_CLEAVER = register(
			"saw_cleaver",
			new BloodborneWeaponItem(
					WEAPON_MATERIAL,
					new Item.Properties().attributes(
							SwordItem.createAttributes(WEAPON_MATERIAL, SAW_CLEAVER_DAMAGE, SAW_CLEAVER_SPEED)
					)
			)
	);

	public static final Item HUNTER_AXE = register(
			"hunter_axe",
			new BloodborneWeaponItem(
					WEAPON_MATERIAL,
					new Item.Properties().attributes(
							SwordItem.createAttributes(WEAPON_MATERIAL, HUNTER_AXE_DAMAGE, HUNTER_AXE_SPEED)
					)
			)
	);

	public static final Item THREADED_CANE = register(
			"threaded_cane",
			new BloodborneWeaponItem(
					WEAPON_MATERIAL,
					new Item.Properties().attributes(
							SwordItem.createAttributes(WEAPON_MATERIAL, THREADED_CANE_DAMAGE, THREADED_CANE_SPEED)
					)
			)
	);

	public static final Item HUNTER_LANTERN = register(
			"hunter_lantern",
			new HunterLanternItem(new Item.Properties().stacksTo(1))
	);

	public static final Item HUNTER_PISTOL = register(
			"hunter_pistol",
			new HunterPistolItem(new Item.Properties().stacksTo(1))
	);

	public static final Item HUNTER_HAT = register(
			"hunter_hat",
			new ArmorItem(ModArmorMaterials.HUNTER, ArmorItem.Type.HELMET, new Item.Properties())
	);

	public static final Item HUNTER_GARB = register(
			"hunter_garb",
			new ArmorItem(ModArmorMaterials.HUNTER, ArmorItem.Type.CHESTPLATE, new Item.Properties())
	);

	public static final Item HUNTER_TROUSERS = register(
			"hunter_trousers",
			new ArmorItem(ModArmorMaterials.HUNTER, ArmorItem.Type.LEGGINGS, new Item.Properties())
	);

	public static final Item HUNTER_BOOTS = register(
			"hunter_boots",
			new ArmorItem(ModArmorMaterials.HUNTER, ArmorItem.Type.BOOTS, new Item.Properties())
	);

	private static Item register(String name, Item item) {
		return Registry.register(
				BuiltInRegistries.ITEM,
				ResourceLocation.fromNamespaceAndPath(BloodborneMod.MOD_ID, name),
				item
		);
	}

	public static void initialize() {
		ModCreativeTabs.initialize();
	}
}
