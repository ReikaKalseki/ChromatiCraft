/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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


public class WaterFloor extends StructurePiece {

	public static final int HEIGHT = 8;
	public final int gridSize;

	private final Lock[][] flowGrid;

	public WaterFloor(WaterPuzzleGenerator s, int r, WaterPath path) {
		super(s);
		gridSize = r*2+1;
		flowGrid = new Lock[gridSize][gridSize];
	}

	public int getWidth() {
		int r = (gridSize-1)/2;
		return r*Lock.SIZE+(r-1)*2+6+3; //locks + gaps + center space + outer wall space
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int r = this.getWidth();
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int h = 0; h <= HEIGHT; h++) {
					if (i == 0 || k == 0 || i == r || k == r || h == 0 || h == HEIGHT)
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					else
						world.setBlock(x+i, y+h, z+k, Blocks.air);
				}
			}
		}
	}

	public boolean hasBeenSolved() {
		return false;
	}

}
