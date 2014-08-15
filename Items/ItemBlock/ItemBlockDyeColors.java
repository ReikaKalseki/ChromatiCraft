/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import Reika.ChromatiCraft.Registry.ChromaBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDyeColors extends ItemBlock {

	public ItemBlockDyeColors(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 16; i++)
			par3List.add(new ItemStack(id, 1, i));
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.getEntryByID(Block.getBlockFromItem(is.getItem())).getMultiValuedName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}


}