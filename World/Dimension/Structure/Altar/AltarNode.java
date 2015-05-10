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

public class AltarNode extends StructurePiece {

	public final boolean Yshunt;

	public AltarNode(DimensionStructureGenerator g) {
		this(g, false);
	}

	public AltarNode(DimensionStructureGenerator g, boolean y) {
		super(g);
		Yshunt = y;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int r = 5;
		int h = Yshunt ? (r+2)*2+3 : r+2;
		Block b1 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m1 = BlockType.STONE.metadata;
		for (int i = -r; i <= r; i++) {
			for (int j = 0; j < h; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					boolean edge = Math.abs(i) == r || Math.abs(k) == r || j == 0 || j == h-1;
					boolean dry = j > 0 && j < 6 || (Yshunt && j >= 11 && j < 16);
					boolean door = dry && ((Math.abs(i) <= 2 && Math.abs(k) == r) || (Math.abs(k) <= 2 && Math.abs(i) == r));
					Block b = edge && !door ? b1 : Blocks.air;
					int m = edge && !door ? m1 : 0;
					world.setBlock(dx, dy, dz, b, m);
				}
			}
		}
	}

}
