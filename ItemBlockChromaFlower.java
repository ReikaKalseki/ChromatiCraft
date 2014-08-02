/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockChromaFlower extends ItemBlock {

	public ItemBlockChromaFlower(int par1) {
		super(par1);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(int id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 1; i++)
			par3List.add(new ItemStack(id, 1, i));
	}

}
