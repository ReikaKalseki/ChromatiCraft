/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;


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

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_tool.png";
	}

}
