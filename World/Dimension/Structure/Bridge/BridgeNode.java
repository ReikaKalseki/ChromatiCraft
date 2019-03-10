/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Bridge;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class BridgeNode extends StructurePiece {

	protected final boolean[] connections = new boolean[4];
	protected final int radius;

	public BridgeNode(BridgeGenerator s, int r) {
		super(s);
		radius = r;
	}

	public BridgeNode connect(ForgeDirection dir) {
		connections[dir.ordinal()-2] = true;
		return this;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = -radius; i <= radius; i++) {
			for (int k = -radius; k <= radius; k++) {
				world.setBlock(x+i, y, z+k, Blocks.planks);
				if (Math.abs(i) == radius || Math.abs(k) == radius) {
					world.setBlock(x+i, y+1, z+k, Blocks.fence);
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			if (connections[i]) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				for (int k = -1; k <= 1; k++) {
					int dx = x+dir.offsetX*radius+k*dir.offsetZ;
					int dz = z+dir.offsetZ*radius+k*dir.offsetX;
					world.setBlock(dx, y, dz, ChromaBlocks.BRIDGE.getBlockInstance());
					world.setBlock(dx, y+1, dz, STRUCTURE_AIR);
				}
			}
		}
		for (int i = -radius; i <= radius; i += radius*2) {
			for (int k = -radius; k <= radius; k += radius*2) {
				boolean flag = true;
				for (int h = 2; flag || h >= -1; h--) {
					BlockKey bk = world.getBlock(x, y+h, z);
					flag = bk == null || bk.blockID == Blocks.water || bk.blockID == STRUCTURE_AIR;
					if (flag || h >= -1)
						world.setBlock(x+i, y+h, z+k, Blocks.planks);
				}
				world.setBlock(x+i, y+3, z+k, Blocks.torch, 5);
			}
		}
	}

}
