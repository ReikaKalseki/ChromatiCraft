/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class DataTowerGenerator implements RetroactiveGenerator {

	public static final DataTowerGenerator instance = new DataTowerGenerator();

	private final ArrayList<TowerLoot> loot = new ArrayList();

	private DataTowerGenerator() {
		loot.add(new TowerLoot(Items.diamond, 2));
		loot.add(new TowerLoot(Items.emerald, 1));
		loot.add(new TowerLoot(Items.gold_ingot, 5));
		loot.add(new TowerLoot(Items.iron_ingot, 8));
		loot.add(new TowerLoot(Items.redstone, 8));
		loot.add(new TowerLoot(ReikaItemHelper.lapisDye, 12));
	}

	public void addLoot(ItemStack is, int max) {
		loot.add(new TowerLoot(is, max));
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			if (this.isGennableChunk(world, chunkX*16, chunkZ*16, random)) {
				if (this.generate(world, chunkX*16, chunkZ*16, random)) {
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
				}
			}
		}
	}

	private boolean generate(World world, int cx, int cz, Random rand) {
		Towers tower = LoreManager.instance.getTower(world, cx, cz);
		if (tower == null)
			return false;

		Coordinate c = tower.getGeneratedLocation();

		int x = 0;
		int y = 0;
		int z = 0;
		if (c == null) {
			x = cx + 8;//rand.nextInt(16);
			z = cz + 8;//rand.nextInt(16);
			y = this.prepareForTower(world, x, z);

			int r = 1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					while (ReikaWorldHelper.softBlocks(world, dx, y-1, dz) || ReikaBlockHelper.isLeaf(world, dx, y-1, dz) || ReikaBlockHelper.isWood(world, dx, y-1, dz)) {
						y--;
					}
				}
			}

			tower.generatedAt(x, y, z);
		}
		else {
			x = c.xCoord;
			y = c.yCoord;
			z = c.zCoord;
		}

		FilledBlockArray f = ChromaStructures.getDataTowerStructure(world, x, y, z);
		//if (f.isSpaceEmpty(world, true)) {
		f.place();
		TileEntityDataNode td = (TileEntityDataNode)world.getTileEntity(x, y+1, z);
		td.setTower(tower);
		int dx = ReikaRandomHelper.getRandomPlusMinus(x, 1);
		int dz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
		world.setBlock(dx, y-1, dz, ChromaBlocks.LOOTCHEST.getBlockInstance(), rand.nextInt(4), 3);
		TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(dx, y-1, dz);
		this.generateLoot(te, rand);
		//}
		//}
		return true;
	}

	private int prepareForTower(World world, int x, int z) {
		int y = world.getTopSolidOrLiquidBlock(x, z);
		Block b = world.getBlock(x, y, z);
		while (b.isAir(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y, z) || ReikaBlockHelper.isWood(world, x, y, z)) {
			y--;
			b = world.getBlock(x, y, z);
		}
		if (ReikaBlockHelper.isLiquid(world.getBlock(x, y+1, z))) {
			this.prepareIsland(world, x, y, z);
		}
		else if (y > 250) {
			this.prepareCrater(world, x, y, z);
		}
		return world.getTopSolidOrLiquidBlock(x, z);
	}

	private void prepareIsland(World world, int x, int y, int z) {
		int r = 18;
		int r2 = 3;
		double F = r2/(double)r;
		int h = 64-y;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				double dr = ReikaMathLibrary.py3d(i, 0, k);
				if (dr <= r) {
					double f = dr/r;
					double df = f < F ? 1 : Math.pow(1-((f-F)/(1-F)), 2);
					int dh = (int)Math.ceil(h*df);
					BiomeGenBase b = world.getBiomeGenForCoords(dx, dz);
					if (dh == h) {
						dh--;
					}
					int j1 = Math.min(0, world.getTopSolidOrLiquidBlock(dx, dz)-y);
					for (int j = j1; j <= dh; j++) {
						int dy = y+j;
						Block b2 = Blocks.stone;
						if (j == dh && dy >= 62) {
							b2 = b.topBlock;
						}
						else if (dh-j <= 3) {
							b2 = b.fillerBlock;
						}
						world.setBlock(dx, dy, dz, b2);
					}
				}
			}
		}
	}

	private void prepareCrater(World world, int x, int y, int z) {
		int r = 12;
		int d = y-250;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				double dr = ReikaMathLibrary.py3d(i, 0, k);
				if (dr <= 4) {
					for (int dy = y; dy <= y+7; dy++) {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
				else if (dr <= r) {
					double f = Math.min(1, 1-dr/r);
					int dd = (int)Math.ceil(f*d);
					for (int dy = 250+d-dd; dy < 256; dy++) {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}
	}

	private void generateLoot(TileEntityLootChest te, Random rand) {
		te.populateChest(ChestGenHooks.STRONGHOLD_LIBRARY, Structures.CAVERN, 2, rand);
		Collections.shuffle(loot);
		for (TowerLoot t : loot) {
			int n = rand.nextInt(1+t.maxCount);
			for (int i = 0; i < n; i++) {
				int slot = rand.nextInt(te.getSizeInventory());
				if (te.getStackInSlot(slot) == null) {
					te.setInventorySlotContents(slot, t.item.copy());
				}
			}
		}
	}

	private boolean isVoidWorld(World world, int x, int z) {
		return world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z);
	}

	private boolean isGennableChunk(World world, int x, int z, Random r) {
		if (this.isVoidWorld(world, x, z))
			return false;
		return true;
	}

	private boolean canGenerateIn(World world) {
		if (world.provider.dimensionId != 0)
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !ChromaOptions.FLATGEN.getState()) {
			return ReikaWorldHelper.getSuperflatHeight(world) > 15;
		}
		return true;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Lore Towers";
	}

	public static class TowerLoot {

		private final ItemStack item;
		private final int maxCount;

		public TowerLoot(Item i, int n) {
			this(new ItemStack(i), n);
		}

		public TowerLoot(ItemStack is, int n) {
			item = is.copy();
			item.stackSize = 1;
			maxCount = n;
		}

	}

}
