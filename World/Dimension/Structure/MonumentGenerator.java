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
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public class MonumentGenerator extends WorldGenerator {

	private final MonumentCenter center = new MonumentCenter();

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		ArrayList<ForgeDirection> li = new ArrayList();

		for (int i = 2; i < 6; i++) {
			if (rand.nextBoolean()) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				li.add(dir);
			}
		}

		center.generate(world, rand, x, y, z, li);

		for (ForgeDirection dir : li) {
			MonumentTunnel mt = new MonumentTunnel(dir, 8+rand.nextInt(24));
			mt.generate(world, rand, x+14+dir.offsetX*15, y+1, z+14+dir.offsetZ*15);
		}


		return true;
	}

}
