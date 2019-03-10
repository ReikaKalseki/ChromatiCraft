/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.GOL;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class GOLEntrance extends DynamicStructurePiece {

	public GOLEntrance(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int top = this.findTop(world, x, z);
		int ms = BlockType.STONE.metadata;
		int mg = BlockType.GLASS.metadata;
		int ml = BlockType.LIGHT.metadata;
		int y = parent.getPosY()+1;

		this.generatePrefab(world, x, y, z, b, ms, mg, ml);

		x -= 3;
		z += 8;

		for (int h = 0; h < top-y; h++) {
			for (int i = -2; i <= 2; i++) {
				for (int k = -2; k <= 2; k++) {
					int dx = x+i;
					int dy = y+h;
					int dz = z+k;
					boolean air = Math.abs(i) <= 1 && Math.abs(k) <= 1 && h > 0;
					world.setBlock(dx, dy, dz, air ? STRUCTURE_AIR : b, air ? 0 : h%8 == 2 && (i == 0 || k == 0) ? ml : ms, 3);
					if (air && h >= 5 && dy < top-5 && h%8 == 5) {
						world.setBlock(dx, dy, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta(), 3);
					}
				}
			}
		}

		for (int i = 1; i <= 3; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+2;
				int dy = y+i;
				int dz = z+k;
				world.setBlock(dx, dy, dz, STRUCTURE_AIR);
			}
		}

		for (int i = 0; i < 4; i++) {
			int dy = top+i;
			int m = i == 3 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
			world.setBlock(x-2, dy, z-2, b, m, 3);
			world.setBlock(x+2, dy, z-2, b, m, 3);
			world.setBlock(x-2, dy, z+2, b, m, 3);
			world.setBlock(x+2, dy, z+2, b, m, 3);
		}

		int r2 = 6;

		for (int dy = top; dy < top+6; dy++) {
			for (int i = -r2; i <= r2; i++) {
				for (int k = -r2; k <= r2; k++) {
					world.setBlock(x+i, dy, z+k, STRUCTURE_AIR);
				}
			}
		}

		for (int i = 0; i < 6; i++) {
			int dy = top+i;
			int m = i == 5 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
			world.setBlock(x-r2, dy, z, b, m, 3);
			world.setBlock(x+r2, dy, z, b, m, 3);
			world.setBlock(x, dy, z-r2, b, m, 3);
			world.setBlock(x, dy, z+r2, b, m, 3);

			world.setBlock(x-r2, dy, z-r2, b, m, 3);
			world.setBlock(x+r2, dy, z-r2, b, m, 3);
			world.setBlock(x-r2, dy, z+r2, b, m, 3);
			world.setBlock(x+r2, dy, z+r2, b, m, 3);
		}

		for (int i = -r2; i <= r2; i++) {
			for (int k = -r2; k <= r2; k++) {
				if (Math.abs(i) > 2 || Math.abs(k) > 2) {
					int dx = x+i;
					int dy = top-1;
					int dz = z+k;
					int m = Math.abs(i) == r2 || Math.abs(k) == r2 ? BlockType.MOSS.metadata : BlockType.STONE.metadata;
					world.setBlock(dx, dy, dz, b, m, 3);
				}
			}
		}

		for (int i = 3; i < r2; i++) {
			world.setBlock(x-i, top-1, z, b, BlockType.MOSS.metadata, 3);
			world.setBlock(x+i, top-1, z, b, BlockType.MOSS.metadata, 3);
			world.setBlock(x, top-1, z-i, b, BlockType.MOSS.metadata, 3);
			world.setBlock(x, top-1, z+i, b, BlockType.MOSS.metadata, 3);
		}

		parent.generatePasswordTile(x, y-1, z);

		parent.offsetEntry(-10, 0);
	}

	private int findTop(World world, int x, int z) {
		int top = -1;
		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				int dx = x+i;
				int dz = z+k;
				int loc = world.getTopSolidOrLiquidBlock(dx, dz);
				while (!world.getBlock(dx, loc, dz).isAir(world, dx, loc, dz))
					loc++;
				loc--;
				top = Math.max(top, loc);
			}
		}
		return top;
	}

	private void generatePrefab(World world, int x, int y, int z, Block b, int ms, int mg, int ml) {
		world.setBlock(x+0, y+0, z+6, b, ms, 3);
		world.setBlock(x+0, y+0, z+7, b, ms, 3);
		world.setBlock(x+0, y+0, z+8, b, ms, 3);
		world.setBlock(x+0, y+0, z+9, b, ms, 3);
		world.setBlock(x+0, y+0, z+10, b, ms, 3);
		world.setBlock(x+0, y+1, z+6, b, ms, 3);
		world.setBlock(x+0, y+1, z+10, b, ms, 3);
		world.setBlock(x+0, y+2, z+6, b, ms, 3);
		world.setBlock(x+0, y+2, z+10, b, ms, 3);
		world.setBlock(x+0, y+3, z+6, b, ms, 3);
		world.setBlock(x+0, y+3, z+10, b, ms, 3);
		world.setBlock(x+0, y+4, z+6, b, ms, 3);
		world.setBlock(x+0, y+4, z+7, b, ms, 3);
		world.setBlock(x+0, y+4, z+8, b, ms, 3);
		world.setBlock(x+0, y+4, z+9, b, ms, 3);
		world.setBlock(x+0, y+4, z+10, b, ms, 3);
		world.setBlock(x+1, y+0, z+5, b, ms, 3);
		world.setBlock(x+1, y+0, z+6, b, ms, 3);
		world.setBlock(x+1, y+0, z+7, b, ms, 3);
		world.setBlock(x+1, y+0, z+8, b, ml, 3);
		world.setBlock(x+1, y+0, z+9, b, ms, 3);
		world.setBlock(x+1, y+0, z+10, b, ms, 3);
		world.setBlock(x+1, y+0, z+11, b, ms, 3);
		world.setBlock(x+1, y+1, z+5, b, ms, 3);
		world.setBlock(x+1, y+1, z+6, b, ms, 3);
		world.setBlock(x+1, y+1, z+10, b, ms, 3);
		world.setBlock(x+1, y+1, z+11, b, ms, 3);
		world.setBlock(x+1, y+2, z+5, b, ms, 3);
		world.setBlock(x+1, y+2, z+6, b, ms, 3);
		world.setBlock(x+1, y+2, z+10, b, ms, 3);
		world.setBlock(x+1, y+2, z+11, b, ms, 3);
		world.setBlock(x+1, y+3, z+5, b, ms, 3);
		world.setBlock(x+1, y+3, z+6, b, ms, 3);
		world.setBlock(x+1, y+3, z+10, b, ms, 3);
		world.setBlock(x+1, y+3, z+11, b, ms, 3);
		world.setBlock(x+1, y+4, z+5, b, ms, 3);
		world.setBlock(x+1, y+4, z+6, b, ms, 3);
		world.setBlock(x+1, y+4, z+7, b, ms, 3);
		world.setBlock(x+1, y+4, z+8, b, ms, 3);
		world.setBlock(x+1, y+4, z+9, b, ms, 3);
		world.setBlock(x+1, y+4, z+10, b, ms, 3);
		world.setBlock(x+1, y+4, z+11, b, ms, 3);
		world.setBlock(x+1, y+5, z+5, b, ms, 3);
		world.setBlock(x+1, y+5, z+6, b, ms, 3);
		world.setBlock(x+1, y+5, z+7, b, ms, 3);
		world.setBlock(x+1, y+5, z+8, b, ms, 3);
		world.setBlock(x+1, y+5, z+9, b, ms, 3);
		world.setBlock(x+1, y+5, z+10, b, ms, 3);
		world.setBlock(x+1, y+5, z+11, b, ms, 3);
		world.setBlock(x+1, y+6, z+5, b, ms, 3);
		world.setBlock(x+1, y+6, z+6, b, ms, 3);
		world.setBlock(x+1, y+6, z+7, b, ms, 3);
		world.setBlock(x+1, y+6, z+8, b, ms, 3);
		world.setBlock(x+1, y+6, z+9, b, ms, 3);
		world.setBlock(x+1, y+6, z+10, b, ms, 3);
		world.setBlock(x+1, y+6, z+11, b, ms, 3);
		world.setBlock(x+2, y+0, z+4, b, ms, 3);
		world.setBlock(x+2, y+0, z+5, b, ms, 3);
		world.setBlock(x+2, y+0, z+6, b, ms, 3);
		world.setBlock(x+2, y+0, z+7, b, ms, 3);
		world.setBlock(x+2, y+0, z+8, b, ms, 3);
		world.setBlock(x+2, y+0, z+9, b, ms, 3);
		world.setBlock(x+2, y+0, z+10, b, ms, 3);
		world.setBlock(x+2, y+0, z+11, b, ms, 3);
		world.setBlock(x+2, y+0, z+12, b, ms, 3);
		world.setBlock(x+2, y+1, z+4, b, ms, 3);
		world.setBlock(x+2, y+1, z+5, b, ms, 3);
		world.setBlock(x+2, y+1, z+11, b, ms, 3);
		world.setBlock(x+2, y+1, z+12, b, ms, 3);
		world.setBlock(x+2, y+2, z+4, b, ms, 3);
		world.setBlock(x+2, y+2, z+5, b, ms, 3);
		world.setBlock(x+2, y+2, z+11, b, ms, 3);
		world.setBlock(x+2, y+2, z+12, b, ms, 3);
		world.setBlock(x+2, y+3, z+4, b, ms, 3);
		world.setBlock(x+2, y+3, z+5, b, ms, 3);
		world.setBlock(x+2, y+3, z+11, b, ms, 3);
		world.setBlock(x+2, y+3, z+12, b, ms, 3);
		world.setBlock(x+2, y+4, z+4, b, ms, 3);
		world.setBlock(x+2, y+4, z+5, b, ms, 3);
		world.setBlock(x+2, y+4, z+11, b, ms, 3);
		world.setBlock(x+2, y+4, z+12, b, ms, 3);
		world.setBlock(x+2, y+5, z+4, b, ms, 3);
		world.setBlock(x+2, y+5, z+5, b, ms, 3);
		world.setBlock(x+2, y+5, z+6, b, ms, 3);
		world.setBlock(x+2, y+5, z+7, b, ms, 3);
		world.setBlock(x+2, y+5, z+8, b, ms, 3);
		world.setBlock(x+2, y+5, z+9, b, ms, 3);
		world.setBlock(x+2, y+5, z+10, b, ms, 3);
		world.setBlock(x+2, y+5, z+11, b, ms, 3);
		world.setBlock(x+2, y+5, z+12, b, ms, 3);
		world.setBlock(x+2, y+6, z+4, b, ms, 3);
		world.setBlock(x+2, y+6, z+5, b, ms, 3);
		world.setBlock(x+2, y+6, z+6, b, ms, 3);
		world.setBlock(x+2, y+6, z+7, b, ms, 3);
		world.setBlock(x+2, y+6, z+8, b, ms, 3);
		world.setBlock(x+2, y+6, z+9, b, ms, 3);
		world.setBlock(x+2, y+6, z+10, b, ms, 3);
		world.setBlock(x+2, y+6, z+11, b, ms, 3);
		world.setBlock(x+2, y+6, z+12, b, ms, 3);
		world.setBlock(x+2, y+7, z+5, b, ms, 3);
		world.setBlock(x+2, y+7, z+6, b, ms, 3);
		world.setBlock(x+2, y+7, z+7, b, ms, 3);
		world.setBlock(x+2, y+7, z+8, b, ms, 3);
		world.setBlock(x+2, y+7, z+9, b, ms, 3);
		world.setBlock(x+2, y+7, z+10, b, ms, 3);
		world.setBlock(x+2, y+7, z+11, b, ms, 3);
		world.setBlock(x+3, y+0, z+3, b, ms, 3);
		world.setBlock(x+3, y+0, z+4, b, ms, 3);
		world.setBlock(x+3, y+0, z+5, b, ms, 3);
		world.setBlock(x+3, y+0, z+6, b, ms, 3);
		world.setBlock(x+3, y+0, z+7, b, ms, 3);
		world.setBlock(x+3, y+0, z+8, b, ms, 3);
		world.setBlock(x+3, y+0, z+9, b, ms, 3);
		world.setBlock(x+3, y+0, z+10, b, ms, 3);
		world.setBlock(x+3, y+0, z+11, b, ms, 3);
		world.setBlock(x+3, y+0, z+12, b, ms, 3);
		world.setBlock(x+3, y+0, z+13, b, ms, 3);
		world.setBlock(x+3, y+1, z+3, b, ms, 3);
		world.setBlock(x+3, y+1, z+4, b, ms, 3);
		world.setBlock(x+3, y+1, z+12, b, ms, 3);
		world.setBlock(x+3, y+1, z+13, b, ms, 3);
		world.setBlock(x+3, y+2, z+3, b, ms, 3);
		world.setBlock(x+3, y+2, z+4, b, ms, 3);
		world.setBlock(x+3, y+2, z+12, b, ms, 3);
		world.setBlock(x+3, y+2, z+13, b, ms, 3);
		world.setBlock(x+3, y+3, z+3, b, ms, 3);
		world.setBlock(x+3, y+3, z+4, b, ms, 3);
		world.setBlock(x+3, y+3, z+12, b, ms, 3);
		world.setBlock(x+3, y+3, z+13, b, ms, 3);
		world.setBlock(x+3, y+4, z+3, b, ms, 3);
		world.setBlock(x+3, y+4, z+4, b, ms, 3);
		world.setBlock(x+3, y+4, z+12, b, ms, 3);
		world.setBlock(x+3, y+4, z+13, b, ms, 3);
		world.setBlock(x+3, y+5, z+3, b, ms, 3);
		world.setBlock(x+3, y+5, z+4, b, ms, 3);
		world.setBlock(x+3, y+5, z+12, b, ms, 3);
		world.setBlock(x+3, y+5, z+13, b, ms, 3);
		world.setBlock(x+3, y+6, z+4, b, ms, 3);
		world.setBlock(x+3, y+6, z+5, b, ms, 3);
		world.setBlock(x+3, y+6, z+6, b, ms, 3);
		world.setBlock(x+3, y+6, z+7, b, ms, 3);
		world.setBlock(x+3, y+6, z+8, b, ms, 3);
		world.setBlock(x+3, y+6, z+9, b, ms, 3);
		world.setBlock(x+3, y+6, z+10, b, ms, 3);
		world.setBlock(x+3, y+6, z+11, b, ms, 3);
		world.setBlock(x+3, y+6, z+12, b, ms, 3);
		world.setBlock(x+3, y+7, z+5, b, ms, 3);
		world.setBlock(x+3, y+7, z+6, b, ms, 3);
		world.setBlock(x+3, y+7, z+7, b, ms, 3);
		world.setBlock(x+3, y+7, z+8, b, ms, 3);
		world.setBlock(x+3, y+7, z+9, b, ms, 3);
		world.setBlock(x+3, y+7, z+10, b, ms, 3);
		world.setBlock(x+3, y+7, z+11, b, ms, 3);
		world.setBlock(x+4, y+0, z+3, b, ms, 3);
		world.setBlock(x+4, y+0, z+4, b, ms, 3);
		world.setBlock(x+4, y+0, z+5, b, ms, 3);
		world.setBlock(x+4, y+0, z+6, b, ml, 3);
		world.setBlock(x+4, y+0, z+7, b, ms, 3);
		world.setBlock(x+4, y+0, z+8, b, ms, 3);
		world.setBlock(x+4, y+0, z+9, b, ms, 3);
		world.setBlock(x+4, y+0, z+10, b, ml, 3);
		world.setBlock(x+4, y+0, z+11, b, ms, 3);
		world.setBlock(x+4, y+0, z+12, b, ms, 3);
		world.setBlock(x+4, y+0, z+13, b, ms, 3);
		world.setBlock(x+4, y+1, z+3, b, ms, 3);
		world.setBlock(x+4, y+1, z+13, b, ms, 3);
		world.setBlock(x+4, y+2, z+3, b, ms, 3);
		world.setBlock(x+4, y+2, z+13, b, ms, 3);
		world.setBlock(x+4, y+3, z+3, b, ms, 3);
		world.setBlock(x+4, y+3, z+13, b, ms, 3);
		world.setBlock(x+4, y+4, z+3, b, ms, 3);
		world.setBlock(x+4, y+4, z+13, b, ms, 3);
		world.setBlock(x+4, y+5, z+3, b, ms, 3);
		world.setBlock(x+4, y+5, z+4, b, ms, 3);
		world.setBlock(x+4, y+5, z+12, b, ms, 3);
		world.setBlock(x+4, y+5, z+13, b, ms, 3);
		world.setBlock(x+4, y+6, z+4, b, ms, 3);
		world.setBlock(x+4, y+6, z+5, b, ms, 3);
		world.setBlock(x+4, y+6, z+11, b, ms, 3);
		world.setBlock(x+4, y+6, z+12, b, ms, 3);
		world.setBlock(x+4, y+7, z+5, b, ms, 3);
		world.setBlock(x+4, y+7, z+6, b, ms, 3);
		world.setBlock(x+4, y+7, z+7, b, ms, 3);
		world.setBlock(x+4, y+7, z+8, b, ms, 3);
		world.setBlock(x+4, y+7, z+9, b, ms, 3);
		world.setBlock(x+4, y+7, z+10, b, ms, 3);
		world.setBlock(x+4, y+7, z+11, b, ms, 3);
		world.setBlock(x+5, y+0, z+3, b, ms, 3);
		world.setBlock(x+5, y+0, z+4, b, ms, 3);
		world.setBlock(x+5, y+0, z+5, b, ms, 3);
		world.setBlock(x+5, y+0, z+6, b, ms, 3);
		world.setBlock(x+5, y+0, z+7, b, ms, 3);
		world.setBlock(x+5, y+0, z+8, b, ms, 3);
		world.setBlock(x+5, y+0, z+9, b, ms, 3);
		world.setBlock(x+5, y+0, z+10, b, ms, 3);
		world.setBlock(x+5, y+0, z+11, b, ms, 3);
		world.setBlock(x+5, y+0, z+12, b, ms, 3);
		world.setBlock(x+5, y+0, z+13, b, ms, 3);
		world.setBlock(x+5, y+1, z+3, b, ms, 3);
		world.setBlock(x+5, y+1, z+13, b, ms, 3);
		world.setBlock(x+5, y+2, z+3, b, ms, 3);
		world.setBlock(x+5, y+2, z+13, b, ms, 3);
		world.setBlock(x+5, y+3, z+3, b, ms, 3);
		world.setBlock(x+5, y+3, z+13, b, ms, 3);
		world.setBlock(x+5, y+4, z+3, b, ms, 3);
		world.setBlock(x+5, y+4, z+13, b, ms, 3);
		world.setBlock(x+5, y+5, z+3, b, ms, 3);
		world.setBlock(x+5, y+5, z+4, b, ms, 3);
		world.setBlock(x+5, y+5, z+12, b, ms, 3);
		world.setBlock(x+5, y+5, z+13, b, ms, 3);
		world.setBlock(x+5, y+6, z+4, b, ms, 3);
		world.setBlock(x+5, y+6, z+5, b, ms, 3);
		world.setBlock(x+5, y+6, z+11, b, ms, 3);
		world.setBlock(x+5, y+6, z+12, b, ms, 3);
		world.setBlock(x+5, y+7, z+5, b, ms, 3);
		world.setBlock(x+5, y+7, z+6, b, ms, 3);
		world.setBlock(x+5, y+7, z+7, b, ms, 3);
		world.setBlock(x+5, y+7, z+8, b, ml, 3);
		world.setBlock(x+5, y+7, z+9, b, ms, 3);
		world.setBlock(x+5, y+7, z+10, b, ms, 3);
		world.setBlock(x+5, y+7, z+11, b, ms, 3);
		world.setBlock(x+6, y+0, z+3, b, ms, 3);
		world.setBlock(x+6, y+0, z+4, b, ms, 3);
		world.setBlock(x+6, y+0, z+5, b, ms, 3);
		world.setBlock(x+6, y+0, z+6, b, ms, 3);
		world.setBlock(x+6, y+0, z+7, b, ms, 3);
		world.setBlock(x+6, y+0, z+8, b, ms, 3);
		world.setBlock(x+6, y+0, z+9, b, ms, 3);
		world.setBlock(x+6, y+0, z+10, b, ms, 3);
		world.setBlock(x+6, y+0, z+11, b, ms, 3);
		world.setBlock(x+6, y+0, z+12, b, ms, 3);
		world.setBlock(x+6, y+0, z+13, b, ms, 3);
		world.setBlock(x+6, y+1, z+1, b, ms, 3);
		world.setBlock(x+6, y+1, z+2, b, ms, 3);
		world.setBlock(x+6, y+1, z+14, b, ms, 3);
		world.setBlock(x+6, y+1, z+15, b, ms, 3);
		world.setBlock(x+6, y+2, z+1, b, ms, 3);
		world.setBlock(x+6, y+2, z+2, b, ms, 3);
		world.setBlock(x+6, y+2, z+14, b, ms, 3);
		world.setBlock(x+6, y+2, z+15, b, ms, 3);
		world.setBlock(x+6, y+3, z+1, b, ms, 3);
		world.setBlock(x+6, y+3, z+2, b, ms, 3);
		world.setBlock(x+6, y+3, z+14, b, ms, 3);
		world.setBlock(x+6, y+3, z+15, b, ms, 3);
		world.setBlock(x+6, y+4, z+3, b, ms, 3);
		world.setBlock(x+6, y+4, z+13, b, ms, 3);
		world.setBlock(x+6, y+5, z+3, b, ms, 3);
		world.setBlock(x+6, y+5, z+4, b, ms, 3);
		world.setBlock(x+6, y+5, z+12, b, ms, 3);
		world.setBlock(x+6, y+5, z+13, b, ms, 3);
		world.setBlock(x+6, y+6, z+4, b, ms, 3);
		world.setBlock(x+6, y+6, z+5, b, ms, 3);
		world.setBlock(x+6, y+6, z+11, b, ms, 3);
		world.setBlock(x+6, y+6, z+12, b, ms, 3);
		world.setBlock(x+6, y+7, z+5, b, ms, 3);
		world.setBlock(x+6, y+7, z+6, b, ms, 3);
		world.setBlock(x+6, y+7, z+7, b, ms, 3);
		world.setBlock(x+6, y+7, z+8, b, ms, 3);
		world.setBlock(x+6, y+7, z+9, b, ms, 3);
		world.setBlock(x+6, y+7, z+10, b, ms, 3);
		world.setBlock(x+6, y+7, z+11, b, ms, 3);
		world.setBlock(x+7, y+0, z+0, b, ms, 3);
		world.setBlock(x+7, y+0, z+1, b, ms, 3);
		world.setBlock(x+7, y+0, z+2, b, ms, 3);
		world.setBlock(x+7, y+0, z+3, b, ms, 3);
		world.setBlock(x+7, y+0, z+4, b, ms, 3);
		world.setBlock(x+7, y+0, z+5, b, ms, 3);
		world.setBlock(x+7, y+0, z+6, b, ms, 3);
		world.setBlock(x+7, y+0, z+7, b, ms, 3);
		world.setBlock(x+7, y+0, z+8, b, ms, 3);
		world.setBlock(x+7, y+0, z+9, b, ms, 3);
		world.setBlock(x+7, y+0, z+10, b, ms, 3);
		world.setBlock(x+7, y+0, z+11, b, ms, 3);
		world.setBlock(x+7, y+0, z+12, b, ms, 3);
		world.setBlock(x+7, y+0, z+13, b, ms, 3);
		world.setBlock(x+7, y+0, z+14, b, ms, 3);
		world.setBlock(x+7, y+0, z+15, b, ms, 3);
		world.setBlock(x+7, y+0, z+16, b, ms, 3);
		world.setBlock(x+7, y+1, z+0, b, ms, 3);
		world.setBlock(x+7, y+1, z+1, b, ms, 3);
		world.setBlock(x+7, y+1, z+15, b, ms, 3);
		world.setBlock(x+7, y+1, z+16, b, ms, 3);
		world.setBlock(x+7, y+2, z+0, b, ms, 3);
		world.setBlock(x+7, y+2, z+1, b, ms, 3);
		world.setBlock(x+7, y+2, z+15, b, ms, 3);
		world.setBlock(x+7, y+2, z+16, b, ms, 3);
		world.setBlock(x+7, y+3, z+0, b, ms, 3);
		world.setBlock(x+7, y+3, z+1, b, ms, 3);
		world.setBlock(x+7, y+3, z+15, b, ms, 3);
		world.setBlock(x+7, y+3, z+16, b, ms, 3);
		world.setBlock(x+7, y+4, z+0, b, ms, 3);
		world.setBlock(x+7, y+4, z+1, b, ms, 3);
		world.setBlock(x+7, y+4, z+2, b, ms, 3);
		world.setBlock(x+7, y+4, z+14, b, ms, 3);
		world.setBlock(x+7, y+4, z+15, b, ms, 3);
		world.setBlock(x+7, y+4, z+16, b, ms, 3);
		world.setBlock(x+7, y+5, z+3, b, ms, 3);
		world.setBlock(x+7, y+5, z+13, b, ms, 3);
		world.setBlock(x+7, y+6, z+4, b, ms, 3);
		world.setBlock(x+7, y+6, z+5, b, ms, 3);
		world.setBlock(x+7, y+6, z+11, b, ms, 3);
		world.setBlock(x+7, y+6, z+12, b, ms, 3);
		world.setBlock(x+7, y+7, z+5, b, ms, 3);
		world.setBlock(x+7, y+7, z+6, b, ms, 3);
		world.setBlock(x+7, y+7, z+7, b, ms, 3);
		world.setBlock(x+7, y+7, z+8, b, ms, 3);
		world.setBlock(x+7, y+7, z+9, b, ms, 3);
		world.setBlock(x+7, y+7, z+10, b, ms, 3);
		world.setBlock(x+7, y+7, z+11, b, ms, 3);

		world.setBlock(x+0, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+1, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+1, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+1, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+1, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+1, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+1, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+1, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+1, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+1, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+2, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+2, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+2, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+2, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+2, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+2, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+2, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+2, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+2, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+2, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+2, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+2, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+2, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+2, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+2, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+2, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+2, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+2, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+2, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+2, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+5, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+10, STRUCTURE_AIR);
		world.setBlock(x+3, y+5, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+4, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+1, z+12, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+4, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+2, z+12, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+4, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+3, z+12, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+4, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+4, z+12, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+5, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+10, STRUCTURE_AIR);
		world.setBlock(x+4, y+5, z+11, STRUCTURE_AIR);
		world.setBlock(x+4, y+6, z+6, STRUCTURE_AIR);
		world.setBlock(x+4, y+6, z+7, STRUCTURE_AIR);
		world.setBlock(x+4, y+6, z+8, STRUCTURE_AIR);
		world.setBlock(x+4, y+6, z+9, STRUCTURE_AIR);
		world.setBlock(x+4, y+6, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+4, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+5, y+1, z+12, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+4, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+5, y+2, z+12, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+4, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+5, y+3, z+12, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+4, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+5, y+4, z+12, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+5, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+10, STRUCTURE_AIR);
		world.setBlock(x+5, y+5, z+11, STRUCTURE_AIR);
		world.setBlock(x+5, y+6, z+6, STRUCTURE_AIR);
		world.setBlock(x+5, y+6, z+7, STRUCTURE_AIR);
		world.setBlock(x+5, y+6, z+8, STRUCTURE_AIR);
		world.setBlock(x+5, y+6, z+9, STRUCTURE_AIR);
		world.setBlock(x+5, y+6, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+3, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+4, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+12, STRUCTURE_AIR);
		world.setBlock(x+6, y+1, z+13, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+3, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+4, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+12, STRUCTURE_AIR);
		world.setBlock(x+6, y+2, z+13, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+3, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+4, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+12, STRUCTURE_AIR);
		world.setBlock(x+6, y+3, z+13, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+4, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+6, y+4, z+12, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+5, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+10, STRUCTURE_AIR);
		world.setBlock(x+6, y+5, z+11, STRUCTURE_AIR);
		world.setBlock(x+6, y+6, z+6, STRUCTURE_AIR);
		world.setBlock(x+6, y+6, z+7, STRUCTURE_AIR);
		world.setBlock(x+6, y+6, z+8, STRUCTURE_AIR);
		world.setBlock(x+6, y+6, z+9, STRUCTURE_AIR);
		world.setBlock(x+6, y+6, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+2, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+3, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+4, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+12, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+13, STRUCTURE_AIR);
		world.setBlock(x+7, y+1, z+14, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+2, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+3, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+4, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+12, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+13, STRUCTURE_AIR);
		world.setBlock(x+7, y+2, z+14, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+2, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+3, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+4, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+12, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+13, STRUCTURE_AIR);
		world.setBlock(x+7, y+3, z+14, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+3, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+4, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+12, STRUCTURE_AIR);
		world.setBlock(x+7, y+4, z+13, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+4, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+5, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+10, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+11, STRUCTURE_AIR);
		world.setBlock(x+7, y+5, z+12, STRUCTURE_AIR);
		world.setBlock(x+7, y+6, z+6, STRUCTURE_AIR);
		world.setBlock(x+7, y+6, z+7, STRUCTURE_AIR);
		world.setBlock(x+7, y+6, z+8, STRUCTURE_AIR);
		world.setBlock(x+7, y+6, z+9, STRUCTURE_AIR);
		world.setBlock(x+7, y+6, z+10, STRUCTURE_AIR);
	}

}
