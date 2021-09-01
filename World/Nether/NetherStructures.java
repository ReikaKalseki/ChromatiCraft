/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Nether;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;


public enum NetherStructures {

	HUT(200),
	TEMPLE(15),
	MAZE(30),
	SPIRAL(24),
	DIORAMA(8);

	static final double BASE_GEN_FACTOR = 0.03125/2;
	private static final WeightedRandom<NetherStructures> structureRand = new WeightedRandom();

	private final double genChance;

	private static final NetherStructures[] list = values();

	private NetherStructures(double c) {
		genChance = c;
	}

	static {
		for (int i = 0; i < list.length; i++) {
			structureRand.addEntry(list[i], list[i].genChance);
		}
	}

	static NetherStructures getRandomStructure() {
		return structureRand.getRandomEntry();
	}

	public void generate(World world, int x, int z, Random rand) {
		int y = 127+1; //top solid layer+1
		switch(this) {
			case HUT:
				for (int i = -4; i <= 4; i++) {
					for (int k = -4; k <= 4; k++) {
						int h = Math.abs(i) == 4 || Math.abs(k) == 4 ? 0 : 1;
						for (int j = 0; j <= h; j++) {
							int dx = x+i;
							int dz = z+k;
							int dy = y+j;
							world.setBlock(dx, dy, dz, Blocks.bedrock);
						}
					}
				}
				for (int i = -2; i <= 2; i++) {
					world.setBlock(x+i, y, z+2, Blocks.tnt);
					world.setBlock(x+i, y, z-2, Blocks.tnt);
					world.setBlock(x+2, y, z+i, Blocks.tnt);
					world.setBlock(x-2, y, z+i, Blocks.tnt);
				}
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						int dx = x+i;
						int dz = z+k;
						world.setBlock(dx, y+1, dz, Blocks.nether_brick);

						if (Math.abs(i) != 2 || Math.abs(k) != 2)
							world.setBlock(dx, y+5, dz, Blocks.nether_brick);
					}
				}
				for (int j = 2; j <= 4; j++) {
					world.setBlock(x-2, y+j, z-2, Blocks.nether_brick);
					world.setBlock(x+2, y+j, z-2, Blocks.nether_brick);
					world.setBlock(x-2, y+j, z+2, Blocks.nether_brick);
					world.setBlock(x+2, y+j, z+2, Blocks.nether_brick);
				}
				world.setBlock(x+1, y, z, Blocks.tnt);
				world.setBlock(x-1, y, z, Blocks.tnt);
				world.setBlock(x, y, z+1, Blocks.tnt);
				world.setBlock(x, y, z-1, Blocks.tnt);
				world.setBlock(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance());
				TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(x, y, z);
				te.populateChest(ChestGenHooks.DUNGEON_CHEST, null, 0, rand, false);
				world.setBlock(x, y+1, z, Blocks.lava);
				break;
			case TEMPLE:
				NetherTemple.generateAt(world, x, y, z, rand);
				break;
			case MAZE:
				NetherMaze.generateAt(world, x, y, z, rand);
				break;
			case DIORAMA:
				NetherDiorama.generateAt(world, x, y, z, rand);
				break;
			case SPIRAL:
				NetherSpiral.generateAt(world, x, y, z, rand);
				break;
		}
	}

}
