/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.IWG.ColorTreeGenerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class RainbowForestGenerator extends WorldGenerator {

	//private static Simplex3DGenerator colorOffsetNoise;

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (ColorTreeGenerator.canGenerateTree(world, x, z) && BlockDyeSapling.canGrowAt(world, x, y, z, true)) {
			//ColorTreeGenerator.growTree(world, x, y, z, 5+random.nextInt(3), random, this.getColor(x, y, z));
			//TreeShaper.getInstance().generateTallTree(world, x, y, z);
			ReikaDyeHelper color = getColor(x, y, z);
			if (random.nextInt(10) == 0) {
				if (RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z)) {
					RainbowTreeGenerator.getInstance().generateRainbowTree(world, x, y, z, random);
					if (ModList.THAUMCRAFT.isLoaded() && ChromaOptions.ETHEREAL.getState()) {
						for (int i = 0; i < 8; i++) {
							int dx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
							int dz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
							while (!world.checkChunksExist(dx, 0, dz, dx, world.provider.getActualHeight(), dz)) {
								dx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
								dz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
							}
							int dy = world.getTopSolidOrLiquidBlock(dx, dz);
							if (ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
								Block id = ThaumItemHelper.BlockEntry.ETHEREAL.getBlock();
								int meta = ThaumItemHelper.BlockEntry.ETHEREAL.metadata;
								world.setBlock(dx, dy, dz, id, meta, 3);
								world.func_147451_t(dx, dy, dz);
								world.func_147479_m(dx, dy, dz);
							}
						}
					}
				}
				else {
					TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, color, false);
				}
			}
			else {
				TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, color, false);
			}
			return true;
		}
		return false;
	}

	public static ReikaDyeHelper getColor(/*World world, */int x, int y, int z) {
		/*
		if (colorOffsetNoise == null || colorOffsetNoise.seed != world.getSeed()) {
			colorOffsetNoise = (Simplex3DGenerator)new Simplex3DGenerator(world.getSeed()).setFrequency(1/60D);
		}*/
		int idx = (Math.abs(x/16)+y+Math.abs(z/16));
		//idx += ReikaMathLibrary.normalizeToBounds(colorOffsetNoise.getValue(idx, y, z), 0, 16);
		return ReikaDyeHelper.dyes[(idx+16)%16];
	}

}
