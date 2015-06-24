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
import java.util.Random;

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
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclideanGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ThreeDMazeGenerator;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public abstract class DimensionStructureGenerator implements TileCallback {

	protected final ChunkSplicedGenerationCache world = new ChunkSplicedGenerationCache();
	private DimensionStructureType structureType;
	private CrystalElement genColor;
	private ChunkCoordIntPair genCore;
	private ChunkCoordIntPair center;

	private final MultiMap<ChunkCoordIntPair, DynamicPieceLocation> dynamicParts = new MultiMap().setNullEmpty();

	protected DimensionStructureGenerator() {

	}

	public final DimensionStructureType getType() {
		return structureType;
	}

	protected final void addDynamicStructure(DynamicStructurePiece dsp, int x, int z) {
		dynamicParts.addValue(new ChunkCoordIntPair(x >> 4, z >> 4), new DynamicPieceLocation(dsp, x, z));
	}

	/** chunk X and Z are already *16 */
	protected abstract void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand);

	public final void startCalculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {
		genColor = e;
		genCore = new ChunkCoordIntPair(chunkX >> 4, chunkZ >> 4);
		this.calculate(chunkX, chunkZ, e, rand);
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

	public final void clear() {
		world.clear();
		dynamicParts.clear();
		center = null;
		this.clearCaches();
	}

	public final CrystalElement getColor() {
		return genColor;
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
			((TileEntityDimensionCore)te).setStructure(new StructurePair(structureType, this.getCoreColor(world)));
		}
	}

	private CrystalElement getCoreColor(World world) {
		return CrystalElement.elements[(int)((world.getSeed()%16)+16+structureType.ordinal())%16];//genColor;
	}

	public final void placeCore(int x, int y, int z) {
		world.setTileEntity(x, y, z, ChromaTiles.DIMENSIONCORE.getBlock(), ChromaTiles.DIMENSIONCORE.getBlockMetadata(), this);
	}

	public static class StructurePair {

		public final DimensionStructureType generator;
		public final CrystalElement color;

		public StructurePair(DimensionStructureType gen, CrystalElement e) {
			generator = gen;
			color = e;
		}

	}

	public static enum DimensionStructureType {

		TDMAZE(ThreeDMazeGenerator.class),
		ALTAR(AltarGenerator.class),
		SHIFTMAZE(ShiftMazeGenerator.class),
		LOCKS(LocksGenerator.class),
		MUSIC(MusicPuzzleGenerator.class),
		NONEUCLID(NonEuclideanGenerator.class);

		private final Class generatorClass;
		private DimensionStructureGenerator generator;

		private static final String NBT_TAG = "structuresCompleted";

		public static final DimensionStructureType[] types = values();

		private DimensionStructureType(Class<? extends DimensionStructureGenerator> c) {
			generatorClass = c;
		}

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
