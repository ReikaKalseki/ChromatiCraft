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
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class WorldGenFloatstone extends ChromaWorldGenerator {

	public WorldGenFloatstone(DimensionGenerators g) {
		super(g);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int y2 = y+12+rand.nextInt(12);

		int n = 2+rand.nextInt(6);
		for (int i = 0; i < n; i++) {
			int s = 24+rand.nextInt(16);
			if (n >= 4)
				s *= 1-n/16D;
			int dy = ReikaRandomHelper.getRandomPlusMinus(y2, 3);
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8);
			new WorldGenMinable(ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.FLOATSTONE.ordinal(), s, Blocks.air).generate(world, rand, dx, dy, dz);
		}

		return true;
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.02F;
	}

}
