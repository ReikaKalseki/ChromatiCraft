/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Laser;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;


public class LaserEntrance extends DynamicStructurePiece<LaserPuzzleGenerator> {

	public LaserEntrance(LaserPuzzleGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int r = 5;
		int ty = world.getTopSolidOrLiquidBlock(x, z);
		while (!world.getBlock(x, ty, z).isAir(world, x, ty, z))
			ty++;

		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				for (int j = 2; j <= 7; j++) {
					int dy = parent.getPosY()+j;
					boolean wall = Math.abs(i) == r || Math.abs(k) == r || j == 2 || j == 7;
					boolean wall2 = Math.abs(i) == Math.abs(k) && Math.abs(i) == r/2;
					wall = wall || wall2;
					wall = wall && (i != r || j > 6 || j == 2 || Math.abs(k) > (j == 6 ? 1 : 2));
					if (wall) {
						if (wall2 && j == 4)
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);
						else
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
					}
					else {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}
		r = 2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				for (int dy = parent.getPosY()+7; dy <= ty; dy++) {
					boolean wall = Math.abs(i) == r || Math.abs(k) == r;
					if (wall) {
						if (dy%12 == 0) {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);
						}
						else {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}
					}
					else {
						if (dy == parent.getPosY()+7 || dy == parent.getPosY()+12) {
							world.setBlock(dx, dy, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta(), 3);
						}
						else {
							world.setBlock(dx, dy, dz, Blocks.air);
						}
					}
				}
			}
		}

		for (int r2 = 3; r2 <= 6; r2++) {
			int m = r2 <= 3 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
			int dy = r2 <= 4 ? ty : ty-1;
			for (int i = -r2; i <= r2; i++) {
				world.setBlock(x+i, dy, z-r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x+i, dy, z+r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x-r2, dy, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x+r2, dy, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			}
		}
	}

}
