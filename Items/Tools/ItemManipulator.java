/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemManipulator extends ItemChromaTool {

	public ItemManipulator(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		ChromaTiles t = ChromaTiles.getTile(world, x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (t == ChromaTiles.RIFT) {
			TileEntityRift te = (TileEntityRift)tile;
			te.setDirection(ForgeDirection.VALID_DIRECTIONS[s]);
		}
		return false;
	}

}
