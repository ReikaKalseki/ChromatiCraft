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

public class ItemBlockMultiType extends ItemBlock {

	public ItemBlockMultiType(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		ChromaBlocks cb = ChromaBlocks.getEntryByID(field_150939_a);
		for (int i = 0; i < cb.getNumberMetadatas(); i++) {
			ItemStack is = new ItemStack(item, 1, i);
			li.add(is);
		}
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
	}

}
