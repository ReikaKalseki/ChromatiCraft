/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EnderPoolGenerator extends WorldGenerator {

	private static final List<Block> placeables = new ArrayList();

	static {
		placeables.add(Blocks.dirt);
		placeables.add(Blocks.grass);
		placeables.add(Blocks.gravel);
		placeables.add(Blocks.stone);
		placeables.add(Blocks.sand);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		for (int i = 2; i < 7; i++) {
			if (placeables.contains(world.getBlock(x, y-i, z)))
				if (this.generateRandomShapeAt(world, random, x, y-i, z, 6))
					return true;
		}
		return false;
	}

	private boolean generateRandomShapeAt(World world, Random r, int x, int y, int z, int max) {
		Block id = ChromatiCraft.instance.getEnderBlockToGenerate();

		BlockArray blocks = new BlockArray();
		boolean gen = false;

		if (r.nextDouble() < 0.5)
			gen = this.generateCircularPool(world, x, y, z, r, id, blocks);
		else
			gen = this.generateEllipticalPool(world, x, y, z, r, id, blocks);

		if (gen) {
			//ReikaJavaLibrary.pConsole(blocks);
			for (int i = 0; i < blocks.getSize(); i++) {
				Coordinate c = blocks.getNthBlock(i);
				if (c.yCoord <= y)
					c.setBlock(world, id);
				else {
					c.setBlock(world, Blocks.air);
					Block bid = world.getBlock(c.xCoord, c.yCoord-1, c.zCoord);
					if (bid == Blocks.dirt) {
						world.setBlock(c.xCoord, c.yCoord-1, c.zCoord, Blocks.grass);
					}
				}
			}
		}

		return gen;
	}

	private boolean generateCircularPool(World world, int x, int y, int z, Random rand, Block id, BlockArray blocks) {
		int d = 2+rand.nextInt(3);
		int[] r = {d,d-1,d-3};
		for (int i = 0; i < 3; i++) {
			for (int j = -r[i]; j <= r[i]; j++) {
				for (int k = -r[i]; k <= r[i]; k++) {
					double dd = ReikaMathLibrary.py3d(j, 0, k);
					if (dd <= r[i]+0.5) {
						//world.setBlock(x+j, y-i, z+k, id);
						if (this.isValidBlock(world, x+j, y-i, z+k))
							blocks.addBlockCoordinate(x+j, y-i, z+k);
						else
							return false;
					}
				}
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = -r[i]-1; j <= r[i]+1; j++) {
				for (int k = -r[i]-1; k <= r[i]+1; k++) {
					double dd = ReikaMathLibrary.py3d(j, 0, k);
					if (dd <= r[i]+1.5) {
						//world.setBlock(x+j, y+i+1, z+k, 0);
						if (world.getBlock(x+j, y+i+1, z+k) instanceof BlockLiquid)
							return false;
						blocks.addBlockCoordinate(x+j, y+i+1, z+k);
					}
				}
			}
		}
		return true;
	}

	private boolean generateEllipticalPool(World world, int x, int y, int z, Random rand, Block id, BlockArray blocks) {
		int d = 2+rand.nextInt(3);
		int[] r = {d,d-1,d-3};
		double sc = 0.5+rand.nextDouble()*0.5;
		for (int i = 0; i < 3; i++) {
			for (int j = -r[i]; j <= r[i]; j++) {
				for (int k = (int)Math.floor(-r[i]*sc); k <= r[i]*sc; k++) {
					double dd = ReikaMathLibrary.py3d(j, 0, k);
					if (dd <= r[i]+0.5) {
						//world.setBlock(x+j, y-i, z+k, id);
						if (this.isValidBlock(world, x+j, y-i, z+k))
							blocks.addBlockCoordinate(x+j, y-i, z+k);
						else
							return false;
					}
				}
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = -r[i]-1; j <= r[i]+1; j++) {
				for (int k = (int)Math.floor(-r[i]*sc-1); k <= r[i]*sc+1; k++) {
					double dd = ReikaMathLibrary.py3d(j, 0, k);
					if (dd <= r[i]+1.5) {
						//world.setBlock(x+j, y+i+1, z+k, 0);
						if (world.getBlock(x+j, y+i+1, z+k) instanceof BlockLiquid)
							return false;
						blocks.addBlockCoordinate(x+j, y+i+1, z+k);
					}
				}
			}
		}
		return true;
	}

	private boolean isValidBlock(World world, int x, int y, int z) {
		Block id = ChromatiCraft.instance.getEnderBlockToGenerate();
		Block idx = world.getBlock(x, y, z);
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		ForgeDirection[] dirs = ForgeDirection.values();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (dir == ForgeDirection.UP) {
				Block bid = world.getBlock(dx, dy, dz);
				if (bid != Blocks.air && bid instanceof BlockLiquid || bid == id)
					return false;
			}
			else {
				boolean soft = ReikaWorldHelper.softBlocks(world, dx, dy, dz);
				boolean ender = world.getBlock(dx, dy, dz) == id;
				if (soft)
					return false;
			}
		}
		return true;
	}

}
