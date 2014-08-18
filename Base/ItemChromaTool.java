/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import Reika.ChromatiCraft.ChromatiCraft;

import net.minecraft.creativetab.CreativeTabs;


public abstract class ItemChromaTool extends ItemChromaBasic {

	public ItemChromaTool(int index) {
		super(index);
		maxStackSize = 1;
		this.setNoRepair();
	}

	@Override
	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

}
