package Reika.ChromatiCraft.World.Dimension;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenAurorae;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenChromaMeteor;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenCrystalMountain;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenCrystalPit;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenCrystalShrub;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenCrystalTree;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenFireJet;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenFissure;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenFloatstone;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenLightedShrub;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenLightedTree;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenMiasma;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenMiniAltar;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenMoonPool;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenTerrainCrystal;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenTreeCluster;
import Reika.DragonAPI.Exception.RegistrationException;


public enum DimensionGenerators {

	RIFT(WorldGenFissure.class, 					GeneratorType.TERRAIN, 		GeneratorTheme.ENERGY,			Integer.MIN_VALUE),
	METEOR(WorldGenChromaMeteor.class, 				GeneratorType.FEATURE, 		GeneratorTheme.GEOHISTORICAL,	Integer.MIN_VALUE),
	GEODE(WorldGenCrystalPit.class, 				GeneratorType.FEATURE, 		GeneratorTheme.CRYSTAL,			-100),
	JETS(WorldGenFireJet.class, 					GeneratorType.TERRAIN, 		GeneratorTheme.ENERGY,			0),
	FLOATSTONE(WorldGenFloatstone.class, 			GeneratorType.TERRAIN, 		GeneratorTheme.SKYFEATURE,		100),
	MIASMA(WorldGenMiasma.class, 					GeneratorType.FEATURE, 		GeneratorTheme.SKYFEATURE,		0),
	TREES(WorldGenLightedTree.class, 				GeneratorType.FEATURE, 		GeneratorTheme.FOLIAGE,			-50),
	FORESTS(WorldGenTreeCluster.class, 				GeneratorType.FEATURE, 		GeneratorTheme.FOLIAGE,			-50),
	MOONPOOL(WorldGenMoonPool.class, 				GeneratorType.STRUCTURE, 	GeneratorTheme.OCEANIC,			0),
	TERRAINCRYSTAL(WorldGenTerrainCrystal.class, 	GeneratorType.TERRAIN, 		GeneratorTheme.SKYFEATURE,		Integer.MAX_VALUE),
	ALTAR(WorldGenMiniAltar.class, 					GeneratorType.STRUCTURE, 	GeneratorTheme.PRECURSORS,		-500),
	MOUNTAIN(WorldGenCrystalMountain.class,			GeneratorType.TERRAIN,		GeneratorTheme.CRYSTAL,			Integer.MIN_VALUE),
	CRYSTALTREE(WorldGenCrystalTree.class,			GeneratorType.FEATURE,		GeneratorTheme.CRYSTAL,			-100),
	GLOWBUSH(WorldGenLightedShrub.class,			GeneratorType.FEATURE,		GeneratorTheme.FOLIAGE,			500),
	CRYSBUSH(WorldGenCrystalShrub.class,			GeneratorType.FEATURE,		GeneratorTheme.FOLIAGE,			500),
	AURORA(WorldGenAurorae.class,					GeneratorType.FEATURE,		GeneratorTheme.SKYFEATURE,		Integer.MAX_VALUE);

	private final Class genClass;
	public final GeneratorType type;
	public final GeneratorTheme theme;
	public final int genTime;

	private ChromaWorldGenerator generator;

	public static final DimensionGenerators[] generators = values();

	private DimensionGenerators(Class<? extends ChromaWorldGenerator> c, GeneratorType t, GeneratorTheme h, int p) {
		genClass = c;
		type = t;
		theme = h;
		genTime = p;
	}

	public ChromaWorldGenerator getGenerator() {
		if (generator == null) {
			try {
				Constructor<ChromaWorldGenerator> c = genClass.getConstructor(DimensionGenerators.class);
				generator = c.newInstance(this);
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Could not create generator for dimension generator "+this, e);
			}
		}
		return generator;
	}

	public boolean generateIn(ChromaDimensionBiome b) {
		if (b.biomeType == Biomes.STRUCTURE)
			return false;
		if (theme == GeneratorTheme.SKYFEATURE)
			return b.biomeType == Biomes.SKYLANDS;
		if (this == FORESTS)
			return b.biomeType == Biomes.FOREST;
		if (this == TREES || this == GLOWBUSH)
			return b.biomeType == Biomes.FOREST || b.biomeType == Biomes.PLAINS;
		switch(this) {
			case ALTAR:
				return b.getExactType().isReasonablyFlat();
			case CRYSBUSH:
			case CRYSTALTREE:
				return b == SubBiomes.CRYSFOREST.getBiome();
			case GEODE:
				return b == Biomes.PLAINS.getBiome();
			case JETS:
				return true;
			case METEOR:
				return !b.getExactType().isWaterBiome() && b.getExactType().isReasonablyFlat();
			case MIASMA:
				return b.biomeType == Biomes.PLAINS;
			case MOONPOOL:
				return b.biomeType == Biomes.ISLANDS;
			case MOUNTAIN:
				return b == SubBiomes.MOUNTAINS.getBiome();
			case RIFT:
				return b == Biomes.PLAINS.getBiome();
			default:
				return true;
		}
	}

	public static ArrayList<ChromaWorldGenerator> getSortedList() {
		ArrayList<ChromaWorldGenerator> ret = new ArrayList();
		ArrayList<DimensionGenerators> li = new ArrayList();
		for (int i = 0; i < DimensionGenerators.generators.length; i++) {
			DimensionGenerators gen = DimensionGenerators.generators[i];
			li.add(gen);
		}
		Collections.sort(li, generationSorter);
		for (DimensionGenerators g : li) {
			ret.add(g.getGenerator());
		}
		return ret;
	}

	public static enum GeneratorType {

		TERRAIN(),
		STRUCTURE(),
		FEATURE();

	}

	public static enum GeneratorTheme {

		SKYFEATURE(),
		CRYSTAL(),
		PRECURSORS(),
		ENERGY(),
		FOLIAGE(),
		GEOHISTORICAL(),
		OCEANIC();

	}

	private static final Comparator<DimensionGenerators> generationSorter = new GeneratorSorter();

	private static class GeneratorSorter implements Comparator<DimensionGenerators> {

		private GeneratorSorter() {

		}

		@Override
		public int compare(DimensionGenerators o1, DimensionGenerators o2) {
			return Integer.compare(o1.genTime, o2.genTime);
		}

	}

}
