/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
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
	}

	private void renderBlock(int posX, int posY) {
		int mod = 2000;
		int metas = page.getBlock().getNumberMetadatas();
		int meta = (int)((System.currentTimeMillis()/mod)%metas);
		this.drawBlockRender(posX, posY, page.getBlock().getBlockInstance(), meta);
	}
}
