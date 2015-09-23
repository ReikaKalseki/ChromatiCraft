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

/** Implement this to make the duplication wand unable to copy and paste your block.
 * Implementing this is strongly recommended for blocks that form multiblock structures. */
public interface UnCopyableBlock {

	/** Return true to disallow copying the block; air will be placed in its place. Args: Block metadata */
	public boolean disallowCopy(int meta);

}
