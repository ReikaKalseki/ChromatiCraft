/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.world.World;

public class RuneAPI {

	public static boolean isRune(World world, int x, int y, int z, CrystalElementProxy e) {
		return world.getBlock(x, y, z).getClass().getSimpleName().equals("BlockCrystalRune") && world.getBlockMetadata(x, y, z) == e.ordinal();
	}

}
