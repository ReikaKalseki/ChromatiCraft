/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;

public abstract class ChromaWorldGenerator extends WorldGenerator {

	public final DimensionGenerators type;

	protected final Random rand;
	protected final long seed;

	public ChromaWorldGenerator(DimensionGenerators g, Random r, long s) {
		type = g;
		rand = r;
		seed = s;
	}

	public abstract float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome);

}
