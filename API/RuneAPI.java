/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.world.World;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

public class RuneAPI {

	public static final RuneAPI instance = new RuneAPI();

	private RuneAPI() {
		ChromatiAPI.runes = this;
	}

	public boolean isRune(World world, int x, int y, int z, CrystalElementProxy e) {
		return world.getBlock(x, y, z).getClass().getSimpleName().equals("BlockCrystalRune") && world.getBlockMetadata(x, y, z) == e.ordinal();
	}

}
