/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.TDMaze;


import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class TDMazeEntrance extends DynamicStructurePiece {

	public TDMazeEntrance(DimensionStructureGenerator g) {
		super(g);
	}

	@Override
	public void generate(World world, int x, int z) {
		int height = this.calculateHeight(world, x, z);
		int posY = parent.getPosY();
		int y = posY+height;
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ml = BlockType.LIGHT.metadata;
		int ms = BlockType.STONE.metadata;
		Block ps = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int dy = posY; dy <= y; dy++) {
			for (int n = -2; n <= 2; n++) {
				world.setBlock(x-2, dy, z+n, sh, ms, 3);
				world.setBlock(x+2, dy, z+n, sh, ms, 3);
				world.setBlock(x+n, dy, z+2, sh, ms, 3);
				world.setBlock(x+n, dy, z-2, sh, ms, 3);
				for (int a = -1; a <= 1; a++) {
					for (int b = -1; b <= 1; b++) {
						world.setBlock(x+a, dy, z+b, Blocks.air);
					}
				}
			}
		}
		x -= 6;
		z -= 6;
		int r = 8;
		for (int a = -r; a <= r; a++) {
			for (int b = -r; b <= r; b++) {
				int dx = x+a+6;
				int dz = z+b+6;
				if (Math.abs(a) > 1 || Math.abs(b) > 1) {
					world.setBlock(dx, y-1, dz, sh, ms, 3);
					for (int h = y-2; h > posY; h--) {
						if (world.getBlock(dx, h, dz) == Blocks.air) {
							world.setBlock(dx, h, dz, Blocks.dirt);
						}
					}
				}
			}
		}
		for (int a = 0; a <= 12; a++) {
			for (int b = 0; b <= 5; b++) {
				for (int c = 0; c <= 12; c++) {
					world.setBlock(x+a, y+b, z+c, Blocks.air);
				}
			}
		}
		world.setBlock(x+0, y+0, z+0, ps, 2, 3);
		world.setBlock(x+0, y+0, z+4, ps, 2, 3);
		world.setBlock(x+0, y+0, z+8, ps, 2, 3);
		world.setBlock(x+0, y+0, z+12, ps, 2, 3);
		world.setBlock(x+0, y+1, z+0, sh, ml, 3);
		world.setBlock(x+0, y+1, z+4, ps, 2, 3);
		world.setBlock(x+0, y+1, z+8, ps, 2, 3);
		world.setBlock(x+0, y+1, z+12, sh, ml, 3);
		world.setBlock(x+0, y+2, z+4, ps, 2, 3);
		world.setBlock(x+0, y+2, z+8, ps, 2, 3);
		world.setBlock(x+0, y+3, z+4, ps, 7, 3);
		world.setBlock(x+0, y+3, z+5, ps, 1, 3);
		world.setBlock(x+0, y+3, z+6, ps, 1, 3);
		world.setBlock(x+0, y+3, z+7, ps, 1, 3);
		world.setBlock(x+0, y+3, z+8, ps, 7, 3);
		world.setBlock(x+1, y+0, z+4, sh, ms, 3);
		world.setBlock(x+1, y+0, z+8, sh, ms, 3);
		world.setBlock(x+1, y+1, z+4, sh, ms, 3);
		world.setBlock(x+1, y+1, z+8, sh, ms, 3);
		world.setBlock(x+1, y+2, z+4, sh, ms, 3);
		world.setBlock(x+1, y+2, z+8, sh, ms, 3);
		world.setBlock(x+1, y+3, z+4, ps, 1, 3);
		world.setBlock(x+1, y+3, z+5, sh, ms, 3);
		world.setBlock(x+1, y+3, z+6, sh, ms, 3);
		world.setBlock(x+1, y+3, z+7, sh, ms, 3);
		world.setBlock(x+1, y+3, z+8, ps, 1, 3);
		world.setBlock(x+2, y+0, z+4, sh, ms, 3);
		world.setBlock(x+2, y+0, z+8, sh, ms, 3);
		world.setBlock(x+2, y+1, z+4, sh, ms, 3);
		world.setBlock(x+2, y+1, z+8, sh, ms, 3);
		world.setBlock(x+2, y+2, z+4, sh, ms, 3);
		world.setBlock(x+2, y+2, z+8, sh, ms, 3);
		world.setBlock(x+2, y+3, z+4, ps, 1, 3);
		world.setBlock(x+2, y+3, z+5, sh, ms, 3);
		world.setBlock(x+2, y+3, z+6, sh, ml, 3);
		world.setBlock(x+2, y+3, z+7, sh, ms, 3);
		world.setBlock(x+2, y+3, z+8, ps, 1, 3);
		world.setBlock(x+3, y+0, z+4, sh, ms, 3);
		world.setBlock(x+3, y+0, z+8, sh, ms, 3);
		world.setBlock(x+3, y+1, z+4, sh, ms, 3);
		world.setBlock(x+3, y+1, z+8, sh, ms, 3);
		world.setBlock(x+3, y+2, z+4, sh, ms, 3);
		world.setBlock(x+3, y+2, z+8, sh, ms, 3);
		world.setBlock(x+3, y+3, z+4, ps, 1, 3);
		world.setBlock(x+3, y+3, z+5, sh, ms, 3);
		world.setBlock(x+3, y+3, z+6, sh, ms, 3);
		world.setBlock(x+3, y+3, z+7, sh, ms, 3);
		world.setBlock(x+3, y+3, z+8, ps, 1, 3);
		world.setBlock(x+4, y+0, z+0, ps, 2, 3);
		world.setBlock(x+4, y+0, z+1, sh, ms, 3);
		world.setBlock(x+4, y+0, z+2, sh, ms, 3);
		world.setBlock(x+4, y+0, z+3, sh, ms, 3);
		world.setBlock(x+4, y+0, z+4, ps, 2, 3);
		world.setBlock(x+4, y+0, z+8, ps, 2, 3);
		world.setBlock(x+4, y+0, z+9, sh, ms, 3);
		world.setBlock(x+4, y+0, z+10, sh, ms, 3);
		world.setBlock(x+4, y+0, z+11, sh, ms, 3);
		world.setBlock(x+4, y+0, z+12, ps, 2, 3);
		world.setBlock(x+4, y+1, z+0, ps, 2, 3);
		world.setBlock(x+4, y+1, z+1, sh, ms, 3);
		world.setBlock(x+4, y+1, z+2, sh, ms, 3);
		world.setBlock(x+4, y+1, z+3, sh, ms, 3);
		world.setBlock(x+4, y+1, z+4, ps, 2, 3);
		world.setBlock(x+4, y+1, z+8, ps, 2, 3);
		world.setBlock(x+4, y+1, z+9, sh, ms, 3);
		world.setBlock(x+4, y+1, z+10, sh, ms, 3);
		world.setBlock(x+4, y+1, z+11, sh, ms, 3);
		world.setBlock(x+4, y+1, z+12, ps, 2, 3);
		world.setBlock(x+4, y+2, z+0, ps, 2, 3);
		world.setBlock(x+4, y+2, z+1, sh, ms, 3);
		world.setBlock(x+4, y+2, z+2, sh, ms, 3);
		world.setBlock(x+4, y+2, z+3, sh, ms, 3);
		world.setBlock(x+4, y+2, z+4, ps, 2, 3);
		world.setBlock(x+4, y+2, z+8, ps, 2, 3);
		world.setBlock(x+4, y+2, z+9, sh, ms, 3);
		world.setBlock(x+4, y+2, z+10, sh, ms, 3);
		world.setBlock(x+4, y+2, z+11, sh, ms, 3);
		world.setBlock(x+4, y+2, z+12, ps, 2, 3);
		world.setBlock(x+4, y+3, z+0, ps, 7, 3);
		world.setBlock(x+4, y+3, z+1, ps, 1, 3);
		world.setBlock(x+4, y+3, z+2, ps, 1, 3);
		world.setBlock(x+4, y+3, z+3, ps, 1, 3);
		world.setBlock(x+4, y+3, z+4, ps, 8, 3);
		world.setBlock(x+4, y+3, z+5, ps, 1, 3);
		world.setBlock(x+4, y+3, z+6, ps, 1, 3);
		world.setBlock(x+4, y+3, z+7, ps, 1, 3);
		world.setBlock(x+4, y+3, z+8, ps, 8, 3);
		world.setBlock(x+4, y+3, z+9, ps, 1, 3);
		world.setBlock(x+4, y+3, z+10, ps, 1, 3);
		world.setBlock(x+4, y+3, z+11, ps, 1, 3);
		world.setBlock(x+4, y+3, z+12, ps, 7, 3);
		world.setBlock(x+4, y+4, z+4, ps, 2, 3);
		world.setBlock(x+4, y+4, z+5, sh, ms, 3);
		world.setBlock(x+4, y+4, z+6, sh, ms, 3);
		world.setBlock(x+4, y+4, z+7, sh, ms, 3);
		world.setBlock(x+4, y+4, z+8, ps, 2, 3);
		world.setBlock(x+4, y+5, z+4, ps, 7, 3);
		world.setBlock(x+4, y+5, z+5, ps, 1, 3);
		world.setBlock(x+4, y+5, z+6, ps, 1, 3);
		world.setBlock(x+4, y+5, z+7, ps, 1, 3);
		world.setBlock(x+4, y+5, z+8, ps, 7, 3);
		world.setBlock(x+5, y+3, z+0, ps, 1, 3);
		world.setBlock(x+5, y+3, z+1, sh, ms, 3);
		world.setBlock(x+5, y+3, z+2, sh, ms, 3);
		world.setBlock(x+5, y+3, z+3, sh, ms, 3);
		world.setBlock(x+5, y+3, z+4, ps, 1, 3);
		world.setBlock(x+5, y+3, z+8, ps, 1, 3);
		world.setBlock(x+5, y+3, z+9, sh, ms, 3);
		world.setBlock(x+5, y+3, z+10, sh, ms, 3);
		world.setBlock(x+5, y+3, z+11, sh, ms, 3);
		world.setBlock(x+5, y+3, z+12, ps, 1, 3);
		world.setBlock(x+5, y+4, z+4, sh, ms, 3);
		world.setBlock(x+5, y+4, z+8, sh, ms, 3);
		world.setBlock(x+5, y+5, z+4, ps, 1, 3);
		world.setBlock(x+5, y+5, z+5, sh, ms, 3);
		world.setBlock(x+5, y+5, z+6, sh, ms, 3);
		world.setBlock(x+5, y+5, z+7, sh, ms, 3);
		world.setBlock(x+5, y+5, z+8, ps, 1, 3);
		world.setBlock(x+6, y+3, z+0, ps, 1, 3);
		world.setBlock(x+6, y+3, z+1, sh, ms, 3);
		world.setBlock(x+6, y+3, z+2, sh, ml, 3);
		world.setBlock(x+6, y+3, z+3, sh, ms, 3);
		world.setBlock(x+6, y+3, z+4, ps, 1, 3);
		world.setBlock(x+6, y+3, z+6, sh, ml, 3);
		world.setBlock(x+6, y+3, z+8, ps, 1, 3);
		world.setBlock(x+6, y+3, z+9, sh, ms, 3);
		world.setBlock(x+6, y+3, z+10, sh, ml, 3);
		world.setBlock(x+6, y+3, z+11, sh, ms, 3);
		world.setBlock(x+6, y+3, z+12, ps, 1, 3);
		world.setBlock(x+6, y+4, z+4, sh, ms, 3);
		world.setBlock(x+6, y+4, z+6, ps, 2, 3);
		world.setBlock(x+6, y+4, z+8, sh, ms, 3);
		world.setBlock(x+6, y+5, z+4, ps, 1, 3);
		world.setBlock(x+6, y+5, z+5, sh, ms, 3);
		world.setBlock(x+6, y+5, z+6, sh, ms, 3);
		world.setBlock(x+6, y+5, z+7, sh, ms, 3);
		world.setBlock(x+6, y+5, z+8, ps, 1, 3);
		world.setBlock(x+7, y+3, z+0, ps, 1, 3);
		world.setBlock(x+7, y+3, z+1, sh, ms, 3);
		world.setBlock(x+7, y+3, z+2, sh, ms, 3);
		world.setBlock(x+7, y+3, z+3, sh, ms, 3);
		world.setBlock(x+7, y+3, z+4, ps, 1, 3);
		world.setBlock(x+7, y+3, z+8, ps, 1, 3);
		world.setBlock(x+7, y+3, z+9, sh, ms, 3);
		world.setBlock(x+7, y+3, z+10, sh, ms, 3);
		world.setBlock(x+7, y+3, z+11, sh, ms, 3);
		world.setBlock(x+7, y+3, z+12, ps, 1, 3);
		world.setBlock(x+7, y+4, z+4, sh, ms, 3);
		world.setBlock(x+7, y+4, z+8, sh, ms, 3);
		world.setBlock(x+7, y+5, z+4, ps, 1, 3);
		world.setBlock(x+7, y+5, z+5, sh, ms, 3);
		world.setBlock(x+7, y+5, z+6, sh, ms, 3);
		world.setBlock(x+7, y+5, z+7, sh, ms, 3);
		world.setBlock(x+7, y+5, z+8, ps, 1, 3);
		world.setBlock(x+8, y+0, z+0, ps, 2, 3);
		world.setBlock(x+8, y+0, z+1, sh, ms, 3);
		world.setBlock(x+8, y+0, z+2, sh, ms, 3);
		world.setBlock(x+8, y+0, z+3, sh, ms, 3);
		world.setBlock(x+8, y+0, z+4, ps, 2, 3);
		world.setBlock(x+8, y+0, z+8, ps, 2, 3);
		world.setBlock(x+8, y+0, z+9, sh, ms, 3);
		world.setBlock(x+8, y+0, z+10, sh, ms, 3);
		world.setBlock(x+8, y+0, z+11, sh, ms, 3);
		world.setBlock(x+8, y+0, z+12, ps, 2, 3);
		world.setBlock(x+8, y+1, z+0, ps, 2, 3);
		world.setBlock(x+8, y+1, z+1, sh, ms, 3);
		world.setBlock(x+8, y+1, z+2, sh, ms, 3);
		world.setBlock(x+8, y+1, z+3, sh, ms, 3);
		world.setBlock(x+8, y+1, z+4, ps, 2, 3);
		world.setBlock(x+8, y+1, z+8, ps, 2, 3);
		world.setBlock(x+8, y+1, z+9, sh, ms, 3);
		world.setBlock(x+8, y+1, z+10, sh, ms, 3);
		world.setBlock(x+8, y+1, z+11, sh, ms, 3);
		world.setBlock(x+8, y+1, z+12, ps, 2, 3);
		world.setBlock(x+8, y+2, z+0, ps, 2, 3);
		world.setBlock(x+8, y+2, z+1, sh, ms, 3);
		world.setBlock(x+8, y+2, z+2, sh, ms, 3);
		world.setBlock(x+8, y+2, z+3, sh, ms, 3);
		world.setBlock(x+8, y+2, z+4, ps, 2, 3);
		world.setBlock(x+8, y+2, z+8, ps, 2, 3);
		world.setBlock(x+8, y+2, z+9, sh, ms, 3);
		world.setBlock(x+8, y+2, z+10, sh, ms, 3);
		world.setBlock(x+8, y+2, z+11, sh, ms, 3);
		world.setBlock(x+8, y+2, z+12, ps, 2, 3);
		world.setBlock(x+8, y+3, z+0, ps, 7, 3);
		world.setBlock(x+8, y+3, z+1, ps, 1, 3);
		world.setBlock(x+8, y+3, z+2, ps, 1, 3);
		world.setBlock(x+8, y+3, z+3, ps, 1, 3);
		world.setBlock(x+8, y+3, z+4, ps, 8, 3);
		world.setBlock(x+8, y+3, z+5, ps, 1, 3);
		world.setBlock(x+8, y+3, z+6, ps, 1, 3);
		world.setBlock(x+8, y+3, z+7, ps, 1, 3);
		world.setBlock(x+8, y+3, z+8, ps, 8, 3);
		world.setBlock(x+8, y+3, z+9, ps, 1, 3);
		world.setBlock(x+8, y+3, z+10, ps, 1, 3);
		world.setBlock(x+8, y+3, z+11, ps, 1, 3);
		world.setBlock(x+8, y+3, z+12, ps, 7, 3);
		world.setBlock(x+8, y+4, z+4, ps, 2, 3);
		world.setBlock(x+8, y+4, z+5, sh, ms, 3);
		world.setBlock(x+8, y+4, z+6, sh, ms, 3);
		world.setBlock(x+8, y+4, z+7, sh, ms, 3);
		world.setBlock(x+8, y+4, z+8, ps, 2, 3);
		world.setBlock(x+8, y+5, z+4, ps, 7, 3);
		world.setBlock(x+8, y+5, z+5, ps, 1, 3);
		world.setBlock(x+8, y+5, z+6, ps, 1, 3);
		world.setBlock(x+8, y+5, z+7, ps, 1, 3);
		world.setBlock(x+8, y+5, z+8, ps, 7, 3);
		world.setBlock(x+9, y+0, z+4, sh, ms, 3);
		world.setBlock(x+9, y+0, z+8, sh, ms, 3);
		world.setBlock(x+9, y+1, z+4, sh, ms, 3);
		world.setBlock(x+9, y+1, z+8, sh, ms, 3);
		world.setBlock(x+9, y+2, z+4, sh, ms, 3);
		world.setBlock(x+9, y+2, z+8, sh, ms, 3);
		world.setBlock(x+9, y+3, z+4, ps, 1, 3);
		world.setBlock(x+9, y+3, z+5, sh, ms, 3);
		world.setBlock(x+9, y+3, z+6, sh, ms, 3);
		world.setBlock(x+9, y+3, z+7, sh, ms, 3);
		world.setBlock(x+9, y+3, z+8, ps, 1, 3);
		world.setBlock(x+10, y+0, z+4, sh, ms, 3);
		world.setBlock(x+10, y+0, z+8, sh, ms, 3);
		world.setBlock(x+10, y+1, z+4, sh, ms, 3);
		world.setBlock(x+10, y+1, z+8, sh, ms, 3);
		world.setBlock(x+10, y+2, z+4, sh, ms, 3);
		world.setBlock(x+10, y+2, z+8, sh, ms, 3);
		world.setBlock(x+10, y+3, z+4, ps, 1, 3);
		world.setBlock(x+10, y+3, z+5, sh, ms, 3);
		world.setBlock(x+10, y+3, z+6, sh, ml, 3);
		world.setBlock(x+10, y+3, z+7, sh, ms, 3);
		world.setBlock(x+10, y+3, z+8, ps, 1, 3);
		world.setBlock(x+11, y+0, z+4, sh, ms, 3);
		world.setBlock(x+11, y+0, z+8, sh, ms, 3);
		world.setBlock(x+11, y+1, z+4, sh, ms, 3);
		world.setBlock(x+11, y+1, z+8, sh, ms, 3);
		world.setBlock(x+11, y+2, z+4, sh, ms, 3);
		world.setBlock(x+11, y+2, z+8, sh, ms, 3);
		world.setBlock(x+11, y+3, z+4, ps, 1, 3);
		world.setBlock(x+11, y+3, z+5, sh, ms, 3);
		world.setBlock(x+11, y+3, z+6, sh, ms, 3);
		world.setBlock(x+11, y+3, z+7, sh, ms, 3);
		world.setBlock(x+11, y+3, z+8, ps, 1, 3);
		world.setBlock(x+12, y+0, z+0, ps, 2, 3);
		world.setBlock(x+12, y+0, z+4, ps, 2, 3);
		world.setBlock(x+12, y+0, z+8, ps, 2, 3);
		world.setBlock(x+12, y+0, z+12, ps, 2, 3);
		world.setBlock(x+12, y+1, z+0, sh, ml, 3);
		world.setBlock(x+12, y+1, z+4, ps, 2, 3);
		world.setBlock(x+12, y+1, z+8, ps, 2, 3);
		world.setBlock(x+12, y+1, z+12, sh, ml, 3);
		world.setBlock(x+12, y+2, z+4, ps, 2, 3);
		world.setBlock(x+12, y+2, z+8, ps, 2, 3);
		world.setBlock(x+12, y+3, z+4, ps, 7, 3);
		world.setBlock(x+12, y+3, z+5, ps, 1, 3);
		world.setBlock(x+12, y+3, z+6, ps, 1, 3);
		world.setBlock(x+12, y+3, z+7, ps, 1, 3);
		world.setBlock(x+12, y+3, z+8, ps, 7, 3);
	}

	private int calculateHeight(World world, int x, int z) {
		int top = world.getTopSolidOrLiquidBlock(x, z);
		Block b = world.getBlock(x, top, z);
		Block b2 = world.getBlock(x, top+1, z);
		while (b instanceof BlockFluidBase || b instanceof BlockLiquid || b2 instanceof BlockFluidBase || b2 instanceof BlockLiquid) {
			top++;
			b = world.getBlock(x, top, z);
			b2 = world.getBlock(x, top+1, z);
		}
		return top-parent.getPosY();
	}

}
