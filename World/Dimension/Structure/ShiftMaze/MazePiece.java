/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazePiece extends StructurePiece {

	private boolean[] connections = new boolean[6];

	public final int size;

	public MazePiece(DimensionStructureGenerator g, int size) {
		super(g);
		this.size = size;
	}

	public MazePiece connect(ForgeDirection dir) {
		connections[dir.ordinal()] = true;
		return this;
	}

	public MazePiece disconnect(ForgeDirection dir) {
		connections[dir.ordinal()] = false;
		return this;
	}

	public static MazePiece omni(DimensionStructureGenerator g, int size) {
		MazePiece tp = new MazePiece(g, size);
		for (int i = 0; i < 6; i++)
			tp.connect(ForgeDirection.VALID_DIRECTIONS[i]);
		return tp;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= 3; j++) {
				for (int k = 0; k <= size; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					boolean c2 = k == 0 && j != 0 && i != 0 && i != size;
					boolean c3 = k == size && j != 0 && i != 0 && i != size;
					boolean c4 = i == 0 && j != 0 && k != 0 && k != size;
					boolean c5 = i == size && j != 0 && k != 0 && k != size;
					boolean tunnel2 = connections[2] && c2;
					boolean tunnel3 = connections[3] && c3;
					boolean tunnel4 = connections[4] && c4;
					boolean tunnel5 = connections[5] && c5;
					boolean tunnel = tunnel2 || tunnel3 || tunnel4 || tunnel5;
					boolean fill = !tunnel && (i == 0 || i == size || j == 0 || k == 0 || k == size);
					Block b = fill ? ChromaBlocks.STRUCTSHIELD.getBlockInstance() : Blocks.air;
					int meta = fill ? BlockType.STONE.metadata : 0;
					world.setBlock(dx, dy, dz, b, meta);
				}
			}
		}
	}

}
