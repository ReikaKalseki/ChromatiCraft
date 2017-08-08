/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.AntFarm;


import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class AntFarmEntrance extends DynamicStructurePiece {

	public final int startY;

	public AntFarmEntrance(DimensionStructureGenerator s, int y) {
		super(s);

		startY = y;
	}

	@Override
	public void generate(World world, int x, int z) {
		int p = 4;
		int top = world.getTopSolidOrLiquidBlock(x, z)+p;
		for (int y = startY; y <= top; y++) {
			int dy = top-y+p;
			int r = Math.min(6, 2+dy/3);
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					double d = ReikaMathLibrary.py3d(i, 0, k);
					if (d <= r+0.5) {
						int dx = x+i;
						int dz = z+k;
						BlockKey b = d <= r-0.75 ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						world.setBlock(dx, y, dz, b.blockID, b.metadata, 3);
					}
				}
			}
		}
	}

}
