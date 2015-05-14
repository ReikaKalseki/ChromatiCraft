/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class LockRoomConnector extends StructurePiece {

	private final int[] length;
	private boolean window;

	public LockRoomConnector(DimensionStructureGenerator s, int[] len) {
		super(s);
		length = len;
	}

	public LockRoomConnector(DimensionStructureGenerator s, int lenn, int lens, int lenw, int lene) {
		this(s, new int[]{lenn, lens, lenw, lene});
	}

	public LockRoomConnector setLength(ForgeDirection dir, int len) {
		length[dir.ordinal()-2] = len;
		return this;
	}

	public LockRoomConnector setWindowed() {
		window = true;
		return this;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockType.STONE.metadata;
		int ml = BlockType.LIGHT.metadata;
		int mg = BlockType.GLASS.metadata;
		int r = 2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				world.setBlock(dx, y, dz, b, ms);
				world.setBlock(dx, y+5, dz, b, ms);

				for (int h = 1; h <= 4; h++) {
					world.setBlock(dx, y+h, dz, Blocks.air);
				}
			}
		}

		world.setBlock(x-2, y, z-2, b, ml);
		world.setBlock(x+2, y, z-2, b, ml);
		world.setBlock(x-2, y, z+2, b, ml);
		world.setBlock(x+2, y, z+2, b, ml);

		for (int k = 2; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int len = length[k-2];
			if (len > 0) {
				for (int i = 3; i < len+3; i++) {
					int dx = x+dir.offsetX*i;
					int dz = z+dir.offsetZ*i;
					for (int w = -2; w <= 2; w++) {
						int dx2 = dx+w*left.offsetX;
						int dz2 = dz+w*left.offsetZ;
						world.setBlock(dx2, y, dz2, b, ms);
						world.setBlock(dx2, y+4, dz2, b, ms);
					}
					for (int h = 1; h <= 3; h++) {
						world.setBlock(dx+2*left.offsetX, y+h, dz+2*left.offsetZ, b, ms);
						world.setBlock(dx-2*left.offsetX, y+h, dz-2*left.offsetZ, b, ms);

						world.setBlock(dx, y+h, dz, Blocks.air);
						world.setBlock(dx+1*left.offsetX, y+h, dz+1*left.offsetZ, Blocks.air);
						world.setBlock(dx-1*left.offsetX, y+h, dz-1*left.offsetZ, Blocks.air);
					}
				}
			}
			else {
				int dx = x+dir.offsetX*3;
				int dz = z+dir.offsetZ*3;
				for (int w = -2; w <= 2; w++) {
					int dx2 = dx+w*left.offsetX;
					int dz2 = dz+w*left.offsetZ;
					for (int h = 0; h <= 4; h++)
						world.setBlock(dx2, y+h, dz2, b, ms);
				}
			}
		}

		boolean window = false;
		if (window) {
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					int dx = x+i;
					int dz = z+k;
					world.setBlock(dx, y+5, dz, b, mg);
				}
			}
		}
	}

}
