/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class RetroCrystalGenerator implements RetroactiveGenerator {

	@Override
	public void generate(Random rand, World world, int chunkX, int chunkZ) {
		for (int i = 0; i < CrystalGenerator.PER_CHUNK*CrystalGenerator.getDensityFactor(world, chunkX, chunkZ); i++) {
			int posX = chunkX + rand.nextInt(16);
			int posZ = chunkZ + rand.nextInt(16);
			int posY = 4+rand.nextInt(64-4);
			Block id = ChromaBlocks.CRYSTAL.getBlockInstance();
			int meta = rand.nextInt(16);
			if (CrystalGenerator.canGenerateAt(world, posX, posY, posZ)) {
				world.setBlock(posX, posY, posZ, id, meta, 3);
			}
			int r = 3;
			for (int k = -r; k <= r; k++) {
				for (int l = -r; l <= r; l++) {
					for (int m = -r; m <= r; m++) {
						world.func_147479_m(posX, posY, posZ);
					}
				}
			}
		}
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraftCrystal";
	}

}
