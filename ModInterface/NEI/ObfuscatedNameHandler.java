/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import java.awt.Dimension;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import codechicken.lib.gui.GuiDraw;
import codechicken.lib.gui.GuiDraw.ITooltipLineHandler;

public final class ObfuscatedNameHandler implements ITooltipLineHandler {

	private final Dimension dim;
	private final FontRenderer font;
	private final String text;

	public ObfuscatedNameHandler(ItemStack is) {
		text = is.getDisplayName();
		font = is.getItem().getFontRenderer(is);
		dim = new Dimension(font.getStringWidth(text), text.endsWith(GuiDraw.TOOLTIP_LINESPACE) ? 12 : 10);
	}

	private ObfuscatedNameHandler(String name, ItemStack is) {
		text = name;
		font = is.getItem().getFontRenderer(is);
		dim = new Dimension(font.getStringWidth(text), text.endsWith(GuiDraw.TOOLTIP_LINESPACE) ? 12 : 10);
	}

	@Override
	public Dimension getSize() {
		return dim;
	}

	@Override
	public void draw(int x, int y) {
		font.drawStringWithShadow(text, x, y, -1);
	}

	public static String registerName(String name, ItemStack is) {
		ObfuscatedNameHandler handler = new ObfuscatedNameHandler(name, is);
		int id = GuiDraw.getTipLineId(handler);
		return GuiDraw.TOOLTIP_HANDLER+String.valueOf(id);
	}

}
