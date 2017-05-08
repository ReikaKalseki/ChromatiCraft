/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterFloor;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterPath;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class WaterPuzzleGenerator extends DimensionStructureGenerator {

	private final ArrayList<WaterFloor> levels = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {

		posX = chunkX;
		posZ = chunkZ;
		posY = 10+rand.nextInt(10);

		int n = this.getSize();
		int r = this.getRadius(0);
		int startx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		int startz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		for (int i = 0; i < n; i++) {
			r = this.getRadius(i);
			int endx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			int endz = ReikaRandomHelper.getRandomPlusMinus(0, r);
			WaterPath path = new WaterPath(startx, startz, endx, endz, r);
			path.genPath(rand);
			WaterFloor w = new WaterFloor(this, i, r, path);
			levels.add(w);
			startx = endx;
			startz = endx;
		}

		int y = posY+levels.size()*WaterFloor.HEIGHT;

		for (WaterFloor l : levels) {
			l.generate(world, posX, y, posZ);
			y -= l.HEIGHT;
		}
	}

	private int getRadius(int i) {
		return 2+i;
	}

	private static int getSize() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 6;
			case 2:
				return 8;
			case 3:
			default:
				return 12;
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
		for (WaterFloor f : levels) {
			if (!f.hasBeenSolved())
				return false;
		}
		return true;
	}

	@Override
	protected void clearCaches() {
		levels.clear();
	}

	public WaterFloor getLevel(int i) {
		return levels.get(i);
	}

	public int levelCount() {
		return levels.size();
	}

}
