/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Implement this to make the Mineral Extractor care about and mine your block. */
public interface MinerBlock {

	/** Whether or not to target the block. Args: Block metadata */
	public boolean isMineable(int meta);

	/** What items the block will provide when harvested. Normally returns getDrops(world, x, y, z, meta, 0). */
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune);

}
