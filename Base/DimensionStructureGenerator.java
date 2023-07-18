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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.StructureInterfaceTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.ChromatiCraft.World.Dimension.Structure.AltarGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.GOLGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.GravityPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanelGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclideanGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PinballGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.TessellationGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ThreeDMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.TracePuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader.HashType;
import Reika.DragonAPI.Instantiable.Data.Collections.ChancedOutputList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerator.TileCallback;

import cpw.mods.fml.common.Loader;

public abstract class DimensionStructureGenerator implements TileCallback {

	protected final ChunkSplicedGenerationCache world = new ChunkSplicedGenerationCache();
	private DimensionStructureType structureType;
	private int structureTypeIndex;

	private ChunkCoordIntPair genCore;
	private ChunkCoordIntPair center;

	private final HashSet<Coordinate> breakable = new HashSet();

	protected int posX;
	protected int posY;
	protected int posZ;

	protected int entryX;
	protected int entryZ;

	protected Coordinate coreLocation = null;

	private CrystalElement genColor;

	private final MultiMap<ChunkCoordIntPair, DynamicPieceLocation> dynamicParts = new MultiMap().setNullEmpty();

	public final UUID id = UUID.randomUUID();

	private boolean forcedOpen;

	protected DimensionStructureGenerator() {

	}

	public final int getPosX() {
		return posX;
	}

	public final int getPosY() {
		return posY;
	}

	public final int getPosZ() {
		return posZ;
	}

	public final Coordinate getPos() {
		return new Coordinate(posX, posY, posZ);
	}

	public final int getEntryPosX() {
		return entryX;
	}

	public final int getEntryPosZ() {
		return entryZ;
	}

	public void offsetEntry(int dx, int dz) {
		entryX += dx;
		entryZ += dz;
	}

	public final DimensionStructureType getType() {
		return structureType;
	}

	public int getGenerationIndex() {
		return structureTypeIndex;
	}

	protected final void addDynamicStructure(DynamicStructurePiece dsp, int x, int z) {
		dynamicParts.addValue(new ChunkCoordIntPair(x >> 4, z >> 4), new DynamicPieceLocation(dsp, x, z));
	}

	/** chunk X and Z are already *16 */
	protected abstract void calculate(int chunkX, int chunkZ, Random rand);

	public final void startCalculate(CrystalElement e, int chunkX, int chunkZ, Random rand) {
		genColor = e;
		genCore = new ChunkCoordIntPair(chunkX >> 4, chunkZ >> 4);
		posX = chunkX;
		posZ = chunkZ;
		entryX = chunkX;
		entryZ = chunkZ;
		long time = System.currentTimeMillis();
		ChromatiCraft.logger.log("Calculating a "+e+" "+this.getType());
		this.calculate(chunkX, chunkZ, rand);
		long dur = System.currentTimeMillis()-time;
		ChromatiCraft.logger.log("Done in "+dur+" ms");
	}

	public final void generateChunk(World w, ChunkCoordIntPair cp) {
		world.generate(w, cp);

		Collection<DynamicPieceLocation> c = dynamicParts.get(cp);
		if (c != null) {
			for (DynamicPieceLocation dsp : c) {
				int x = (cp.chunkXPos << 4)+dsp.relX;
				int z = (cp.chunkZPos << 4)+dsp.relZ;
				dsp.generator.generate(w, x, z);
			}
		}
	}

	public boolean isChunkGenerated(int x, int z) {
		return world.isChunkGenerated(x, z);
	}

	public final void generateAll(World w) {
		world.generateAll(w);

		for (ChunkCoordIntPair cp : dynamicParts.keySet()) {
			Collection<DynamicPieceLocation> c = dynamicParts.get(cp);
			if (c != null) {
				for (DynamicPieceLocation dsp : c) {
					int x = (cp.chunkXPos << 4)+dsp.relX;
					int z = (cp.chunkZPos << 4)+dsp.relZ;
					dsp.generator.generate(w, x, z);
				}
			}
		}
	}

	public Set<Coordinate> getBreakableSpots() {
		return Collections.unmodifiableSet(breakable);
	}

	public void addBreakable(int x, int y, int z) {
		breakable.add(new Coordinate(x, y, z));
	}

	public abstract StructureData createDataStorage();

	public final void clear() {
		world.clear();
		dynamicParts.clear();
		breakable.clear();

		center = null;
		coreLocation = null;

		this.clearCaches();
	}

	public final ChunkCoordIntPair getLocation() {
		return genCore;
	}

	public final ChunkCoordIntPair getCentralLocation() {
		if (center == null) {
			center = new ChunkCoordIntPair(genCore.chunkXPos+(this.getCenterXOffset() >> 4), genCore.chunkZPos+(this.getCenterZOffset() >> 4));
		}
		return center;
	}

	public final ChunkCoordIntPair getEntryLocation() {
		return new ChunkCoordIntPair(entryX >> 4, entryZ >> 4);
	}

	public final String getCentralBlockCoords() {
		ChunkCoordIntPair ctr = this.getCentralLocation();
		return String.format("%d, %d", ctr.chunkXPos*16+this.getCenterXOffset(), ctr.chunkZPos*16+this.getCenterZOffset());
	}

	protected abstract int getCenterXOffset();
	protected abstract int getCenterZOffset();

	protected abstract void clearCaches();

	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof TileEntityDimensionCore) {
			StructurePair sp = new StructurePair(this, this.getCoreColor());
			sp.generatedDimension = world.provider.dimensionId;
			((TileEntityDimensionCore)te).setStructure(sp);
		}
		else {
			ChromatiCraft.logger.logError(te+" instead of a Dimension Core at "+x+", "+y+", "+z+"!!");
		}
	}

	public CrystalElement getCoreColor() {
		return genColor;
	}

	public final void placeCore(int x, int y, int z) {
		world.setTileEntity(x, y, z, ChromaTiles.DIMENSIONCORE.getBlock(), ChromaTiles.DIMENSIONCORE.getBlockMetadata(), this);
		coreLocation = new Coordinate(x, y, z);
	}

	public boolean hasCore() {
		return coreLocation != null;
	}

	public boolean isComplete() {
		return this.hasCore();
	}

	public final boolean shouldAllowCoreMining(World world, EntityPlayer ep) {
		return forcedOpen || this.hasBeenSolved(world);
	}

	protected abstract boolean hasBeenSolved(World world);

	public final void forceOpen(World world, EntityPlayer ep) {
		forcedOpen = true;
		this.openStructure(world);
		TileEntityDimensionCore te = this.getCoreTile(world);
		if (te != null)
			te.whitelistPlayer(ep);
	}

	public final TileEntityDimensionCore getCoreTile(World world) {
		return (TileEntityDimensionCore)coreLocation.getTileEntity(world);
	}

	public boolean forcedOpen() {
		return forcedOpen;
	}

	protected abstract void openStructure(World world);

	public int getPassword(EntityPlayer ep) {
		return new StructureTypeData(this).getPassword(ep);
	}

	@Override
	public final String toString() {
		return this.getClass().getSimpleName()+" @ "+this.getCentralBlockCoords()+" (E = "+this.getEntryLocation()+")";
	}

	public static final class StructurePair {

		public final DimensionStructureGenerator generator;
		public final CrystalElement color;

		public int generatedDimension = Integer.MIN_VALUE+1;

		public StructurePair(DimensionStructureGenerator gen, CrystalElement e) {
			generator = gen;
			color = e;
		}

		@Override
		public int hashCode() {
			return (color.ordinal() << 8) | generator.getType().ordinal();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof StructurePair) {
				StructurePair p = (StructurePair)o;
				return p.color == color && p.generator == generator;
			}
			return false;
		}

		@Override
		public String toString() {
			return color.name()+" "+generator;
		}

	}

	public static final class StructureTypeData {

		public final CrystalElement color;
		public final DimensionStructureType type;
		public final int generationIndex;

		public StructureTypeData(StructurePair p) {
			this(p.color, p.generator.getType(), p.generator.getGenerationIndex());
		}

		public StructureTypeData(DimensionStructureGenerator gen) {
			this(gen.genColor, gen.getType(), gen.getGenerationIndex());
		}

		public StructureTypeData(CrystalElement e, DimensionStructureType type, int idx) {
			color = e;
			this.type = type;
			generationIndex = idx;
		}

		public int getPassword(EntityPlayer ep) {
			int hash = (ep != null ? ep.getUniqueID() : DragonAPICore.Reika_UUID).toString().hashCode();
			hash = hash ^ 3178531*generationIndex;
			hash = hash ^ 1780943*type.ordinal();
			hash = hash ^ 4702617*ChromaOptions.getStructureDifficulty();
			hash = hash ^ 3689507*Loader.MC_VERSION.hashCode();
			return HashType.SHA1.hash(hash).hashCode();
		}
	}

	public static enum DimensionStructureType {

		TDMAZE(ThreeDMazeGenerator.class, "Three-Dimensional Maze"),
		ALTAR(AltarGenerator.class, ""),
		SHIFTMAZE(ShiftMazeGenerator.class, "Shifting Maze"),
		LOCKS(LocksGenerator.class, "Locks and Keys"),
		MUSIC(MusicPuzzleGenerator.class, "Crystal Music"),
		NONEUCLID(NonEuclideanGenerator.class, "Complex Spaces"),
		GOL(GOLGenerator.class, "Cellular Automata"),
		ANTFARM(AntFarmGenerator.class, "Fading Light"),
		LASER(LaserPuzzleGenerator.class, "Chromatic Beams"),
		PINBALL(PinballGenerator.class, "Expanding Motion"),
		GRAVITY(GravityPuzzleGenerator.class, "Luma Bursts"),
		BRIDGE(BridgeGenerator.class, "Dynamic Bridges"),
		WATER(WaterPuzzleGenerator.class, "Channeled Flow"),
		TESSELLATION(TessellationGenerator.class, "Spatial Satisfaction"),
		LIGHTPANEL(LightPanelGenerator.class, "Glowing Logic"),
		PISTONTAPE(PistonTapeGenerator.class, "Filter Cycle"),
		RAYBLEND(RayBlendGenerator.class, "Crystal Interference"),
		TRACES(TracePuzzleGenerator.class, "Parallel Connections"),//http://www.cross-plus-a.com/data/arukone.gif
		;

		private final Class generatorClass;
		//private DimensionStructureGenerator generator;
		private final String desc;

		private final boolean gennedCore;

		private static final String NBT_TAG = "structuresCompleted";

		public static final DimensionStructureType[] types = values();

		private final HashMap<UUID, DimensionStructureGenerator> generators = new HashMap();
		private static final HashMap<UUID, DimensionStructureType> generatorTypes = new HashMap();

		private DimensionStructureType(Class<? extends DimensionStructureGenerator> c, String s) {
			generatorClass = c;
			desc = s;

			//Test
			DimensionStructureGenerator gen = this.createGenerator(false, -1);
			gen.startCalculate(CrystalElement.WHITE, 0, 0, new Random());
			gennedCore = gen.isComplete();
			gen.clear();
		}
		/*
		public DimensionStructureGenerator getGenerator() {
			if (generator == null) {
				try {
					generator = (DimensionStructureGenerator)generatorClass.newInstance();
					generator.structureType = this;
				}
				catch (Exception e) {
					throw new RegistrationException(ChromatiCraft.instance, "Error initializing structure type "+this);
				}
			}
			return generator;
		}
		 */

		public DimensionStructureGenerator createGenerator(int idx) {
			return this.createGenerator(true, idx);
		}

		private DimensionStructureGenerator createGenerator(boolean doCache, int idx) {
			try {
				DimensionStructureGenerator generator = (DimensionStructureGenerator)generatorClass.newInstance();
				generator.structureType = this;
				generator.structureTypeIndex = idx;
				if (doCache) {
					generators.put(generator.id, generator);
					generatorTypes.put(generator.id, generator.getType());
				}
				return generator;
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Error creating a generator for structure type "+this);
			}
		}

		public void markPlayerCompleted(EntityPlayer ep) {
			NBTTagCompound tag = ChromaResearchManager.instance.getRootNBTTag(ep);
			NBTTagCompound dat = tag.getCompoundTag(NBT_TAG);
			dat.setBoolean("struct_"+this.ordinal(), true);
			tag.setTag(NBT_TAG, dat);
		}

		public boolean hasPlayerCompleted(EntityPlayer ep) {
			NBTTagCompound tag = ChromaResearchManager.instance.getRootNBTTag(ep);
			NBTTagCompound dat = tag.getCompoundTag(NBT_TAG);
			return dat.getBoolean("struct_"+this.ordinal());
		}

		public boolean isComplete() {
			return gennedCore;
		}

		public String getDisplayText() {
			return desc;
		}

		public DimensionStructureGenerator getGenerator(UUID uid) {
			return generators.get(uid);
		}

		public Collection<UUID> getUUIDs() {
			return Collections.unmodifiableCollection(generators.keySet());
		}

		public int getIconIndex() {
			return 8+this.ordinal();
		}

	}

	public static void resetCachedGenerators() {
		DimensionStructureType.generatorTypes.clear();
		for (int i = 0; i < DimensionStructureType.types.length; i++) {
			DimensionStructureType.types[i].generators.clear();
		}
	}

	public static DimensionStructureGenerator getGeneratorByID(UUID uid) {
		DimensionStructureType type = DimensionStructureType.generatorTypes.get(uid);
		return type != null ? type.generators.get(uid) : null;
	}

	public static class DynamicPieceLocation {

		private final DynamicStructurePiece generator;
		private final int relX;
		private final int relZ;

		private DynamicPieceLocation(DynamicStructurePiece gen, int x, int z) {
			generator = gen;
			relX = ChunkSplicedGenerationCache.modAndAlign(x);
			relZ = ChunkSplicedGenerationCache.modAndAlign(z);
		}

	}

	public final void generateDataTile(int x, int y, int z, Object... data) {
		world.setTileEntity(x, y, z, ChromaBlocks.DIMDATA.getBlockInstance(), 0, new StructureInterfaceCallback(this, data));
	}

	public final void generatePasswordTile(World world, int x, int y, int z) {
		//if (ChromaOptions.structureBypassEnabled()) {
		world.setBlock(x, y, z, ChromaBlocks.DIMDATA.getBlockInstance(), 1, 2);
		StructureInterfaceCallback sr = new StructureInterfaceCallback(this, null);
		sr.onTilePlaced(world, x, y, z, world.getTileEntity(x, y, z));
		//}
		//ReikaJavaLibrary.pConsole("Genning password tile @ "+x+", "+y+", "+z);
	}

	public final void generatePasswordTile(int x, int y, int z) {
		//if (ChromaOptions.structureBypassEnabled())
		world.setTileEntity(x, y, z, ChromaBlocks.DIMDATA.getBlockInstance(), 1, new StructureInterfaceCallback(this, null));
		//ReikaJavaLibrary.pConsole("Genning password tile @ "+x+", "+y+", "+z);
	}

	public final void generateLootChest(int x, int y, int z, ForgeDirection dir, String chest, int bonus, Object... extras) {
		world.setTileEntity(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), BlockLootChest.getMeta(dir), new LootChestCallback(chest, bonus, extras));
	}

	public void tickPlayer(EntityPlayer ep) {

	}

	public void onBlockUpdate(World world, int x, int y, int z, Block b) {

	}

	public void updateTick(World world) {

	}

	public final void writeToNBT(NBTTagCompound tag) {

	}

	public final void readFromNBT(NBTTagCompound tag) {

	}

	public static abstract class DimensionStructureTileCallback implements TileCallback {

		public final void writeToNBT(NBTTagCompound tag) {

		}

		public final void readFromNBT(NBTTagCompound tag) {

		}
	}

	private static final class StructureInterfaceCallback extends DimensionStructureTileCallback {

		private final DimensionStructureGenerator generator;
		private final HashMap<String, Object> data;

		private StructureInterfaceCallback(DimensionStructureGenerator gen, Object[] data) {
			generator = gen;
			this.data = data != null ? new HashMap() : null;
			if (this.data != null) {
				for (int i = 0; i < data.length; i += 2) {
					String s = (String)data[i];
					this.data.put(s, data[i+1]);
				}
			}
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof StructureInterfaceTile) {
				//ReikaJavaLibrary.pConsole("Generating "+te+" @ "+new Coordinate(te));
				((StructureInterfaceTile)te).loadData(generator, data);
			}
		}

	}

	public static final class UUIDPlace extends DimensionStructureTileCallback {

		private final UUID uid;

		public UUIDPlace(UUID id) {
			uid = id;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof StructureBlockTile) {
				((StructureBlockTile)te).uid = uid;
			}
		}

	}

	public static final class LootChestCallback extends DimensionStructureTileCallback {

		private final String chest;
		private final int bonus;
		private final ChancedOutputList extras;

		public LootChestCallback(String s, int b, Object... ex) {
			this(s, b, ChancedOutputList.parseFromArray(false, ex));
		}

		public LootChestCallback(String s, int b, ChancedOutputList ex) {
			chest = s;
			bonus = b;
			extras = ex;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityLootChest) {
				TileEntityLootChest tc = (TileEntityLootChest)te;
				tc.populateChest(chest, null, bonus, world.rand);

				Collection<ItemStack> extra = extras.calculate();
				for (ItemStack is : extra) {
					int slot = world.rand.nextInt(tc.getSizeInventory());
					tc.setInventorySlotContents(slot, is);
				}
			}
		}

	}

}
