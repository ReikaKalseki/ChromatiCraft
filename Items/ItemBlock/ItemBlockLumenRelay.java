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
import Reika.ChromatiCraft.Block.Relay.BlockLumenRelay;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ItemBlockLumenRelay extends ItemBlock {

	public ItemBlockLumenRelay(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		for (int i = 0; i <= 16; i++) {
			li.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.getItemDamage() == 16) {
			li.add("Conducts all elements.");
		}
		else {
			CrystalElement e = CrystalElement.elements[is.getItemDamage()];
			li.add("Conducts "+e.displayName+".");
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		return ((BlockLumenRelay)field_150939_a).canPlaceOn(world, x, y, z, side) && super.onItemUse(stack, ep, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			((BlockLumenRelay)field_150939_a).setSide(world, x, y, z, side);
			field_150939_a.onBlockPlacedBy(world, x, y, z, ep, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

}
