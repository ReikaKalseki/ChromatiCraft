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
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiCraftableDesc extends GuiDescription {

	public GuiCraftableDesc(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	protected final int getMaxSubpage() {
		return 0;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		if (page.getParent() == ChromaResearch.BLOCKS) {
			this.renderBlock(posX, posY);
		}
		if (page.getParent() == ChromaResearch.RESOURCEDESC) {
			GL11.glPushMatrix();
			double s = 4;
			GL11.glScaled(s, s, 1);
			GL11.glTranslated(33, 3, 0);
			ItemStack is = page.getTabIcon();
			ArrayList<ItemStack> li = page.getItemStacks();
			if (li != null && !li.isEmpty()) {
				int tick = (int)((System.currentTimeMillis()/1000)%li.size());
				is = li.get(tick);
			}
			api.drawItemStack(itemRender, is, (int)(posX/s), (int)(posY/s));
			GL11.glPopMatrix();
		}
	}

	private void renderBlock(int posX, int posY) {
		int mod = 2000;
		int metas = page.getBlock().getNumberMetadatas();
		int meta = (int)((System.currentTimeMillis()/mod)%metas);
		this.drawBlockRender(posX, posY, page.getBlock().getBlockInstance(), meta);
	}
}
