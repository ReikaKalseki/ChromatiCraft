/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GlowingCliffsEdge extends BiomeGlowingCliffs {

	public GlowingCliffsEdge(int id) {
		super(id, true);

		biomeName = "Luminous Cliffs Shores";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMapColor(World world, int x, int z) {
		return 0x428AFF;
	}

}
