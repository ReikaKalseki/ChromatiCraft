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
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemExcavator extends ItemChromaTool {

	public ItemExcavator(int index) {
		super(index);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer ep) {
		World world = ep.worldObj;/*
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b2 = world.getBlock(dx, dy, dz);
			int meta2 = world.getBlockMetadata(dx, dy, dz);
			if (b == b2 && meta == meta2) {
				ProgressiveRecursiveBreaker.instance.addCoordinate(world, dx, dy, dz, 30);
			}
		}*/
		ProgressiveRecursiveBreaker.instance.addCoordinate(world, x, y, z, 12);
		return true;
	}

}
