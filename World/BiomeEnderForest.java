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

import Reika.ChromatiCraft.Registry.ChromaOptions;

import java.awt.Color;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenForest;

public class BiomeEnderForest extends BiomeGenForest {

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

}
