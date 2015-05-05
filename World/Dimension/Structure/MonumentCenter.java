/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class MonumentCenter {

	public boolean generate(World world, Random rand, int x, int y, int z, ArrayList<ForgeDirection> li) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m = BlockType.STONE.metadata;
		this.generateBody(world, rand, x, y, z, b, m);
		this.generateAir(world, rand, x, y, z);
		this.generateDoors(world, rand, x, y, z, li);
		this.generateFloor(world, x, y, z, b, m);
		this.generatePillars(world, rand, x, y, z);
		this.generateDeco(world, rand, x, y, z);
		this.generateStands(world, rand, x, y, z);
		return true;
	}

	private void generateDoors(World world, Random rand, int x, int y, int z, ArrayList<ForgeDirection> li) {
		/*
		for (int j = 1; j <= 4; j++) {
			int in = j == 4 ? 13 : 12;
			int max = 28-in;
			for (int i = 0; i <= 28; i++) {
				for (int k = 0; k <= 28; k++) {
					if ((i >= in && i <= max) && (k == 0 || k == 28) || ((k >= in && k <= max) && (i == 0 || i == 28))) {

						world.setBlock(x+i, y+j, z+k, Blocks.air);
					}
				}
			}
		}
		 */
		for (ForgeDirection dir : li) {
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int dx = x+14+dir.offsetX*14;
			int dz = z+14+dir.offsetZ*14;
			int h = y+5;
			for (int dy = y+1; dy < h; dy++) {
				int w = dy == h-1 ? 1 : 2;
				for (int n = -w; n <= w; n++) {
					world.setBlock(dx+n*left.offsetX, dy, dz+n*left.offsetZ, Blocks.air);
				}
			}
		}
	}

	private void generateAir(World world, Random rand, int x, int y, int z) {
		for (int j = 1; j < 10; j++) {
			int in = j <= 5 ? 0 : j <= 6 ? 1 : j <= 7 ? 3 : (j-8)*4+5;
			int min = 1+in;
			int max = 28-in;
			for (int i = min; i < max; i++) {
				for (int k = min; k < max; k++) {
					world.setBlock(x+i, y+j, z+k, Blocks.air);
				}
			}
		}
	}

	private void generateFloor(World world, int x, int y, int z, Block b, int m) {
		for (int i = 0; i <= 28; i++) {
			for (int k = 0; k <= 28; k++) {
				world.setBlock(x+i, y-1, z+k, b, m, 3);
			}
		}
	}

	private void generateDeco(World world, Random rand, int x, int y, int z) {

		Block gl = Blocks.glowstone;
		Block rd = Blocks.redstone_block;
		Block gd = Blocks.gold_block;
		Block em = Blocks.emerald_block;
		Block dm = Blocks.diamond_block;
		world.setBlock(x+4, y+0, z+5, gl);
		world.setBlock(x+4, y+0, z+6, gl);
		world.setBlock(x+4, y+0, z+7, gl);
		world.setBlock(x+4, y+0, z+8, gl);
		world.setBlock(x+4, y+0, z+9, gl);
		world.setBlock(x+4, y+0, z+10, gl);
		world.setBlock(x+4, y+0, z+11, gl);
		world.setBlock(x+4, y+0, z+12, gl);
		world.setBlock(x+4, y+0, z+13, gl);
		world.setBlock(x+4, y+0, z+14, gl);
		world.setBlock(x+4, y+0, z+15, gl);
		world.setBlock(x+4, y+0, z+16, gl);
		world.setBlock(x+4, y+0, z+17, gl);
		world.setBlock(x+4, y+0, z+18, gl);
		world.setBlock(x+4, y+0, z+19, gl);
		world.setBlock(x+4, y+0, z+20, gl);
		world.setBlock(x+4, y+0, z+21, gl);
		world.setBlock(x+4, y+0, z+22, gl);
		world.setBlock(x+4, y+0, z+23, gl);
		world.setBlock(x+5, y+0, z+4, gl);
		world.setBlock(x+5, y+0, z+9, rd);
		world.setBlock(x+5, y+0, z+19, rd);
		world.setBlock(x+5, y+0, z+24, gl);
		world.setBlock(x+6, y+0, z+4, gl);
		world.setBlock(x+6, y+0, z+8, rd);
		world.setBlock(x+6, y+0, z+20, rd);
		world.setBlock(x+6, y+0, z+24, gl);
		world.setBlock(x+7, y+0, z+4, gl);
		world.setBlock(x+7, y+0, z+7, rd);
		world.setBlock(x+7, y+0, z+12, gd);
		world.setBlock(x+7, y+0, z+13, gd);
		world.setBlock(x+7, y+0, z+14, gd);
		world.setBlock(x+7, y+0, z+15, gd);
		world.setBlock(x+7, y+0, z+16, gd);
		world.setBlock(x+7, y+0, z+21, rd);
		world.setBlock(x+7, y+0, z+24, gl);
		world.setBlock(x+8, y+0, z+4, gl);
		world.setBlock(x+8, y+0, z+6, rd);
		world.setBlock(x+8, y+0, z+10, gd);
		world.setBlock(x+8, y+0, z+11, gd);
		world.setBlock(x+8, y+0, z+17, gd);
		world.setBlock(x+8, y+0, z+18, gd);
		world.setBlock(x+8, y+0, z+22, rd);
		world.setBlock(x+8, y+0, z+24, gl);
		world.setBlock(x+9, y+0, z+4, gl);
		world.setBlock(x+9, y+0, z+5, rd);
		world.setBlock(x+9, y+0, z+9, gd);
		world.setBlock(x+9, y+0, z+19, gd);
		world.setBlock(x+9, y+0, z+23, rd);
		world.setBlock(x+9, y+0, z+24, gl);
		world.setBlock(x+10, y+0, z+4, gl);
		world.setBlock(x+10, y+0, z+8, gd);
		world.setBlock(x+10, y+0, z+13, dm);
		world.setBlock(x+10, y+0, z+14, dm);
		world.setBlock(x+10, y+0, z+15, dm);
		world.setBlock(x+10, y+0, z+20, gd);
		world.setBlock(x+10, y+0, z+24, gl);
		world.setBlock(x+11, y+0, z+4, gl);
		world.setBlock(x+11, y+0, z+8, gd);
		world.setBlock(x+11, y+0, z+11, dm);
		world.setBlock(x+11, y+0, z+12, dm);
		world.setBlock(x+11, y+0, z+16, dm);
		world.setBlock(x+11, y+0, z+17, dm);
		world.setBlock(x+11, y+0, z+20, gd);
		world.setBlock(x+11, y+0, z+24, gl);
		world.setBlock(x+12, y+0, z+4, gl);
		world.setBlock(x+12, y+0, z+7, gd);
		world.setBlock(x+12, y+0, z+11, dm);
		world.setBlock(x+12, y+0, z+14, em);
		world.setBlock(x+12, y+0, z+17, dm);
		world.setBlock(x+12, y+0, z+21, gd);
		world.setBlock(x+12, y+0, z+24, gl);
		world.setBlock(x+13, y+0, z+4, gl);
		world.setBlock(x+13, y+0, z+7, gd);
		world.setBlock(x+13, y+0, z+10, dm);
		world.setBlock(x+13, y+0, z+14, em);
		world.setBlock(x+13, y+0, z+18, dm);
		world.setBlock(x+13, y+0, z+21, gd);
		world.setBlock(x+13, y+0, z+24, gl);
		world.setBlock(x+14, y+0, z+4, gl);
		world.setBlock(x+14, y+0, z+7, gd);
		world.setBlock(x+14, y+0, z+10, dm);
		world.setBlock(x+14, y+0, z+12, em);
		world.setBlock(x+14, y+0, z+13, em);
		world.setBlock(x+14, y+0, z+14, em);
		world.setBlock(x+14, y+0, z+15, em);
		world.setBlock(x+14, y+0, z+16, em);
		world.setBlock(x+14, y+0, z+18, dm);
		world.setBlock(x+14, y+0, z+21, gd);
		world.setBlock(x+14, y+0, z+24, gl);
		world.setBlock(x+15, y+0, z+4, gl);
		world.setBlock(x+15, y+0, z+7, gd);
		world.setBlock(x+15, y+0, z+10, dm);
		world.setBlock(x+15, y+0, z+14, em);
		world.setBlock(x+15, y+0, z+18, dm);
		world.setBlock(x+15, y+0, z+21, gd);
		world.setBlock(x+15, y+0, z+24, gl);
		world.setBlock(x+16, y+0, z+4, gl);
		world.setBlock(x+16, y+0, z+7, gd);
		world.setBlock(x+16, y+0, z+11, dm);
		world.setBlock(x+16, y+0, z+14, em);
		world.setBlock(x+16, y+0, z+17, dm);
		world.setBlock(x+16, y+0, z+21, gd);
		world.setBlock(x+16, y+0, z+24, gl);
		world.setBlock(x+17, y+0, z+4, gl);
		world.setBlock(x+17, y+0, z+8, gd);
		world.setBlock(x+17, y+0, z+11, dm);
		world.setBlock(x+17, y+0, z+12, dm);
		world.setBlock(x+17, y+0, z+16, dm);
		world.setBlock(x+17, y+0, z+17, dm);
		world.setBlock(x+17, y+0, z+20, gd);
		world.setBlock(x+17, y+0, z+24, gl);
		world.setBlock(x+18, y+0, z+4, gl);
		world.setBlock(x+18, y+0, z+8, gd);
		world.setBlock(x+18, y+0, z+13, dm);
		world.setBlock(x+18, y+0, z+14, dm);
		world.setBlock(x+18, y+0, z+15, dm);
		world.setBlock(x+18, y+0, z+20, gd);
		world.setBlock(x+18, y+0, z+24, gl);
		world.setBlock(x+19, y+0, z+4, gl);
		world.setBlock(x+19, y+0, z+5, rd);
		world.setBlock(x+19, y+0, z+9, gd);
		world.setBlock(x+19, y+0, z+19, gd);
		world.setBlock(x+19, y+0, z+23, rd);
		world.setBlock(x+19, y+0, z+24, gl);
		world.setBlock(x+20, y+0, z+4, gl);
		world.setBlock(x+20, y+0, z+6, rd);
		world.setBlock(x+20, y+0, z+10, gd);
		world.setBlock(x+20, y+0, z+11, gd);
		world.setBlock(x+20, y+0, z+17, gd);
		world.setBlock(x+20, y+0, z+18, gd);
		world.setBlock(x+20, y+0, z+22, rd);
		world.setBlock(x+20, y+0, z+24, gl);
		world.setBlock(x+21, y+0, z+4, gl);
		world.setBlock(x+21, y+0, z+7, rd);
		world.setBlock(x+21, y+0, z+12, gd);
		world.setBlock(x+21, y+0, z+13, gd);
		world.setBlock(x+21, y+0, z+14, gd);
		world.setBlock(x+21, y+0, z+15, gd);
		world.setBlock(x+21, y+0, z+16, gd);
		world.setBlock(x+21, y+0, z+21, rd);
		world.setBlock(x+21, y+0, z+24, gl);
		world.setBlock(x+22, y+0, z+4, gl);
		world.setBlock(x+22, y+0, z+8, rd);
		world.setBlock(x+22, y+0, z+20, rd);
		world.setBlock(x+22, y+0, z+24, gl);
		world.setBlock(x+23, y+0, z+4, gl);
		world.setBlock(x+23, y+0, z+9, rd);
		world.setBlock(x+23, y+0, z+19, rd);
		world.setBlock(x+23, y+0, z+24, gl);
		world.setBlock(x+24, y+0, z+5, gl);
		world.setBlock(x+24, y+0, z+6, gl);
		world.setBlock(x+24, y+0, z+7, gl);
		world.setBlock(x+24, y+0, z+8, gl);
		world.setBlock(x+24, y+0, z+9, gl);
		world.setBlock(x+24, y+0, z+10, gl);
		world.setBlock(x+24, y+0, z+11, gl);
		world.setBlock(x+24, y+0, z+12, gl);
		world.setBlock(x+24, y+0, z+13, gl);
		world.setBlock(x+24, y+0, z+14, gl);
		world.setBlock(x+24, y+0, z+15, gl);
		world.setBlock(x+24, y+0, z+16, gl);
		world.setBlock(x+24, y+0, z+17, gl);
		world.setBlock(x+24, y+0, z+18, gl);
		world.setBlock(x+24, y+0, z+19, gl);
		world.setBlock(x+24, y+0, z+20, gl);
		world.setBlock(x+24, y+0, z+21, gl);
		world.setBlock(x+24, y+0, z+22, gl);
		world.setBlock(x+24, y+0, z+23, gl);
	}

	private void generatePillars(World world, Random rand, int x, int y, int z) {
		Block cr = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		world.setBlock(x+4, y+0, z+4, cr, 2, 3);
		world.setBlock(x+4, y+0, z+24, cr, 2, 3);
		world.setBlock(x+4, y+1, z+4, cr, 2, 3);
		world.setBlock(x+4, y+1, z+24, cr, 2, 3);
		world.setBlock(x+4, y+2, z+4, cr, 2, 3);
		world.setBlock(x+4, y+2, z+24, cr, 2, 3);
		world.setBlock(x+4, y+3, z+4, cr, 2, 3);
		world.setBlock(x+4, y+3, z+24, cr, 2, 3);
		world.setBlock(x+4, y+4, z+4, cr, 2, 3);
		world.setBlock(x+4, y+4, z+24, cr, 2, 3);
		world.setBlock(x+4, y+5, z+4, cr, 2, 3);
		world.setBlock(x+4, y+5, z+24, cr, 2, 3);
		world.setBlock(x+4, y+6, z+4, cr, 2, 3);
		world.setBlock(x+4, y+6, z+24, cr, 2, 3);
		world.setBlock(x+4, y+7, z+4, cr, 2, 3);
		world.setBlock(x+4, y+7, z+24, cr, 2, 3);
		world.setBlock(x+10, y+1, z+14, cr, 2, 3);
		world.setBlock(x+11, y+1, z+11, cr, 2, 3);
		world.setBlock(x+11, y+1, z+17, cr, 2, 3);
		world.setBlock(x+18, y+1, z+14, cr, 2, 3);
		world.setBlock(x+14, y+1, z+10, cr, 2, 3);
		world.setBlock(x+14, y+1, z+18, cr, 2, 3);
		world.setBlock(x+17, y+1, z+11, cr, 2, 3);
		world.setBlock(x+17, y+1, z+17, cr, 2, 3);
		world.setBlock(x+24, y+0, z+24, cr, 2, 3);
		world.setBlock(x+24, y+0, z+4, cr, 2, 3);
		world.setBlock(x+24, y+1, z+4, cr, 2, 3);
		world.setBlock(x+24, y+1, z+24, cr, 2, 3);
		world.setBlock(x+24, y+2, z+4, cr, 2, 3);
		world.setBlock(x+24, y+2, z+24, cr, 2, 3);
		world.setBlock(x+24, y+3, z+4, cr, 2, 3);
		world.setBlock(x+24, y+3, z+24, cr, 2, 3);
		world.setBlock(x+24, y+4, z+4, cr, 2, 3);
		world.setBlock(x+24, y+4, z+24, cr, 2, 3);
		world.setBlock(x+24, y+5, z+4, cr, 2, 3);
		world.setBlock(x+24, y+5, z+24, cr, 2, 3);
		world.setBlock(x+24, y+6, z+4, cr, 2, 3);
		world.setBlock(x+24, y+6, z+24, cr, 2, 3);
		world.setBlock(x+24, y+7, z+4, cr, 2, 3);
		world.setBlock(x+24, y+7, z+24, cr, 2, 3);
	}

	private void generateStands(World world, Random rand, int x, int y, int z) {
		Block cr = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		world.setBlock(x+7, y+1, z+14, cr, 5, 3);
		world.setBlock(x+9, y+1, z+9, cr, 5, 3);
		world.setBlock(x+9, y+1, z+19, cr, 5, 3);
		world.setBlock(x+10, y+2, z+14, cr, 5, 3);
		world.setBlock(x+11, y+2, z+11, cr, 5, 3);
		world.setBlock(x+11, y+2, z+17, cr, 5, 3);
		world.setBlock(x+14, y+1, z+7, cr, 5, 3);
		world.setBlock(x+14, y+1, z+21, cr, 5, 3);
		world.setBlock(x+14, y+2, z+10, cr, 5, 3);
		world.setBlock(x+14, y+2, z+18, cr, 5, 3);
		world.setBlock(x+18, y+2, z+14, cr, 5, 3);
		world.setBlock(x+17, y+2, z+11, cr, 5, 3);
		world.setBlock(x+17, y+2, z+17, cr, 5, 3);
		world.setBlock(x+19, y+1, z+9, cr, 5, 3);
		world.setBlock(x+19, y+1, z+19, cr, 5, 3);
		world.setBlock(x+21, y+1, z+14, cr, 5, 3);

		world.setBlock(x+14, y+4, z+14, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
	}

	private void generateBody(World world, Random rand, int x, int y, int z, Block b, int m) {
		for (int j = 0; j <= 10; j++) {
			for (int i = 0; i <= 28; i++) {
				for (int k = 0; k <= 28; k++) {
					if (j == 0 || j >= 6 || i == 0 || i == 28 || k == 0 || k == 28)
						world.setBlock(x+i, y+j, z+k, b, m, 3);
				}
			}
		}

	}

}
