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
import java.util.Collection;
import java.util.EnumMap;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Instantiable.Worldgen.OriginBlockCache;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public abstract class LockLevel extends StructurePiece implements Comparable<LockLevel> {

	public final LockChannel level;
	private boolean[] mirror;
	protected ForgeDirection facing;

	private OriginBlockCache currentGenerator;

	private final EnumMap<LockColor, CrystalElement> shuffleMap = new EnumMap(LockColor.class);

	protected LockLevel(LocksGenerator s, BlockLockKey.LockChannel level) {
		super(s);
		this.level = level;
	}

	protected abstract void generate(OriginBlockCache world, int x, int y, int z);

	@Override
	public final void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int f = (this.getWidth())/4;
		int dx = x+f*facing.offsetZ;
		int dz = z+f*facing.offsetX;
		OriginBlockCache cache = new OriginBlockCache(dx, y, dz, ForgeDirection.SOUTH);
		currentGenerator = cache;
		this.generate(cache, x, y, z);
		cache.align(facing);
		if (mirror[0])
			cache.flipX();
		if (mirror[1])
			cache.flipZ();
		cache.addToGenCache(world);
		currentGenerator = null;
	}

	protected final void generateWhiteRune(int x, int y, int z) {
		currentGenerator.setBlock(x, y, z, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.WHITE.ordinal());
	}

	protected final void generateRune(int x, int y, int z, LockColor color) {
		currentGenerator.setBlock(x, y, z, ChromaBlocks.RUNE.getBlockInstance(), shuffleMap.get(color).ordinal());
	}

	protected final void generateKey(int x, int y, int z) {
		currentGenerator.setBlock(x, y, z, ChromaBlocks.LOCKKEY.getBlockInstance(), level.ordinal());
	}

	protected final void generateTimer(int x, int y, int z) {
		currentGenerator.setBlock(x, y, z, ChromaBlocks.LOCKFREEZE.getBlockInstance(), level.ordinal());
	}

	protected final void generateLock(int x, int y, int z, LockColor... colors) {
		CrystalElement[] elements = new CrystalElement[colors.length];
		for (int i = 0; i < colors.length; i++) {
			elements[i] = shuffleMap.get(colors[i]);
		}
		currentGenerator.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 0, new LockColorSet(level.ordinal(), elements));
	}

	protected final void generateGate(int x, int y, int z) {
		currentGenerator.setTileEntity(x, y, z, ChromaBlocks.COLORLOCK.getBlockInstance(), 1, new LockColorSet(level.ordinal()));
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

	private static class LockColorSet implements TileCallback {

		private final CrystalElement[] colors;
		private final int channel;

		public LockColorSet(int ch, CrystalElement... c) {
			colors = c;
			channel = ch;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity tile) {
			if (tile instanceof TileEntityColorLock) {
				TileEntityColorLock te = (TileEntityColorLock)tile;
				for (CrystalElement e : colors)
					te.addColor(e);
				te.setChannel(channel);
			}
		}

	}

	public abstract int getWidth();
	public abstract int getLength();

	public abstract int getInitialOffset();

	public abstract int getEnterExitDL();
	public abstract int getEnterExitDT();

	public abstract int getDifficultyRating();

	/**
	 0 = Basic Locks<br>
	 1 = Multi Locks<br>
	 2 = White Locks<br>
	 3 = Fences<br>
	 4 = @Deprecated Pipes<br>
	 5 = Timers<br>
	 6 = @Deprecated Rocks
	 */
	public abstract int getFeatureRating();

	public int getWeightValue() {
		return this.getFeatureRating()*100+this.getDifficultyRating();
	}

	public final boolean canGenerate() {
		return this.getDifficultyRating() <= 2+ChromaOptions.getStructureDifficulty();
	}

	@Override
	public final int compareTo(LockLevel l) {
		return this.getWeightValue()-l.getWeightValue();
	}

	public final void permute(Random rand) {
		shuffleMap.clear();
		ArrayList<CrystalElement> set = ReikaJavaLibrary.makeListFrom(CrystalElement.elements);
		set.remove(CrystalElement.WHITE);
		for (int i = 0; i < LockColor.list.length; i++) {
			LockColor in = LockColor.list[i];
			int outindex = rand.nextInt(set.size());
			CrystalElement out = set.get(outindex);
			set.remove(outindex);
			Collection<CrystalElement> mix = this.getConfusableColors(out);
			for (CrystalElement e : mix)
				set.remove(e);
			shuffleMap.put(in, out);
		}

		mirror = new boolean[2];
	}

	/** To avoid similar-looking colors */
	private Collection<CrystalElement> getConfusableColors(CrystalElement out) {
		ArrayList<CrystalElement> li = new ArrayList();
		switch(out) {
			case WHITE:
				li.add(CrystalElement.LIGHTGRAY);
				break;
			case BLACK:
				li.add(CrystalElement.GRAY);
				break;
			case GRAY:
				li.add(CrystalElement.BLACK);
				li.add(CrystalElement.LIGHTGRAY);
				break;
			case LIGHTGRAY:
				li.add(CrystalElement.GRAY);
				li.add(CrystalElement.WHITE);
				break;
			default:
				break;
		}
		return li;
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
