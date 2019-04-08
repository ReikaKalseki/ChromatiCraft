/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import net.minecraft.init.Blocks;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class WaterLoot extends StructurePiece<WaterPuzzleGenerator> {

	private final boolean hasCore;

	public WaterLoot(WaterPuzzleGenerator s, boolean core) {
		super(s);
		hasCore = core;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int h = 1; h <= 5; h++) {
			int r = 2;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					int dy = y-h;
					if (Math.abs(i) == r || Math.abs(k) == r || h >= 2) {
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						parent.addBreakable(dx, dy, dz);
					}
					else {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}

		if (hasCore) {
			this.placeCore(x, y, z);
		}
		else {
			world.setBlock(x, y-1, z, Blocks.double_stone_slab);
			world.setBlock(x, y, z, Blocks.cake);
		}
	}

}
