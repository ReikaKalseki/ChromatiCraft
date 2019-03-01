/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.TileEntityCrystalGlow;

public class ItemBlockCrystalGlow extends ItemBlockDyeTypes {

	public ItemBlockCrystalGlow(Block b) {
		super(b);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < Bases.baseList.length; k++) {
				li.add(new ItemStack(this, 1, i+16*k));
			}
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {

	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return ((BlockCrystalGlow)field_150939_a).canPlaceOn(world, x, y, z, side) && super.onItemUse(stack, ep, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			((BlockCrystalGlow)field_150939_a).setSide(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
			((TileEntityCrystalGlow)world.getTileEntity(x, y, z)).base = Bases.baseList[stack.getItemDamage()/16];
			field_150939_a.onBlockPlacedBy(world, x, y, z, ep, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

}
