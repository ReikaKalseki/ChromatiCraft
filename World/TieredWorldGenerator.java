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

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Block.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.BlockTieredPlant.TieredPlants;
import cpw.mods.fml.common.IWorldGenerator;

public class TieredWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		for (int i = 0; i < TieredPlants.list.length; i++) {
			TieredPlants p = TieredPlants.list[i];
			boolean flag = false;
			if (random.nextInt(p.getGenerationChance()) == 0) {
				int n = p.getGenerationCount();
				for (int k = 0; k < n; k++) {
					int posX = chunkX + random.nextInt(16);
					int posZ = chunkZ + random.nextInt(16);
					flag |= p.generate(world, posX, posZ, random);
				}
			}
		}

		for (int i = 0; i < TieredOres.list.length; i++) {
			TieredOres p = TieredOres.list[i];
			boolean flag = false;
			if (random.nextInt(p.getGenerationChance()) == 0) {
				int n = p.getGenerationCount();
				for (int k = 0; k < n; k++) {
					int posX = chunkX + random.nextInt(16);
					int posZ = chunkZ + random.nextInt(16);
					flag |= p.generate(world, posX, posZ, random);
				}
			}
		}

	}

}
