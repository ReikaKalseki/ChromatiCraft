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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Block.BlockPath;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class ItemBlockPath extends ItemBlock {

	public ItemBlockPath(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < BlockPath.PathType.list.length; i++)
			par3List.add(new ItemStack(id, 1, i));
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.PATH.getMultiValuedName(is.getItemDamage());
	}

}
