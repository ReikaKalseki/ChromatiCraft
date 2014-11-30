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

public class GuiResourceItem extends GuiDescription {

	public GuiResourceItem(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

}
