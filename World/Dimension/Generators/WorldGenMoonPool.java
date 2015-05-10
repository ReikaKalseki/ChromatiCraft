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
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldGenMoonPool extends ChromaWorldGenerator {

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int dy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		if (world.getBlock(x, dy, z) == Blocks.water && ReikaWorldHelper.getWaterDepth(world, x, dy, z) >= 5) {
			if (ReikaWorldHelper.getWaterDepth(world, x+6, dy, z) >= 3 && ReikaWorldHelper.getWaterDepth(world, x-6, dy, z) >= 3) {
				if (ReikaWorldHelper.getWaterDepth(world, x, dy, z+6) >= 3 && ReikaWorldHelper.getWaterDepth(world, x, dy, z-6) >= 3) {

					float e = 0.3F+rand.nextFloat()*0.7F;

					for (int p = -1; p < 6; p++) {
						int h = dy+p;
						int ra = p <= 0 ? 10 : p == 1 ? 8 : p < 3 ? 6 : 6-(p-3);
						ra *= 1+rand.nextFloat();
						int rb = Math.max(2, (int)(ra*e));
						int r2 = (rb-2)*(rb-2);
						for (int i = -ra; i <= ra; i++) {
							for (int k = -rb; k <= rb; k++) {
								int dx = x+i;
								int dz = z+k;
								if (p < 5 && i*i+k*k <= r2) {
									world.setBlock(dx, h, dz, Blocks.air);
								}
								else if (i*i+k*k <= (ra+0.5)*(rb+0.5)) {
									world.setBlock(dx, h, dz, Blocks.grass);
								}
							}
						}
					}

					return true;
				}
			}
		}
		return false;
	}

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.002F;
	}

}
