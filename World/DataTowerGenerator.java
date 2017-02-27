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
import java.util.Collections;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Magic.Lore.LoreEntry;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
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
				if (this.tryGenerate(world, chunkX*16, chunkZ*16, random)) {
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
				}
			}
		}
	}

	private boolean tryGenerate(World world, int cx, int cz, Random rand) {
		LoreEntry l = LoreManager.instance.getEntry(cx, cz);
		if (l == null)
			return false;
		int x = cx + rand.nextInt(16);
		int z = cz + rand.nextInt(16);
		//generate struct + a chest?
		int y = world.getTopSolidOrLiquidBlock(x, z);
		if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
			int r = 1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					if (!ReikaBlockHelper.isGroundType(world, x+i, y-1, z+k))
						return false;
				}
			}

			FilledBlockArray f = ChromaStructures.getDataTowerStructure(world, x, y, z);
			if (f.isSpaceEmpty(world, true)) {
				f.place();
				int dx = ReikaRandomHelper.getRandomPlusMinus(x, 1);
				int dz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
				world.setBlock(dx, y-1, dz, ChromaBlocks.LOOTCHEST.getBlockInstance(), rand.nextInt(4), 3);
				TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(dx, y-1, dz);
				this.generateLoot(te, rand);
			}
		}
		return false;
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
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
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
