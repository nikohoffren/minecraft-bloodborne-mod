package dev.minecraftmods.simpleweapon;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public final class ModItems {
	/** Material for durability, mining level, enchantability, and repair (same role as picking iron/diamond). */
	public static final Tier MATERIAL = Tiers.IRON;

	/**
	 * Sword damage modifier passed to {@link SwordItem#createAttributes}.
	 * Shown attack damage is about {@code 1 + attackDamageModifier + MATERIAL.getAttackDamageBonus()}.
	 * Iron (+2): modifier 997 gives ~1000. Diamond (+3): use 996 for ~1000.
	 */
	public static final int ATTACK_DAMAGE_MODIFIER = 997;

	/** Attack speed modifier (vanilla swords use -2.4). */
	public static final float ATTACK_SPEED = -2.4F;

	public static final Item VESIPULLO = register(
			"vesipullo",
			new VesipulloItem(
					MATERIAL,
					new Item.Properties().attributes(
						SwordItem.createAttributes(MATERIAL, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED)
					)
			)
	);

	public static final Item HUNTER_LANTERN = register(
			"hunter_lantern",
			new HunterLanternItem(
					new Item.Properties().stacksTo(1)
			)
	);

	private static Item register(String name, Item item) {
		return Registry.register(
				BuiltInRegistries.ITEM,
				ResourceLocation.fromNamespaceAndPath(SimpleWeaponMod.MOD_ID, name),
				item
		);
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
				.register(entries -> {
					entries.accept(VESIPULLO);
					entries.accept(HUNTER_LANTERN);
				});
	}
}
