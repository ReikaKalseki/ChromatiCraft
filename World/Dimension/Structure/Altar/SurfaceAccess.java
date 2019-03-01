/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Altar;


import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;

public class SurfaceAccess extends DynamicStructurePiece {

	public final ForgeDirection direction;

	public SurfaceAccess(DimensionStructureGenerator g, ForgeDirection dir) {
		super(g);
		direction = dir;
	}

	@Override
	public void generate(World world, int x, int z) {

	}

}
