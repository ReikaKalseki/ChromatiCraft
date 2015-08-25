/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityDimensionCore;
import Reika.ChromatiCraft.World.Dimension.Structure.AltarGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.GOLGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclideanGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ThreeDMazeGenerator;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public abstract class DimensionStructureGenerator implements TileCallback {

	protected final ChunkSplicedGenerationCache world = new ChunkSplicedGenerationCache();
	private DimensionStructureType structureType;
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
		this.calculate(chunkX, chunkZ, rand);
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

	protected void clearCaches() {

	}

	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof TileEntityDimensionCore) {
			((TileEntityDimensionCore)te).setStructure(new StructurePair(this, this.getCoreColor(world)));
		}
		else {
			ChromatiCraft.logger.logError(te+" instead of a Dimension Core at "+x+", "+y+", "+z+"!!");
		}
	}

	public CrystalElement getCoreColor(World world) {
		return genColor;//CrystalElement.elements[(int)((world.getSeed()%16)+16+structureType.ordinal())%16];
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

	public static final class StructurePair {

		public final DimensionStructureGenerator generator;
		public final CrystalElement color;

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

	}

	public static enum DimensionStructureType {

		TDMAZE(ThreeDMazeGenerator.class, "Three-Dimensional Maze"),
		ALTAR(AltarGenerator.class, ""),
		SHIFTMAZE(ShiftMazeGenerator.class, "Shifting Maze"),
		LOCKS(LocksGenerator.class, "Locks and Keys"),
		MUSIC(MusicPuzzleGenerator.class, "Crystal Music"),
		NONEUCLID(NonEuclideanGenerator.class, "Complex Spaces"),
		GOL(GOLGenerator.class, "Cellular Automata");

		private final Class generatorClass;
		//private DimensionStructureGenerator generator;
		private final String desc;

		private final boolean gennedCore;

		private static final String NBT_TAG = "structuresCompleted";

		public static final DimensionStructureType[] types = values();

		private final HashMap<UUID, DimensionStructureGenerator> generators = new HashMap();

		private DimensionStructureType(Class<? extends DimensionStructureGenerator> c, String s) {
			generatorClass = c;
			desc = s;

			//Test
			DimensionStructureGenerator gen = this.createGenerator();
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
		public DimensionStructureGenerator createGenerator() {
			try {
				DimensionStructureGenerator generator = (DimensionStructureGenerator)generatorClass.newInstance();
				generator.structureType = this;
				generators.put(generator.id, generator);
				return generator;
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Error creating a generator for structure type "+this);
			}
		}

		public void markPlayerCompleted(EntityPlayer ep) {
			NBTTagCompound tag = ReikaPlayerAPI.getDeathPersistentNBT(ep);
			NBTTagCompound dat = tag.getCompoundTag(NBT_TAG);
			dat.setBoolean("struct_"+this.ordinal(), true);
			tag.setTag(NBT_TAG, dat);
		}

		public boolean hasPlayerCompleted(EntityPlayer ep) {
			NBTTagCompound tag = ReikaPlayerAPI.getDeathPersistentNBT(ep);
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

	}

	public static class DynamicPieceLocation {

		private final DynamicStructurePiece generator;
		private final int relX;
		private final int relZ;

		private DynamicPieceLocation(DynamicStructurePiece gen, int x, int z) {
			generator = gen;
			x = x%16;
			z = z%16;
			if (x < 0) {
				x += 16;
				if (x%16 == 0)
					x += 16;
			}
			if (z < 0) {
				z += 16;
				if (z%16 == 0)
					z += 16;
			}
			relX = x;
			relZ = z;
		}

	}

}
