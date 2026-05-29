package dev.minecraftmods.bloodborne;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.List;

public final class ModCreativeTabs {

	private static final List<Item> VANILLA_SWORDS = List.of(
			Items.WOODEN_SWORD,
			Items.STONE_SWORD,
			Items.IRON_SWORD,
			Items.GOLDEN_SWORD,
			Items.DIAMOND_SWORD,
			Items.NETHERITE_SWORD
	);

	private static final List<Item> VANILLA_ARMOR = List.of(
			Items.LEATHER_HELMET,
			Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS,
			Items.LEATHER_BOOTS,
			Items.CHAINMAIL_HELMET,
			Items.CHAINMAIL_CHESTPLATE,
			Items.CHAINMAIL_LEGGINGS,
			Items.CHAINMAIL_BOOTS,
			Items.IRON_HELMET,
			Items.IRON_CHESTPLATE,
			Items.IRON_LEGGINGS,
			Items.IRON_BOOTS,
			Items.GOLDEN_HELMET,
			Items.GOLDEN_CHESTPLATE,
			Items.GOLDEN_LEGGINGS,
			Items.GOLDEN_BOOTS,
			Items.DIAMOND_HELMET,
			Items.DIAMOND_CHESTPLATE,
			Items.DIAMOND_LEGGINGS,
			Items.DIAMOND_BOOTS,
			Items.NETHERITE_HELMET,
			Items.NETHERITE_CHESTPLATE,
			Items.NETHERITE_LEGGINGS,
			Items.NETHERITE_BOOTS
	);

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
			FabricItemGroupEntries fabricEntries = (FabricItemGroupEntries) entries;
			removeItems(fabricEntries, VANILLA_SWORDS);
			removeItems(fabricEntries, VANILLA_ARMOR);

			entries.accept(ModItems.SAW_CLEAVER);
			entries.accept(ModItems.HUNTER_AXE);
			entries.accept(ModItems.THREADED_CANE);
			entries.accept(ModItems.HUNTER_LANTERN);
			entries.accept(ModItems.HUNTER_HAT);
			entries.accept(ModItems.HUNTER_GARB);
			entries.accept(ModItems.HUNTER_TROUSERS);
			entries.accept(ModItems.HUNTER_BOOTS);
		});
	}

	private static void removeItems(FabricItemGroupEntries entries, List<Item> items) {
		for (Item item : items) {
			entries.getDisplayStacks().removeIf(stack -> stack.is(item));
			entries.getSearchTabStacks().removeIf(stack -> stack.is(item));
		}
	}

	private ModCreativeTabs() {
	}
}
