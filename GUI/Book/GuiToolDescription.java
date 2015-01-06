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

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiToolDescription extends GuiDescription {

	public GuiToolDescription(EntityPlayer ep, ChromaResearch i) {
		super(ep, i, 256, 220);
	}

	@Override
	protected int getMaxSubpage() {
		return page.getItem().getItemInstance() instanceof ItemWandBase ? 1 : 0;
	}

}
