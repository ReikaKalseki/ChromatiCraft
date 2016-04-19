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
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.DimDecoTileTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldGenFireJet extends ChromaWorldGenerator {

	public WorldGenFireJet(DimensionGenerators g) {
		super(g);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.1F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int dy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		if (world.getBlock(x, dy, z) == Blocks.water && ReikaWorldHelper.getWaterDepth(world, x, dy, z) >= 2) {

			world.setBlock(x, dy, z, ChromaBlocks.DIMGENTILE.getBlockInstance(), DimDecoTileTypes.FIREJET.ordinal(), 3);

			world.setBlock(x+1, dy, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x-1, dy, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x, dy, z+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x, dy, z-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);

			return true;
		}

		return false;
	}

}
