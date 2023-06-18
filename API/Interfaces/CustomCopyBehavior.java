/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.block.Block;

/** Implement this to have special copying behavior on your block.
 * Implementing this is strongly recommended for blocks that form multiblock structures. */
public interface CustomCopyBehavior {

	/** Return false to disallow copying the block; air will be placed in its place. Args: Block metadata */
	public boolean allowCopy(int meta);

	/** Return a different block type than the one in world */
	public Block getBlock(int meta);

	/** Return a different metadata than the one in world */
	public int getMeta(int meta);

}
