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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSavanna;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.Location;
import Reika.DragonAPI.Instantiable.Worldgen.VillageBuilding.PerVillageStructureEntry;
import Reika.DragonAPI.Instantiable.Worldgen.VillageBuilding.StructureEntry;
import Reika.DragonAPI.Instantiable.Worldgen.VillageBuilding.VillagePiece;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;

import cpw.mods.fml.common.registry.VillagerRegistry;



public class VillagersFailChromatiCraft {

	private static final ArrayList<StructureEntry> entries = new ArrayList();

	static {
		entries.add(new PerVillageStructureEntry(BrokenChromaStructure.class, 1, "FailChC", 15, 6, 16, ChromaOptions.getVillageStructureRarity(7, 3)));
		entries.add(new PerVillageStructureEntry(WoodenChromaStructure.class, 4, "ChCHouse", 15, 8, 15, ChromaOptions.getVillageStructureRarity(4, 1)));
	}

	public static void register() {
		for (StructureEntry e : entries) {
			VillagerRegistry.instance().registerVillageCreationHandler(e.build());
		}
	}

	private static abstract class ChromaVillagePiece extends VillagePiece {

		protected static final WeightedRandom<ItemStack> itemRand = new WeightedRandom();

		static {
			itemRand.addEntry(new ItemStack(Items.coal), 125);
			itemRand.addEntry(new ItemStack(Items.iron_ingot), 100);
			itemRand.addEntry(new ItemStack(Items.gold_ingot), 50);
			itemRand.addEntry(new ItemStack(Items.redstone), 75);
			itemRand.addEntry(ReikaItemHelper.lapisDye, 40);
			itemRand.addEntry(new ItemStack(Items.diamond), 10);
			itemRand.addEntry(new ItemStack(Items.emerald), 2);
		}

		public ChromaVillagePiece() {
			super();
		}

		public ChromaVillagePiece(StructureVillagePieces.Start start, int par2, Random rand, StructureBoundingBox bb, int par5, int x, int y, int z) {
			super(start, par2, rand, bb, par5, x, y, z);
		}

		protected final TileEntityLootChest generateLootChest(World world, int i, int j, int k, Location s, int bonus, int dir, Random rand) {
			return ChromaAux.generateLootChest(world, this.getXWithOffset(i, k), this.getYWithOffset(j), this.getZWithOffset(i, k), dir, rand, s, bonus);
		}

		protected final TileEntityLootChest generateLootChestFixed(World world, int i, int j, int k, Location s, int bonus, int dir, Random rand) {
			return ChromaAux.generateLootChest(world, i+boundingBox.minX, j+boundingBox.minY, k+boundingBox.minZ, dir, rand, s, bonus);
		}

	}

	public static class WoodenChromaStructure extends ChromaVillagePiece {

		private BiomeGenBase genBiome;

		public WoodenChromaStructure(Start start, int par2, Random rand, StructureBoundingBox bb, int par5, int x, int y, int z) {
			super(start, par2, rand, bb, par5, x, y, z);
		}

		public WoodenChromaStructure() {
			super();
		}
		/*
		@Override
		public int getMinimumSeparation() {
			return 2048;
		}
		 */
		@Override
		protected boolean generate(World world, Random rand) {

			//ReikaJavaLibrary.pConsole("Genning "+boundingBox+" @ "+coordBaseMode);

			this.rise(world);
			this.clearVolume(world);

			genBiome = world.getBiomeGenForCoords(boundingBox.getCenterX(), boundingBox.getCenterZ());

			this.placeBlockAtFixedPosition(world, 0, 0, 4, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 0, 0, 5, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 0, 0, 6, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 0, 0, 7, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 0, 0, 8, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 0, 0, 9, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 0, 0, 10, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 1, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 2, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 3, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 5, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 9, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 11, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 12, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 1, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 7, Blocks.double_stone_slab);
			this.placeBlockAtFixedPosition(world, 1, 1, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 1, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 1, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 1, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 1, 2, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 2, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 2, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 2, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 2, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 2, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 2, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 2, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 2, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 2, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 2, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 3, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 3, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 3, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 3, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 3, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 3, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 3, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 3, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 3, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 3, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 3, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 3, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 3, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 4, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 4, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 4, 5, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 6, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 4, 8, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 9, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 4, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 4, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 5, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 5, 2, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 5, 3, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 5, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 5, 5, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 5, 6, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 5, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 1, 5, 8, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 5, 9, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 1, 5, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 5, 11, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 5, 12, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 5, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 6, 4, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 1, 6, 5, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 6, 6, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 6, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 1, 6, 8, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 6, 9, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 1, 6, 10, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 2, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 2, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 2, 1, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 1, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 2, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 2, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 3, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 3, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 2, 5, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 2, 5, 2, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 3, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 4, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 10, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 11, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 12, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 5, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 2, 6, 2, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 6, 3, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 6, 4, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 6, 5, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 6, 6, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 6, 7, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 6, 8, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 6, 9, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 2, 6, 10, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 6, 11, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 6, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 2, 7, 5, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 7, 6, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 7, 7, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 7, 8, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 2, 7, 9, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 3, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 3, 1, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 1, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 2, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 2, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 3, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 3, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 3, 5, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 3, 5, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 3, 5, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 3, 5, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 3, 6, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 3, 6, 3, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 3, 6, 4, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 3, 6, 10, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 3, 6, 11, this.getStair(world), 5);
			this.placeBlockAtFixedPosition(world, 3, 6, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 3, 7, 3, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 3, 7, 4, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 3, 7, 5, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 3, 7, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 7, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 3, 7, 9, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 3, 7, 10, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 3, 7, 11, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 4, 0, 0, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 4, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 5, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 9, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 0, 14, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 4, 1, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 1, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 1, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 1, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 4, 2, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 2, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 3, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 3, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 4, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 4, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 4, 5, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 5, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 4, 5, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 4, 5, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 6, 1, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 4, 6, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 4, 6, 3, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 4, 6, 11, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 4, 6, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 4, 6, 13, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 4, 7, 3, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 4, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 4, 7, 11, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 5, 0, 0, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 5, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 5, 0, 14, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 5, 3, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 3, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 5, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 5, 5, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 5, 5, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 5, 6, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 5, 6, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 5, 6, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 5, 6, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 5, 7, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 5, 7, 3, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 5, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 7, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 7, 6, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 5, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 7, 8, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 5, 7, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 5, 7, 11, this.getStair(world), 0);
			this.placeBlockAtFixedPosition(world, 5, 7, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 6, 0, 0, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 6, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 0, 14, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 6, 1, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 6, 1, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 2, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 2, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 3, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 3, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 6, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 6, 5, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 6, 5, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 6, 6, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 6, 6, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 6, 6, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 6, 6, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 6, 7, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 6, 7, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 7, 5, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 6, 7, 6, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 6, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 7, 8, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 6, 7, 9, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 6, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 7, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 6, 7, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 7, 0, 0, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 7, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 2, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 3, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 11, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 12, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 7, 0, 14, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 7, 1, 1, Blocks.double_stone_slab);
			this.placeBlockAtFixedPosition(world, 7, 1, 7, Blocks.crafting_table);
			this.placeBlockAtFixedPosition(world, 7, 1, 13, Blocks.double_stone_slab);
			this.placeBlockAtFixedPosition(world, 7, 2, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 2, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 3, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 3, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 4, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 4, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 5, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 5, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 7, 6, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 6, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 7, 6, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 7, 6, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 7, 7, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 7, Blocks.glowstone);
			this.placeBlockAtFixedPosition(world, 7, 7, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 7, 7, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 8, 0, 0, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 8, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 0, 14, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 8, 1, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 8, 1, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 2, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 2, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 3, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 3, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 8, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 8, 5, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 8, 5, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 8, 6, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 8, 6, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 8, 6, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 8, 6, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 8, 7, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 8, 7, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 7, 5, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 8, 7, 6, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 8, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 7, 8, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 8, 7, 9, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 8, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 7, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 8, 7, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 9, 0, 0, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 9, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 9, 0, 14, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 9, 3, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 3, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 9, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 9, 5, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 9, 5, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 9, 6, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 9, 6, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 9, 6, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 9, 6, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 9, 7, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 9, 7, 3, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 9, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 7, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 7, 6, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 9, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 7, 8, Blocks.glass);
			this.placeBlockAtFixedPosition(world, 9, 7, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 9, 7, 11, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 9, 7, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 10, 0, 0, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 10, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 5, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 9, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 0, 14, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 10, 1, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 1, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 1, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 1, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 10, 2, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 2, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 3, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 3, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 4, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 4, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 10, 5, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 5, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 10, 5, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 10, 5, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 6, 1, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 10, 6, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 10, 6, 3, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 10, 6, 11, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 10, 6, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 10, 6, 13, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 10, 7, 3, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 10, 7, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 10, 7, 11, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 11, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 11, 1, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 1, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 2, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 2, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 3, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 3, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 11, 5, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 11, 5, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 11, 5, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 11, 5, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 11, 6, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 11, 6, 3, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 11, 6, 4, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 11, 6, 10, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 11, 6, 11, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 11, 6, 12, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 11, 7, 3, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 11, 7, 4, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 11, 7, 5, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 11, 7, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 7, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 7, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 11, 7, 9, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 11, 7, 10, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 11, 7, 11, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 2, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 3, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 11, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 12, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 12, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 12, 1, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 1, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 2, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 2, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 3, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 3, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 4, 1, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 4, 13, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 12, 5, 1, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 12, 5, 2, this.getStair(world), 7);
			this.placeBlockAtFixedPosition(world, 12, 5, 3, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 5, 4, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 5, 10, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 5, 11, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 5, 12, this.getStair(world), 6);
			this.placeBlockAtFixedPosition(world, 12, 5, 13, this.getColumns(world, 4));
			this.placeBlockAtFixedPosition(world, 12, 6, 2, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 12, 6, 3, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 6, 4, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 6, 5, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 6, 6, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 6, 7, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 6, 8, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 6, 9, this.getStair(world), 4);
			this.placeBlockAtFixedPosition(world, 12, 6, 10, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 6, 11, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 6, 12, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 7, 5, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 7, 6, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 7, 7, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 7, 8, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 12, 7, 9, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 13, 0, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 2, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 3, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 5, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 6, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 7, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 8, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 9, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 11, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 12, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 0, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 1, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 1, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 1, 4, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 7, Blocks.double_stone_slab);
			this.placeBlockAtFixedPosition(world, 13, 1, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 10, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 1, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 1, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 1, 13, this.getBase(world));
			this.placeBlockAtFixedPosition(world, 13, 2, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 2, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 2, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 2, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 2, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 2, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 2, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 2, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 2, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 2, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 2, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 3, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 3, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 3, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 3, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 3, 5, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 3, 6, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 3, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 3, 8, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 3, 9, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 3, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 3, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 3, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 3, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 4, 1, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 4, 2, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 3, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 4, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 4, 5, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 6, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 4, 8, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 9, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 10, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 4, 11, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 12, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 4, 13, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 5, 1, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 5, 2, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 5, 3, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 5, 4, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 5, 5, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 5, 6, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 5, 7, this.getColumns(world, 0));
			this.placeBlockAtFixedPosition(world, 13, 5, 8, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 5, 9, Blocks.glass_pane);
			this.placeBlockAtFixedPosition(world, 13, 5, 10, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 5, 11, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 5, 12, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 5, 13, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 6, 4, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 13, 6, 5, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 6, 6, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 6, 7, this.getFloor(world));
			this.placeBlockAtFixedPosition(world, 13, 6, 8, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 6, 9, this.getColumns(world, 8));
			this.placeBlockAtFixedPosition(world, 13, 6, 10, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 14, 0, 4, this.getStair(world), 2);
			this.placeBlockAtFixedPosition(world, 14, 0, 5, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 14, 0, 6, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 14, 0, 7, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 14, 0, 8, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 14, 0, 9, this.getStair(world), 1);
			this.placeBlockAtFixedPosition(world, 14, 0, 10, this.getStair(world), 3);
			this.placeBlockAtFixedPosition(world, 0, 2, 1, Blocks.torch, 2);
			this.placeBlockAtFixedPosition(world, 0, 2, 13, Blocks.torch, 2);
			this.placeBlockAtFixedPosition(world, 0, 3, 5, Blocks.torch, 2);
			this.placeBlockAtFixedPosition(world, 0, 3, 9, Blocks.torch, 2);
			this.placeBlockAtFixedPosition(world, 1, 1, 5, Blocks.wooden_door);
			this.placeBlockAtFixedPosition(world, 1, 1, 9, Blocks.wooden_door);
			this.placeBlockAtFixedPosition(world, 1, 2, 0, Blocks.torch, 4);
			this.placeBlockAtFixedPosition(world, 1, 2, 5, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 1, 2, 9, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 1, 2, 14, Blocks.torch, 3);
			this.placeBlockAtFixedPosition(world, 4, 2, 4, Blocks.torch, 5);
			this.placeBlockAtFixedPosition(world, 4, 2, 10, Blocks.torch, 5);
			this.placeBlockAtFixedPosition(world, 5, 1, 1, Blocks.wooden_door, 1);
			this.placeBlockAtFixedPosition(world, 5, 1, 13, Blocks.wooden_door, 3);
			this.placeBlockAtFixedPosition(world, 5, 2, 1, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 5, 2, 13, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 5, 3, 0, Blocks.torch, 4);
			this.placeBlockAtFixedPosition(world, 5, 3, 14, Blocks.torch, 3);
			this.placeBlockAtFixedPosition(world, 9, 1, 1, Blocks.wooden_door, 1);
			this.placeBlockAtFixedPosition(world, 9, 1, 13, Blocks.wooden_door, 3);
			this.placeBlockAtFixedPosition(world, 9, 2, 1, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 9, 2, 13, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 9, 3, 0, Blocks.torch, 4);
			this.placeBlockAtFixedPosition(world, 9, 3, 14, Blocks.torch, 3);
			this.placeBlockAtFixedPosition(world, 10, 2, 4, Blocks.torch, 5);
			this.placeBlockAtFixedPosition(world, 10, 2, 10, Blocks.torch, 5);
			this.placeBlockAtFixedPosition(world, 13, 1, 5, Blocks.wooden_door, 2);
			this.placeBlockAtFixedPosition(world, 13, 1, 9, Blocks.wooden_door, 2);
			this.placeBlockAtFixedPosition(world, 13, 2, 0, Blocks.torch, 4);
			this.placeBlockAtFixedPosition(world, 13, 2, 5, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 13, 2, 9, Blocks.wooden_door, 8);
			this.placeBlockAtFixedPosition(world, 13, 2, 14, Blocks.torch, 3);
			this.placeBlockAtFixedPosition(world, 14, 2, 1, Blocks.torch, 1);
			this.placeBlockAtFixedPosition(world, 14, 2, 13, Blocks.torch, 1);
			this.placeBlockAtFixedPosition(world, 14, 3, 5, Blocks.torch, 1);
			this.placeBlockAtFixedPosition(world, 14, 3, 9, Blocks.torch, 1);

			TileEntitySign ts = (TileEntitySign)this.placeTileEntityAtFixedPosition(world, 7, 2, 7, Blocks.standing_sign, 9);
			if (ts != null)
				ts.signText = new String[]{"Your thingy was", "weird. It's my", "house now", "--Villager 19"};

			TileEntityLootChest te = this.generateLootChestFixed(world, 7, 0, 7, Location.VILLAGE, 1, 0, rand);
			if (te != null)
				te.addProgress(ProgressStage.VILLAGECASTING);

			this.clearDroppedItems(world);

			return true;
		}

		private Block getStair(World world) {
			if (BiomeDictionary.isBiomeOfType(genBiome, Type.SANDY))
				return Blocks.sandstone_stairs;
			if (genBiome == BiomeGenBase.taiga || genBiome == BiomeGenBase.taigaHills || genBiome == BiomeGenBase.coldTaiga || genBiome == BiomeGenBase.coldTaigaHills)
				return Blocks.spruce_stairs;
			if (genBiome == BiomeGenBase.jungle || genBiome == BiomeGenBase.jungleEdge || genBiome == BiomeGenBase.jungleHills)
				return Blocks.jungle_stairs;
			if (genBiome == BiomeGenBase.birchForest || genBiome == BiomeGenBase.birchForestHills)
				return Blocks.birch_stairs;
			if (genBiome == BiomeGenBase.roofedForest)
				return Blocks.dark_oak_stairs;
			if (genBiome instanceof BiomeGenSavanna)
				return Blocks.acacia_stairs;
			return Blocks.oak_stairs;
		}

		private Block getBase(World world) {
			return BiomeDictionary.isBiomeOfType(genBiome, Type.SANDY) ? Blocks.sandstone : Blocks.cobblestone;
		}

		private BlockKey getColumns(World world, int m) {
			if (BiomeDictionary.isBiomeOfType(genBiome, Type.SANDY))
				return new BlockKey(Blocks.sandstone, 1);
			Block log = Blocks.log;
			if (genBiome == BiomeGenBase.taiga || genBiome == BiomeGenBase.taigaHills || genBiome == BiomeGenBase.coldTaiga || genBiome == BiomeGenBase.coldTaigaHills)
				m += ReikaTreeHelper.SPRUCE.getBaseLogMeta();
			else if (genBiome == BiomeGenBase.jungle || genBiome == BiomeGenBase.jungleEdge || genBiome == BiomeGenBase.jungleHills)
				m += ReikaTreeHelper.JUNGLE.getBaseLogMeta();
			else if (genBiome == BiomeGenBase.birchForest || genBiome == BiomeGenBase.birchForestHills)
				m += ReikaTreeHelper.BIRCH.getBaseLogMeta();
			else if (genBiome == BiomeGenBase.roofedForest) {
				log = Blocks.log2;
				m += ReikaTreeHelper.DARKOAK.getBaseLogMeta();
			}
			else if (genBiome instanceof BiomeGenSavanna) {
				log = Blocks.log2;
				m += ReikaTreeHelper.ACACIA.getBaseLogMeta();
			}
			return new BlockKey(log, m);
		}

		private BlockKey getFloor(World world) {
			if (BiomeDictionary.isBiomeOfType(genBiome, Type.SANDY))
				return new BlockKey(Blocks.sandstone, 2);
			if (genBiome == BiomeGenBase.taiga || genBiome == BiomeGenBase.taigaHills || genBiome == BiomeGenBase.coldTaiga || genBiome == BiomeGenBase.coldTaigaHills)
				return ReikaItemHelper.spruceWood;
			if (genBiome == BiomeGenBase.jungle || genBiome == BiomeGenBase.jungleEdge || genBiome == BiomeGenBase.jungleHills)
				return ReikaItemHelper.jungleWood;
			if (genBiome == BiomeGenBase.birchForest || genBiome == BiomeGenBase.birchForestHills)
				return ReikaItemHelper.birchWood;
			if (genBiome == BiomeGenBase.roofedForest)
				return ReikaItemHelper.darkOakWood;
			if (genBiome instanceof BiomeGenSavanna)
				return ReikaItemHelper.acaciaWood;
			return ReikaItemHelper.oakWood;
		}

	}

	public static class BrokenChromaStructure extends ChromaVillagePiece {

		public BrokenChromaStructure(StructureVillagePieces.Start start, int par2, Random rand, StructureBoundingBox bb, int par5, int x, int y, int z) {
			super(start, par2, rand, bb, par5, x, y, z);
		}

		public BrokenChromaStructure() {
			super();
		}
		/*
		@Override
		public int getMinimumSeparation() {
			return 4096;
		}
		 */
		@Override
		protected boolean generate(World world, Random rand) {

			this.rise(world);
			Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

			//ReikaJavaLibrary.pConsole("Genning "+boundingBox+" @ "+coordBaseMode);

			this.placeBlockAtCurrentPosition(world, 1, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 6, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 8, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 9, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 1, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 1, 1, 4, b);
			this.placeBlockAtCurrentPosition(world, 1, 1, 7, b);
			this.placeBlockAtCurrentPosition(world, 1, 1, 10, b, 8);
			this.placeBlockAtCurrentPosition(world, 1, 1, 13, b);
			this.placeBlockAtCurrentPosition(world, 1, 1, 16, b);
			this.placeBlockAtCurrentPosition(world, 1, 2, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 2, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 2, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 2, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 2, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 3, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 3, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 3, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 3, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 3, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 4, 3, Blocks.coal_ore);
			this.placeBlockAtCurrentPosition(world, 1, 4, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 4, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 4, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 4, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 4, 15, b);
			this.placeBlockAtCurrentPosition(world, 1, 4, 16, Blocks.coal_ore);
			this.placeBlockAtCurrentPosition(world, 1, 5, 5, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 5, 6, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 5, 7, b, 8);
			this.placeBlockAtCurrentPosition(world, 1, 5, 8, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 5, 9, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 5, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 1, 5, 13, b);
			this.placeBlockAtCurrentPosition(world, 1, 5, 14, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 5, 15, b);
			this.placeBlockAtCurrentPosition(world, 1, 6, 11, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 6, 12, b, 1);
			this.placeBlockAtCurrentPosition(world, 1, 6, 13, b, 7);
			this.placeBlockAtCurrentPosition(world, 2, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 2, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 2, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 2, 1, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 2, 5, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 3, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 3, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 3, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 3, 1, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 3, 5, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 4, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 8, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 9, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 11, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 12, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 4, 0, 14, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 0, 15, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 4, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 7, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 9, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 11, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 12, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 13, b);
			this.placeBlockAtCurrentPosition(world, 4, 1, 16, b);
			this.placeBlockAtCurrentPosition(world, 4, 2, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 2, 11, b, 7);
			this.placeBlockAtCurrentPosition(world, 4, 2, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 3, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 4, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 4, 5, 4, b);
			this.placeBlockAtCurrentPosition(world, 4, 6, 4, b, 8);
			this.placeBlockAtCurrentPosition(world, 5, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 5, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 5, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 5, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 5, 1, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 5, 6, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 6, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 6, 0, 6, b);
			this.placeBlockAtCurrentPosition(world, 6, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 6, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 6, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 6, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 6, 1, 13, b);
			this.placeBlockAtCurrentPosition(world, 6, 1, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 6, 4, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 7, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 5, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 6, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 9, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 11, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 14, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 15, b);
			this.placeBlockAtCurrentPosition(world, 7, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 7, 1, 4, b, 7);
			this.placeBlockAtCurrentPosition(world, 7, 1, 10, Blocks.crafting_table);
			this.placeBlockAtCurrentPosition(world, 7, 1, 16, b, 8);
			this.placeBlockAtCurrentPosition(world, 7, 2, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 2, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 3, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 3, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 4, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 4, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 5, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 5, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 7, 6, 4, Blocks.wool, 11);
			this.placeBlockAtCurrentPosition(world, 7, 6, 16, Blocks.wool, 3);
			this.placeBlockAtCurrentPosition(world, 8, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 8, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 8, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 8, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 8, 1, 13, b);
			this.placeBlockAtCurrentPosition(world, 8, 4, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 8, 6, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 9, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 9, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 9, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 9, 4, 4, b, 1);
			this.placeBlockAtCurrentPosition(world, 9, 6, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 10, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 8, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 9, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 11, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 12, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 14, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 15, b);
			this.placeBlockAtCurrentPosition(world, 10, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 10, 1, 4, b);
			this.placeBlockAtCurrentPosition(world, 10, 1, 9, b);
			this.placeBlockAtCurrentPosition(world, 10, 1, 11, b);
			this.placeBlockAtCurrentPosition(world, 10, 1, 16, b);
			this.placeBlockAtCurrentPosition(world, 10, 2, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 2, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 3, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 3, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 4, 4, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 4, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 10, 5, 4, b);
			this.placeBlockAtCurrentPosition(world, 10, 5, 16, b);
			this.placeBlockAtCurrentPosition(world, 10, 6, 4, b, 8);
			this.placeBlockAtCurrentPosition(world, 10, 6, 16, b, 8);
			this.placeBlockAtCurrentPosition(world, 11, 0, 0, b);
			this.placeBlockAtCurrentPosition(world, 11, 0, 3, b, 1);
			this.placeBlockAtCurrentPosition(world, 11, 0, 5, b, 1);
			this.placeBlockAtCurrentPosition(world, 11, 0, 12, b);
			this.placeBlockAtCurrentPosition(world, 11, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 11, 6, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 12, 0, 12, b);
			this.placeBlockAtCurrentPosition(world, 12, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 12, 6, 16, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 0, 1, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 3, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 5, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 6, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 7, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 10, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 12, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 13, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 14, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 15, b);
			this.placeBlockAtCurrentPosition(world, 13, 0, 16, b);
			this.placeBlockAtCurrentPosition(world, 13, 1, 4, b);
			this.placeBlockAtCurrentPosition(world, 13, 1, 7, b);
			this.placeBlockAtCurrentPosition(world, 13, 1, 10, b, 7);
			this.placeBlockAtCurrentPosition(world, 13, 1, 13, b);
			this.placeBlockAtCurrentPosition(world, 13, 1, 16, b);
			this.placeBlockAtCurrentPosition(world, 13, 2, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 2, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 2, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 2, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 3, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 3, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 3, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 3, 16, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 4, 7, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 4, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 4, 13, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 4, 14, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 4, 15, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 4, 16, Blocks.coal_ore);
			this.placeBlockAtCurrentPosition(world, 13, 5, 7, b);
			this.placeBlockAtCurrentPosition(world, 13, 5, 10, b, 2);
			this.placeBlockAtCurrentPosition(world, 13, 5, 11, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 5, 12, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 5, 13, b);
			this.placeBlockAtCurrentPosition(world, 13, 5, 16, b);
			this.placeBlockAtCurrentPosition(world, 13, 6, 4, Blocks.coal_ore);
			this.placeBlockAtCurrentPosition(world, 13, 6, 5, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 6, 6, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 6, 7, b, 8);
			this.placeBlockAtCurrentPosition(world, 13, 6, 8, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 6, 9, b, 1);
			this.placeBlockAtCurrentPosition(world, 13, 6, 10, Blocks.wool, 11);
			this.placeBlockAtCurrentPosition(world, 13, 6, 13, b, 8);
			this.placeBlockAtCurrentPosition(world, 13, 6, 16, b);
			this.placeBlockAtCurrentPosition(world, 15, 0, 2, b, 2);
			this.placeBlockAtCurrentPosition(world, 15, 0, 4, b);
			this.placeBlockAtCurrentPosition(world, 15, 0, 6, b, 2);

			this.placeBlockAtCurrentPosition(world, 4, 0, 1, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 4, 0, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 5, 0, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 5, 1, 4, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 0, 1, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 0, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 0, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 0, 5, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 1, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 1, 4, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 6, 2, 4, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 0, 1, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 0, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 0, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 1, 1, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 1, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 1, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 2, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 2, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 7, 3, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 8, 0, 2, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 8, 0, 3, Blocks.gravel);
			this.placeBlockAtCurrentPosition(world, 8, 1, 2, Blocks.gravel);

			TileEntityLootChest te = this.generateLootChest(world, 7, 0, 10, Location.VILLAGE, 1, 0, rand);
			if (te != null)
				te.addProgress(ProgressStage.VILLAGECASTING);

			TileEntityChest tc = (TileEntityChest)this.generateTileEntity(world, 7, 1, 11, Blocks.chest, 4);
			if (tc != null) {
				int[] slots = {2, 4, 6, 11, 12, 13, 15, 20, 22, 24};
				for (int i = 0; i < slots.length; i++) {
					tc.setInventorySlotContents(slots[i], itemRand.getRandomEntry().copy());
				}
			}

			TileEntitySign ts = (TileEntitySign)this.generateTileEntity(world, 0, 6, 11, Blocks.wall_sign, 4);
			if (ts != null)
				ts.signText = new String[]{"guys i think my", "lapiz is broken", "", "--Villager 26"};
			ts = (TileEntitySign)this.generateTileEntity(world, 1, 3, 3, Blocks.wall_sign, 2);
			if (ts != null)
				ts.signText = new String[]{"Couldn't reach.", "", "Good enough?", ""};
			ts = (TileEntitySign)this.generateTileEntity(world, 6, 1, 11, Blocks.wall_sign, 4);
			if (ts != null)
				ts.signText = new String[]{"Propertee", "of Villager", "#73", "~angryface"};

			this.placeBlockAtCurrentPosition(world, 1, 6, 10, Blocks.flowing_water);

			this.clearDroppedItems(world);

			return true;
		}

	}

}
