/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome.ChromaDimensionSubBiome;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCentral;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenChromaMountains;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenChromaOcean;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCrystalForest;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenCrystalPlains;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenGlowCracks;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenGlowingForest;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenIslands;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenSkylands;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenSparkle;
import Reika.ChromatiCraft.World.Dimension.Biome.BiomeGenVoidlands;
import Reika.ChromatiCraft.World.Dimension.Biome.MonumentBiome;
import Reika.ChromatiCraft.World.Dimension.Biome.StructureBiome;
import Reika.ChromatiCraft.World.Dimension.Rendering.Aurora;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaDimensionManager {

	private static final HashSet<BlockKey> bannedBlocks = new HashSet();
	private static final HashSet<KeyedItemStack> bannedItems = new HashSet();

	private static final PlayerMap<DimensionStructureGenerator> playersInStructures = new PlayerMap();
	private static final HashMap<Integer, ChromaDimensionBiomeType> IDMap = new HashMap();

	private static final Collection<EntityAurora> aurorae = new HashSet();

	static int dimensionAge = 0;
	static long dimensionSeed = -1;
	public static boolean serverStopping = false;
	private static boolean dimensionClearing = false;

	public static enum Biomes implements ChromaDimensionBiomeType {
		PLAINS(BiomeGenCrystalPlains.class,	"Crystal Plains",			8, 0,	ExtraChromaIDs.PLAINS, 		SubBiomes.MOUNTAINS, 	Type.MAGICAL, Type.PLAINS),
		ISLANDS(BiomeGenIslands.class,		"Iridescent Archipelago",	6, -5,	ExtraChromaIDs.ISLANDS, 	SubBiomes.DEEPOCEAN, 	Type.MAGICAL, Type.BEACH, Type.WET),
		SKYLANDS(BiomeGenSkylands.class,	"Lumen Skylands",			2, 0,	ExtraChromaIDs.SKYLANDS,	SubBiomes.VOIDLANDS,	Type.MAGICAL, Type.COLD),
		FOREST(BiomeGenGlowingForest.class,	"Glowing Forest",			10, 10,	ExtraChromaIDs.FOREST, 		SubBiomes.CRYSFOREST,	Type.MAGICAL, Type.FOREST),
		SPARKLE(BiomeGenSparkle.class,		"Sparkling Sands",			4, 0,	ExtraChromaIDs.SPARKLE,								Type.MAGICAL, Type.BEACH, Type.SANDY),
		GLOWCRACKS(BiomeGenGlowCracks.class,"Radiant Fissures",			3, 0,	ExtraChromaIDs.GLOWCRACKS,							Type.MAGICAL, Type.HOT),
		STRUCTURE(StructureBiome.class,		"Structure Field",			0, 0,	ExtraChromaIDs.STRUCTURE, 							Type.MAGICAL, Type.PLAINS),
		CENTER(BiomeGenCentral.class, 		"Luminescent Sanctuary",	0, 0,	ExtraChromaIDs.CENTRAL,								Type.MAGICAL, Type.FOREST, Type.DENSE),
		MONUMENT(MonumentBiome.class,		"Monument Field",			0, 0,	ExtraChromaIDs.MONUMENT,							Type.MAGICAL, Type.PLAINS);

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
			IDMap.put(id, this);

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

		public static ChromaDimensionBiomeType getFromID(int id) {
			return IDMap.get(id);
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
			IDMap.put(id, this);

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
		RetroGenController.instance.excludeWorld(id);

		for (int i = 0; i < Biomes.biomeList.length; i++) {
			Biomes b = Biomes.biomeList[i];
			b.create();
			BiomeDictionary.registerBiomeType(b.instance, b.types);
			if (b.subBiome != null)
				BiomeDictionary.registerBiomeType(b.subBiome.instance, b.subBiome.types);
		}
	}

	public static void checkChromaDimensionUnload() {
		World world = DimensionManager.getWorld(ExtraChromaIDs.DIMID.getValue());
		if (world != null) {
			if (world.playerEntities.isEmpty())
				resetDimension(world);
		}
	}

	public static void resetDimension(World world) {
		if (!DragonAPICore.hasGameLoaded())
			return;
		if (dimensionAge <= 1200 && !serverStopping) {
			ChromatiCraft.logger.log("Dimension is only "+dimensionAge+" ticks old; not resetting");
			return;
		}
		if (dimensionClearing) {
			ChromatiCraft.logger.log("Dimension already resetting; not attempting to reset during a reset");
			return;
		}
		dimensionClearing = true;
		ChromatiCraft.logger.log("Resetting dimension of age "+dimensionAge+"; Server shutdown? "+serverStopping);
		dimensionSeed = -1;
		dimensionAge = 0;
		playersInStructures.clear();
		aurorae.clear();
		if (world instanceof WorldServer)
			((WorldServer)world).flush(); //Hopefully kill all I/O
		DimensionStructureGenerator.resetCachedGenerators();
		getChunkProvider(world).clearCaches(!serverStopping);
		System.gc();
		String path = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath().replaceAll("\\\\", "/").replaceAll("/\\./", "/");
		File dim = new File(path+"/DIM"+ExtraChromaIDs.DIMID.getValue());
		if (dim.exists() && dim.isDirectory()) {
			boolean del = ReikaFileReader.deleteFolderWithContents(dim, 100);
			if (!del) {
				ChromatiCraft.logger.logError("Could not delete dimension chunk data; you must delete it manually or the dimension will be invalid.");
			}
		}
		ReikaPacketHelper.sendDataPacketToEntireServer(ChromatiCraft.packetChannel, ChromaPackets.LEAVEDIM.ordinal());
		dimensionClearing = false;
	}

	public static void resetDimensionClient() {
		ChromatiCraft.logger.log("Resetting clientside dimension");
		dimensionSeed = -1;
		dimensionAge = 0;
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
				disallowBannedItems(ep);
			}
		}
	}

	private static void disallowBannedItems(EntityPlayer ep) {
		ItemStack held = ep.getCurrentEquippedItem();
		if (held != null && bannedItems.contains(new KeyedItemStack(held).setSimpleHash(true))) {
			/*
			boolean flag = false;
			for (int i = 0; i < ep.inventory.getSizeInventory(); i++) {
				if (ep.inventory.getStackInSlot(i) == null) {
					ep.inventory.setInventorySlotContents(i, held);
					flag = true;
					break;
				}
			}
			if (!flag) {
			 */

			//ReikaItemHelper.dropItem(ep, held);

			punishCheatingPlayer(ep);
		}
	}

	//Make this HURT
	public static void punishCheatingPlayer(EntityPlayer ep) {
		ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.explode", 1, 1);
		ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.explode", 1, 0.5F);
		ReikaParticleHelper.EXPLODE.spawnAt(ep);
		ep.attackEntityFrom(DamageSource.generic, ReikaRandomHelper.getRandomBetween(5, 10));
		Vec3 v = ep.getLookVec();
		ReikaEntityHelper.knockbackEntityFromPos(ep.posX+v.xCoord, ep.posY+v.yCoord-1.5, ep.posZ+v.zCoord, ep, 2.5);
		ep.velocityChanged = true;
		ep.fallDistance += 10;
		//}
		ep.setCurrentItemOrArmor(0, null); //destroy item

		ProgressStage.STRUCTCHEAT.stepPlayerTo(ep);
	}

	public static DimensionStructureGenerator getStructurePlayerIsIn(EntityPlayer ep) {
		return playersInStructures.get(ep);
	}

	public static void addPlayerToStructure(EntityPlayer ep, DimensionStructureGenerator structure) {
		playersInStructures.put(ep, structure);
		/*
		if (ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, structure.getCoreColor(ep.worldObj))) {
			structure.forceOpen(ep.worldObj, ep);
		}
		 */
	}

	@SideOnly(Side.CLIENT)
	public static void addPlayerToStructureClient(EntityPlayer ep, DimensionStructureType structure) {
		playersInStructures.put(ep, structure.createGenerator(-1));
	}

	public static void removePlayerFromStructure(EntityPlayer ep) {
		playersInStructures.remove(ep);
	}

	private static void banBlock(Block b) {
		if (b == null)
			return;
		BlockKey bk = new BlockKey(b);
		bannedBlocks.add(bk);
		Item i = Item.getItemFromBlock(b);
		if (i != null)
			bannedItems.add(new KeyedItemStack(i).setIgnoreMetadata(!bk.hasMetadata()).setSimpleHash(true));
	}

	static {
		if (ModList.ENDERIO.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTravelAnchor");
			banBlock(b);

			b = GameRegistry.findBlock(ModList.ENDERIO.modLabel, "blockTelePad");
			banBlock(b);

			Item i = GameRegistry.findItem(ModList.ENDERIO.modLabel, "itemTravelStaff");
			if (i != null)
				bannedItems.add(new KeyedItemStack(i).setIgnoreNBT(true).setSimpleHash(true));
		}

		Item i = GameRegistry.findItem("GraviSuite", "vajra");
		if (i != null)
			bannedItems.add(new KeyedItemStack(i).setIgnoreNBT(true).setSimpleHash(true));

		if (ModList.THAUMICTINKER.isLoaded()) {
			Block b = GameRegistry.findBlock(ModList.THAUMICTINKER.modLabel, "warpGate");
			banBlock(b);
		}

		i = GameRegistry.findItem(ModList.DRACONICEVO.modLabel, "teleporterMKI");
		if (i != null)
			bannedItems.add(new KeyedItemStack(i).setIgnoreNBT(true).setSimpleHash(true));
		i = GameRegistry.findItem(ModList.DRACONICEVO.modLabel, "teleporterMKII");
		if (i != null)
			bannedItems.add(new KeyedItemStack(i).setIgnoreNBT(true).setSimpleHash(true));
	}

	@SideOnly(Side.CLIENT)
	public static void addAurora(EntityAurora e) {
		aurorae.add(e);
	}

	@SideOnly(Side.CLIENT)
	public static void renderAurorae() {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_FOG);
		for (EntityAurora e : aurorae) {
			Aurora a = e.getAurora();
			if (a != null) {
				GL11.glPushMatrix();
				//GL11.glTranslated(-e.posX, -e.posY, -e.posZ);
				GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
				//GL11.glTranslated(RenderManager.renderPosX-e.posX, RenderManager.renderPosY-e.posY, RenderManager.renderPosZ-e.posZ);
				a.render();
				GL11.glPopMatrix();
			}
		}
		GL11.glPopAttrib();
	}

}
