/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.BlockDimensionDeco.Types;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class WorldGenMiasma extends ChromaWorldGenerator {

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int r = 12+rand.nextInt(36);
		int e = 1+rand.nextInt(3);
		double ecc = 1-rand.nextDouble()*0.5;
		int r2 = (int)(r*ecc);

		for (int i = -r; i <= r; i++) {
			for (int k = -r2; k <= r2; k++) {
				double dd = i*i+k*k;
				double c = dd > 0 ? 2*r*r2/dd/100D : 1;
				if (c >= 1 || ReikaRandomHelper.doWithChance(c)) {
					int dx = x+i;
					int dz = z+k;
					int dy = world.getTopSolidOrLiquidBlock(dx, dz);
					int h = 4+rand.nextInt(8);
					for (int dy2 = dy+e; dy2 <= dy+h+e; dy2++) {
						if (rand.nextInt(dy2-dy) == 0 && world.getBlock(dx, dy2, dz) == Blocks.air)
							world.setBlock(dx, dy2, dz, ChromaBlocks.DIMGEN.getBlockInstance(), Types.MIASMA.ordinal(), 3);
					}
				}
			}
		}

		return true;
	}

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.02F;
	}

}
