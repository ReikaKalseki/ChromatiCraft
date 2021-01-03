/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.awt.Color;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.CustomMapColorBiome;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public abstract class ChromaDimensionBiome extends BiomeGenBase implements CustomMapColorBiome {

	public final Biomes biomeType;

	protected final SimplexNoiseGenerator grassColor;

	public ChromaDimensionBiome(int id, String n, Biomes t) {
		super(id);
		this.setBiomeName(n);
		this.func_76733_a(5159473);
		this.setDisableRain();
		biomeType = t;
		grassColor = new SimplexNoiseGenerator(System.currentTimeMillis());

		spawnableMonsterList.clear();
		spawnableCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawnableWaterCreatureList.clear();
		this.initSpawnRules();
	}

	protected void initSpawnRules() {
		//spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
	}

	//public abstract boolean allowsGenerator(DimensionGenerators gen);

	@Override
	public final int getMapColor(World world, int x, int z) {
		int color = biomeType != null ? 0xff000000 | Color.HSBtoRGB(biomeType.ordinal()/(float)Biomes.biomeList.length, 1, 1) : 0xffffffff;
		if (this.getExactType() instanceof SubBiomes)
			color = ReikaColorAPI.getColorWithBrightnessMultiplier(color, 0.67F);
		if (this.getExactType() == Biomes.STRUCTURE)
			color = 0x606060;
		return color;
	}

	@Override
	public int getBiomeGrassColor(int x, int y, int z) {
		int c = ChromatiCraft.rainbowforest.getBiomeGrassColor(x, y, z);
		double rx = x/8D;
		double rz = z/8D;
		float f = (float)ReikaMathLibrary.normalizeToBounds(grassColor.getValue(rx, rz), 1, 1.5);
		return ReikaColorAPI.multiplyChannels(c, 1, f, 1);
	}

	@Override
	public int getWaterColorMultiplier() {
		return ChromatiCraft.rainbowforest.getWaterColorMultiplier();
	}

	public ChromaDimensionBiomeType getExactType() {
		return biomeType;
	}

	public static abstract class ChromaDimensionSubBiome extends ChromaDimensionBiome {

		public final SubBiomes subType;

		public ChromaDimensionSubBiome(int id, String n, SubBiomes t) {
			super(id, n, t.getParent());
			subType = t;
		}

		@Override
		public ChromaDimensionBiomeType getExactType() {
			return subType;
		}

	}

}
