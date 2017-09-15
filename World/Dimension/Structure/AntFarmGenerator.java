/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarm.AntFarmEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarm.AntFarmLevel;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarm.AntFarmTunnel;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class AntFarmGenerator extends DimensionStructureGenerator {

	public static final float LIGHT_DURATION = 1024;

	private final HashSet<Coordinate> airSpaces = new HashSet();
	private final HashSet<Coordinate> tunnelSpaces = new HashSet();
	private final HashSet<Coordinate> levelSpaces = new HashSet();

	private final LinkedList<AntFarmLevel> levels = new LinkedList();
	private final ArrayList<AntFarmTunnel> tunnels = new ArrayList();

	private final HashMap<Coordinate, BlockKey> blocks = new HashMap();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int h = 90;
		int mh = 20;

		int dhy = 10;
		for (int y = mh; y <= mh+h; y += dhy) {
			/*
			int x = ReikaRandomHelper.getRandomPlusMinus(chunkX, 96);
			int z = ReikaRandomHelper.getRandomPlusMinus(chunkZ, 96);
			if (y == mh+h) {
				x = chunkX;
				z = chunkZ;
			}
			 */
			double a = Math.toRadians(360D*(y-mh)/h);
			double dr = ReikaRandomHelper.getRandomBetween(48D, 96D);
			int x = chunkX+MathHelper.floor_double(dr*Math.cos(a));
			int z = chunkZ+MathHelper.floor_double(dr*Math.sin(a));
			AntFarmLevel pre = levels.isEmpty() ? null : levels.getLast();
			int lh = 4;
			int lr = 8;
			AntFarmLevel l = new AntFarmLevel(this, x, y, z, lr, lh);
			l.register(this, levelSpaces);
			levels.addLast(l);
			if (pre != null) {
				AntFarmTunnel t = this.createTunnel(pre.posX, pre.posY, pre.posZ, x, y, z, 3, 2, rand);
				while (t.intersectsWith(tunnelSpaces, levelSpaces)) {
					t = this.createTunnel(pre.posX, pre.posY, pre.posZ, x, y, z, 3, 2, rand);
				}
				t.register(this, tunnelSpaces);
				tunnels.add(t);
			}
		}

		for (AntFarmLevel l : levels) {
			int n = 1;//this.getRoomsPerLevel(rand);
			for (int i = 0; i < n; i++) {
				double ang = Math.toRadians(rand.nextDouble()*360);
				double slope = ReikaRandomHelper.getRandomPlusMinus(0D, 45D);
				double len = ReikaRandomHelper.getRandomBetween(12D, 64D);
				double[] d = ReikaPhysicsHelper.polarToCartesian(len, slope, ang);
				int x = MathHelper.floor_double(d[0]);
				int y = MathHelper.floor_double(d[1]);
				int z = MathHelper.floor_double(d[2]);
				int dx = l.posX+x;
				int dy = l.posY+y;
				int dz = l.posZ+z;
				AntFarmTunnel t = this.createTunnel(l.posX, l.posY, l.posZ, dx, dy, dz, 3, 3, rand);
				while (t.intersectsWith(tunnelSpaces, levelSpaces) && false) {
					t = this.createTunnel(l.posX, l.posY, l.posZ, dx, dy, dz, 3, 3, rand);
				}
				t.register(this, tunnelSpaces);
				tunnels.add(t);
			}
		}

		/*
		for (AntFarmTunnel a : tunnels) {
			a.generate(world, 0, 0, 0);
		}

		for (AntFarmLevel a : levels) {
			a.generate(world, a.posX, a.posY, a.posZ);
		}
		 */

		for (Coordinate c : blocks.keySet()) {
			BlockKey bk = blocks.get(c);
			world.setBlock(c.xCoord, c.yCoord, c.zCoord, bk.blockID, bk.metadata);
		}

		this.addDynamicStructure(new AntFarmEntrance(this, mh+h), chunkX, chunkZ);
	}

	private AntFarmTunnel createTunnel(int x, int y, int z, int x2, int y2, int z2, int w, int c, Random rand) {
		return new AntFarmTunnel(this, new Coordinate(x, y, z), new Coordinate(x2, y2, z2), c, w);
	}

	private int getRoomsPerLevel(Random rand) {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return rand.nextInt(6) == 0 ? 2 : 1;
			case 2:
				return rand.nextInt(2) == 0 ? 2 : 1;
			case 3:
			default:
				return rand.nextInt(3) == 0 ? 3 : rand.nextInt(4) > 0 ? 2 : 1;
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	public boolean hasBeenSolved(World world) {
		return false;
	}

	@Override
	public void openStructure(World world) {

	}

	@Override
	protected void clearCaches() {
		levels.clear();
		tunnels.clear();

		blocks.clear();

		airSpaces.clear();
		tunnelSpaces.clear();
		levelSpaces.clear();
	}

	public void cutBlock(Coordinate c, boolean air) {
		BlockKey b = air ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		if (airSpaces.contains(c))
			b = new BlockKey(Blocks.air);
		blocks.put(c, b);
		if (b.blockID == Blocks.air) {
			airSpaces.add(c);
		}
	}

}
