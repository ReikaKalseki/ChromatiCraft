/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Altar;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class SurfaceAccess extends StructurePiece {

	public final ForgeDirection direction;

	public SurfaceAccess(ForgeDirection dir) {
		direction = dir;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

}
