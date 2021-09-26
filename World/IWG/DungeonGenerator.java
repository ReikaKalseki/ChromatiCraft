/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.BlockFluidBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.BurrowStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.DesertStructure;
import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.OceanStructure;
import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.Base.GeneratedStructureBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import Reika.DragonAPI.Instantiable.IO.NBTFile.SimpleNBTFile;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.PlanetDimensionHandler;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DungeonGenerator implements RetroactiveGenerator {

	public static final DungeonGenerator instance = new DungeonGenerator();

	private final ForgeDirection[] dirs = ForgeDirection.values();

	private EnumMap<ChromaStructures, VoronoiNoiseGenerator> structs = new EnumMap(ChromaStructures.class);
	private EnumMap<ChromaStructures, HashMap<WorldChunk, StructureGenStatus>> statusMap = new EnumMap(ChromaStructures.class);
	private EnumMap<ChromaStructures, TileEntityCache<StructureGenData>> structureMap = new EnumMap(ChromaStructures.class);

	private DungeonGenerator() {
		ChromaStructures[] li = {ChromaStructures.CAVERN, ChromaStructures.BURROW, ChromaStructures.OCEAN, ChromaStructures.DESERT, ChromaStructures.SNOWSTRUCT, ChromaStructures.BIOMEFRAG};
		for (ChromaStructures s : li) {
			structs.put(s, null);
			structureMap.put(s, new TileEntityCache());
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void updateNoisemaps(World world) {
		for (Entry<ChromaStructures, VoronoiNoiseGenerator> e : structs.entrySet()) {
			VoronoiNoiseGenerator v = e.getValue();
			ChromaStructures s = e.getKey();
			long sd = world.getSeed() ^ (s.ordinal()*41381);
			if (v == null || v.seed != sd) {
				v = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(sd ^ (ReikaFileReader.getRealPath(world.getSaveHandler().getWorldDirectory()).hashCode() + s.ordinal())).setFrequency(0.75D/this.getNoiseScale(s));
				v.randomFactor = 0.55;
				e.setValue(v);
			}
		}
	}

	public Collection<ChromaStructures> getStructureTypes() {
		return Collections.unmodifiableCollection(structs.keySet());
	}

	private Collection<WorldLocation> getNearbyZones(ChromaStructures s, WorldServer world, double x, double z, double r) {
		this.updateNoisemaps(world);
		Collection<DecimalPosition> li = structs.get(s).getCellsWithin2D(x, z, r);
		//ReikaJavaLibrary.pConsole("Found all potential zones within "+r+" of "+x+", "+z+": "+li);
		Collection<WorldLocation> ret = new ArrayList();
		HashMap<WorldChunk, StructureGenStatus> cache = this.getStatusCache(s);
		for (DecimalPosition d : li) {
			int cx = MathHelper.floor_double(d.xCoord) >> 4;
			int cz = MathHelper.floor_double(d.zCoord) >> 4;
			WorldChunk wc = new WorldChunk(world, cx, cz);
			StructureGenStatus get = cache.get(wc);
			if (get == null)
				cache.put(wc, StructureGenStatus.PLANNED);
			if (get != null && get.isFinalized() && !get.hasStructure())
				continue;
			ret.add(new WorldLocation(world, d));
		}
		return ret;
	}

	private WorldLocation getNearestZone(ChromaStructures s, WorldServer world, double x, double z, double r) {
		return this.getNearestZone(s, world, x, z, r, null);
	}

	private WorldLocation getNearestZone(ChromaStructures s, WorldServer world, double x, double z, double r, WorldLocation exclude) {
		Collection<WorldLocation> c = this.getNearbyZones(s, world, MathHelper.floor_double(x), MathHelper.floor_double(z), r);
		WorldLocation closest = null;
		double d = Double.POSITIVE_INFINITY;
		for (WorldLocation loc : c) {
			if (exclude != null && exclude.equals(loc))
				continue;
			double dist = loc.getDistanceTo(x, loc.yCoord, z);
			if ((closest == null || dist < d) && dist <= r) {
				d = dist;
				closest = loc;
			}
		}
		return closest;
	}

	/** Block coords! */
	public StructureSeekData getNearestRealStructure(ChromaStructures s, WorldServer world, double x, double z, double r, boolean requireGenned) {
		this.updateNoisemaps(world);
		WorldLocation src = new WorldLocation(world, x, 0, z);
		TileEntityCache<StructureGenData> cache = structureMap.get(s);
		Collection<WorldLocation> li = cache.getAllLocationsNear(src, r);
		Iterator<WorldLocation> it = li.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			StructureGenData dat = cache.get(loc);
			StructureGenStatus stat = dat.status;
			if (!stat.hasStructure() && stat.isFinalized())
				it.remove();
			else if (requireGenned && !stat.isGenerated())
				it.remove();
			else if (stat == StructureGenStatus.INERT || stat == StructureGenStatus.INERT_GEN)
				it.remove();
		}
		boolean genned = true;
		if (!requireGenned && li.isEmpty()) { //no generated valid, consult noise
			genned = false;
			Collection<DecimalPosition> li2 = structs.get(s).getCellsWithin2D(x, z, r);
			Iterator<DecimalPosition> it2 = li2.iterator();
			while (it2.hasNext()) {
				DecimalPosition loc = it2.next();
				StructureGenStatus stat = this.getGenStatus(s, world, MathHelper.floor_double(loc.xCoord), MathHelper.floor_double(loc.zCoord));
				if (!stat.hasStructure() && stat.isFinalized())
					it2.remove();
				else if (requireGenned && !stat.isGenerated())
					it2.remove();
				else if (stat == StructureGenStatus.INERT || stat == StructureGenStatus.INERT_GEN)
					it2.remove();
				else if (!this.isValidBiomeNear(world, MathHelper.floor_double(loc.xCoord), MathHelper.floor_double(loc.zCoord), s))
					it2.remove();
			}
			if (li2.isEmpty())
				return null;
			for (DecimalPosition loc : li2) {
				li.add(new WorldLocation(world, loc));
			}
		}
		if (li.isEmpty())
			return null;
		WorldLocation closest = null;
		double d = Double.POSITIVE_INFINITY;
		for (WorldLocation loc : li) {
			double dist = loc.getDistanceTo(x, loc.yCoord, z);
			if ((closest == null || dist < d) && dist <= r) {
				d = dist;
				closest = loc;
			}
		}
		return closest == null ? null : new StructureSeekData(closest, genned);
	}

	/** In BLOCK coords */
	public StructureGenStatus getGenStatus(ChromaStructures s, WorldServer world, int x, int z) {
		StructureGenStatus def = this.isGennableChunk(world, x, z, s);
		boolean genned = ReikaWorldHelper.isChunkGenerated(world, x, z);
		if (def == StructureGenStatus.INERT && genned)
			def = StructureGenStatus.INERT_GEN;
		//if (def == StructureGenStatus.PLANNED && !this.isValidBiomeNear(world, x, z, s))
		//	def = StructureGenStatus.FAILURE;
		Collection<WorldLocation> c = this.getNearbyZones(s, world, x, z, 32);
		HashMap<WorldChunk, StructureGenStatus> cache = this.getStatusCache(s);
		for (WorldLocation loc : c) {
			WorldChunk wc = new WorldChunk(world, loc.xCoord >> 4, loc.zCoord >> 4);
			StructureGenStatus get = cache.get(wc);
			if (get == null || def.canOverwrite(get))
				cache.put(wc, def);
		}
		WorldChunk wc = new WorldChunk(world, x >> 4, z >> 4);
		StructureGenStatus stat = cache.get(wc);
		if (stat == StructureGenStatus.INERT && genned) {
			stat = StructureGenStatus.INERT_GEN;
			cache.put(wc, stat);
		}
		return stat != null ? stat : def;
	}

	/** In CHUNK coords */
	private void markChunkStatus(World world, int x, int z, ChromaStructures s, StructureGenStatus stat) {
		//ReikaJavaLibrary.pConsole("Marking "+x*16+", "+z*16+" as "+stat);
		WorldChunk wc = new WorldChunk(world, x, z);
		this.getStatusCache(s).put(wc, stat);
		TileEntityCache<StructureGenData> cache = structureMap.get(s);
		WorldLocation loc = new WorldLocation(world, x << 4, 0, z << 4);
		StructureGenData data = cache.get(loc);
		if (data == null) {
			data = new StructureGenData(wc, stat);
			cache.put(loc, data);
		}
		data.status = stat;
	}

	public void recacheStructureTile(TileEntityStructControl te) {
		this.markChunkStatus(te.worldObj, te.xCoord >> 4, te.zCoord >> 4, te.getStructureType(), StructureGenStatus.SUCCESS);
	}

	public void removeStructureTile(TileEntityStructControl te) {
		this.markChunkStatus(te.worldObj, te.xCoord >> 4, te.zCoord >> 4, te.getStructureType(), StructureGenStatus.REMOVED);
	}

	private HashMap<WorldChunk, StructureGenStatus> getStatusCache(ChromaStructures s) {
		if (statusMap.isEmpty()) {
			try {
				this.loadStatusCache();
			}
			catch (IOException e) {
				ChromatiCraft.logger.logError("Could not load structure status cache!");
				e.printStackTrace();
			}
		}
		HashMap<WorldChunk, StructureGenStatus> set = statusMap.get(s);
		if (set == null) {
			set = new HashMap();
			statusMap.put(s, set);
		}
		return set;
	}

	private void loadStatusCache() throws IOException {
		HashMap<WorldChunk, StructureGenStatus> ret = new HashMap();
		SimpleNBTFile nf = new SimpleNBTFile(this.getStatusCacheFile());
		nf.load();
		if (nf.data != null) {
			this.loadStatusCacheFromNBT(nf.data);
		}
	}

	@SubscribeEvent
	public void saveStatusCache(WorldEvent.Save evt) throws IOException {
		if (evt.world.provider.dimensionId == 0) {
			NBTTagCompound tag = new NBTTagCompound();
			this.writeStatusCacheToNBT(tag);
			SimpleNBTFile nf = new SimpleNBTFile(this.getStatusCacheFile());
			nf.data = tag;
			nf.save();
		}
	}

	private void loadStatusCacheFromNBT(NBTTagCompound data) {
		for (Object o : data.func_150296_c()) {
			String sg = (String)o;
			NBTTagCompound tag = data.getCompoundTag(sg);
			ChromaStructures s = ChromaStructures.valueOf(sg);
			HashMap<WorldChunk, StructureGenStatus> map = this.loadMapFromNBT(s, tag);
			statusMap.put(s, map);
		}
	}

	private void writeStatusCacheToNBT(NBTTagCompound nbt) {
		for (ChromaStructures s : statusMap.keySet()) {
			HashMap<WorldChunk, StructureGenStatus> map = statusMap.get(s);
			NBTTagCompound tag = new NBTTagCompound();
			this.writeMapToNBT(tag, map);
			nbt.setTag(s.name(), tag);
		}
	}

	private HashMap<WorldChunk, StructureGenStatus> loadMapFromNBT(ChromaStructures s, NBTTagCompound tag) {
		HashMap<WorldChunk, StructureGenStatus> map = new HashMap();
		for (Object o : tag.func_150296_c()) {
			String sg = (String)o;
			WorldChunk wc = WorldChunk.fromSerialString(sg);
			map.put(wc, StructureGenStatus.valueOf(tag.getString(sg)));
		}
		return map;
	}

	private void writeMapToNBT(NBTTagCompound tag, HashMap<WorldChunk, StructureGenStatus> map) {
		for (Entry<WorldChunk, StructureGenStatus> e : map.entrySet()) {
			tag.setString(e.getKey().toSerialString(), e.getValue().name());
		}
	}

	private File getStatusCacheFile() {
		return new File(DimensionManager.getCurrentSaveRootDirectory(), "ChromatiCraft_Data/StructureStatus.dat");
	}

	private int getNoiseScale(ChromaStructures s) {
		switch(s) {
			case DESERT:
				return 512;
			case OCEAN:
				return 1024;
			case CAVERN:
				return 144;
			case BURROW:
				return 256;
			case SNOWSTRUCT:
				return 768;
			case BIOMEFRAG:
				return 440;
			default:
				return 1;
		}
	}

	private int getExclusionZone(ChromaStructures s) {
		switch(s) {
			case DESERT:
				return 256;
			case OCEAN:
				return 512;
			case CAVERN:
				return 64;
			case BURROW:
				return 64;
			case SNOWSTRUCT:
				return 256;
			case BIOMEFRAG:
				return 384;
			default:
				return 1;
		}
	}

	public void onGenerateStructure(ChromaStructures s, FragmentStructureBase s2, World world, TileEntityStructControl te) {
		s2.onPlace(world, te);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			for (ChromaStructures s : structs.keySet()) {
				this.checkChunk(world, chunkX, chunkZ, random, s);
			}
		}
	}

	private boolean checkChunk(World world, int chunkX, int chunkZ, Random random, ChromaStructures s) {
		if (!this.isVoidWorld(world, chunkX*16, chunkZ*16) && this.isGennableChunk(world, chunkX*16, chunkZ*16, s) == StructureGenStatus.PLANNED) {
			//ReikaWorldHelper.forceGenAndPopulate(world, chunkX*16, chunkZ*16, s == Structures.OCEAN ? 2 : 1); causes extra structures
			ChunkCoordIntPair pos = this.tryGenerateInChunk(world, chunkX*16, chunkZ*16, random, s, ChromaOptions.getStructureTriesPerChunk());
			if (pos == null)
				pos = this.tryGenerateInChunksAround(world, chunkX*16, chunkZ*16, random, s, ChromaOptions.getStructureTriesPerChunk());
			if (pos != null) {
				this.markChunkStatus(world, chunkX, chunkZ, s, StructureGenStatus.INERT_GEN);
				this.markChunkStatus(world, pos.chunkXPos, pos.chunkZPos, s, StructureGenStatus.SUCCESS);
				//ChromatiCraft.logger.log("CC STRUCTURE STATUS: Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
				return true;
			}
			else {
				//ChromatiCraft.logger.log("CC STRUCTURE STATUS: Failed to generate a "+s.name()+" at "+chunkX*16+", "+chunkZ*16+"; this grid cell is dead");
				this.markChunkStatus(world, chunkX, chunkZ, s, StructureGenStatus.FAILURE);
			}
		}
		else {
			//ChromatiCraft.logger.log("CC STRUCTURE STATUS: Not generating a "+s.name()+" at "+chunkX*16+", "+chunkZ*16+"; not planned");
			//this.markChunkStatus(world, chunkX << 4, chunkZ << 4, s, StructureStatus.INERT);
		}
		return false;
	}

	private ChunkCoordIntPair tryGenerateInChunksAround(World world, int cx, int cz, Random rnd, ChromaStructures s, int tries) {
		int r = 16;
		while (r <= 48) {
			for (int i = -r; i <= r; i += 16) {
				for (int k = -r; k <= r; k += 16) {
					if (i == 0 && k == 0)
						continue;
					int dx = cx+i;
					int dz = cz+k;
					ChunkCoordIntPair ret = this.tryGenerateInChunk(world, dx, dz, rnd, s, tries);
					if (ret != null)
						return ret;
				}
			}
			r += 16;
		}
		return null;
	}

	private ChunkCoordIntPair tryGenerateInChunk(World world, int cx, int cz, Random r, ChromaStructures s, int tries) {
		this.markChunkStatus(world, cx >> 4, cz >> 4, s, StructureGenStatus.GENERATING);
		boolean flag = false;
		int n = 0;
		while (!flag && n < tries) {
			int x = cx + r.nextInt(16);
			int z = cz + r.nextInt(16);
			int rx = x;
			int rz = z;
			FragmentStructureBase s2 = (FragmentStructureBase)s.getStructure();
			s2.resetToDefaults();
			s2.markForWorldgen();
			s2.setRNG(r);
			n++;
			if (!this.isValidBiome(s, world.getBiomeGenForCoords(x, z)))
				continue;
			switch(s) {
				default:
					break;
				case CAVERN: {
					int y = 10+r.nextInt(40);
					FilledBlockArray struct = ChromaStructures.CAVERN.getArray(world, x, y, z, r);
					if (this.isValidCavernLocation(world, x, y, z, struct)) {
						struct.place(2);
						TileEntityStructControl te = null;
						//generate tunnel
						for (int i = 7; i < 18; i++) {
							int dx = x+i;
							Block b = world.getBlock(dx, y, z);
							Block b2 = world.getBlock(dx, y-1, z);
							if (b.isAir(world, dx, y, z) && b2.isAir(world, dx, y-1, z)) {
								break;
							}
							else {
								world.setBlock(dx, y, z, Blocks.air);
								world.setBlock(dx, y-1, z, Blocks.air);
								//ReikaJavaLibrary.pConsole("Digging tunnel @ depth "+i);
							}
						}
						//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+x+","+y+","+z);
						try {
							world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
							te = (TileEntityStructControl)world.getTileEntity(x, y, z);
							te.generate(s, CrystalElement.WHITE);
							rx = te.xCoord;
							rz = te.zCoord;
							this.onGenerateStructure(s, s2, world, te);
						}
						catch (Exception e) {
							((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+x+", "+y+", "+z, e));
						}
						this.populateChests(s, struct, r);
						((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
						flag = true;
						this.logErrors(world, x, y, z, s, te);
					}
					break;
				}
				case BURROW: {
					int y = world.getTopSolidOrLiquidBlock(x, z)-1;
					CrystalElement e = CrystalElement.randomElement();
					FilledBlockArray arr = ChromaStructures.BURROW.getArray(world, x, y, z, r, e);
					if (this.isValidBurrowLocation(world, x, y, z, arr)) {
						((GeneratedStructureBase)s.getStructure()).markForWorldgen();
						arr.place(2);
						TileEntityStructControl te = null;
						//world.setBlockMetadataWithNotify(x-7, y-5, z-2, 5, 3); //that chest that never points right
						//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+x+","+y+","+z);
						this.populateChests(s, arr, r);
						FilledBlockArray furn = null;
						FilledBlockArray loot = null;
						if (r.nextInt(2) == 0) {
							furn = this.tryGenerateBurrowFurnaceRoom(world, x, y, z, s, arr, r);
							if (furn != null) {
								if (r.nextInt(2) == 0) {
									loot = this.tryGenerateBurrowLootRoom(world, x, y, z, s, arr, r);
								}
							}
						}
						try {
							world.setBlock(x-5, y-8, z-2, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
							te = (TileEntityStructControl)world.getTileEntity(x-5, y-8, z-2);
							te.generate(s, e);
							te.setBurrowAddons(furn != null, loot != null);
							rx = te.xCoord;
							rz = te.zCoord;
							this.onGenerateStructure(s, s2, world, te);
						}
						catch (Exception ex) {
							((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+(x-5)+", "+(y-8)+", "+(z-2), ex));
						}
						((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
						this.modifyBlocks(s, arr, r, Modify.MOSSIFY, Modify.GRASSDIRT);
						if (furn != null)
							this.modifyBlocks(s, furn, r, Modify.MOSSIFY, Modify.GRASSDIRT);
						if (loot != null)
							this.modifyBlocks(s, loot, r, Modify.MOSSIFY, Modify.GRASSDIRT);
						flag = true;
						this.logErrors(world, x, y, z, s, te);
					}
					break;
				}
				case OCEAN: {
					int d = 3;
					int y = world.getTopSolidOrLiquidBlock(x, z)-d;
					Block b = world.getBlock(x, y+d, z);
					if (b == Blocks.water || b == Blocks.flowing_water) {
						//ReikaJavaLibrary.pConsole("Attempting gen @ "+x+", "+y+", "+z);
						//while (b == Blocks.water || b == Blocks.flowing_water && y > 0) {
						//	y--;
						//	b = world.getBlock(x, y, z);
						//}
						FilledBlockArray struct = ChromaStructures.OCEAN.getArray(world, x, y, z, r);
						if (y > 0 && this.isValidOceanLocation(world, x, y, z, struct)) {
							struct.place(2);
							TileEntityStructControl te = null;
							try {
								world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
								te = (TileEntityStructControl)world.getTileEntity(x, y, z);
								te.generate(s, CrystalElement.WHITE);
								rx = te.xCoord;
								rz = te.zCoord;
								this.onGenerateStructure(s, s2, world, te);
							}
							catch (Exception e) {
								((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+x+", "+y+", "+z, e));
							}
							this.populateChests(s, struct, r);
							this.programSpawners(s, struct);
							((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
							this.modifyBlocks(s, struct, r, Modify.MOSSIFY);
							this.generatePit(world, x, y, z);
							flag = true;
							this.logErrors(world, x, y, z, s, te);
						}
					}
					break;
				}
				case DESERT: {
					int y = world.getTopSolidOrLiquidBlock(x, z);
					if (world.getBlock(x, y-1, z) != Blocks.sand)
						continue;

					y -= 8;
					Block b = world.getBlock(x, y, z);
					BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
					if (this.isValidBiomeForDesertStruct(biome)) {

						x -= 7;
						y -= 3;
						z -= 7;

						FilledBlockArray struct = ChromaStructures.DESERT.getArray(world, x, y, z, r);
						DesertStructure.getTerrain(struct, x, y, z);
						if (this.isValidDesertLocation(world, x, y, z, struct)) {
							struct.place(2);
							TileEntityStructControl te = null;
							try {
								world.setBlock(x+7, y+3, z+7, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
								te = (TileEntityStructControl)world.getTileEntity(x+7, y+3, z+7);
								te.generate(s, CrystalElement.WHITE);
								rx = te.xCoord;
								rz = te.zCoord;
								this.onGenerateStructure(s, s2, world, te);
							}
							catch (Exception e) {
								((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+(x+7)+", "+(y+3)+", "+(z+7), e));
							}
							this.populateChests(s, struct, r);
							this.programSpawners(s, struct);
							((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
							for (int k = 0; k < struct.getSize(); k++) {
								Coordinate c = struct.getNthBlock(k);
								if (c.yCoord > struct.getMaxY()-2) {
									Block b1 = c.offset(0, 1, 0).getBlock(world);
									Block b2 = c.offset(0, -1, 0).getBlock(world);
									if (b1 == Blocks.air && b2 == Blocks.sand && ReikaRandomHelper.doWithChance(20+30D*Math.abs(Math.abs(c.xCoord-struct.getMidX())+Math.abs(c.zCoord-struct.getMidZ()))/7D)) {
										c.setBlock(world, Blocks.air);
									}
								}
							}
							for (int k1 = struct.getMinX(); k1 <= struct.getMaxX(); k1++) {
								for (int k2 = struct.getMinZ(); k2 <= struct.getMaxZ(); k2++) {
									Coordinate c = new Coordinate(k1, world.getTopSolidOrLiquidBlock(k1, k2), k2);
									if (c.getBlock(world) == Blocks.air && c.offset(0, -1, 0).getBlock(world) == Blocks.sand && ReikaRandomHelper.doWithChance(2)) {
										if (c.offset(1, 0, 0).getBlock(world) == Blocks.air && c.offset(-1, 0, 0).getBlock(world) == Blocks.air) {
											if (c.offset(0, 0, 1).getBlock(world) == Blocks.air && c.offset(0, 0, -1).getBlock(world) == Blocks.air) {
												c.setBlock(world, Blocks.cactus);
												if (ReikaRandomHelper.doWithChance(40)) {
													c.offset(0, 1, 0).setBlock(world, Blocks.cactus);
													if (ReikaRandomHelper.doWithChance(40))
														c.offset(0, 2, 0).setBlock(world, Blocks.cactus);
												}
											}
										}
									}
								}
							}
							flag = true;
							this.logErrors(world, x, y, z, s, te);
						}
					}
					break;
				}
				case SNOWSTRUCT: {
					int y = world.getTopSolidOrLiquidBlock(x, z)-1;
					FilledBlockArray arr = ChromaStructures.SNOWSTRUCT.getArray(world, x, y, z, r);
					if (this.isValidSnowStructLocation(world, x, y, z, arr)) {
						arr.offset(0, -6, 0);
						arr.place(2);
						TileEntityStructControl te = null;
						try {
							world.setBlock(x+8, y-3, z+6, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
							te = (TileEntityStructControl)world.getTileEntity(x+8, y-3, z+6);
							te.generate(s, CrystalElement.WHITE);
							rx = te.xCoord;
							rz = te.zCoord;
							this.onGenerateStructure(s, s2, world, te);
						}
						catch (Exception e) {
							((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+(x+8)+", "+(y-3)+", "+(z+6), e));
						}
						this.modifyBlocks(s, arr, r, Modify.MOSSIFY, Modify.GRASSDIRT, Modify.ADJTREES, Modify.CLEANENTRANCE);
						this.addMissingSupport(arr, Blocks.stone, 0, 5);
						this.addSnowCover(arr, 4);
						this.programSpawners(s, arr);
						this.populateChests(s, arr, r);
						((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
						flag = true;
						this.logErrors(world, x, y, z, s, te);
					}
					break;
				}
				case BIOMEFRAG: {
					int y = world.getTopSolidOrLiquidBlock(x, z)-5;
					FilledBlockArray arr = ChromaStructures.BIOMEFRAG.getArray(world, x, y, z, r);
					if (this.isValidBiomeStructLocation(world, x, y, z, arr)) {
						arr.place(2);
						TileEntityStructControl te = null;
						try {
							world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
							te = (TileEntityStructControl)world.getTileEntity(x, y, z);
							te.generate(s, CrystalElement.WHITE);
							rx = te.xCoord;
							rz = te.zCoord;
							this.onGenerateStructure(s, s2, world, te);
						}
						catch (Exception e) {
							((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to place controller @ "+x+", "+y+", "+z, e));
						}
						this.modifyBlocks(s, arr, r, Modify.MOSSIFY, Modify.GRASSDIRT, Modify.ADJTREES2);
						this.populateChests(s, arr, r);
						((GeneratedStructureBase)s.getStructure()).runCallbacks(world, r);
						flag = true;
						this.logErrors(world, x, y, z, s, te);
					}
					break;
				}
			}
			if (flag) {
				if (rx != cx || rz != cz)
					this.markChunkStatus(world, cx >> 4, cz >> 4, s, StructureGenStatus.INERT_GEN);
				return new ChunkCoordIntPair(rx >> 4, rz >> 4);
			}
		}
		this.markChunkStatus(world, cx >> 4, cz >> 4, s, StructureGenStatus.INERT_GEN);
		return null;
	}

	private void logErrors(World world, int x, int y, int z, ChromaStructures s, TileEntityStructControl te) {
		Collection<Exception> errors = ((GeneratedStructureBase)s.getStructure()).getErrors();
		if (!errors.isEmpty()) {/*
			if (te != null && te.getStructureType() != null) {
				te.markErroredForRegen();
				return;
			}*/
			long id = world.rand.nextLong();
			EntityPlayer ep = world.getClosestPlayer(x, y, z, -1);
			if (ep != null)
				ReikaChatHelper.sendChatToPlayer(ep, "Structure "+s+" encountered "+errors.size()+" errors during generation. Check your log for details. Error ID: "+id);
			ChromatiCraft.logger.logError("Structure "+s+" encountered "+errors.size()+" errors during generation @ "+x+", "+y+", "+z+"; Error ID: "+id);
			int top = world.getTopSolidOrLiquidBlock(x, z)-1;
			ReikaJavaLibrary.pConsole("Regional metadata: Biome - "+world.getBiomeGenForCoords(x, z)+"; top block: "+BlockKey.getAt(world, x, top, z)+" @ y="+top+"; block ="+BlockKey.getAt(world, x, y, z));
			ReikaJavaLibrary.pConsole("Error list:");
			for (Exception e : errors) {
				ReikaJavaLibrary.pConsole("=======================");
				e.printStackTrace();
				ReikaJavaLibrary.pConsole("=======================");
			}
		}
	}

	private boolean isValidBiomeForDesertStruct(BiomeGenBase biome) {
		return biome.topBlock == Blocks.sand && BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MESA);
	}

	private boolean isValidDesertLocation(World world, int x, int y, int z, FilledBlockArray struct) {
		if (!this.isValidBiomeForDesertStruct(world.getBiomeGenForCoords(struct.getMinX(), struct.getMinZ())))
			return false;
		if (!this.isValidBiomeForDesertStruct(world.getBiomeGenForCoords(struct.getMaxX(), struct.getMinZ())))
			return false;
		if (!this.isValidBiomeForDesertStruct(world.getBiomeGenForCoords(struct.getMinX(), struct.getMaxZ())))
			return false;
		if (!this.isValidBiomeForDesertStruct(world.getBiomeGenForCoords(struct.getMaxX(), struct.getMaxZ())))
			return false;
		if (world.getBlock(struct.getMinX(), struct.getMinY()+3, struct.getMinZ()) == Blocks.air)
			return false;
		if (world.getBlock(struct.getMaxX(), struct.getMinY()+3, struct.getMinZ()) == Blocks.air)
			return false;
		if (world.getBlock(struct.getMinX(), struct.getMinY()+3, struct.getMaxZ()) == Blocks.air)
			return false;
		if (world.getBlock(struct.getMaxX(), struct.getMinY()+3, struct.getMaxZ()) == Blocks.air)
			return false;
		return true;
	}

	/** BLOCK coords */
	private boolean isValidBiomeNear(World world, int x, int z, ChromaStructures s) {
		for (int i = -32; i <= 32; i += 8) {
			for (int k = -32; k <= 32; k += 8) {
				if (this.isValidBiomeAt(world, x, z, s))
					return true;
			}
		}
		return false;
	}

	/** Does not gen the chunks, but uses planned biomes; BLOCK coords */
	private boolean isValidBiomeAt(World world, int x, int z, ChromaStructures s) {
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		return this.isValidBiome(s, biome);
	}

	private boolean isValidBiome(ChromaStructures s, BiomeGenBase b) {
		boolean ocean = ReikaBiomeHelper.isOcean(b);
		switch(s) {
			case OCEAN:
				return ocean;
			case CAVERN:
				return true;
			case BURROW:
				return !ocean && b.topBlock == Blocks.grass && !this.isValidBiome(ChromaStructures.SNOWSTRUCT, b) && !ChromatiCraft.isCCBiome(b);
			case DESERT:
				return !ocean && this.isValidBiomeForDesertStruct(b);
			case SNOWSTRUCT:
				return !ocean && b.topBlock == Blocks.grass && b.getEnableSnow() && ReikaBiomeHelper.getBiomeDecorator(b).treesPerChunk < 1 && !b.biomeName.toLowerCase(Locale.ENGLISH).contains("forest");
			case BIOMEFRAG:
				return ChromatiCraft.isCCBiome(b);
			default:
				return false;
		}
	}

	private FilledBlockArray tryGenerateBurrowFurnaceRoom(World world, int x, int y, int z, ChromaStructures s, FilledBlockArray arr, Random r) {
		FilledBlockArray arr2 = ((BurrowStructure)s.getStructure()).getFurnaceRoom(world, x, y, z);
		for (Coordinate c : arr2.keySet()) {
			if (c.isEmpty(world))
				return null;
		}
		arr2.place();
		return arr2;
	}

	private FilledBlockArray tryGenerateBurrowLootRoom(World world, int x, int y, int z, ChromaStructures s, FilledBlockArray arr, Random r) {
		FilledBlockArray arr2 = ((BurrowStructure)s.getStructure()).getLootRoom(world, x, y, z);
		for (Coordinate c : arr2.keySet()) {
			if (c.isEmpty(world))
				return null;
		}
		arr2.place();
		return arr2;
	}

	private static FilledBlockArray getPitSlice(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;

		array.setBlock(x+1, y+0, z+1, b, 8);
		array.setBlock(x+1, y+0, z+2, b, 8);
		array.setBlock(x+1, y+0, z+3, b, 8);
		array.setBlock(x+1, y+0, z+4, b, 8);
		array.setBlock(x+1, y+0, z+5, b, 8);
		array.setBlock(x+2, y+0, z+1, b, 8);
		array.setBlock(x+2, y+0, z+5, b, 8);
		array.setBlock(x+3, y+0, z+1, b, 8);
		array.setBlock(x+3, y+0, z+5, b, 8);
		array.setBlock(x+5, y+0, z+1, b, 8);
		array.setBlock(x+5, y+0, z+2, b, 8);
		array.setBlock(x+5, y+0, z+3, b, 8);
		array.setBlock(x+5, y+0, z+4, b, 8);
		array.setBlock(x+5, y+0, z+5, b, 8);
		array.setBlock(x+4, y+0, z+1, b, 8);
		array.setBlock(x+4, y+0, z+5, b, 8);

		array.setBlock(x+2, y+0, z+2, Blocks.air);
		array.setBlock(x+2, y+0, z+3, Blocks.air);
		array.setBlock(x+2, y+0, z+4, Blocks.air);
		array.setBlock(x+3, y+0, z+2, Blocks.air);
		array.setBlock(x+3, y+0, z+3, Blocks.air);
		array.setBlock(x+3, y+0, z+4, Blocks.air);
		array.setBlock(x+4, y+0, z+2, Blocks.air);
		array.setBlock(x+4, y+0, z+3, Blocks.air);
		array.setBlock(x+4, y+0, z+4, Blocks.air);

		return array;
	}

	private static FilledBlockArray getEndcap1(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;
		array.setBlock(x+30, y+8, z+2, Blocks.air);
		array.setBlock(x+30, y+8, z+3, Blocks.air);
		array.setBlock(x+30, y+8, z+4, Blocks.air);
		array.setBlock(x+30, y+9, z+2, Blocks.air);
		array.setBlock(x+30, y+9, z+3, Blocks.air);
		array.setBlock(x+30, y+9, z+4, Blocks.air);
		array.setBlock(x+30, y+10, z+2, Blocks.air);
		array.setBlock(x+30, y+10, z+3, Blocks.air);
		array.setBlock(x+30, y+10, z+4, Blocks.air);

		return array;
	}

	private static FilledBlockArray getEndcap2(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;

		array.setBlock(x+2, y+8, z+30, Blocks.air);
		array.setBlock(x+2, y+9, z+30, Blocks.air);
		array.setBlock(x+2, y+10, z+30, Blocks.air);
		array.setBlock(x+3, y+8, z+30, Blocks.air);
		array.setBlock(x+3, y+9, z+30, Blocks.air);
		array.setBlock(x+3, y+10, z+30, Blocks.air);
		array.setBlock(x+4, y+8, z+30, Blocks.air);
		array.setBlock(x+4, y+9, z+30, Blocks.air);
		array.setBlock(x+4, y+10, z+30, Blocks.air);

		return array;
	}

	private void generatePit(World world, int x, int y, int z) {
		for (int i = 3; i < 32; i++) {
			FilledBlockArray arr = this.getPitSlice(world, x, y-i, z);
			boolean flag = true;
			for (int k = 0; k < arr.getSize(); k++) {
				Coordinate c = arr.getNthBlock(k);
				Block b = c.getBlock(world);
				if (b != Blocks.air) {
					flag = false;
				}
			}
			if (flag && i > 6) {
				break;
			}
			else {
				arr.place();
			}
		}

		BlockArray arr = OceanStructure.getPitCover(x, y, z);
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(world);
			c.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
		}
	}

	public static enum Modify {
		MOSSIFY,
		GRASSDIRT,
		ADJTREES,
		ADJTREES2,
		CLEANENTRANCE,
		@Deprecated ENCRUSTED,
		;

		private final int bit;

		private Modify() {
			bit = 1 << this.ordinal();
		}

		private static int getFlags(Modify... flags) {
			int ret = 0;
			for (Modify m : flags) {
				ret |= m.bit;
			}
			return ret;
		}

		private void apply(ChromaStructures s, FilledBlockArray arr, Random r) {
			switch(this) {
				case ADJTREES:
					break;
				case ADJTREES2:
					for (int x = arr.getMinX()-1; x <= arr.getMaxX()+1; x++) {
						for (int z = arr.getMinZ()-1; z <= arr.getMaxZ()+1; z++) {
							boolean walls = x >= arr.getMinX() && x <= arr.getMaxX() && z >= arr.getMinZ() && z <= arr.getMaxZ();
							for (int y = arr.getMinY(); y <= arr.getMaxY()+2; y++) {
								if (ReikaBlockHelper.isWood(arr.world, x, y, z) || (walls && ReikaBlockHelper.isLeaf(arr.world, x, y, z))) {
									arr.world.setBlockToAir(x, y, z);
								}
							}
						}
					}
					break;
				case CLEANENTRANCE:
					break;
				case ENCRUSTED:
					break;
				case GRASSDIRT:
					break;
				case MOSSIFY:
					break;
			}
		}

		private boolean applyEach(ChromaStructures s, FilledBlockArray arr, Coordinate c, Random r) {
			switch(this) {
				case ADJTREES: {
					for (int i = 0; i < 6; i++) {
						Coordinate c2 = c.offset(ForgeDirection.VALID_DIRECTIONS[i], 1);
						Block b = c2.getBlock(arr.world);
						if (b != Blocks.air && b != ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
							int meta = c2.getBlockMetadata(arr.world);
							TreeType tree = ReikaTreeHelper.getTree(b, meta);
							if (tree == null)
								tree = ModWoodList.getModWood(b, meta);
							if (tree != null) {
								BlockArray barr = new BlockArray();
								barr.recursiveMultiAddWithBounds(arr.world, c2.xCoord, c2.yCoord, c2.zCoord, c2.xCoord-12, c2.yCoord-12, c2.zCoord-12, c2.xCoord+12, c2.yCoord+12, c2.zCoord+12, b, tree.getLeafID());
								for (Coordinate c3 : barr.keySet()) {
									c3.setBlock(arr.world, Blocks.air);
								}
								return true;
							}
						}
					}
					break;
				}
				case ADJTREES2: {
					break;
				}
				case CLEANENTRANCE: {
					Block b = c.getBlock(arr.world);
					if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && c.getBlockMetadata(arr.world) == BlockType.MOSS.metadata) {
						Block b3 = c.offset(0, 1, 0).getBlock(arr.world);
						if (b3 != b) {
							for (int i = 1; i < 5; i++) {
								for (int dx = -i; dx <= i; dx++) {
									for (int dz = -i; dz <= i; dz++) {
										Coordinate c2 = c.offset(dx, i, dz);
										Block b2 = c2.getBlock(arr.world);
										if (b2 == Blocks.grass || b2.getMaterial() == Material.ground || b2.getMaterial() == Material.plants || b2 == Blocks.stone || b2 == Blocks.snow_layer) {
											c2.setBlock(arr.world, Blocks.air);
										}
									}
								}
							}
							return true;
						}
					}
					break;
				}
				case GRASSDIRT: {
					Block b = c.getBlock(arr.world);
					if (b == Blocks.dirt) {
						if (arr.world.getBlockLightValue(c.xCoord, c.yCoord+1, c.zCoord) > 8) {
							c.setBlock(arr.world, Blocks.grass);
							return true;
						}
					}
					break;
				}
				case MOSSIFY: {
					Block b = c.getBlock(arr.world);
					if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
						int meta = c.getBlockMetadata(arr.world);
						if (meta == BlockType.STONE.metadata) {
							int dy = c.yCoord-arr.getMinY();
							int dh = s == ChromaStructures.BURROW ? 1 : 2;
							int dc = s == ChromaStructures.BURROW ? 5 : 0;
							int ct = Math.max(1, dy*dh-2+dc);
							if (r.nextInt(ct) == 0) {
								arr.world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, BlockType.MOSS.metadata, 2);
								return true;
							}
						}
					}
					break;
				}
				case ENCRUSTED: {
					int n = s == ChromaStructures.BURROW ? 12 : 20;
					if (r.nextInt(n) == 0) {
						Block b = c.getBlock(arr.world);
						if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
							for (int i = 0; i < 6; i++) {
								Coordinate c2 = c.offset(ForgeDirection.VALID_DIRECTIONS[i], 1);
								Block b2 = c2.getBlock(arr.world);
								if (b2 == Blocks.air) {

								}
							}
						}
					}
					break;
				}
			}
			return false;
		}
	}

	public void modifyBlocks(ChromaStructures s, FilledBlockArray arr, Random r, Modify... flags) {
		for (Modify m : flags) {
			try {
				m.apply(s, arr, r);
			}
			catch (Exception e) {
				((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to run pass '"+m+"'", e));
			}
		}
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			for (Modify m : flags) {
				try {
					m.applyEach(s, arr, c, r);
				}
				catch (Exception e) {
					((GeneratedStructureBase)s.getStructure()).addError(new Exception("Failed to run localized pass '"+m+"' @ "+c, e));
				}
			}
		}
	}

	public void programSpawners(ChromaStructures s, FilledBlockArray arr) {
		switch(s) {
			case OCEAN:
				for (Coordinate c : ((GeneratedStructureBase)s.getStructure()).getCachedBlocks(Blocks.mob_spawner)) {
					Block b = c.getBlock(arr.world);
					if (b == Blocks.mob_spawner) {
						TileEntityMobSpawner te = (TileEntityMobSpawner)arr.world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
						te.func_145881_a().activatingRangeFromPlayer = 8;
						te.func_145881_a().maxNearbyEntities = 16;
						te.func_145881_a().maxSpawnDelay = 400;
						ReikaSpawnerHelper.setMobSpawnerMob(te, (String)EntityList.classToStringMapping.get(EntityCreeper.class));
					}
				}
				break;
			case DESERT:
				for (Coordinate c : ((GeneratedStructureBase)s.getStructure()).getCachedBlocks(Blocks.mob_spawner)) {
					Block b = c.getBlock(arr.world);
					if (b == Blocks.mob_spawner) {
						TileEntityMobSpawner te = (TileEntityMobSpawner)arr.world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
						Class mob = c.yCoord <= arr.getMinY()+4 ? EntityBlaze.class : Math.abs(c.xCoord-arr.getMinX()) == Math.abs(c.zCoord-arr.getMinZ()) ? EntitySpider.class : EntitySilverfish.class;
						ReikaSpawnerHelper.setMobSpawnerMob(te, (String)EntityList.classToStringMapping.get(mob));
						te.func_145881_a().activatingRangeFromPlayer = 4;
						te.func_145881_a().spawnDelay = 0;
						if (mob == EntityBlaze.class) {
							te.func_145881_a().maxSpawnDelay = 100;
							te.func_145881_a().minSpawnDelay = 40;
						}
					}
				}
				break;
			case SNOWSTRUCT:
				for (Coordinate c : ((GeneratedStructureBase)s.getStructure()).getCachedBlocks(Blocks.mob_spawner)) {
					Block b = c.getBlock(arr.world);
					if (b == Blocks.mob_spawner) {
						TileEntityMobSpawner te = (TileEntityMobSpawner)arr.world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
						ReikaSpawnerHelper.setMobSpawnerMob(te, (String)EntityList.classToStringMapping.get(EntityWolf.class));
						te.func_145881_a().activatingRangeFromPlayer = 30;
						te.func_145881_a().spawnDelay = 0;
						te.func_145881_a().maxNearbyEntities = 8;
					}
				}
				break;
			default:
				break;
		}
	}

	public void populateChests(ChromaStructures struct, FilledBlockArray arr, Random r) {
		try {
			for (Coordinate c : ((GeneratedStructureBase)struct.getStructure()).getCachedBlocks(GeneratedStructureBase.getChestGen())) {
				Block b = c.getBlock(arr.world);
				if (b == ChromaStructureBase.getChestGen()) {
					TileEntityLootChest te = (TileEntityLootChest)c.getTileEntity(arr.world);
					if (te.isUntouchedWorldgen()) {
						int bonus = ((FragmentStructureBase)struct.getStructure()).getChestYield(c, te, arr, r);
						this.populateChest(c, te, struct, arr, bonus, r);
					}
				}
			}
		}
		catch (Exception e) {
			((GeneratedStructureBase)struct.getStructure()).addError(new Exception("Failed to populate chests", e));
		}
	}

	public void populateChest(Coordinate c, TileEntityLootChest te, ChromaStructures struct, FilledBlockArray arr, int bonus, Random r) {
		String s = ((FragmentStructureBase)struct.getStructure()).getChestLootTable(c, te, arr, r);
		if (s == null) {
			return;
		}
		te.populateChest(s, struct, bonus, r);
	}

	private void addMissingSupport(FilledBlockArray arr, Block b, int meta, int d) {
		for (int x = arr.getMinX(); x <= arr.getMaxX(); x++) {
			for (int z = arr.getMinZ(); z <= arr.getMaxZ(); z++) {
				int bottom = arr.getBottomBlockAtXZ(x, z);
				if (bottom != Integer.MAX_VALUE) {
					bottom--;
					for (int i = 1; i <= d; i++) {
						int y = bottom-i;
						if (ReikaWorldHelper.softBlocks(arr.world, x, y, z))
							arr.world.setBlock(x, y, z, b, meta, 2);
					}
				}
			}
		}
	}

	private void addSnowCover(FilledBlockArray arr, int r) {
		/*
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			if (c.isEmpty(arr.world) && Blocks.snow_layer.canPlaceBlockAt(arr.world, c.xCoord, c.yCoord, c.zCoord)) {
				if (arr.world.getPrecipitationHeight(c.xCoord, c.zCoord) <= c.yCoord)
					c.setBlock(arr.world, Blocks.snow_layer);
			}
		}
		 */
		for (int x = arr.getMinX()-r; x <= arr.getMaxX()+r; x++) {
			for (int z = arr.getMinZ()-r; z <= arr.getMaxZ()+r; z++) {
				int top = arr.world.getTopSolidOrLiquidBlock(x, z)-1;
				if (arr.world.getBlock(x, top, z) != Blocks.snow_layer && arr.world.getBlock(x, top+1, z) == Blocks.air) {
					arr.world.setBlock(x, top+1, z, Blocks.snow_layer);
				}
			}
		}
	}

	private boolean isValidCavernLocation(World world, int x, int y, int z, FilledBlockArray arr) {
		boolean flag = false;
		for (int i = 6; i < 18; i++) {
			int dx = x+i;
			Block b = world.getBlock(dx, y, z);
			Block b2 = world.getBlock(dx, y-1, z);
			if (b.isAir(world, dx, y, z) && b2.isAir(world, dx, y-1, z)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int k = 0; k < arr.getSize(); k++) {
				Coordinate c = arr.getNthBlock(k);
				Coordinate c2 = c.offset(0, 1, 0);
				Block b = c.getBlock(world);
				Block b2 = c2.getBlock(world);
				if (b.isAir(world, c.xCoord, c.yCoord, c.zCoord) || ReikaBlockHelper.isLiquid(b))
					return false;
				if (b.isAir(world, c2.xCoord, c2.yCoord, c2.zCoord) || ReikaBlockHelper.isLiquid(b2))
					return false;
			}
			return true;
		}
		return false;
	}

	private boolean isValidBurrowLocation(World world, int x, int y, int z, FilledBlockArray arr) {

		if (world.getBlock(x, y, z) != Blocks.grass)
			return false;

		//Surface visibility
		for (int i = 1; i <= 8; i++) {
			for (int k = -3; k <= 1; k++) {
				Block b = world.getBlock(x, y+i, z+k);
				if (!ReikaWorldHelper.softBlocks(world, x, y+i, z+k)) {
					return false;
				}
			}
		}

		//No lakes
		int r = 1;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					Block b = world.getBlock(x+i, y+j, z+k);
					if (b instanceof BlockLiquid || b instanceof BlockFluidBase) {
						return false;
					}
				}
			}
		}

		//No air exposure
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.CLIFFSTONE.getBlockInstance())
				return false;
			if (world.getTopSolidOrLiquidBlock(dx, dz) < y-2)
				return false;
			if (arr.hasBlockAt(dx, dy, dz, Blocks.stone) || arr.hasBlockAt(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance())) {
				if (b.isAir(world, dx, dy, dz) || ReikaWorldHelper.checkForAdjMaterial(world, dx, dy, dz, Material.air) != null) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidOceanLocation(World world, int x, int y, int z, FilledBlockArray struct) {
		//needs to be at least 8 blocks deep
		if (world.getBlock(x, y+8, z) != Blocks.water && world.getBlock(x, y+8, z) != Blocks.flowing_water) {
			ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: Not deep enough");
			return false;
		}

		//at least one end open
		boolean flag1 = true;
		boolean flag2 = true;
		FilledBlockArray cap = (FilledBlockArray)this.getEndcap1(world, x, y, z).offset(1, 0, 0);
		for (int k = 0; k < cap.getSize(); k++) {
			Coordinate c = cap.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag1 = false;
			}
		}
		cap = (FilledBlockArray)this.getEndcap2(world, x, y, z).offset(0, 0, 1);
		for (int k = 0; k < cap.getSize(); k++) {
			Coordinate c = cap.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag2 = false;
			}
		}
		if (!flag1 && !flag2) {
			ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: Blocked ends");
			return false;
		}

		//bury lower half, and ensure not near shore or intersecting another
		for (int k = 0; k < struct.getSize(); k++) {
			Coordinate c = struct.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
				ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: Intersects other structure");
				return false;
			}
			if (world.getTopSolidOrLiquidBlock(c.xCoord, c.zCoord) <= y) {
				ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: Extends out of water");
				return false;
			}
			if (!ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(c.xCoord, c.zCoord))) {
				ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: Bounds outside ocean");
				return false;
			}
		}

		//can generate pit to cave
		int consec = 0;
		for (int i = 3; i < y; i++) {
			FilledBlockArray slice = this.getPitSlice(world, x, y-i, z);
			boolean flag = true;
			for (int k = 0; k < slice.getSize(); k++) {
				Coordinate c = slice.getNthBlock(k);
				Block b = c.getBlock(world);
				if (b != Blocks.air) {
					flag = false;
				}
			}
			if (flag && i > 6) {
				consec++;
				if (consec >= 3)
					return true;
			}
			else {
				consec = 0;
			}
		}
		ChromatiCraft.logger.debug("Ocean Temple generation @ "+x+", "+y+", "+z+" failed: No cave.");
		return false;
	}

	private boolean isValidSnowStructLocation(World world, int x, int y, int z, FilledBlockArray arr) {
		int h1 = world.getTopSolidOrLiquidBlock(arr.getMinX(), arr.getMinZ());
		int h2 = world.getTopSolidOrLiquidBlock(arr.getMaxX(), arr.getMinZ());
		int h3 = world.getTopSolidOrLiquidBlock(arr.getMinX(), arr.getMaxZ());
		int h4 = world.getTopSolidOrLiquidBlock(arr.getMaxX(), arr.getMaxZ());
		int max = ReikaMathLibrary.multiMax(h1, h2, h3, h4);
		int min = ReikaMathLibrary.multiMin(h1, h2, h3, h4);
		if (Math.abs(max-min) > 2)
			return false;
		BiomeGenBase b1 = world.getBiomeGenForCoords(arr.getMinX(), arr.getMinZ());
		BiomeGenBase b2 = world.getBiomeGenForCoords(arr.getMaxX(), arr.getMinZ());
		BiomeGenBase b3 = world.getBiomeGenForCoords(arr.getMinX(), arr.getMaxZ());
		BiomeGenBase b4 = world.getBiomeGenForCoords(arr.getMaxX(), arr.getMaxZ());
		if (b1 != b2 || b1 != b3 || b1 != b4)
			return false;
		for (int d = 1; d <= 5; d++) { //starts at the top non-air
			Block id1 = world.getBlock(arr.getMinX(), h1-d, arr.getMinZ());
			Block id2 = world.getBlock(arr.getMaxX(), h2-d, arr.getMinZ());
			Block id3 = world.getBlock(arr.getMinX(), h3-d, arr.getMaxZ());
			Block id4 = world.getBlock(arr.getMaxX(), h4-d, arr.getMaxZ());
			if (id1 == Blocks.air || id2 == Blocks.air || id3 == Blocks.air || id4 == Blocks.air)
				return false;
			if (ReikaBlockHelper.isLiquid(id1) || ReikaBlockHelper.isLiquid(id2) || ReikaBlockHelper.isLiquid(id3) || ReikaBlockHelper.isLiquid(id4))
				return false;
		}
		return true;
	}

	private boolean isValidBiomeStructLocation(World world, int x, int y, int z, FilledBlockArray arr) {
		int h1 = world.getTopSolidOrLiquidBlock(arr.getMinX(), arr.getMinZ());
		int h2 = world.getTopSolidOrLiquidBlock(arr.getMaxX(), arr.getMinZ());
		int h3 = world.getTopSolidOrLiquidBlock(arr.getMinX(), arr.getMaxZ());
		int h4 = world.getTopSolidOrLiquidBlock(arr.getMaxX(), arr.getMaxZ());
		int max = ReikaMathLibrary.multiMax(h1, h2, h3, h4);
		int min = ReikaMathLibrary.multiMin(h1, h2, h3, h4);
		if (Math.abs(max-min) > 2)
			return false;
		BiomeGenBase b1 = world.getBiomeGenForCoords(arr.getMinX(), arr.getMinZ());
		BiomeGenBase b2 = world.getBiomeGenForCoords(arr.getMaxX(), arr.getMinZ());
		BiomeGenBase b3 = world.getBiomeGenForCoords(arr.getMinX(), arr.getMaxZ());
		BiomeGenBase b4 = world.getBiomeGenForCoords(arr.getMaxX(), arr.getMaxZ());
		if (b1 != b2 || b1 != b3 || b1 != b4)
			return false;
		for (int d = 1; d <= 5; d++) { //starts at the top non-air
			Block id1 = world.getBlock(arr.getMinX(), h1-d, arr.getMinZ());
			Block id2 = world.getBlock(arr.getMaxX(), h2-d, arr.getMinZ());
			Block id3 = world.getBlock(arr.getMinX(), h3-d, arr.getMaxZ());
			Block id4 = world.getBlock(arr.getMaxX(), h4-d, arr.getMaxZ());
			if (id1 == Blocks.air || id2 == Blocks.air || id3 == Blocks.air || id4 == Blocks.air)
				return false;
			if (ReikaBlockHelper.isLiquid(id1) || ReikaBlockHelper.isLiquid(id2) || ReikaBlockHelper.isLiquid(id3) || ReikaBlockHelper.isLiquid(id4))
				return false;
		}
		return true;
	}

	private boolean isVoidWorld(World world, int x, int z) {
		return world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z);
	}

	/** Block coords */
	public StructureGenStatus isGennableChunk(World world, int x, int z, ChromaStructures s) {
		this.updateNoisemaps(world);
		ArrayList<WorldLocation> c = new ArrayList(this.getNearbyZones(s, (WorldServer)world, x, z, this.getExclusionZone(s)));
		if (c.isEmpty())
			return StructureGenStatus.INERT;
		StructureGenStatus failret = StructureGenStatus.INERT;
		Iterator<WorldLocation> it = c.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (!this.isValidBiomeNear(world, loc.xCoord, loc.zCoord, s)) {
				it.remove();
				if ((x >> 4 == loc.xCoord >> 4) && (z >> 4 == loc.zCoord >> 4))
					failret = StructureGenStatus.BIOMEFAIL;
			}
		}
		if (c.isEmpty())
			return failret;
		WorldLocation usable = null;
		if (c.size() == 1) {
			usable = c.get(0);
		}
		else {
			usable = this.getBestFromCluster(c);
			for (WorldLocation loc : c) {
				if (loc != usable) {
					this.markChunkStatus(world, loc.xCoord >> 4, loc.zCoord >> 4, s, StructureGenStatus.CLUSTERED);
				}
			}
		}
		return (x >> 4 == usable.xCoord >> 4) && (z >> 4 == usable.zCoord >> 4) ? StructureGenStatus.PLANNED : StructureGenStatus.INERT;
	}

	private WorldLocation getBestFromCluster(ArrayList<WorldLocation> c) {
		Collections.sort(c);/*
		ReikaJavaLibrary.pConsole("Structure cluster of size "+c.size()+": "+c);
		if (c.size() == 2) {
			double d = c.get(0).getDistanceTo(c.get(1));
			if (d <= 80) {
				ReikaJavaLibrary.pConsole("Distance is only "+d+"!");
			}
		}*/
		return c.get(0);
	}

	private boolean canGenerateIn(World world) {
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (!MystPages.Pages.STRUCTURES.existsInWorld(world)) {
				return false;
			}
		}
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
			return ChromaOptions.FLATGEN.getState() && ReikaWorldHelper.getSuperflatHeight(world) > 15;
		}
		if (world.provider.dimensionId == 0)
			return true;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			return false;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return false;
		if (world.provider.getClass().getName().equals("WorldProviderMiner"))
			return false;
		if (PlanetDimensionHandler.isOtherWorld(world))
			return false;
		return !ChromatiCraft.config.isDimensionBlacklistedForStructures(world.provider.dimensionId);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Prefab Structures";
	}

	public static enum StructureGenStatus {
		INERT(0x000000),
		INERT_GEN(0x303030),
		BIOMEFAIL(0xff9020),
		PLANNED(0xffffff),
		GENERATING(0x22aaff),
		SUCCESS(0x00ff00),
		FAILURE(0xff0000),
		CLUSTERED(0xff00ff),
		REMOVED(0xffff00);

		public static final StructureGenStatus[] list = values();

		public final int renderColor;

		private StructureGenStatus(int c) {
			renderColor = c;
		}

		public boolean hasStructure() {
			return this == SUCCESS || this == PLANNED || this == GENERATING;
		}

		public boolean isGenerated() {
			return this == SUCCESS || this == FAILURE || this == REMOVED || this == INERT_GEN;
		}

		public boolean isFinalized() {
			return this.isGenerated() || this == CLUSTERED || this == BIOMEFAIL;
		}

		public boolean canOverwrite(StructureGenStatus g) {
			if (this == INERT_GEN && g == PLANNED)
				return false;
			if (this.isFinalized() && !g.isFinalized())
				return true;
			return false;
		}
	}

	private static class StructureGenData {

		private StructureGenStatus status;
		private final WorldChunk rootLocation;
		private Coordinate generatedLocation;

		private StructureGenData(WorldChunk wc, StructureGenStatus def) {
			status = def;
			rootLocation = wc;
		}

		public static StructureGenData readFromNBT(NBTTagCompound tag) {
			StructureGenStatus stat = StructureGenStatus.valueOf(tag.getString("status"));
			StructureGenData ret = new StructureGenData(WorldChunk.fromSerialString(tag.getString("chunk")), stat);
			if (tag.hasKey("location"))
				ret.generatedLocation = Coordinate.readFromNBT("location", tag);
			return ret;
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound ret = new NBTTagCompound();
			ret.setString("status", status.name());
			ret.setString("chunk", rootLocation.toSerialString());
			if (generatedLocation != null) {
				generatedLocation.writeToNBT("location", ret);
			}
			return ret;
		}

	}

	public static class StructureSeekData {

		public final WorldLocation location;
		public final boolean isKnownSuccess;

		private StructureSeekData(WorldLocation loc, boolean b) {
			location = loc;
			isKnownSuccess = b;
		}

	}

}
