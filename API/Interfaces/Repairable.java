/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.world.World;


/** Use this on TileEntities or blocks that are repairable in block form (NOT tools). */
public interface Repairable {

	public void repair(World world, int x, int y, int z, int tier);

}
