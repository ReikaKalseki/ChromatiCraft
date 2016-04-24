package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.world.World;


/** Use this on TileEntities or blocks that are repairable in block form (NOT tools). */
public interface Repairable {

	public void repair(World world, int x, int y, int z, int tier);

}
