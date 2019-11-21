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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.BlockFluidBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Event.PylonGenerationEvent;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Aura.BaseAura;
import Reika.ChromatiCraft.Magic.Network.PylonLinkNetwork;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityChromaCrystal;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLogoutEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PylonGenerator implements RetroactiveGenerator {

	public static final PylonGenerator instance = new PylonGenerator();

	private final ForgeDirection[] dirs = ForgeDirection.values();

	//private final int CHANCE = 40;

	private final int avgDist = 10; //16
	private final int maxDeviation = 4;
	private final Random rand = new Random();

	private final int GRIDSIZE = 256;

	private final HashMap<Integer, boolean[][]> data = new HashMap();

	public static final String NBT_TAG = "pylonloc";
	private final EnumMap<CrystalElement, Collection<PylonEntry>> colorCache = new EnumMap(CrystalElement.class);

	private PylonGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void savePylonLocations(NBTTagCompound NBT) {
		NBTTagList li = NBT.getTagList(NBT_TAG, NBTTypes.COMPOUND.ID);
		for (CrystalElement e : colorCache.keySet()) {
			Collection<PylonEntry> c = colorCache.get(e);
			for (PylonEntry loc : c) {
				NBTTagCompound tag = new NBTTagCompound();
				loc.location.writeToNBT("pos", tag);
				tag.setInteger("color", e.ordinal());
				tag.setBoolean("turbo", loc.isTurboCharged);
				tag.setBoolean("struct", loc.hasStructure);
				if (loc.playerLink != null) {
					tag.setString("link", loc.playerLink.toString());
				}
				NBTTagList li2 = new NBTTagList();
				for (Coordinate c2 : loc.powerCrystals) {
					li2.appendTag(c2.writeToTag());
				}
				tag.setTag("crystals", li2);
				li.appendTag(tag);
			}
		}
		NBT.setTag(NBT_TAG, li);
		//ChromatiCraft.logger.log("["+FMLCommonHandler.instance().getEffectiveSide()+"] Saved pylons to "+li+": "+colorCache);
	}

	public void loadPylonLocations(NBTTagCompound NBT) {
		NBTTagList li = NBT.getTagList(NBT_TAG, NBTTypes.COMPOUND.ID);
		Iterator<NBTTagCompound> it = li.tagList.iterator();
		while (it.hasNext()) {
			NBTTagCompound tag = it.next();
			WorldLocation loc = WorldLocation.readFromNBT("pos", tag);
			CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
			boolean turbo = tag.getBoolean("turbo");
			String uids = tag.hasKey("link") ? tag.getString("link") : null;
			UUID uid = uids != null ? UUID.fromString(uids) : null;
			boolean struct = tag.getBoolean("struct");
			NBTTagList crystals = tag.getTagList("crystals", NBTTypes.COMPOUND.ID);
			ArrayList<Coordinate> li2 = new ArrayList();
			for (Object o : crystals.tagList) {
				NBTTagCompound tag2 = (NBTTagCompound)o;
				Coordinate c = Coordinate.readTag(tag2);
				li2.add(c);
			}
			if (this.validateCachedLocation(loc, e)) {
				this.addLocation(loc, e, li2, turbo, uid, struct);
			}
			else {
				it.remove();
			}
		}
		NBT.setTag(NBT_TAG, li);
		//ChromatiCraft.logger.log("["+FMLCommonHandler.instance().getEffectiveSide()+"] Loaded pylons from "+li+": "+colorCache);
	}

	private boolean validateCachedLocation(WorldLocation loc, CrystalElement e) {
		TileEntity te = loc.getTileEntity();
		return te instanceof TileEntityCrystalPylon && ((TileEntityCrystalPylon)te).getColor() == e;
	}

	public void sendDimensionCacheToPlayer(EntityPlayerMP ep, int dim) {
		//ReikaJavaLibrary.pConsole("["+FMLCommonHandler.instance().getEffectiveSide()+"] Sending cache in DIM"+dim+" to "+ep+": "+colorCache);
		for (CrystalElement e : colorCache.keySet()) {
			Collection<PylonEntry> c = colorCache.get(e);
			for (PylonEntry loc : c) {
				if (loc.location.dimensionID == dim) {
					ArrayList<Integer> li = ReikaJavaLibrary.makeIntListFromArray(loc.location.xCoord, loc.location.yCoord, loc.location.zCoord, e.ordinal());
					li.add(loc.isTurboCharged ? 1 : 0);
					li.add(loc.hasStructure ? 1 : 0);
					if (loc.playerLink != null) {
						long l1 = loc.playerLink.getLeastSignificantBits();
						long l2 = loc.playerLink.getMostSignificantBits();
						int[] a1 = ReikaJavaLibrary.splitLong(l1);
						int[] a2 = ReikaJavaLibrary.splitLong(l2);
						li.add(a1[0]);
						li.add(a1[1]);
						li.add(a2[0]);
						li.add(a2[1]);
					}
					else {
						li.add(-1);
						li.add(-1);
						li.add(-1);
						li.add(-1);
					}
					for (Coordinate c2 : loc.powerCrystals) {
						li.add(c2.xCoord);
						li.add(c2.yCoord);
						li.add(c2.zCoord);
					}
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PYLONCACHE.ordinal(), ep, li);
				}
			}
		}
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		if (evt.world.isRemote) {
			//this.clear(evt.world);
		}
		else {
			this.clear(evt.world);
			//ReikaPacketHelper.sendDataPacketToEntireServer(ChromatiCraft.packetChannel, ChromaPackets.PYLONCLEAR.ordinal(), evt.world.provider.dimensionId);
		}
	}

	@SubscribeEvent
	public void clearOnLogout(ClientDisconnectionFromServerEvent evt) {
		ChromatiCraft.logger.debug("["+FMLCommonHandler.instance().getEffectiveSide()+"] Logout clear");
		colorCache.clear();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clearOnLogout(ClientLogoutEvent evt) {
		ChromatiCraft.logger.debug("["+FMLCommonHandler.instance().getEffectiveSide()+"] Logout clear");
		colorCache.clear();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clearOnLogout(SinglePlayerLogoutEvent evt) {
		ChromatiCraft.logger.debug("["+FMLCommonHandler.instance().getEffectiveSide()+"] Logout clear");
		colorCache.clear();
	}

	private void clear(World world) {
		this.clearDimension(world.provider.dimensionId);
	}

	//@SideOnly(Side.CLIENT)
	public void clearDimension(int dim) {
		data.remove(dim);

		/*
		for (CrystalElement e : colorCache.keySet()) {
			Iterator<WorldLocation> it = colorCache.get(e).iterator();
			while (it.hasNext()) {
				WorldLocation loc = it.next();
				if (loc.dimensionID == dim) {
					it.remove();
					//ReikaJavaLibrary.pConsole("["+FMLCommonHandler.instance().getEffectiveSide()+"] #"+System.identityHashCode(colorCache)+"/"+System.identityHashCode(this)+" Removed "+loc+"="+e+" from DIM"+dim);
				}
			}
		}
		 */
	}

	private void fillArray(World world) {
		int id = world.provider.dimensionId;
		rand.setSeed(world.getSeed() ^ id);
		boolean[][] grid = this.getGrid(id);
		for (int x = maxDeviation; x < GRIDSIZE-maxDeviation; x += avgDist) {
			for (int z = maxDeviation; z < GRIDSIZE-maxDeviation; z += avgDist) {
				int x2 = ReikaRandomHelper.getRandomPlusMinus(x, maxDeviation);
				int z2 = ReikaRandomHelper.getRandomPlusMinus(z, maxDeviation);
				grid[x2][z2] = true;
				//ChromatiCraft.logger.debug(x + ", " + z + " | " + x2 + ", " + z2);
			}
		}
		//if (ChromatiCraft.logger.shouldDebug())
		//ChromatiCraft.logger.log("Dimension Pylon Generation Array: \n"+getDimensionString(id));
	}

	private String getDimensionString(int id) {
		boolean[][] arr = this.getGrid(id);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < GRIDSIZE; i++) {
			for (int j = 0; j < GRIDSIZE; j++) {
				String c = arr[i][j] ? "[#]" : "[ ]";
				sb.append(c);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private boolean[][] getGrid(int dim) {
		boolean[][] arr = data.get(dim);
		if (arr == null) {
			arr = new boolean[GRIDSIZE][GRIDSIZE];
			data.put(dim, arr);
		}
		return arr;
	}

	private boolean filledDim(World world) {
		return data.containsKey(world.provider.dimensionId);
	}

	public PylonEntry getNearestPylonSpawn(World world, double x, double y, double z, CrystalElement e) {
		Collection<PylonEntry> c = colorCache.get(e);
		if (c == null)
			return null;
		double dist = Double.POSITIVE_INFINITY;
		PylonEntry close = null;
		for (PylonEntry loc : c) {
			if (loc.location.dimensionID == world.provider.dimensionId) {
				double d = loc.location.getDistanceTo(x, y, z);
				if (d < dist) {
					dist = d;
					close = loc;
				}
			}
		}
		return close;
	}

	private boolean isGennableChunk(World world, int chunkX, int chunkZ) {
		boolean[][] arr = this.getGrid(world.provider.dimensionId);
		while (chunkX < 0)
			chunkX += GRIDSIZE;
		while (chunkZ < 0)
			chunkZ += GRIDSIZE;
		return arr[chunkX%GRIDSIZE][chunkZ%GRIDSIZE];
	}

	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider gen, IChunkProvider p) {
		if (this.canGenerateIn(world)) {

			if (!this.filledDim(world)) {
				this.fillArray(world);
			}

			if (this.isGennableChunk(world, chunkX, chunkZ)) {
				this.tryForceGenerate(world, chunkX*16, chunkZ*16, r);
			}
		}
	}

	private void tryForceGenerate(World world, int cx, int cz, Random r) {
		int maxtries = 24;
		for (int i = 0; i < maxtries; i++) {
			int x = cx+r.nextInt(16);
			int z = cz+r.nextInt(16);

			//world.setBlock(x, 128, z, Blocks.flowing_lava);
			int y = world.getTopSolidOrLiquidBlock(x, z)-1;
			if (this.canGenerateAt(world, x, y, z)) {
				ChromatiCraft.logger.debug("Generated pylon at "+x+", "+z);
				this.generatePylon(r, world, x, y, z);
				break;
			}
		}
	}

	public boolean canGenerateIn(World world) {
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			return MystPages.Pages.PYLONS.existsInWorld(world);
		}
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !ChromaOptions.FLATGEN.getState())
			return false;
		if (world.provider.dimensionId == 0)
			return true;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.isHellWorld || world.provider.hasNoSky)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return false;
		return ChromaOptions.NONWORLDGEN.getState();
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			return false;

		if (world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z)) //sky/void world
			return false;

		if (ReikaBlockHelper.isWood(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y, z))
			return false;

		/*
		for (int i = y+1; i < world.getHeight(); i++) {
			Block b = world.getBlock(x, i, z);
			if (b != Blocks.air && b != Blocks.leaves && b != Blocks.leaves2 && !ReikaWorldHelper.softBlocks(world, x, i, z))
				;//return false;
		}
		 */

		StructuredBlockArray blocks = new StructuredBlockArray(world);

		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					blocks.addBlockCoordinate(dx, dy, dz);
					if (dir.offsetX == 0) {
						blocks.addBlockCoordinate(dx+dir.offsetZ, dy, dz);
						blocks.addBlockCoordinate(dx-dir.offsetZ, dy, dz);
					}
					else if (dir.offsetZ == 0) {
						blocks.addBlockCoordinate(dx, dy, dz+dir.offsetX);
						blocks.addBlockCoordinate(dx, dy, dz-dir.offsetX);
					}
				}
			}
		}

		//precalc:
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			Block b = world.getBlock(dx, dy, dz);
			//if (b == Blocks.stone || b == Blocks.dirt || b == Blocks.grass) {
			//	blocks.offset(0, 1, 0);
			//	break precalc;
			//}
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase)
				return false;
			if (!ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
				int meta = world.getBlockMetadata(dx, dy, dz);

				if (dy == blocks.getMinY()) {
					if (!this.isFloorReplaceable(b, meta))
						return false;
				}
				else {
					if (!this.isAirReplaceable(b, meta))
						return false;
				}
			}
		}

		return true;
	}

	private boolean isFloorReplaceable(Block b, int meta) {
		if (b == Blocks.stone)
			return true;
		if (b == Blocks.dirt)
			return true;
		if (b == Blocks.grass)
			return true;
		if (b == Blocks.gravel)
			return true;
		if (b == Blocks.sand)
			return true;
		if (b == Blocks.hardened_clay)
			return true;
		if (b.getMaterial() == Material.ground)
			return true;
		if (b == Blocks.log || b == Blocks.log2 || ModWoodList.isModWood(b, meta))
			return true;
		if (b == Blocks.leaves || b == Blocks.leaves2 || ModWoodList.isModLeaf(b, meta))
			return true;
		if (b == Blocks.red_flower || b == Blocks.yellow_flower || b instanceof BlockFlower)
			return true;
		if (b == Blocks.red_mushroom || b == Blocks.brown_mushroom)
			return true;
		if (b instanceof BlockBush)
			return true;
		if (b == Blocks.reeds)
			return true;
		if (b == Blocks.cactus)
			return true;
		return false;
	}

	private boolean isAirReplaceable(Block b, int meta) {
		if (b == Blocks.dirt)
			return true;
		if (b == Blocks.grass)
			return true;
		if (b == Blocks.gravel)
			return true;
		if (b == Blocks.log || b == Blocks.log2 || ModWoodList.isModWood(b, meta))
			return true;
		if (b == Blocks.leaves || b == Blocks.leaves2 || ModWoodList.isModLeaf(b, meta))
			return true;
		if (b == Blocks.red_flower || b == Blocks.yellow_flower || b instanceof BlockFlower)
			return true;
		if (b == Blocks.red_mushroom || b == Blocks.brown_mushroom)
			return true;
		if (b instanceof BlockBush)
			return true;
		if (b == Blocks.reeds)
			return true;
		if (b == Blocks.cactus)
			return true;
		return false;
	}

	private void generatePylon(Random rand, World world, int x, int y, int z) {
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		ElementTagCompound tag = BaseAura.getBaseAura(world, x, y, z);
		if (rand.nextInt(25) > 0)
			tag.clipToPrimaries();
		CrystalElement e = CrystalElement.randomElement();//tag.asWeightedRandom().getRandomEntry();
		FilledBlockArray array = ChromaStructures.PYLON.getArray(world, x, y+9, z, e);

		boolean broken = ChromaOptions.BROKENPYLON.getState() && rand.nextInt(2) == 0;
		if (broken)
			this.breakPylon(array);

		Coordinate offset = this.getTreeDodgeAttempt(array, world, x, y, z, 8, 16);
		array.offset(offset.xCoord, offset.yCoord, offset.zCoord);

		y -= array.sink(world, Material.wood, Material.leaves);

		if (array.getMaxY() >= 255)
			return;

		for (int n = -4; n < 0; n++) {
			int dy = y+n;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					if (ReikaWorldHelper.softBlocks(world, dx, dy, dz))
						array.setBlock(dx, dy, dz, b, 0);
					if (dir.offsetX == 0) {
						if (ReikaWorldHelper.softBlocks(world, dx+dir.offsetZ, dy, dz))
							array.setBlock(dx+dir.offsetZ, dy, dz, b, 0);
						if (ReikaWorldHelper.softBlocks(world, dx-dir.offsetZ, dy, dz))
							array.setBlock(dx-dir.offsetZ, dy, dz, b, 0);
					}
					else if (dir.offsetZ == 0) {
						if (ReikaWorldHelper.softBlocks(world, dx, dy, dz+dir.offsetX))
							array.setBlock(dx, dy, dz+dir.offsetX, b, 0);
						if (ReikaWorldHelper.softBlocks(world, dx, dy, dz-dir.offsetX))
							array.setBlock(dx, dy, dz-dir.offsetX, b, 0);
					}
				}
			}
		}

		array.place();

		//TileEntity
		world.setBlock(x, y+9, z, ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata(), 3);
		TileEntityCrystalPylon te = (TileEntityCrystalPylon)world.getTileEntity(x, y+9, z);
		te.generateColor(e);
		if (broken)
			te.invalidateMultiblock();
		else
			te.validateMultiblock(array);
		world.func_147451_t(x, y+9, z);
		this.cachePylon(te);
		MinecraftForge.EVENT_BUS.post(new PylonGenerationEvent(world, x, y+9, z, rand, broken, e.getAPIProxy()));
	}

	private Coordinate getTreeDodgeAttempt(FilledBlockArray array, World world, int x0, int y0, int z0, int tries, int range) {
		if (!this.isTreePylon(array, world, x0, y0, z0))
			return new Coordinate(0, 0, 0);

		for (int i = 0; i < tries; i++) {
			int x = ReikaRandomHelper.getRandomPlusMinus(x0, range);
			int z = ReikaRandomHelper.getRandomPlusMinus(z0, range);
			int y = world.getTopSolidOrLiquidBlock(x, z)-1;
			if (!this.isTreePylon(array, world, x, y, z)) {
				Coordinate c = new Coordinate(x-x0, y-y0, z-z0);
				ChromatiCraft.logger.debug("Moved pylon @ "+x0+","+z0+" to avoid in-tree generation: delta="+c);
				return c;
			}
			else if (ChromatiCraft.logger.shouldDebug()) {
				Coordinate c = new Coordinate(x-x0, y-y0, z-z0);
				ChromatiCraft.logger.debug("Pylon @ "+x0+","+z0+" tree avoidance attempt "+i+": delta "+c+" failed. Floor block = "+world.getBlock(array.getMidX(), array.getMinY(), array.getMidZ()));
			}
		}
		ChromatiCraft.logger.debug("Tried moving pylon @ "+x0+","+z0+" to avoid in-tree generation, but failed to find better spawn after "+tries+" attempts.");
		return new Coordinate(0, 0, 0);
	}

	private boolean isTreePylon(FilledBlockArray array, World world, int x, int y, int z) {
		int midx = array.getMidX();
		int miny = array.getMinY();
		int midz = array.getMidZ();
		if (ChromatiCraft.logger.shouldDebug()) {
			ChromatiCraft.logger.debug("Testing prospective pylon location "+x+", "+y+", "+z);
		}
		if (ReikaBlockHelper.isWood(world, midx, miny, midz) || ReikaBlockHelper.isLeaf(world, midx, miny, midz))
			return true;
		if (ReikaBlockHelper.isWood(world, midx, miny-1, midz) || ReikaBlockHelper.isLeaf(world, midx, miny-1, midz))
			return true;
		if (ChromatiCraft.logger.shouldDebug()) {
			ChromatiCraft.logger.debug("Pylon location passed tree test.");
		}
		return false;
	}

	public void removeCachedPylon(TileEntityCrystalPylon te) {
		PylonEntry loc = new PylonEntry(te);
		Collection<PylonEntry> c = colorCache.get(te.getColor());
		if (c != null) {
			c.remove(loc);
		}
	}

	public void cachePylon(TileEntityCrystalPylon te) {
		this.addToCache(new PylonEntry(te));
	}

	private void addToCache(PylonEntry e) {
		this.addLocation(e);
		List<Integer> li = ReikaJavaLibrary.makeIntListFromArray(e.location.xCoord, e.location.yCoord, e.location.zCoord, e.color.ordinal());
		li.add(e.isTurboCharged ? 1 : 0);
		li.add(e.hasStructure ? 1 : 0);
		if (e.playerLink != null) {
			long l1 = e.playerLink.getLeastSignificantBits();
			long l2 = e.playerLink.getMostSignificantBits();
			int[] a1 = ReikaJavaLibrary.splitLong(l1);
			int[] a2 = ReikaJavaLibrary.splitLong(l2);
			li.add(a1[0]);
			li.add(a1[1]);
			li.add(a2[0]);
			li.add(a2[1]);
		}
		else {
			li.add(-1);
			li.add(-1);
			li.add(-1);
			li.add(-1);
		}
		for (Coordinate c : e.powerCrystals) {
			li.add(c.xCoord);
			li.add(c.yCoord);
			li.add(c.zCoord);
		}
		ReikaPacketHelper.sendDataPacketToEntireServer(ChromatiCraft.packetChannel, ChromaPackets.PYLONCACHE.ordinal(), li);
	}

	@SideOnly(Side.CLIENT)
	public void cachePylonLocation(World world, int x, int y, int z, CrystalElement e, ArrayList<Coordinate> li, boolean turbo, UUID link, boolean struct) {
		//ReikaJavaLibrary.pConsole("Receive for cache in DIM"+world.provider.dimensionId+": "+x+","+y+","+z+"="+e);
		this.addLocation(new WorldLocation(world, x, y, z), e, li, turbo, link, struct);
	}

	private void addLocation(WorldLocation loc, CrystalElement e, ArrayList<Coordinate> li, boolean turbo, UUID link, boolean struct) {
		this.addLocation(new PylonEntry(e, loc, li, turbo, link, struct));
	}

	private void addLocation(PylonEntry pe) {
		//ReikaJavaLibrary.pConsole("["+FMLCommonHandler.instance().getEffectiveSide()+"] Add to cache: "+loc+"="+e);
		Collection<PylonEntry> c = colorCache.get(pe.color);
		if (c == null) {
			c = new HashSet();
			colorCache.put(pe.color, c);
		}
		c.remove(pe);
		c.add(pe);
	}

	private void breakPylon(FilledBlockArray array) {
		int n = 3+rand.nextInt(4);
		int i = 0;
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		while (i < n) {
			Coordinate c = array.getRandomBlock();
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			if (array.hasBlockAt(x, y, z, b, 0) || array.hasBlockAt(x, y, z, b, 1) || array.hasBlockAt(x, y, z, b, 2) || array.hasBlockAt(x, y, z, b, 7) || array.hasBlockAt(x, y, z, b, 8)) {
				i++;
				array.setBlock(x, y, z, Blocks.air);
			}
		}
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Pylons";
	}

	public void printPylonCache(ICommandSender ics) {
		ReikaJavaLibrary.pConsole("["+FMLCommonHandler.instance().getEffectiveSide()+"] Cache Debug: "+System.identityHashCode(colorCache)+"/"+System.identityHashCode(this)+"#"+colorCache);
	}

	public static class PylonEntry {

		public final CrystalElement color;
		public final WorldLocation location;
		private final ArrayList<Coordinate> powerCrystals = new ArrayList();
		public final boolean isTurboCharged;
		public final UUID playerLink;
		public final boolean hasStructure;

		private PylonEntry(TileEntityCrystalPylon te) {
			this(te.getColor(), new WorldLocation(te), te.getBoosterCrystals(te.worldObj, te.xCoord, te.yCoord, te.zCoord, false), te.isEnhanced(), te.getLinkTileUUID(), te.hasStructure());
		}

		public PylonEntry(CrystalElement e, WorldLocation loc, ArrayList li, boolean turbo, UUID link, boolean struct) {
			color = e;
			location = loc;
			for (Object o : li) {
				if (o instanceof Coordinate)
					powerCrystals.add((Coordinate)o);
				else if (o instanceof TileEntityChromaCrystal)
					powerCrystals.add(new Coordinate((TileEntity)o));
			}
			isTurboCharged = turbo;
			playerLink = link;
			hasStructure = struct;
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof PylonEntry && ((PylonEntry)o).location.equals(location);
		}

		@Override
		public String toString() {
			return color.name()+" @ "+location.toString()+" [CRY = "+powerCrystals.size()+"x/TRB = "+isTurboCharged+"/LNK = "+playerLink+"/STR = "+hasStructure+"]";
		}

		public Collection<Coordinate> getCrystals() {
			return Collections.unmodifiableCollection(powerCrystals);
		}

		/** Returns the number of pylons *other* than this one that are linked. */
		public int getLinkedPylons() {
			if (playerLink == null)
				return 0;
			return PylonLinkNetwork.instance.getLinkedPylons(location.getWorld(), playerLink, color).size()-1;
		}

	}

}
