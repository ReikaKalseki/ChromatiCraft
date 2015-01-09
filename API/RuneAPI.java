package Reika.ChromatiCraft.API;

import net.minecraft.world.World;

public class RuneAPI {

	public static boolean isRune(World world, int x, int y, int z, CrystalElementProxy e) {
		return world.getBlock(x, y, z).getClass().getSimpleName().equals("BlockCrystalRune") && world.getBlockMetadata(x, y, z) == e.ordinal();
	}

}
