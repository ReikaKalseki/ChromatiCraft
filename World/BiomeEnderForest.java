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

import java.awt.Color;
import java.util.Random;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Interfaces.CustomMapColorBiome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeEnderForest extends BiomeGenForest implements CustomMapColorBiome {

	private final WorldGenAbstractTree enderOakLarge = new EnderOakGenerator(3, 7, 5, 12, 3, 5, 0.15F, 6, 0.15F);
	private final WorldGenAbstractTree enderOakSmall = new EnderOakGenerator(2, 4, 3, 5, 2, 3, 0, 0, 0.1F);
	private final WorldGenAbstractTree enderOakNarrow = new EnderOakGenerator(6, 12, 6, 15, 1, 2, 0.35F, 4, 0F);

	private final WeightedRandom<WorldGenAbstractTree> treeTypes = new WeightedRandom();

	public BiomeEnderForest(int id) {
		super(id, 0);
		//thin the trees a little
		theBiomeDecorator.treesPerChunk *= 0.7;

		this.setDisableRain();

		biomeName = "Ender Forest";

		spawnableMonsterList.clear();

		//boost Enderman spawn rates relative
		spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 1, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 1, 1, 4));

		treeTypes.addEntry(worldGeneratorTrees, 20);
		treeTypes.addEntry(worldGeneratorBigTree, 4);
		treeTypes.addEntry(enderOakSmall, 50);
		treeTypes.addEntry(enderOakLarge, 10);
		treeTypes.addEntry(enderOakNarrow, 6);
	}

	@Override
	public boolean canSpawnLightningBolt()
	{
		return true;
	}

	@Override
	public float getSpawningChance()
	{
		return 0.1F;
	}

	@Override
	public int getBiomeGrassColor(int x, int y, int z)
	{
		if (!ChromaOptions.ENDERCOLORING.getState())
			return BiomeGenBase.forest.getBiomeGrassColor(x, y, z);
		int r = 255;
		int g = 200;
		int b = 255;
		return new Color(r, g, b).getRGB();
	}

	@Override
	public int getBiomeFoliageColor(int x, int y, int z)
	{
		if (!ChromaOptions.ENDERCOLORING.getState())
			return BiomeGenBase.forest.getBiomeFoliageColor(x, y, z);
		int r = 255;
		int g = 150;
		int b = 255;
		return new Color(r, g, b).getRGB();
	}

	@Override
	public int getWaterColorMultiplier()
	{
		if (!ChromaOptions.ENDERCOLORING.getState())
			return BiomeGenBase.forest.getWaterColorMultiplier();
		int r = 195;
		int g = 0;
		int b = 105;
		return new Color(r, g, b).getRGB();
	}

	@Override
	public BiomeDecorator createBiomeDecorator()
	{
		return new DecoratorEnderForest();
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		if (rand.nextInt(4) > 0) {
			switch(rand.nextInt(2)) {
				case 0:
					if (Flowers.ENDERFLOWER.canPlantAt(world, x, y, z)) {
						world.setBlock(x, y, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.ENDERFLOWER.ordinal(), 3);
					}
					break;
				case 1:
					if (Flowers.RESOCLOVER.canPlantAt(world, x, y, z)) {
						world.setBlock(x, y, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.RESOCLOVER.ordinal(), 3);
					}
					break;
			}
		}
		else {
			super.plantFlower(world, rand, x, y, z);
		}
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		treeTypes.setRNG(rand);
		return treeTypes.getRandomEntry();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMapColor(World world, int x, int z) {
		return 0xC872DB;
	}

}
