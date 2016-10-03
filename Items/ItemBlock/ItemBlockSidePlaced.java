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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class ItemBlockSidePlaced extends ItemBlock {

	public ItemBlockSidePlaced(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		super.getSubItems(item, c, li);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		super.addInformation(is, ep, li, vb);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return ((SidedBlock)field_150939_a).canPlaceOn(world, x, y, z, side) && super.onItemUse(stack, ep, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		ChromaBlocks b = ChromaBlocks.getEntryByID(field_150939_a);
		return b.hasMultiValuedName() ? b.getMultiValuedName(is.getItemDamage()) : b.getBasicName();
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			((SidedBlock)field_150939_a).setSide(world, x, y, z, side);
			field_150939_a.onBlockPlacedBy(world, x, y, z, ep, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

}
