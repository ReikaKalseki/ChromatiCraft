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
import java.util.Random;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome.ChromaDimensionSubBiome;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager.TuningThresholds;
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
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.IDCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;

public class ChromaDimensionManager {

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
			IDCollisionTracker.instance.addBiomeID(ChromatiCraft.instance, id, biomeClass);
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

		public boolean isFarRegions() {
			switch(this) {
				case FOREST:
				case GLOWCRACKS:
				case ISLANDS:
				case PLAINS:
				case SKYLANDS:
				case SPARKLE:
					return true;
				case CENTER:
				case STRUCTURE:
				case MONUMENT:
					return false;
			}
			return false;
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
			IDCollisionTracker.instance.addBiomeID(ChromatiCraft.instance, id, biomeClass);
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

		public int ordinal();

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
			ChromatiCraft.logger.log("Checking dimension for unload - player entities: "+world.playerEntities);
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
		File path = DimensionManager.getCurrentSaveRootDirectory();
		File dim = new File(path, "DIM"+ExtraChromaIDs.DIMID.getValue());
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
		File f = new File(DragonAPICore.getMinecraftDirectory(), "mods/VoxelMods/voxelMap/cache");
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

	public static void tickPlayersInStructures(World world) {
		for (UUID id : playersInStructures.keySet()) {
			EntityPlayer ep = world.func_152378_a(id);
			if (ep != null) {
				if (ep.posY >= 64+ChunkProviderChroma.VERTICAL_OFFSET+2) {
					removePlayerFromStructure(ep);
				}
				else {
					playersInStructures.directGet(id).tickPlayer(ep);
					CheatingPreventionSystem.instance.tick(ep);
				}
			}
		}
	}

	public static DimensionStructureGenerator getStructurePlayerIsIn(EntityPlayer ep) {
		return playersInStructures.get(ep);
	}

	public static boolean addPlayerToStructure(EntityPlayerMP ep, DimensionStructureGenerator structure) {
		if (!DimensionTuningManager.TuningThresholds.STRUCTURES.isSufficientlyTuned(ep))
			return false;
		playersInStructures.put(ep, structure);
		/*
		if (ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, structure.getCoreColor(ep.worldObj))) {
			structure.forceOpen(ep.worldObj, ep);
		}
		 */
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTUREENTRY.ordinal(), ep, structure.getType().ordinal());
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void addPlayerToStructureClient(EntityPlayer ep, DimensionStructureType structure) {
		playersInStructures.put(ep, structure.createGenerator(-1));
	}

	public static void removePlayerFromStructure(EntityPlayer ep) {
		playersInStructures.remove(ep);
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

	public static void onPlayerBlockedFromBiome(World world, int x, int y, int z, Entity ep) {
		if (world.isRemote) {
			onPlayerBlockedFromBiomeClient(world, x, y, z, ep);
		}
		else {
			if (world.rand.nextInt(5) == 0)
				ChromaSounds.LOREHEX.playSound(ep, 0.05F, 0.75F);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void onPlayerBlockedFromBiomeClient(World world, int x, int y, int z, Entity ep) {
		Random rand = world.rand;
		//EntityBlurFX fx = new EntityBlurFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble());

		/*
		int r = 1;
		for (int j = -r; j <= r; j++) {
			int dy = y+j;
			if (world.getBlock(x, dy, z) != Blocks.air)
				continue;
			AxisAlignedBB box1 = ep.boundingBox.expand(0, 4, 0);
			AxisAlignedBB box2 = ReikaAABBHelper.getBlockAABB(x, dy, z);
			double d = 0.0;
			while (!box1.intersectsWith(box2)) {
				box1 = box1.expand(d, d, d);
				box2 = box2.expand(d, d, d);
				d += 0.025;
			}
			double nx = Math.max(box1.minX, box2.minX);
			double ny = Math.max(box1.minY, box2.minY);
			double nz = Math.max(box1.minZ, box2.minZ);
			double px = Math.min(box1.maxX, box2.maxX);
			double py = Math.min(box1.maxY, box2.maxY);
			double pz = Math.min(box1.maxZ, box2.maxZ);
			//AxisAlignedBB result = AxisAlignedBB.getBoundingBox(nx, ny, nz, px, py, pz);
			double sx = ReikaRandomHelper.getRandomBetween(nx, px);
			double sy = ReikaRandomHelper.getRandomBetween(ny, py);
			double sz = ReikaRandomHelper.getRandomBetween(nz, pz);
		 */

		if (rand.nextBoolean())
			return;
		int r = 6;
		double dr0 = r-0.5;
		double dr1 = r-0.35;
		double dr2 = r-2;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					if (i*i+j*j+k*k > dr0*dr0)
						continue;
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (world.getBlock(dx, dy, dz) != Blocks.air)
						continue;
					if (isStructureBiome(world.getBiomeGenForCoords(dx, dz)))
						continue;
					for (int d = 2; d < 6; d++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
						int ddx = dx+dir.offsetX;
						int ddz = dz+dir.offsetZ;
						if (world.getBlock(ddx, dy, ddz) != Blocks.air)
							continue;
						if (isStructureBiome(world.getBiomeGenForCoords(ddx, ddz)))
							continue;
						double sx = dx+rand.nextDouble();
						double sy = dy+rand.nextDouble();
						double sz = dz+rand.nextDouble();
						switch(dir) {
							case NORTH:
								sz = dz;
								break;
							case SOUTH:
								sz = dz+1;
								break;
							case EAST:
								sx = dx+1;
								break;
							case WEST:
								sx = dx;
								break;
							default:
								break;
						}
						if (ReikaMathLibrary.py3d(sx-ep.posX, sy-ep.posY, sz-ep.posZ) > dr1)
							continue;

						EntityCCBlurFX fx = new EntityCCBlurFX(world, sx, sy, sz);
						float sc = 3.5F;//2;
						float br = 0.08F;//0.2F;
						if (ReikaMathLibrary.py3d(sx-ep.posX, sy-ep.posY, sz-ep.posZ) > dr2) {
							double dd = ReikaMathLibrary.py3d(sx-ep.posX, sy-ep.posY, sz-ep.posZ)-dr2;
							br -= dd/20D;
						}
						if (br <= 0)
							continue;
						int l = ReikaRandomHelper.getRandomBetween(80, 180);
						fx.setIcon(ChromaIcons.FADE_GENTLE).setAlphaFading().setScale(sc).setColor(ReikaColorAPI.getColorWithBrightnessMultiplier(0x22aaff, br)).setRapidExpand().setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);

					}
				}
			}
		}
	}

	public static boolean isBlockedAir(World world, int x, int y, int z, Block b, Entity ep) {
		return !b.isOpaqueCube() && ep instanceof EntityPlayer && isStructureBiome(world.getBiomeGenForCoords(x, z)) && !TuningThresholds.STRUCTUREBIOMES.isSufficientlyTuned((EntityPlayer)ep);
	}

	public static boolean isStructureBiome(BiomeGenBase b) {
		ChromaDimensionBiomeType type = Biomes.getFromID(b.biomeID);
		return type == Biomes.STRUCTURE || type == Biomes.MONUMENT;
	}

	public static boolean isDisallowedEntity(Entity e) {
		if (ModList.THAUMCRAFT.isLoaded() && isThaumEvilMob(e))
			return true;
		if (ModList.ARSMAGICA.isLoaded() && e.getClass().getName().endsWith("EntityDryad"))
			return true;
		return false;
	}

	@ModDependent(ModList.THAUMCRAFT)
	private static boolean isThaumEvilMob(Entity e) {
		if (e instanceof ITaintedMob || e instanceof IEldritchMob)
			return true;
		if (e instanceof IMob && e.getClass().getName().startsWith("thaumcraft.common.entities.monster"))
			return true;
		return false;
	}

}
