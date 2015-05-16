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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public abstract class LockLevel extends StructurePiece implements Comparable<LockLevel> {

	public final LockChannel level;
	private boolean[] mirror;
	protected ForgeDirection facing;

	private final EnumMap<LockColor, CrystalElement> shuffleMap = new EnumMap(LockColor.class);

	protected LockLevel(LocksGenerator s, BlockLockKey.LockChannel level) {
		super(s);
		this.level = level;
	}

	protected final void generateWhiteRune(int x, int y, int z) {
		parent.world.setBlock(x, y, z, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.WHITE.ordinal());
	}

	protected final void generateRune(int x, int y, int z, LockColor color) {
		parent.world.setBlock(x, y, z, ChromaBlocks.RUNE.getBlockInstance(), shuffleMap.get(color).ordinal());
	}

	protected final void generateLock(int x, int y, int z, LockColor... colors) {
		CrystalElement[] elements = new CrystalElement[colors.length];
		for (int i = 0; i < colors.length; i++) {
			elements[i] = shuffleMap.get(colors[i]);
		}
		parent.world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 0, new LockColorSet(elements));
	}

	protected final void generateGate(int x, int y, int z) {
		parent.world.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 1, new LockColorSet());
	}

	public final LockLevel mirrorX() {
		mirror[0] = true;
		return this;
	}

	public final LockLevel mirrorZ() {
		mirror[1] = true;
		return this;
	}

	public final LockLevel setDirection(ForgeDirection dir) {
		facing = dir;
		return this;
	}

	protected final boolean isMirroredX() {
		return mirror[0];
	}

	protected final boolean isMirroredZ() {
		return mirror[1];
	}

	private class LockColorSet implements TileCallback {

		private final CrystalElement[] colors;

		public LockColorSet(CrystalElement... c) {
			colors = c;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity tile) {
			if (tile instanceof TileEntityColorLock) {
				TileEntityColorLock te = (TileEntityColorLock)tile;
				for (CrystalElement e : colors)
					te.addColor(e);
				te.setChannel(level.ordinal());
			}
		}

	}

	public abstract int getWidth();
	public abstract int getLength();

	public abstract int getEnterExitDL();
	public abstract int getEnterExitDT();

	public abstract int getDifficultyRating();

	/**
	 0 = Basic Locks<br>
	 1 = Multi Locks<br>
	 2 = White Locks<br>
	 3 = Fences<br>
	 4 = Pipes<br>
	 5 = Timers<br>
	 6 = Rocks
	 */
	public abstract int getFeatureRating();

	public int getWeightValue() {
		return this.getFeatureRating()*100+this.getDifficultyRating();
	}

	@Override
	public final int compareTo(LockLevel l) {
		return this.getWeightValue()-l.getWeightValue();
	}

	public final void permute(Random rand) {
		shuffleMap.clear();
		ArrayList<CrystalElement> set = ReikaJavaLibrary.makeListFrom(CrystalElement.elements);
		for (int i = 0; i < LockColor.list.length; i++) {
			LockColor in = LockColor.list[i];
			int outindex = rand.nextInt(set.size());
			CrystalElement out = set.get(outindex);
			set.remove(outindex);
			shuffleMap.put(in, out);
		}

		mirror = new boolean[2];
	}

	protected static enum LockColor {
		RED(),
		BLUE(),
		YELLOW(),
		GREEN(),
		PURPLE();

		private static final LockColor[] list = values();
	}

}
