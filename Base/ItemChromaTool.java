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

import net.minecraft.creativetab.CreativeTabs;
import Reika.ChromatiCraft.ChromatiCraft;


public abstract class ItemChromaTool extends ItemChromaBasic {

	public ItemChromaTool(int id, int index) {
		super(id, index);
		maxStackSize = 1;
		this.setNoRepair();
	}

	@Override
	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

}
