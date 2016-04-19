/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;

public abstract class ChromaWorldGenerator extends WorldGenerator {

	public final DimensionGenerators type;

	public ChromaWorldGenerator(DimensionGenerators g) {
		type = g;
	}

	public abstract float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome);

}
