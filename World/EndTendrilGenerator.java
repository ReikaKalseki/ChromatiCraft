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

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class EndTendrilGenerator implements RetroactiveGenerator {

	public static final EndTendrilGenerator instance = new EndTendrilGenerator();

	private static final double TENDRIL_THRESH_0 = 0.03;
	private static final double TENDRIL_THRESH_1 = 0.02;
	private static final double TENDRIL_THRESH_2 = 0.065;

	private static final double MIN_HEIGHT = 40;
	private static final double MAX_HEIGHT = 55;


	private final BlockKey coreBlock = new BlockKey(Blocks.obsidian, 0);
	private final BlockKey coatingBlock = new BlockKey(Blocks.end_stone, 0);

	private long seed;
	private SimplexNoiseGenerator placementNoise;
	private SimplexNoiseGenerator yLevelNoise;

	private EndTendrilGenerator() {

	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != 1)
			return;

		WorldGenInterceptionRegistry.skipLighting = true;
		SetBlockEvent.eventEnabledPre = false;
		SetBlockEvent.eventEnabledPost = false;
		BlockTickEvent.disallowAllUpdates = true;

		this.setSeed(world.getSeed());

		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = chunkX*16+i;
				int dz = chunkZ*16+k;
				double val = Math.abs(placementNoise.getValue(dx, dz));
				if (val <= TENDRIL_THRESH_2) {
					double h = 0;
					int c = 0;
					int min = Integer.MAX_VALUE;
					for (int a = -24; a <= 24; a += 8) {
						for (int b = -24; b <= 24; b += 8) {
							double hval = ReikaMathLibrary.normalizeToBounds(yLevelNoise.getValue(dx+a, dz+b), MIN_HEIGHT, MAX_HEIGHT);
							int top = world.getTopSolidOrLiquidBlock(dx, dz);
							if (top > 0)
								hval = Math.min(hval, top-3);
							h += hval;
							min = Math.min((int)hval, min);
							c++;
						}
					}
					h /= c;
					int y = (int)h;
					double f = ((val-TENDRIL_THRESH_1)/(TENDRIL_THRESH_2-TENDRIL_THRESH_1));
					double t = val < TENDRIL_THRESH_1 ? 1 : 1-f*f;
					int th = (int)(t*4);
					int y0 = y-th;
					for (int dy = y0; dy <= y; dy++) {
						if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
							BlockKey bk = val < TENDRIL_THRESH_0 && (dy == y || dy >= y0+2) ? coreBlock : coatingBlock;
							if (bk == coreBlock && dy >= min)
								continue;
							bk.place(world, dx, dy, dz);
						}
					}
				}
			}
		}

		BlockTickEvent.disallowAllUpdates = false;
		WorldGenInterceptionRegistry.skipLighting = false;
		SetBlockEvent.eventEnabledPre = true;
		SetBlockEvent.eventEnabledPost = true;
	}

	private void setSeed(long seed) {
		if (seed != this.seed || placementNoise == null) {
			this.seed = seed;
			placementNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed).setFrequency(0.01);
			yLevelNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(-seed).setFrequency(0.13);
		}
	}

	@Override
	public String getIDString() {
		return "Chroma_EndTendrils";
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return world.provider.dimensionId == 1;
	}

}
