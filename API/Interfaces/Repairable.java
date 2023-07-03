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

import net.minecraft.world.World;


/** Use this on TileEntities or blocks that are repairable in block form (NOT tools). */
public interface Repairable {

	public void repair(World world, int x, int y, int z, int tier);

	/** A general description of what repairing means for this block, eg "repairs gearbox damage" or "Removes debris from smelter output tray". */
	public String getDescription();

}
