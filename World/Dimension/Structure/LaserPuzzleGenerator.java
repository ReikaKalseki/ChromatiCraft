/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.ChromatiCraft.World.Dimension.Structure.Laser.LaserLevel;


public class LaserPuzzleGenerator extends DimensionStructureGenerator {

	private static final ArrayList<String> order = new ArrayList();

	private final ArrayList<LaserLevel> rooms = new ArrayList();

	static {
		order.add("mirrortut");
		order.add("mirrors2");
		order.add("refractortut");
		order.add("splittertut");
		order.add("filtertut");
		order.add("polartut");
		order.add("polar2");
		order.add("oneway");
		order.add("prismtut1");
		order.add("prismtut2");
		order.add("prism3");
		order.add("complex");
	}

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int x = chunkX;
		int z = chunkZ;
		int y = 10;
		for (String s : order) {
			LaserLevel l = new LaserLevel(this, s);
			rooms.add(l);
			l.generate(world, x, y, z);
			int dx = l.getLengthX()+10;
			x += dx;
		}
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
		for (LaserLevel l : rooms) {
			if (!l.isSolved) {
				return false;
			}
		}
		return true;
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	protected void clearCaches() {
		rooms.clear();
	}

}
