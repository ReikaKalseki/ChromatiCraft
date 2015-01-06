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
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiAbilityDesc extends GuiDescription {

	public GuiAbilityDesc(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

}
