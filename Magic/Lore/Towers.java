/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.HexGrid;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Hex;
import Reika.DragonAPI.Instantiable.Math.HexGrid.MapShape;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Point;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public enum Towers {

	ALPHA("\u03b1", 24, 0, 0, 0),
	BETA("\u03b2", 25, 0, 4, -4),
	GAMMA("\u03b3", 26, -2, 4, -2),
	DELTA("\u03B4", 27, -4, 4, 0),
	PSI("\u03c8", 47, -4, 2, 2),
	PHI("\u03c6", 45, -4, 0, 4),
	OMEGA("\u03c9", 48, -2, -2, 4),
	LAMBDA("\u03bb", 34, 0, -4, 4),
	THETA("\u03b8", 31, 2, -4, 2),
	CHI("\u03c7", 46, 4, -4, 0),
	MU("\u03bc", 35, 4, -2, -2),
	TAU("\u03c4", 43, 4, 0, -4),
	SIGMA("\u03c3", 42, 2, 2, -4);

	public final String character;
	public final int textureIndex;
	private final Hex hex;
	private ChunkCoordIntPair position;

	public static final Towers[] towerList = values();

	private static final HashMap<ChunkCoordIntPair, Towers> towerChunkCache = new HashMap();
	private static final EnumMap<Towers, Coordinate> towerCache = new EnumMap(Towers.class);
	private static long lastWorldSeed;

	private static final double TOWER_OFFSET_RADIUS = 2500;

	private Towers(String s, int idx, int x, int y, int z) {
		character = s;
		textureIndex = idx;
		hex = new Hex(1, x, y, z);
	}

	/** This is BLOCK coords! */
	public ChunkCoordIntPair getRootPosition() {
		return position;
	}

	@SideOnly(Side.CLIENT)
	public void setLocationFromServer(int x, int z) {
		towerChunkCache.remove(position);
		position = new ChunkCoordIntPair(x, z);
		towerChunkCache.put(position, this);
	}

	public static void loadPositions(World world, double radius) { //radius is in chunks, and is the size of a hex in "chunks as pixels"
		towerChunkCache.clear();
		towerCache.clear();

		double r = radius*MathHelper.clamp_double(DragonOptions.WORLDSIZE.getValue()/6000D, 0.5, 5);
		HexGrid grid = new HexGrid(9, r, true, MapShape.HEXAGON).flower();

		Random rand = new Random(world.getSeed() ^ world.getSaveHandler().getWorldDirectory().getCanonicalPath().toString().hashCode());
		rand.nextBoolean();
		rand.nextBoolean();
		double a = rand.nextDouble()*360;
		double dx = -TOWER_OFFSET_RADIUS+rand.nextDouble()*TOWER_OFFSET_RADIUS*2+DragonOptions.WORLDCENTERX.getValue();
		double dz = -TOWER_OFFSET_RADIUS+rand.nextDouble()*TOWER_OFFSET_RADIUS*2+DragonOptions.WORLDCENTERZ.getValue();

		for (int i = 0; i < towerList.length; i++) {
			Towers t = towerList[i];
			grid.addHex(t.hex.q, t.hex.r, t.hex.s);
			Hex ref = grid.getHex(t.hex.q, t.hex.r, t.hex.s);
			Point p = grid.getHexLocation(ref).rotate(a, 0, 0).translate(dx, dz);
			int x = ReikaMathLibrary.roundToNearestX(16, (int)Math.round(p.x));
			int y = ReikaMathLibrary.roundToNearestX(16, (int)Math.round(p.y));
			t.position = new ChunkCoordIntPair(x, y);
			towerChunkCache.put(t.position, t);

			//ReikaJavaLibrary.pConsole("Poscalc: Tower "+t+" @ "+t.position+" from seed "+world.getSeed());
		}

		lastWorldSeed = world.getSeed();
	}

	/** In block coords */
	static Towers getTowerForChunk(int cx, int cz) {
		return towerChunkCache.get(new ChunkCoordIntPair(cx, cz));
	}

	public void generatedAt(int x, int y, int z) {
		towerCache.put(this, new Coordinate(x, y, z));
	}

	public Coordinate getGeneratedLocation() {
		return towerCache.get(this);
	}

	public Towers getNeighbor1() {
		if (this == ALPHA)
			return null;
		if (this == SIGMA)
			return BETA;
		return towerList[this.ordinal()+1];
	}

	public Towers getNeighbor2() {
		if (this == ALPHA)
			return null;
		if (this == BETA)
			return SIGMA;
		return towerList[this.ordinal()-1];
	}

	public static boolean initialized(World world) {
		return !towerChunkCache.isEmpty() && lastWorldSeed == world.getSeed();
	}


}
