package dev.minecraftmods.bloodborne.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class HudLabelRenderer {

	private static final int PADDING = 2;
	private static final int ICON_SIZE = 16;
	private static final int ICON_TEXT_GAP = 4;
	private static final int BACKGROUND_COLOR = 0xC0000000;

	private HudLabelRenderer() {
	}

	public static void drawBoxedLabelLeft(GuiGraphics graphics, int x, int y, Component label, int textColor) {
		Minecraft client = Minecraft.getInstance();
		int labelWidth = client.font.width(label);
		int textY = y;

		graphics.fill(
				x - PADDING,
				textY - PADDING,
				x + labelWidth + PADDING,
				textY + client.font.lineHeight + PADDING,
				BACKGROUND_COLOR
		);
		graphics.drawString(client.font, label, x, textY, textColor, false);
	}

	public static void drawBoxedLabel(GuiGraphics graphics, int centerX, int y, Component label, int textColor) {
		Minecraft client = Minecraft.getInstance();
		int labelWidth = client.font.width(label);
		int textX = centerX - labelWidth / 2;
		int textY = y;

		graphics.fill(
				textX - PADDING,
				textY - PADDING,
				textX + labelWidth + PADDING,
				textY + client.font.lineHeight + PADDING,
				BACKGROUND_COLOR
		);
		graphics.drawString(client.font, label, textX, textY, textColor, false);
	}

	/** Item icon (16×16) with label to the right, same style as the offhand quick-slot readout. */
	public static void drawBoxedItemWithLabel(
			GuiGraphics graphics,
			int x,
			int y,
			ItemStack iconStack,
			Component label,
			int textColor
	) {
		Minecraft client = Minecraft.getInstance();
		int labelWidth = client.font.width(label);
		int textX = x + ICON_SIZE + ICON_TEXT_GAP;
		int textY = y + (ICON_SIZE - client.font.lineHeight) / 2;

		int boxRight = textX + labelWidth + PADDING;
		int boxBottom = y + ICON_SIZE + PADDING;

		graphics.fill(x - PADDING, y - PADDING, boxRight, boxBottom, BACKGROUND_COLOR);
		graphics.renderItem(iconStack, x, y);
		graphics.drawString(client.font, label, textX, textY, textColor, false);
	}

	/** Centers an item+label panel horizontally on centerX. */
	public static void drawBoxedItemWithLabelCentered(
			GuiGraphics graphics,
			int centerX,
			int y,
			ItemStack iconStack,
			Component label,
			int textColor
	) {
		Minecraft client = Minecraft.getInstance();
		int labelWidth = client.font.width(label);
		int panelWidth = ICON_SIZE + ICON_TEXT_GAP + labelWidth;
		int x = centerX - panelWidth / 2;
		drawBoxedItemWithLabel(graphics, x, y, iconStack, label, textColor);
	}

	/** Item icon with numeric count only (no prefix text). */
	public static void drawBoxedItemWithCount(
			GuiGraphics graphics,
			int x,
			int y,
			ItemStack iconStack,
			int count,
			int textColor
	) {
		drawBoxedItemWithLabel(graphics, x, y, iconStack, Component.literal(String.valueOf(count)), textColor);
	}

	public static void drawBoxedItemWithCountCentered(
			GuiGraphics graphics,
			int centerX,
			int y,
			ItemStack iconStack,
			int count,
			int textColor
	) {
		Minecraft client = Minecraft.getInstance();
		String countText = String.valueOf(count);
		int textWidth = client.font.width(countText);
		int panelWidth = ICON_SIZE + ICON_TEXT_GAP + textWidth;
		int x = centerX - panelWidth / 2;
		drawBoxedItemWithCount(graphics, x, y, iconStack, count, textColor);
	}
}
