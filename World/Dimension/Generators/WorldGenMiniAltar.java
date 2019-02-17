/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.ArrayList;
import java.util.Random;

import Reika.ChromatiCraft.API.Event.DimensionAltarLootItemEvent;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;


public class WorldGenMiniAltar extends ChromaWorldGenerator {

	public WorldGenMiniAltar(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.025F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		y--;

		if (this.canGenerate(world, x, y, z)) {

			Block bk = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int m = (i == 0 && k == 0) ? BlockType.LIGHT.metadata%8 : BlockType.STONE.metadata%8;
					world.setBlock(x+i, y, z+k, bk, m, 3);
				}
			}

			this.generateArches(world, x, y, z, rand, bk);
			this.generateBurrow(world, x, y, z, rand, bk);
		}

		return false;
	}

	private void generateArches(World world, int x, int y, int z, Random rand, Block bk) {
		int t = 4;

		for (int h = 1; h <= t; h++) {
			world.setBlock(x+2, y+h, z+2, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x-2, y+h, z+2, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+2, y+h, z-2, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x-2, y+h, z-2, bk, BlockType.COBBLE.metadata%8, 3);
		}

		for (int n = -1; n <= 1; n++) {
			world.setBlock(x-2, y+t, z+n, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+2, y+t, z+n, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+n, y+t, z-2, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+n, y+t, z+2, bk, BlockType.COBBLE.metadata%8, 3);

			world.setBlock(x-2, y, z+n, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+2, y, z+n, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+n, y, z-2, bk, BlockType.COBBLE.metadata%8, 3);
			world.setBlock(x+n, y, z+2, bk, BlockType.COBBLE.metadata%8, 3);
		}

		for (int a = -1; a <= 1; a++) {
			for (int b = -1; b <= 1; b++) {
				world.setBlock(x+a, y+t, z+b, bk, BlockType.GLASS.metadata%8, 3);
			}
		}

		world.setBlock(x, y+t, z, bk, BlockType.LIGHT.metadata%8, 3);

		world.setBlock(x, y+t+1, z, ChromaBlocks.LAMP.getBlockInstance(), rand.nextInt(16), 3);
	}

	private void generateBurrow(World world, int x, int y, int z, Random rand, Block bk) {
		int yc = 3;
		for (int d = 1; d <= yc+1; d++) {
			for (int a = -2; a <= 2; a++) {
				for (int b = -2; b <= 2; b++) {
					boolean wall = Math.abs(a) == 2 || Math.abs(b) == 2 || d == yc+1;
					world.setBlock(x+a, y-d, z+b, wall ? bk : Blocks.air, wall ? BlockType.STONE.metadata%8 : 0, 3);
				}
			}
		}

		this.generateChest(world, x, y, z, yc, rand);
	}

	private void generateChest(World world, int x, int y, int z, int yc, Random rand) {
		world.setBlock(x, y-yc, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), rand.nextInt(4), 3);

		TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(x, y-yc, z);

		int n = 4+rand.nextInt(24);
		ArrayList<ItemStack> li = ItemMagicRegistry.instance.getAllRegisteredItems();
		for (int i = 0; i < n; i++) {
			int idx = rand.nextInt(li.size());
			ItemStack in = li.get(idx);
			if (MinecraftForge.EVENT_BUS.post(new DimensionAltarLootItemEvent(te, in)))
				continue;
			ElementTagCompound value = ItemMagicRegistry.instance.getItemValue(in);
			int max = Math.min(16, Math.max(1, 24/value.getMaximumValue()));
			int num = Math.min(1+rand.nextInt(max), in.getMaxStackSize());
			ItemStack is = ReikaItemHelper.getSizedItemStack(in, num);

			int slot = rand.nextInt(te.getSizeInventory());
			while (te.getStackInSlot(slot) != null) {
				slot = rand.nextInt(te.getSizeInventory());
			}
			te.setInventorySlotContents(slot, is);
		}

		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (b instanceof ChromaDimensionBiome) {
			ItemStack is = this.getRandomBiomeItem((ChromaDimensionBiome)b);
			if (is != null) {
				te.setInventorySlotContents(rand.nextInt(te.getSizeInventory()), is);
			}
		}
	}

	private ItemStack getRandomBiomeItem(ChromaDimensionBiome biome) {
		switch(biome.biomeType) {
			case FOREST:
				return ChromaStacks.multiShard;//ChromaStacks.forestloot;
			case ISLANDS:
				return null;//ChromaStacks.islandloot; //maybe generates in oceans?
			case PLAINS:
				return ChromaStacks.iridCrystal;//ChromaStacks.plainsloot;
			case SKYLANDS:
				return null;//ChromaStacks.skylandloot; //currently cannot generate here
			default:
				return null;
		}
	}

	private boolean canGenerate(World world, int x, int y, int z) {
		int r = 3;
		for (int i = -r; i <= 3; i++) {
			for (int k = -r; k <= 3; k++) {
				if (world.getBlock(x+i, y, z+k) != Blocks.grass || world.getBlock(x+i, y+1, z+k) != Blocks.air) {
					//ReikaJavaLibrary.pConsole(world.getBlock(x+i, y, z+k)+":"+world.getBlock(x+i, y+1, z+k));
					return false;
				}
			}
		}
		return true;
	}

}
