/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.TDMaze;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class TunnelPiece extends StructurePiece {

	private boolean[] connections = new boolean[6];

	public final int size;

	public TunnelPiece(int size) {
		this.size = size;
	}

	public TunnelPiece connect(ForgeDirection dir) {
		connections[dir.ordinal()] = true;
		return this;
	}

	public TunnelPiece disconnect(ForgeDirection dir) {
		connections[dir.ordinal()] = false;
		return this;
	}

	public static TunnelPiece omni(int size) {
		TunnelPiece tp = new TunnelPiece(size);
		for (int i = 0; i < 6; i++)
			tp.connect(ForgeDirection.VALID_DIRECTIONS[i]);
		return tp;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				for (int k = 0; k <= size; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					boolean tunnel0 = connections[0] && j == 0 && i != 0 && i != size && k != 0 && k != size;
					boolean tunnel1 = connections[1] && j == size && i != 0 && i != size && k != 0 && k != size;
					boolean tunnel2 = connections[2] && k == 0 && j != 0 && j != size && i != 0 && i != size;
					boolean tunnel3 = connections[3] && k == size && j != 0 && j != size && i != 0 && i != size;
					boolean tunnel4 = connections[4] && i == 0 && j != 0 && j != size && k != 0 && k != size;
					boolean tunnel5 = connections[5] && i == size && j != 0 && j != size && k != 0 && k != size;
					boolean tunnel = tunnel0 || tunnel1 || tunnel2 || tunnel3 || tunnel4 || tunnel5;
					boolean fill = !tunnel && (i == 0 || i == size || j == 0 || j == size || k == 0 || k == size);
					Block b = fill ? ChromaBlocks.STRUCTSHIELD.getBlockInstance() : Blocks.air;
					int meta = fill ? BlockType.STONE.metadata : 0;
					world.setBlock(dx, dy, dz, b, meta);
				}
			}
		}
	}

}
