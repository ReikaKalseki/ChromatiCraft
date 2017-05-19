/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.DimDecoTileTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class WorldGenGlowingCracks extends ChromaWorldGenerator {

	private final WeightedRandom<OreType> oreRand = new WeightedRandom();

	public WorldGenGlowingCracks(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);

		oreRand.addEntry(ReikaOreHelper.EMERALD, 10);
		oreRand.addEntry(ReikaOreHelper.DIAMOND, 20);
		oreRand.addEntry(ReikaOreHelper.REDSTONE, 50);
		oreRand.addEntry(ReikaOreHelper.GOLD, 40);
		if (ModOreList.SAPPHIRE.existsInGame())
			oreRand.addEntry(ModOreList.SAPPHIRE, 30);
		if (ModOreList.PLATINUM.existsInGame())
			oreRand.addEntry(ModOreList.PLATINUM, 20);
		if (ModOreList.AMETHYST.existsInGame())
			oreRand.addEntry(ModOreList.AMETHYST, 30);
		if (ModOreList.MANA.existsInGame())
			oreRand.addEntry(ModOreList.MANA, 20);
		if (ModOreList.MOONSTONE.existsInGame())
			oreRand.addEntry(ModOreList.MOONSTONE, 10);
		if (ModOreList.VINTEUM.existsInGame())
			oreRand.addEntry(ModOreList.VINTEUM, 30);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.25F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				if (world.getBlock(x+i, y-1, z+k) != Blocks.grass)
					return false;
			}
		}
		world.setBlock(x, y, z, ChromaBlocks.DIMGENTILE.getBlockInstance(), DimDecoTileTypes.GLOWCRACKS.ordinal(), 3);

		if (rand.nextInt(2) == 0) {
			new WorldGenMinable(ChromaBlocks.TIEREDORE.getBlockInstance(), this.getRandomOre(rand), 32, Blocks.stone).generate(world, rand, x-7, y-4, z-7);
		}
		else {
			oreRand.setSeed(rand.nextLong());
			OreType ore = oreRand.getRandomEntry();
			ItemStack is = ore.getFirstOreBlock();
			new WorldGenMinable(Block.getBlockFromItem(is.getItem()), is.getItemDamage(), 32, Blocks.stone).generate(world, rand, x-7, y-4, z-7);
		}
		return true;
	}

	private int getRandomOre(Random rand) {
		TieredOres ore = TieredOres.list[rand.nextInt(TieredOres.list.length)];
		while (ore.genBlock != Blocks.stone) {
			ore = TieredOres.list[rand.nextInt(TieredOres.list.length)];
		}
		return ore.ordinal();
	}

}
