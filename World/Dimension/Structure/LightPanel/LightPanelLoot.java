/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;

import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanelGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class LightPanelLoot extends StructurePiece {

	public static final int WIDTH = 10;
	public static final int HEIGHT = 6;

	public LightPanelLoot(LightPanelGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int maxh = WIDTH*2+1;
		for (int i = -WIDTH; i <= WIDTH; i++) {
			for (int k = -WIDTH; k <= WIDTH; k++) {
				boolean wall = Math.abs(i) == WIDTH || Math.abs(k) == WIDTH;
				int dx = x+i;
				int dz = z+k;
				int h = Math.max(0, WIDTH-(Math.abs(i)+Math.abs(k))/2-(Math.abs(i) <= 1 || Math.abs(k) <= 1 ? 6 : 5));
				for (int j = 0; j <= maxh; j++) {
					int dy = y+j;
					if (!wall && j > h && j < h+HEIGHT) {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
					else {
						int m = j > h && j < maxh && !wall ? BlockType.CLOAK.metadata : BlockType.STONE.metadata;
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
						if (j == 0) {
							parent.addBreakable(dx, dy, dz);
						}
					}
				}
			}
		}

		world.setBlock(x+WIDTH-2, y, z+WIDTH-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x+WIDTH-2, y, z-WIDTH+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		this.placeCore(x, y+WIDTH-4, z);

		for (int i = -2; i <= 2; i++) {
			for (int k = 1; k <= 3; k++) {
				world.setBlock(x-WIDTH, y+k, z+i, Blocks.air);
			}
		}
		world.setBlock(x-WIDTH, y+4, z+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x-WIDTH, y+4, z-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x-WIDTH, y+2, z-3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x-WIDTH, y+2, z+3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
	}

}
