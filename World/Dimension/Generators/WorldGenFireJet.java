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
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldGenFireJet extends ChromaWorldGenerator {

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.1F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int dy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		if (world.getBlock(x, dy, z) == Blocks.water && ReikaWorldHelper.getWaterDepth(world, x, dy, z) >= 2) {

			world.setBlock(x, dy, z, ChromaBlocks.DIMGENTILE.getBlockInstance(), 0, 3);

			world.setBlock(x+1, dy, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x-1, dy, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x, dy, z+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
			world.setBlock(x, dy, z-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);

			return true;
		}

		return false;
	}

}
