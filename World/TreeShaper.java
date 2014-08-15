/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TreeShaper {

	public static final int LUMPY_CHANCE = 10;
	public static final int TALL_CHANCE = 30;
	public static final int NORMAL_CHANCE = 60;

	private static final Block leafID = ChromaBlocks.DECAY.getBlockInstance();

	private static final TreeShaper instance = new TreeShaper();

	private final Random rand = new Random();

	private final ArrayList<ItemStack> validLogs = new ArrayList();

	public boolean isLogTypeEverAllowed(ModWoodList wood) {
		return wood != ModWoodList.BAMBOO;
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

	public void generateRandomWeightedTree(World world, int x, int y, int z, ReikaDyeHelper color, boolean forceGen) {
		int val = rand.nextInt(100);
		int chance = LUMPY_CHANCE;
		if (val < chance) {
			this.generateLumpyTree(world, x, y, z, color, forceGen);
			return;
		}
		chance += TALL_CHANCE;
		if (val < chance) {
			this.generateTallTree(world, x, y, z, color, forceGen);
			return;
		}
		this.generateNormalTree(world, x, y, z, color, forceGen);
	}

	public ItemStack getLogType() {
		return validLogs.get(rand.nextInt(validLogs.size()));
	}

	public void generateNormalTree(World world, int x, int y, int z, ReikaDyeHelper color, boolean force) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int meta = color.ordinal();
			ItemStack log = this.getLogType();
			int w = 2;
			int h = 5+rand.nextInt(3);

			for (int i = 0; i < h; i++) {
				ReikaWorldHelper.setBlock(world, x, y+i, z, log);
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-3, z+j)) {
						world.setBlock(x+i, y+h-3, z+j, leafID, meta, 3);
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
		}
	}

	public void generateTallTree(World world, int x, int y, int z, ReikaDyeHelper color, boolean force) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int h = 10+rand.nextInt(3);
			ItemStack log = this.getLogType();
			int meta = color.ordinal();
			int w = 2;

			for (int i = 0; i < h; i++) {
				ReikaWorldHelper.setBlock(world, x, y+i, z, log);
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i*j == 0)
						if (this.canGenerateLeavesAt(world, x+i, y+h-8, z+j)) {
							world.setBlock(x+i, y+h-8, z+j, leafID, meta, 3);
						}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (this.canGenerateLeavesAt(world, x+i, y+h-7, z+j)) {
						world.setBlock(x+i, y+h-7, z+j, leafID, meta, 3);
					}
				}
			}
			for (int i = -w; i <= w; i++) {
				for (int j = -w; j <= w; j++) {
					if (i*j != w*w && i*j != -w*w)
						if (this.canGenerateLeavesAt(world, x+i, y+h-6, z+j)) {
							world.setBlock(x+i, y+h-6, z+j, leafID, meta, 3);
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
		}
	}

	public void generateLumpyTree(World world, int x, int y, int z, ReikaDyeHelper color, boolean force) {
		if (force || ColorTreeGenerator.canGenerateTree(world, x, z)) {
			int h = 8+rand.nextInt(4);
			ItemStack log = this.getLogType();
			int meta = color.ordinal();

			for (int i = 0; i < h; i++) {
				ReikaWorldHelper.setBlock(world, x, y+i, z, log);
			}

			for (int i = 1; i < 2; i++) {
				int dx = x+i;
				int dy = y+h-2;
				int dz = z;
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
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
				ReikaWorldHelper.setBlock(world, dx, dy, dz, log);
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						for (int m = -1; m <= 1; m++) {
							if (j*k*m == 0 && this.canGenerateLeavesAt(world, dx+j, dy+k, dz+m)) {
								world.setBlock(dx+j, dy+k, dz+m, leafID, meta, 3);
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
		}
	}

	private boolean canGenerateLeavesAt(World world, int x, int y, int z) {
		boolean soft = ReikaWorldHelper.softBlocks(world, x, y, z);
		boolean leaves = world.getBlock(x, y, z) == Blocks.leaves || world.getBlock(x, y, z) == Blocks.leaves2;
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, !soft && !leaves && world.getBlock(x, y, z) != Blocks.log.blockID);
		return soft || leaves;
	}

}