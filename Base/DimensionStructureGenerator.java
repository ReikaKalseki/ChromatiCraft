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
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ThreeDMazeGenerator;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public abstract class DimensionStructureGenerator implements TileCallback {

	protected final ChunkSplicedGenerationCache world = new ChunkSplicedGenerationCache();
	private DimensionStructureType structureType;
	private CrystalElement genColor;

	protected DimensionStructureGenerator() {

	}

	public final DimensionStructureType getType() {
		return structureType;
	}

	/** chunk X and Z are already *16 */
	protected abstract void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand);

	public final void startCalculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {
		genColor = e;
		this.calculate(chunkX, chunkZ, e, rand);
	}

	public final void generateChunk(World w, ChunkCoordIntPair cp) {
		world.generate(w, cp);
	}

	public final void clear() {
		world.clear();
		this.clearCaches();
	}

	protected void clearCaches() {

	}

	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof TileEntityDimensionCore) {
			((TileEntityDimensionCore)te).setStructure(new StructurePair(structureType, genColor));
		}
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
		LOCKS(LocksGenerator.class);

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

}
