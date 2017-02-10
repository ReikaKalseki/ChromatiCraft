/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.DragonAPI.Instantiable.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public abstract class ChromaDimensionBiome extends BiomeGenBase {

	public final Biomes biomeType;

	protected final SimplexNoiseGenerator grassColor;

	public ChromaDimensionBiome(int id, String n, Biomes t) {
		super(id);
		this.setBiomeName(n);
		this.func_76733_a(5159473);
		this.setDisableRain();
		biomeType = t;
		grassColor = new SimplexNoiseGenerator(System.currentTimeMillis());
	}

	//public abstract boolean allowsGenerator(DimensionGenerators gen);

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
