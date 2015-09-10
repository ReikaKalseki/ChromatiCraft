/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiToolDescription extends GuiDescription {

	public GuiToolDescription(EntityPlayer ep, ChromaResearch i) {
		super(ep, i, 256, 220);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		GL11.glPushMatrix();
		double s = 4;
		GL11.glScaled(s, s, 1);
		GL11.glTranslated(33, 3, 0);
		ArrayList<ItemStack> li = page.getItemStacks();
		if (!li.isEmpty()) {
			int idx = (int)((System.currentTimeMillis()/2000)%li.size());
			ItemStack is = li.get(idx);
			api.drawItemStack(itemRender, is, (int)(posX/s), (int)(posY/s));
		}
		GL11.glPopMatrix();
	}

	@Override
	protected int getMaxSubpage() {
		return page.getItem().getItemInstance() instanceof ItemWandBase ? 1 : 0;
	}

}
