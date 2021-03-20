/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.IWG.ColorTreeGenerator;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public class TreeShaper {

	private static final Block leafID = ChromaBlocks.DECAY.getBlockInstance();
	private static final float BASE_VINE_CHANCE = 0.2F;
	private static final float BASE_FERTILE_CHANCE = 0.6F;//0.4F;

	private static enum TreeShape {
		BASIC(60),
		TALL(30),
		LUMPY(10),
		;

		private final int spawnWeight;

		private TreeShape(int weight) {
			spawnWeight = weight;
		}
	}

	private static final TreeShaper instance = new TreeShaper();

	private final ArrayList<BlockKey> validLogs = new ArrayList();
	private final WeightedRandom<TreeShape> treeRand = new WeightedRandom();

	public boolean isLogTypeEverAllowed(ModWoodList wood) {
		return wood != ModWoodList.BAMBOO && wood != ModWoodList.LIGHTED && wood != ModWoodList.SLIME && wood != ModWoodList.TAINTED;
	}

	private TreeShaper() {
		for (int i = 0; i < ReikaTreeHelper.treeList.length; i++) {
			ReikaTreeHelper tree = ReikaTreeHelper.treeList[i];
			if (ChromatiCraft.config.shouldGenerateLogType(tree)) {
				validLogs.add(tree.getLog());
				ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getName()+" logs: Enabled");
			}
			else {
				ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getName()+" logs: Disabled");
			}
		}

		for (TreeShape shape : TreeShape.values()) {
			treeRand.addEntry(shape, shape.spawnWeight);
		}

		for (int i = 0; i < ModWoodList.woodList.length; i++) {
			ModWoodList tree = ModWoodList.woodList[i];
			if (this.isLogTypeEverAllowed(tree)) {
				if (tree.exists()) {
					if (ChromatiCraft.config.shouldGenerateLogType(tree)) {
						validLogs.add(tree.getItem());
						ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getBasicInfo()+": Enabled");
					}
					else {
						ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getBasicInfo()+": Disabled (Config Option)");
					}
				}
				else {
					ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getBasicInfo()+": Disabled (Not Loaded)");
				}
			}
			else {
				ChromatiCraft.logger.log("Dye Tree Generation with Log Type "+tree.getBasicInfo()+": Disabled (Disallowed)");
			}
		}

		if (validLogs.isEmpty()) {
			throw new InstallationException(ChromatiCraft.instance, "You must enable at least one log type!");
		}
	}

	public static TreeShaper getInstance() {
		return instance;
	}

	public void generateRandomWeightedTree(World world, int x, int y, int z, Random rand, ReikaDyeHelper color, boolean forceGen, float vineChance, float vineFertilityFactor) {
		treeRand.setRNG(rand);
		TreeShape shape = treeRand.getRandomEntry();
		int top = -1;
		switch(shape) {
			case BASIC:
				top = this.generateNormalTree(world, x, y, z, rand, color, forceGen, vineChance, vineFertilityFactor);
				break;
			case TALL:
				top = this.generateTallTree(world, x, y, z, rand, color, forceGen, vineChance, vineFertilityFactor);
				break;
			case LUMPY:
				top = this.generateLumpyTree(world, x, y, z, rand, color, forceGen, vineChance, vineFertilityFactor);
				break;
		}
		/*
		if (top >= 0 && vineChance > 0) {

		}*/
	}

	public BlockKey getLogType(Random rand) {
		return validLogs.get(rand.nextInt(validLogs.size()));
	}

	private void generateVine(float fertileFactor, World world, int x, int y, int z, int meta, Random rand) {
		boolean below = world.getBlock(x, y-1, z).isAir(world, x, y-1, z);
		int n = 0;
		int max = ReikaRandomHelper.getRandomBetween(1, 3, rand);
		while (n < max && below) {
			int y0 = y;
			y--;
			below = world.getBlock(x, y-1, z).isAir(world, x, y-1, z);
			Block b = fertileFactor > 0 && !below && ReikaRandomHelper.doWithChance(BASE_FERTILE_CHANCE*fertileFactor) ? ChromaBlocks.FERTILEDYEVINE.getBlockInstance() : ChromaBlocks.DYEVINE.getBlockInstance();
			world.setBlock(x, y0, z, b, meta, 2);
			n++;
		}
	}

	public int generateNormalTree(World world, int x, int y, int z, Random rand, ReikaDyeHelper color, boolean force, float vineChance, float vineFertility) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int meta = color.ordinal();
			BlockKey log = this.getLogType(rand);
			int w = 2;
			int h = 5+rand.nextInt(3);

			for (int i = 0; i < h; i++) {
				log.place(world, x, y+i, z);
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-3, z+j)) {
						world.setBlock(x+i, y+h-3, z+j, leafID, meta, 3);
						if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
							this.generateVine(vineFertility, world, x+i, y+h-4, z+j, meta, rand);
						}
					}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-2, z+j)) {
						world.setBlock(x+i, y+h-2, z+j, leafID, meta, 3);
					}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-1, z+j)) {
						world.setBlock(x+i, y+h-1, z+j, leafID, meta, 3);
					}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i*j == 0) {
						if (this.canGenerateLeavesAt(world, x+i, y+h, z+j)) {
							world.setBlock(x+i, y+h, z+j, leafID, meta, 3);
						}
					}
				}
			}
			return y+h-4;
		}
		return -1;
	}

	public int generateTallTree(World world, int x, int y, int z, Random rand, ReikaDyeHelper color, boolean force, float vineChance, float vineFertility) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int h = 10+rand.nextInt(3);
			BlockKey log = this.getLogType(rand);
			int meta = color.ordinal();
			int w = 2;

			for (int i = 0; i < h; i++) {
				log.place(world, x, y+i, z);
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i*j == 0)
						if (this.canGenerateLeavesAt(world, x+i, y+h-8, z+j)) {
							world.setBlock(x+i, y+h-8, z+j, leafID, meta, 3);
							if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
								this.generateVine(vineFertility, world, x+i, y+h-9, z+j, meta, rand);
							}
						}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-7, z+j)) {
						world.setBlock(x+i, y+h-7, z+j, leafID, meta, 3);
						if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
							this.generateVine(vineFertility, world, x+i, y+h-8, z+j, meta, rand);
						}
					}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (i*j != w*w && i*j != -w*w)
						if (this.canGenerateLeavesAt(world, x+i, y+h-6, z+j)) {
							world.setBlock(x+i, y+h-6, z+j, leafID, meta, 3);
							if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
								this.generateVine(vineFertility, world, x+i, y+h-7, z+j, meta, rand);
							}
						}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (i*j != w*w && i*j != -w*w)
						if (this.canGenerateLeavesAt(world, x+i, y+h-5, z+j)) {
							world.setBlock(x+i, y+h-5, z+j, leafID, meta, 3);
						}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-4, z+j)) {
						world.setBlock(x+i, y+h-4, z+j, leafID, meta, 3);
					}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (i*j != w*w && i*j != -w*w)
						if (this.canGenerateLeavesAt(world, x+i, y+h-3, z+j)) {
							world.setBlock(x+i, y+h-3, z+j, leafID, meta, 3);
						}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (i*j != w*w && i*j != -w*w)
						if (this.canGenerateLeavesAt(world, x+i, y+h-2, z+j)) {
							world.setBlock(x+i, y+h-2, z+j, leafID, meta, 3);
						}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-1, z+j)) {
						world.setBlock(x+i, y+h-1, z+j, leafID, meta, 3);
					}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i*j == 0) {
						if (this.canGenerateLeavesAt(world, x+i, y+h, z+j)) {
							world.setBlock(x+i, y+h, z+j, leafID, meta, 3);
						}
					}
				}
			}
			return y+h-9;
		}
		return -1;
	}

	public int generateLumpyTree(World world, int x, int y, int z, Random rand, ReikaDyeHelper color, boolean force, float vineChance, float vineFertility) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int h = 8+rand.nextInt(4);
			BlockKey log = this.getLogType(rand);
			int meta = color.ordinal();

			for (int i = 0; i < h; i++) {
				log.place(world, x, y+i, z);
			}

			for (int i = 1; i < 2; i++) {
				int dx = x+i;
				int dy = y+h-2;
				int dz = z;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
							}
						}
					}
				}

				dx = x-i;
				dz = z;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
							}
						}
					}
				}

				dx = x;
				dz = z-i;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
							}
						}
					}
				}

				dx = x;
				dz = z+i;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
							}
						}
					}
				}

				dx = x+i;
				dy = y+h-6;
				dz = z;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
								if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
									this.generateVine(vineFertility, world, dx+j, dy+k-1, dz+m, meta, rand);
								}
							}
						}
					}
				}

				dx = x-i;
				dz = z;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
								if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
									this.generateVine(vineFertility, world, dx+j, dy+k-1, dz+m, meta, rand);
								}
							}
						}
					}
				}

				dx = x;
				dz = z-i;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
								if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
									this.generateVine(vineFertility, world, dx+j, dy+k-1, dz+m, meta, rand);
								}
							}
						}
					}
				}

				dx = x;
				dz = z+i;
				log.place(world, dx, dy, dz);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
								if (vineChance > 0 && ReikaRandomHelper.doWithChance(BASE_VINE_CHANCE*vineChance, rand)) {
									this.generateVine(vineFertility, world, dx+j, dy+k-1, dz+m, meta, rand);
								}
							}
						}
					}
				}
			}

			int dy = y+h-4;
			for (int i = 1; i < 2; i++) {
				if (this.canGenerateLeavesAt(world, x+i, dy, z)) {
					world.setBlock(x+i, dy, z, leafID, meta, 3);
				}
				if (this.canGenerateLeavesAt(world, x-i, dy, z)) {
					world.setBlock(x-i, dy, z, leafID, meta, 3);
				}
				if (this.canGenerateLeavesAt(world, x, dy, z+i)) {
					world.setBlock(x, dy, z+i, leafID, meta, 3);
				}
				if (this.canGenerateLeavesAt(world, x, dy, z-i)) {
					world.setBlock(x, dy, z-i, leafID, meta, 3);
				}
			}

			dy = y+h;
			for (int k = -1; k <= 1; k++) {
				for (int m = -1; m <= 1; m++) {
					if (k*m == 0 && this.canGenerateLeavesAt(world, x+k, dy, z+m)) {
						world.setBlock(x+k, dy, z+m, leafID, meta, 3);
					}
				}
			}
			return y+h-8;
		}
		return -1;
	}

	public static boolean canGenerateLeavesAt(World world, int x, int y, int z) {
		boolean soft = ReikaWorldHelper.softBlocks(world, x, y, z);
		boolean leaves = world.getBlock(x, y, z) == Blocks.leaves || world.getBlock(x, y, z) == Blocks.leaves2;
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, !soft && !leaves && world.getBlock(x, y, z) != Blocks.log.blockID);
		return soft || leaves;
	}

}
