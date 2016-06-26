/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome.ChromaDimensionSubBiome;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCentral;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenChromaMountains;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenChromaOcean;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCrystalForest;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCrystalPlains;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenGlowingForest;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenIslands;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenSkylands;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenVoidlands;
import Reika.ChromatiCraft.World.Dimension.Biome.StructureBiome;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaDimensionManager {

	private static final HashSet<BlockKey> bannedBlocks = new HashSet();
	private static final PlayerMap<DimensionStructureGenerator> playersInStructures = new PlayerMap();

	public static enum Biomes implements ChromaDimensionBiomeType {
		PLAINS(BiomeGenCrystalPlains.class,	"Crystal Plains",			8, 0,	ExtraChromaIDs.PLAINS, 		SubBiomes.MOUNTAINS, 	Type.MAGICAL, Type.PLAINS),
		ISLANDS(BiomeGenIslands.class,		"Iridescent Archipelago",	6, -5,	ExtraChromaIDs.ISLANDS, 	SubBiomes.DEEPOCEAN, 	Type.MAGICAL, Type.BEACH, Type.WET),
		SKYLANDS(BiomeGenSkylands.class,	"Lumen Skylands",			2, 0,	ExtraChromaIDs.SKYLANDS,	SubBiomes.VOIDLANDS,	Type.MAGICAL, Type.COLD),
		FOREST(BiomeGenGlowingForest.class,	"Glowing Forest",			10, 10,	ExtraChromaIDs.FOREST, 		SubBiomes.CRYSFOREST,	Type.MAGICAL, Type.FOREST),
		STRUCTURE(StructureBiome.class,		"Structure Field",			0, 0,	ExtraChromaIDs.STRUCTURE, 							Type.MAGICAL, Type.PLAINS),
		CENTER(BiomeGenCentral.class, 		"Luminescent Sanctuary",	0, 0,	ExtraChromaIDs.CENTRAL,								Type.MAGICAL, Type.FOREST, Type.DENSE);

		private int id;
		public final String biomeName;
		private final Class biomeClass;
		private ChromaDimensionBiome instance;
		//private final List<BiomeEntry> biomeList;
		private final BiomeDictionary.Type[] types;
		public final int spawnWeight;
		private ExtraChromaIDs config;
		private final SubBiomes subBiome;
		public final int baseHeightDelta;

		public static final Biomes[] biomeList = values();

		private Biomes(Class<? extends ChromaDimensionBiome> c, String n, int w, int h, ExtraChromaIDs id, BiomeDictionary.Type... t) {
			this(c, n, w, h, id, null, t);
		}

		private Biomes(Class<? extends ChromaDimensionBiome> c, String n, int w, int h, ExtraChromaIDs id, SubBiomes s, BiomeDictionary.Type... t) {
			biomeClass = c;
			types = t;
			config = id;
			spawnWeight = w;
			subBiome = s;
			biomeName = n;
			baseHeightDelta = h;
		}

		private void create() {
			id = config.getValue();
			BiomeCollisionTracker.instance.addBiomeID(ChromatiCraft.instance, id, biomeClass);

			if (subBiome != null) {
				subBiome.create(this);
			}
			try {
				Constructor c = biomeClass.getConstructor(int.class, String.class, Biomes.class);
				instance = (ChromaDimensionBiome)c.newInstance(id, biomeName, this);
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Could not create biome instance "+this+": "+e.getLocalizedMessage());
			}

		}

		public ChromaDimensionBiome getBiome() {
			return instance;
		}

		public SubBiomes getSubBiome() {
			return subBiome;
		}

		public boolean isTechnical() {
			return spawnWeight == 0;
		}

		public boolean isWaterBiome() {
			return this == ISLANDS;
		}

		public boolean isReasonablyFlat() {
			return this != SKYLANDS && this != ISLANDS;
		}

		@Override
		public int getBaseHeightDelta() {
			return baseHeightDelta;
		}
	}

	public static enum SubBiomes implements ChromaDimensionBiomeType {
		MOUNTAINS(BiomeGenChromaMountains.class,	"Crystal Mountains",	0.75, 0,	ExtraChromaIDs.MOUNTAIN, 	Type.MAGICAL, Type.MOUNTAIN),
		DEEPOCEAN(BiomeGenChromaOcean.class,		"Aura Ocean",			0.4, -30,	ExtraChromaIDs.OCEAN, 		Type.MAGICAL, Type.OCEAN),
		CRYSFOREST(BiomeGenCrystalForest.class,		"Crystal Forest",		0.2, 15,	ExtraChromaIDs.CRYSFOREST,	Type.MAGICAL, Type.FOREST),
		VOIDLANDS(BiomeGenVoidlands.class,			"Voidland",				0.1, 8,		ExtraChromaIDs.VOID,		Type.MAGICAL, Type.COLD, Type.END);

		private int id;
		public final String biomeName;
		private final Class biomeClass;
		private ChromaDimensionSubBiome instance;
		//private final List<BiomeEntry> biomeList;
		private final BiomeDictionary.Type[] types;
		public final double spawnWeight;
		private ExtraChromaIDs config;
		private Biomes parent;
		public final int baseHeightDelta;

		public static final SubBiomes[] biomeList = values();

		private SubBiomes(Class<? extends ChromaDimensionSubBiome> c, String n, double w, int h, ExtraChromaIDs id, BiomeDictionary.Type... t) {
			biomeClass = c;
			types = t;
			config = id;
			spawnWeight = w;
			biomeName = n;
			baseHeightDelta = h;
		}

		private void create(Biomes b) {
			parent = b;
			id = config.getValue();
			BiomeCollisionTracker.instance.addBiomeID(ChromatiCraft.instance, id, biomeClass);

			try {
				Constructor c = biomeClass.getConstructor(int.class, String.class, SubBiomes.class);
				instance = (ChromaDimensionSubBiome)c.newInstance(id, biomeName, this);
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Could not create biome instance "+this+": "+e.getLocalizedMessage());
			}

		}

		public Biomes getParent() {
			return parent;
		}

		public ChromaDimensionBiome getBiome() {
			return instance;
		}

		public boolean isWaterBiome() {
			return parent.isWaterBiome();
		}

		public boolean isReasonablyFlat() {
			return this != MOUNTAINS && this != VOIDLANDS;
		}

		@Override
		public int getBaseHeightDelta() {
			return baseHeightDelta;
		}
	}

	public static interface ChromaDimensionBiomeType {

		public ChromaDimensionBiome getBiome();

		public boolean isWaterBiome();

		public boolean isReasonablyFlat();

		public int getBaseHeightDelta();

		public String name();

	}

	public static void initialize() {
		int id = ExtraChromaIDs.DIMID.getValue();
		DimensionManager.registerProviderType(id, WorldProviderChroma.class, false);
		DimensionManager.registerDimension(id, id);

		for (int i = 0; i < Biomes.biomeList.length; i++) {
			Biomes b = Biomes.biomeList[i];
			b.create();
			BiomeDictionary.registerBiomeType(b.instance, b.types);
			if (b.subBiome != null)
				BiomeDictionary.registerBiomeType(b.subBiome.instance, b.subBiome.types);
		}
	}

	public static void resetDimension(World world) {
		playersInStructures.clear();
		if (world instanceof WorldServer)
			((WorldServer)world).flush(); //Hopefully kill all I/O
		getChunkProvider(world).clearCaches();
		System.gc();
		String path = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath().replaceAll("\\\\", "/").replaceAll("/\\./", "/");
		File dim = new File(path+"/DIM"+ExtraChromaIDs.DIMID.getValue());
		if (dim.exists() && dim.isDirectory()) {
			boolean del = ReikaFileReader.deleteFolderWithContents(dim, 100);
		}
	}

	public static void resetDimensionClient() {
		playersInStructures.clear();
		System.gc();
		String path = DragonAPICore.getMinecraftDirectoryString()+"mods/VoxelMods/voxelMap/cache/";
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			File[] saves = f.listFiles();
			for (int i = 0; i < saves.length; i++) {
				File f2 = new File(saves[i], "Chroma (dimension 60)");
				if (f2.exists())
					f2.delete();
			}
		}
	}

	public static ChunkProviderChroma getChunkProvider(World world) {
		return ((WorldProviderChroma)world.provider).getChunkGenerator();
	}

	public static boolean isBannedDimensionBlock(Block b, int meta) {
		return bannedBlocks.contains(new BlockKey(b, meta));
	}

	public static void tickPlayersInStructures(World world) {
		for (UUID id : playersInStructures.keySet()) {
			EntityPlayer ep = world.func_152378_a(id);
			if (ep != null) {
				playersInStructures.directGet(id).tickPlayer(ep);
			}
		}
	}

	public static DimensionStructureGenerator getStructurePlayerIsIn(EntityPlayer ep) {
		return playersInStructures.get(ep);
	}

	public static void addPlayerToStructure(EntityPlayer ep, DimensionStructureGenerator structure) {
		playersInStructures.put(ep, structure);
	}

	@SideOnly(Side.CLIENT)
	public static void addPlayerToStructureClient(EntityPlayer ep, DimensionStructureType structure) {
		playersInStructures.put(ep, structure.createGenerator());
	}

	public static void removePlayerFromStructure(EntityPlayer ep) {
		playersInStructures.remove(ep);
	}

	static {
		if (ModList.ENDERIO.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTravelAnchor");
			if (b != null)
				bannedBlocks.add(new BlockKey(b));

			b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTelePad");
			if (b != null)
				bannedBlocks.add(new BlockKey(b));
		}

		if (ModList.THAUMICTINKER.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.THAUMICTINKER.modLabel, "warpGate");
			if (b != null)
				bannedBlocks.add(new BlockKey(b));
		}
	}

}
