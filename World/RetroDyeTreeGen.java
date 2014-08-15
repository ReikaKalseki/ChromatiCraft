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
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class RetroDyeTreeGen implements RetroactiveGenerator {

	@Override
	public void generate(Random r, World world, int chunkX, int chunkZ) {
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		int trees = ColorTreeGenerator.getTreeCount(biome);
		int x = chunkX+r.nextInt(16);
		int z = chunkZ+r.nextInt(16);
		if (ColorTreeGenerator.canGenerateTree(world, x, z)) {
			for (int i = 0; i < trees; i++) {
				if (r.nextInt(ColorTreeGenerator.CHANCE) == 0) {
					int y = world.getTopSolidOrLiquidBlock(x, z);
					Block b = world.getBlock(x, y, z);
					;
					TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[r.nextInt(16)], false);
				}
			}
		}
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return false;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraftGen";
	}

}