/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Block.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import cpw.mods.fml.common.IWorldGenerator;

public class TieredWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT || ChromaOptions.FLATGEN.getState() && !world.provider.hasNoSky) {
			chunkX *= 16;
			chunkZ *= 16;

			for (int i = 0; i < TieredPlants.list.length; i++) {
				TieredPlants p = TieredPlants.list[i];
				boolean flag = false;
				if (random.nextInt(p.getGenerationChance()) == 0) {
					int n = p.getGenerationCount();
					for (int k = 0; k < n; k++) {
						int posX = chunkX + random.nextInt(16);
						int posZ = chunkZ + random.nextInt(16);
						Coordinate c = p.generate(world, posX, posZ, random);
						if (c != null) {
							c.setBlock(world, p.getBlock(), p.ordinal());
						}
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

}
