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


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class AltarCenter extends StructurePiece {

	public AltarCenter(DimensionStructureGenerator g) {
		super(g);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

		int r = 16;
		Block b1 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m1 = BlockType.STONE.metadata;
		for (int h = 0; h < 12; h++) {
			int r2 = h < 2 ? r : h < 9 ? r+2-h : r-7-2*h;
			double d = h < 9 ? 1.75 : 1.75-(h-9)/4D;
			Block b = h == 0 ? b1 : Blocks.air;
			int m = h == 0 ? m1 : 0;
			for (int i = -r2; i <= r2; i++) {
				for (int k = -r2; k <= r2; k++) {
					if (i*i+k*k <= (r+d)*(r+d)) {
						int dx = x+i;
						int dz = z+k;
						int dy = y+h;
						world.setBlock(dx, dy, dz, b, m);
						world.setBlock(dx, dy+1, dz, b1, m1);
					}
				}
			}
		}

	}


}
