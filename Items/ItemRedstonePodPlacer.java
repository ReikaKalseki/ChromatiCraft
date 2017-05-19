/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockSidePlaced;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public class ItemRedstonePodPlacer extends ItemBlockSidePlaced {

	public ItemRedstonePodPlacer(Block b) {
		super(b);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		boolean flag = super.placeBlockAt(stack, ep, world, x, y, z, side, a, b, c, metadata);
		if (flag) {
			ItemLinkedTilePlacer.linkTile(world, x, y, z, stack, ep);
		}
		return flag;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			WorldLocation loc = ItemLinkedTilePlacer.getRiftLocation(is);
			if (loc != null)
				li.add("Linking to "+loc.toString());
		}
	}

}
