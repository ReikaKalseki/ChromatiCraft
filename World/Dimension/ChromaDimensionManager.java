/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.io.File;
import java.lang.reflect.Constructor;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;

public class ChromaDimensionManager {

	public static enum Biomes {
		ISLANDS(BiomeGenIslands.class, 10, ExtraChromaIDs.ISLANDS, Type.BEACH, Type.WET),
		SKYLANDS(BiomeGenSkylands.class, 2, ExtraChromaIDs.SKYLANDS, Type.MAGICAL, Type.COLD);

		private int id;
		private final Class biomeClass;
		private BiomeGenBase instance;
		//private final List<BiomeEntry> biomeList;
		private final BiomeDictionary.Type[] types;
		public final int spawnWeight;
		private ExtraChromaIDs config;

		private static final Biomes[] list = values();

		private Biomes(Class<? extends ChromaDimensionBiome> c, int w, ExtraChromaIDs id, BiomeDictionary.Type... t) {
			biomeClass = c;
			types = t;
			config = id;
			spawnWeight = w;
		}

		private void create() {
			id = config.getValue();
			try {
				Constructor c = biomeClass.getConstructor(int.class);
				instance = (BiomeGenBase)c.newInstance(id);
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Could not create biome instance "+this+": "+e.getLocalizedMessage());
			}

		}

		public BiomeGenBase getBiome() {
			return instance;
		}
	}

	public static void initialize() {
		int id = ExtraChromaIDs.DIMID.getValue();
		DimensionManager.registerProviderType(id, WorldProviderChroma.class, false);
		DimensionManager.registerDimension(id, id);

		for (int i = 0; i < Biomes.list.length; i++) {
			Biomes b = Biomes.list[i];
			b.create();
			BiomeDictionary.registerBiomeType(b.instance, b.types);
		}
	}

	public static void resetDimension() {
		String path = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath().replaceAll("\\\\", "/").replaceAll("/\\./", "/");
		File dim = new File(path+"/DIM"+ExtraChromaIDs.DIMID.getValue());
		if (dim.exists() && dim.isDirectory()) {
			ReikaFileReader.deleteFolderWithContents(dim, 100);
		}
	}

}
