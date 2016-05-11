/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class ItemBlockChromaFlower extends ItemBlock {

	public ItemBlockChromaFlower(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ChromaBlocks.getEntryByID(field_150939_a).getNumberMetadatas(); i++)
			par3List.add(new ItemStack(id, 1, i));
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
			if (field_150939_a.canPlaceBlockAt(world, x, y, z)) {
				return true;
			}
			else {
				world.setBlock(x, y, z, Blocks.air);
			}
		}
		return false;
	}

}
