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

import java.util.Random;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class ShiftMazeGenerator extends DimensionStructureGenerator {

	@Override
	public void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {


		/*
		int n = 0;
		int p = 0;
		for (int i = -100; i <= 100; i++) {
			for (int k = -100; k <= 100; k++) {
				world.setBlock(i, 100, k, Blocks.wool, n);
			}
			if (p%16 == 0)
				n = (n+1)%16;
			p++;
		}
		 */
	}

}
