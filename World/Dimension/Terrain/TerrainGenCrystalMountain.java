/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.SimplexNoiseGenerator;


public class TerrainGenCrystalMountain extends ChromaDimensionBiomeTerrainShaper {

	private final SimplexNoiseGenerator mountainHeight;
	private final SimplexNoiseGenerator shearNoise;
	private final SimplexNoiseGenerator threshNoise;
	private final SimplexNoiseGenerator gemNoise;
	private final SimplexNoiseGenerator dirtNoise;
	//private final SimplexNoiseGenerator gemNoise2;

	public static final double MAX_AMPLITUDE = 80;

	public static final double MIN_SHEAR = 0;
	public static final double MAX_SHEAR = 45;

	public static final double MIN_THRESH = 4;
	public static final double MAX_THRESH = 24;

	public static final int BIOME_SEARCH = 24;

	public TerrainGenCrystalMountain(long seed) {
		super(seed, SubBiomes.MOUNTAINS);
		mountainHeight = new SimplexNoiseGenerator(seed);

		shearNoise = new SimplexNoiseGenerator(seed ^ -seed);
		threshNoise = new SimplexNoiseGenerator(~(seed ^ -seed));

		gemNoise = new SimplexNoiseGenerator(-seed);
		//gemNoise2 = new SimplexNoiseGenerator(~(-seed));
		dirtNoise = new SimplexNoiseGenerator(~(-seed));
	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, int surface, Random rand, double edgeFactor) {
		double innerScale = 1/16D;
		double mainScale = 1/4D;
		int dx = chunkX+i;
		int dz = chunkZ+k;
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		int dt = (int)ReikaMathLibrary.normalizeToBounds(dirtNoise.getValue(rx*8, rz*8), 0, 4);//1+rand.nextInt(4);
		HeightData dat = this.calcHeight(rx, rz);
		dat.maxHeight *= edgeFactor;
		dat.isCliff = this.isCliff(chunkX, chunkZ, i, k, rx, rz, innerScale, mainScale);
		double g1 = 0;
		double g2 = 0;
		if (dat.isCliff) {
			double dc = dat.maxHeight-dt-dat.shearThreshold;
			double gt = 0.75;
			double gw = ReikaMathLibrary.normalizeToBounds(gemNoise.getValue(rx*2, rz*2), 0, dc*gt);//Math.max(, gemNoiseLow.getValue(rx, rz));
			double base = this.isFlatWorld(world) ? 3 : 64+ChunkProviderChroma.VERTICAL_OFFSET;
			double mid = base+dat.shearThreshold+dc/2;//dat.shearThreshold+dat.shearHeight/2;

			//ReikaJavaLibrary.pConsole(mid+" @ "+dx+", "+dz);
			g1 = mid-gw/2;
			g2 = mid+gw/2;//ReikaMathLibrary.normalizeToBounds(gemNoise2.getValue(rx, rz), shearThresh+1, val-dt-1);
			//ReikaJavaLibrary.pConsole(g1+":"+g2+" by "+mid+" @ "+dx+", "+dz+" in "+(dat.shearThreshold+1)+" & "+(dat.maxHeight-dt-1));
		}
		double gmax = Math.max(g1, g2);
		double gmin = Math.min(g1, g2);
		int h = (int)dat.maxHeight;
		//ReikaJavaLibrary.pConsole(dx+", "+dz+": "+h);
		int my = 63+ChunkProviderChroma.VERTICAL_OFFSET;
		if (this.isFlatWorld(world))
			my = 3;
		for (int y = 0; y < my; y++) {
			world.setBlock(dx, y, dz, Blocks.stone, 0, 2);
		}
		for (int j = 0; j <= h; j++) {
			int dy = my+j;
			int m = 0;
			Block b = Blocks.stone;
			if (j == h)
				b = Blocks.grass;
			else if (h-j <= dt) {
				b = Blocks.dirt;
			}
			else if (dat.isCliff && (int)(gmax-gmin) > 1 && ReikaMathLibrary.isValueInsideBoundsIncl(gmin, gmax, dy)) {
				b = ChromaBlocks.DIMGEN.getBlockInstance();
				m = DimDecoTypes.GEMSTONE.ordinal();
			}
			world.setBlock(dx, dy, dz, b, m, 2);
		}
	}

	private HeightData calcHeight(double rx, double rz) {
		double voff = 0.6;
		double val = Math.max(0, (voff+mountainHeight.getValue(rx, rz))*(MAX_AMPLITUDE-MAX_SHEAR)/(1+voff));
		double shear = ReikaMathLibrary.normalizeToBounds(shearNoise.getValue(rx, rz), MIN_SHEAR, MAX_SHEAR);
		double shearThresh = ReikaMathLibrary.normalizeToBounds(threshNoise.getValue(rx, rz), MIN_THRESH, MAX_THRESH);
		if (val >= shearThresh)
			val += shear;
		return new HeightData(val, shear, shearThresh);
	}

	private boolean isCliff(int cx, int cz, int i, int k, double rx, double rz, double innerScale, double mainScale) {
		double h = this.calcHeight(rx, rz).maxHeight;
		double rxp = rx+innerScale*mainScale;
		double rxn = rx-innerScale*mainScale;
		double rzp = rz+innerScale*mainScale;
		double rzn = rz-innerScale*mainScale;
		double hpx = this.calcHeight(rxp, rz).maxHeight;
		double hnx = this.calcHeight(rxn, rz).maxHeight;
		double hpz = this.calcHeight(rx, rzp).maxHeight;
		double hnz = this.calcHeight(rx, rzn).maxHeight;
		return Math.abs(h-hpx) > 4 || Math.abs(h-hpz) > 4 || Math.abs(h-hnx) > 4 || Math.abs(h-hnz) > 4;
	}

	private static class HeightData {

		private double maxHeight;

		private final double shearHeight;
		private final double shearThreshold;

		private boolean isCliff;

		private HeightData(double h, double s, double t) {
			maxHeight = h;
			shearHeight = s;
			shearThreshold = t;
		}

	}

	@Override
	public double getBiomeSearchDistance() {
		return BIOME_SEARCH;
	}

}
