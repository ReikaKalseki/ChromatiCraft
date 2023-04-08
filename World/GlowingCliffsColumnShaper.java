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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.TerrainShaper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.Satisforestry.API.SFAPI;


public class GlowingCliffsColumnShaper extends TerrainShaper {

	private final NoiseGeneratorBase landmassControl; //rough height map
	private final NoiseGeneratorBase upperPlateauTop;
	private final NoiseGeneratorBase upperPlateauBottom;
	private final NoiseGeneratorBase upperPlateauEdge;
	private final NoiseGeneratorBase middlePlateauCaveDepth;
	private final NoiseGeneratorBase middlePlateauTop;
	private final NoiseGeneratorBase middlePlateauBottom;
	private final NoiseGeneratorBase middlePlateauEdge;
	private final NoiseGeneratorBase lowerPlateauCaveDepth;
	private final NoiseGeneratorBase shoreHeight;
	private final NoiseGeneratorBase dirtThickness;
	private final NoiseGeneratorBase oceanDepth;
	private final NoiseGeneratorBase caveCeilNoise;

	//private final SimplexNoiseGenerator islandLowerNoise;
	//private final SimplexNoiseGenerator islandUpperNoise;
	//private final SimplexNoiseGenerator islandWaterNoise;
	//private final SimplexNoiseGenerator islandRiverNoise;

	private static final double SHORELINE_THRESHOLD = 0.3;//0.25;

	private static final double MIDDLE_MIN_THRESHOLD = 0.35;
	private static final double MIDDLE_MAX_THRESHOLD = 0.4;//0.5;

	private static final double UPPER_MIN_THRESHOLD = 0.7;
	private static final double UPPER_MAX_THRESHOLD = 0.75;//0.85;

	private static final double LOWER_CAVE_MIN_THRESHOLD = 0.4;
	private static final double LOWER_CAVE_MAX_THRESHOLD = 0.6;

	private static final double MIDDLE_CAVE_MIN_THRESHOLD = 0.8;//0.7;
	private static final double MIDDLE_CAVE_MAX_THRESHOLD = 0.95;

	private static final int MIN_OCEAN_FLOOR_Y = 24;
	private static final int MAX_OCEAN_FLOOR_Y = 48;

	public static final int SEA_LEVEL = 62;

	private static final int MIN_SHORE_Y = SEA_LEVEL;
	private static final int MAX_SHORE_Y = 72;

	private static final int MIN_MIDDLE_BOTTOM_Y = 80;
	private static final int MAX_MIDDLE_BOTTOM_Y = 96;

	private static final int MIN_MIDDLE_TOP_Y = 100;
	public static final int MAX_MIDDLE_TOP_Y = 112;

	private static final int MIN_UPPER_BOTTOM_Y = 128;
	private static final int MAX_UPPER_BOTTOM_Y = 140;

	private static final int MIN_UPPER_TOP_Y = 144;
	public static final int MAX_UPPER_TOP_Y = 160;

	private static final double HVAL_LIMIT_EDGE = 0.5;

	private static final Interpolation EDGE_BLENDING = new Interpolation(false).addPoint(0, 1).addPoint(0.05, 0.925).addPoint(0.1, 0.85).addPoint(0.3, 0.8).addPoint(0.33, 0.6).addPoint(0.55, 0.5).addPoint(0.7, 0.35).addPoint(0.75, 0.2).addPoint(0.85, 0.075).addPoint(1, 0);

	private static final double ANGLE_SEARCH_STEP = 7.5;

	private static final BlockKey STONE = new BlockKey(Blocks.stone, 0);
	private static final BlockKey DIRT = new BlockKey(Blocks.dirt, 0);
	private static final BlockKey GRASS = new BlockKey(Blocks.grass, 0);

	private static final BlockKey BIOME_STONE = STONE;//new BlockKey(ChromaBlocks.CLIFFSTONE.getBlockInstance(), Variants.STONE.getMeta(false, false));
	private static final BlockKey BIOME_DIRT = DIRT;//new BlockKey(ChromaBlocks.CLIFFSTONE.getBlockInstance(), Variants.DIRT.getMeta(false, false));
	private static final BlockKey BIOME_GRASS = GRASS;//new BlockKey(ChromaBlocks.CLIFFSTONE.getBlockInstance(), Variants.GRASS.getMeta(false, false));

	public GlowingCliffsColumnShaper(long seed) {
		seed += ChromaOptions.LUMCLIFFSEEDSHIFT.getValue();
		landmassControl = new SimplexNoiseGenerator(seed).setFrequency(1D/*/128D*//*/192D*//*/160D*//144D).addOctave(2.5, 0.5).addOctave(6, 0.125);
		landmassControl.clampEdge = true;

		shoreHeight = new SimplexNoiseGenerator(~seed).setFrequency(1/32D);
		dirtThickness = new SimplexNoiseGenerator(-seed).setFrequency(1/8D);
		oceanDepth = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsRight(seed, 16)).setFrequency(1/32D);
		caveCeilNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 16)).setFrequency(1/16D);

		upperPlateauTop = new SimplexNoiseGenerator(seed*4+8192).setFrequency(1/24D);
		upperPlateauBottom = new SimplexNoiseGenerator(seed*8+4096).setFrequency(1/24D);
		upperPlateauEdge = new SimplexNoiseGenerator(seed*2+65536).setFrequency(1/24D);
		middlePlateauCaveDepth = new SimplexNoiseGenerator(seed*16+32).setFrequency(1/24D);

		middlePlateauTop = new SimplexNoiseGenerator(-seed*4+4096).setFrequency(1/32D);
		middlePlateauBottom = new SimplexNoiseGenerator(-seed*8+65536).setFrequency(1/32D);
		middlePlateauEdge = new SimplexNoiseGenerator(-seed*2+16384).setFrequency(1/32D);
		lowerPlateauCaveDepth = new SimplexNoiseGenerator(-seed*16+32).setFrequency(1/32D);

		//islandLowerNoise = new SimplexNoiseGenerator(~seed+800).setFrequency(1/16D);
		//islandUpperNoise = new SimplexNoiseGenerator(~seed-2000).setFrequency(1/160D);
		//islandWaterNoise = new SimplexNoiseGenerator(~seed*2).setFrequency(1/32D);
		//islandRiverNoise = new SimplexNoiseGenerator(~seed*4).setFrequency(1/32D);
	}

	@Override
	protected void generateColumn(World world, int x, int z, Random rand, BiomeGenBase biome) {
		//ReikaJavaLibrary.pConsole("Genning "+x+", "+z+" with arrays S="+blocks.length);

		int dirtt = (int)ReikaMathLibrary.normalizeToBounds(dirtThickness.getValue(x, z), 1, 4);
		double hval = this.calcHval(world, x, z, biome);
		GlowCliffRegion r = this.getRegion(world, x, z, biome);
		double middlethresh = r == GlowCliffRegion.WATER ? 0 : this.calcMiddleThresh(x, z);
		double topthresh = r == GlowCliffRegion.WATER ? 0 : this.calcTopThresh(x, z);

		switch(r) {
			case WATER:
				this.generateWater(world, x, z, biome, rand, hval, dirtt);
				break;
			case SHORES:
				this.generateLowPlateau(world, x, z, biome, rand, middlethresh, hval, dirtt);
				break;
			case PLATEAU: {
				double cave = ReikaMathLibrary.normalizeToBounds(lowerPlateauCaveDepth.getValue(x, z), LOWER_CAVE_MIN_THRESHOLD, LOWER_CAVE_MAX_THRESHOLD);
				this.generateMidPlateau(world, x, z, biome, rand, hval < cave, middlethresh, cave, hval, dirtt);
				break;
			}
			case HIGH_PLATEAU: {
				double cave = ReikaMathLibrary.normalizeToBounds(middlePlateauCaveDepth.getValue(x, z), MIDDLE_CAVE_MIN_THRESHOLD, MIDDLE_CAVE_MAX_THRESHOLD);
				this.generateUpperPlateau(world, x, z, biome, rand, hval < cave, topthresh, cave, hval, dirtt);
				break;
			}
		}

		//this.generateIslands(world, x, z, rand);

		this.cleanColumn(world, x, z, biome);

		/* not necessary for some reason
		int bedrock = rand.nextInt(5);
		for (int i = 0; i <= bedrock; i++) {
			this.setBlock(x, i, z, Blocks.bedrock);
		}
		 */
	}

	/*
	private void generateIslands(World world, int x, int z, Random rand) {
		int y = 144;
		double y1 = islandLowerNoise.getValue(x, z);
		double y2 = islandUpperNoise.getValue(x, z);
		if (y2 > y1) {
			y2 *= 10;
			y1 *= 10;
			for (int i = (int)y1; i <= y2; i++) {
				this.setBlock(x, y+i, z, Blocks.stone);
			}
		}
	}
	 */
	public void blendEdge(World world, int x, int z, Block[] blockArray, byte[] metaArray) {
		Object[] f = this.getInvertedDistanceFactor(world, x, z, /*64*/16);
		if (f == null)
			return;

		int y = this.calcTop(blockArray, x, z);
		int top = this.getBlendedHeight(world, x, y, z, (double)f[0], (Coordinate)f[1]);
		int posIndex = this.calcPosIndex(x, z);
		if (y < top) {
			for (int i = y+1; i <= top; i++) {
				int idx = posIndex+i;
				blockArray[idx] = Blocks.stone;
				metaArray[idx] = 0;
			}
		}
		else if (y > top) {
			for (int i = y; i > top; i--) {
				int idx = posIndex+i;
				blockArray[idx] = i <= SEA_LEVEL ? Blocks.water : Blocks.air;
				metaArray[idx] = 0;
			}
		}
		//ReikaJavaLibrary.pConsole("Blending @ "+x+", "+z+", from "+y+" to "+top);
	}

	private int calcTop(Block[] blockArray, int x, int z) {
		int posIndex = this.calcPosIndex(x, z);
		for (int y = 255; y >= 0; y--) {
			int idx = posIndex+y;
			if (blockArray[idx] != null && blockArray[idx] != Blocks.air && blockArray[idx].getMaterial() != Material.air && blockArray[idx] != Blocks.water && blockArray[idx].getMaterial() != Material.water)
				return y;
		}
		return 0;
	}

	private double calcTopThresh(int x, int z) {
		return ReikaMathLibrary.normalizeToBounds(upperPlateauEdge.getValue(x, z), UPPER_MIN_THRESHOLD, UPPER_MAX_THRESHOLD);
	}

	private double calcMiddleThresh(int x, int z) {
		return ReikaMathLibrary.normalizeToBounds(middlePlateauEdge.getValue(x, z), MIDDLE_MIN_THRESHOLD, MIDDLE_MAX_THRESHOLD);
	}

	private double calcHval(World world, int x, int z, BiomeGenBase b) {
		double hval = ReikaMathLibrary.normalizeToBounds(landmassControl.getValue(x, z), 0, this.getHvalLimit(world, x, z, b));

		/*
		if (b == ChromatiCraft.glowingcliffs && referenceValue > 0) {
			return referenceValue;
		}
		 */

		//if (hval > SHORELINE_THRESHOLD) {
		BlendPoint f = this.getDistanceFactor(world, x, z, 24, b); //was 48
		if (f != null) {
			//double dh = SHORELINE_THRESHOLD+0.125*(MIDDLE_MIN_THRESHOLD-SHORELINE_THRESHOLD);
			//hval = (hval-dh)*f+dh;
			double dm = f.biome == ChromatiCraft.glowingcliffsEdge ? /*HVAL_LIMIT_EDGE*/this.calcHval(world, f.xCoord, f.zCoord, f.biome/*, hval*/) : this.getBlendedBiomeHeight(f);
			double dh = hval-dm;
			//double ds = 0.5;
			hval = dm+dh*f.distanceFraction;//(f.distanceFraction*ds+(1-ds));
		}
		//}

		/*
		if (b == ChromatiCraft.glowingcliffsEdge) { //blending up from edge to main biome
			Object[] fe = this.getInvertedDistanceFactor(world, x, z, 24);
			if (fe != null) {
				Coordinate c = (Coordinate)fe[1];
				double f2 = EDGE_BLENDING.getValue((double)fe[0]);
				hval = f2*this.calcHval(world, c.xCoord, c.zCoord, ChromatiCraft.glowingcliffs, referenceValue)+(1-f2)*hval;
			}
		}
		 */

		return hval;
	}

	private double getBlendedBiomeHeight(BlendPoint f) {
		if (ChromaOptions.BIOMEBLEND.getState()) {
			if (this.isBiomeOceanic(f.biome))
				return SHORELINE_THRESHOLD*0.25;
			else if (ModList.SATISFORESTRY.isLoaded() && SFAPI.biomeHandler.isPinkForest(f.biome))
				return MIDDLE_MAX_THRESHOLD+0.1;//this.calcMiddleThresh(f.xCoord, f.zCoord);
			return SHORELINE_THRESHOLD;
		}
		else {
			return SHORELINE_THRESHOLD*0.75;
		}
	}

	private double getHvalLimit(World world, int x, int z, BiomeGenBase b) {
		return 1;//b == ChromatiCraft.glowingcliffsEdge ? HVAL_LIMIT_EDGE : 1;
	}

	private boolean isBiomeOceanic(BiomeGenBase b) {
		return BiomeManager.oceanBiomes.contains(b);
	}

	private int getBlendedHeight(World world, int x, int y, int z, double f, Coordinate loc) {
		double f2 = EDGE_BLENDING.getValue(f);
		return (int)(f2*this.calcIntendedHeight(world, loc.xCoord, loc.zCoord)+(1-f2)*y);
	}

	private double calcIntendedHeight(World world, int x, int z) {
		double hval = this.calcHval(world, x, z, ChromatiCraft.glowingcliffsEdge);
		double middle = this.calcMiddleThresh(x, z);
		double top = this.calcTopThresh(x, z);
		if (hval < SHORELINE_THRESHOLD) {
			return this.getOceanFloor(x, z, hval)-1;
		}
		else if (hval == SHORELINE_THRESHOLD) {
			return SEA_LEVEL;
		}
		else if (hval < middle) {
			return this.getLowPlateauHeight(hval, middle, x, z);
		}
		else if (hval < top) {
			return this.getMidPlateauHeight(x, z);
		}
		else {
			return this.getTopPlateauHeight(x, z);
		}
	}

	private Object[] getInvertedDistanceFactor(World world, int x, int z, int search) {
		int mind = Integer.MAX_VALUE;
		int look = search;
		//ScaledCubeDirection result = null;
		Coordinate result = null;
		for (int d = 1; d <= look; d += 4) {
			//for (int i = 0; i < CubeDirections.list.length; i++) {
			//CubeDirections dir = CubeDirections.list[i];
			for (double a = 0; a < 360; a += ANGLE_SEARCH_STEP) {
				int dx = MathHelper.floor_double(x+d*Math.cos(Math.toRadians(a)));
				int dz = MathHelper.floor_double(z+d*Math.sin(Math.toRadians(a)));
				//if (d*dir.projectionFactor <= look) {
				//int dx = x+dir.directionX*d;
				//int dz = z+dir.directionZ*d;
				BiomeGenBase b = this.getBiome(world, dx, dz);

				if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
					//d *= dir.projectionFactor;
					if (d < mind) {
						mind = Math.min(mind, d);
						//result = new ScaledCubeDirection(dir, d);
						result = new Coordinate(dx, 0, dz);
						look = mind-1;
					}
					break;
				}
				//}
			}
		}
		return mind == Integer.MAX_VALUE ? null : new Object[]{mind/(double)search, result};
	}

	private BlendPoint getDistanceFactor(World world, int x, int z, int search, BiomeGenBase biome) {
		BlendPoint ret = null;
		int look = search;
		for (int d = 1; d <= look; d++) {
			//for (int i = 0; i < CubeDirections.list.length; i++) {
			//CubeDirections dir = CubeDirections.list[i];
			for (double a = 0; a < 360; a += ANGLE_SEARCH_STEP) {
				int dx = MathHelper.floor_double(x+d*Math.cos(Math.toRadians(a)));
				int dz = MathHelper.floor_double(z+d*Math.sin(Math.toRadians(a)));
				//if (d*dir.projectionFactor <= look) {
				//int dx = x+dir.directionX*d;
				//int dz = z+dir.directionZ*d;
				BiomeGenBase b = this.getBiome(world, dx, dz);

				if (b != ChromatiCraft.glowingcliffs && b != biome) { //NOT isGlowingCliffs()
					//d *= dir.projectionFactor;
					if (ret == null || d < ret.distance) {
						ret = new BlendPoint(d, (double)d/search, dx, dz, b);
						look = d-1;
					}
					break;
				}
				//}
			}
		}
		return ret;
	}

	private BiomeGenBase getBiome(World world, int x, int z) {
		return world.getWorldChunkManager().getBiomeGenAt(x, z);
	}

	private void generateLowPlateau(World world, int x, int z, BiomeGenBase biome, Random rand, double middlethresh, double hval, int dirt) {
		double cave = ReikaMathLibrary.normalizeToBounds(lowerPlateauCaveDepth.getValue(x, z), LOWER_CAVE_MIN_THRESHOLD, LOWER_CAVE_MAX_THRESHOLD);
		int top = this.getLowPlateauHeight(hval, cave, x, z);
		this.generateLandColumn(world, x, z, biome, rand, dirt, top, Integer.MAX_VALUE, Integer.MIN_VALUE);
	}

	private int getLowPlateauHeight(double hval, double max, int x, int z) {
		return (int)ReikaMathLibrary.linterpolate(hval, SHORELINE_THRESHOLD, max, MIN_SHORE_Y, MAX_SHORE_Y);
	}

	private void generateMidPlateau(World world, int x, int z, BiomeGenBase biome, Random rand, boolean cave, double thresh, double caveThresh, double hval, int dirt) {
		int top = this.getMidPlateauHeight(x, z);
		int caveFloor = Integer.MAX_VALUE;
		int caveCeil = Integer.MIN_VALUE;
		if (cave) {
			caveFloor = (int)ReikaMathLibrary.linterpolate(hval, SHORELINE_THRESHOLD, caveThresh, MIN_SHORE_Y, MAX_SHORE_Y);
			caveCeil = (int)ReikaMathLibrary.normalizeToBounds(middlePlateauBottom.getValue(x, z), MIN_MIDDLE_BOTTOM_Y, MAX_MIDDLE_BOTTOM_Y);

			int baseCeil = caveCeil;

			/*
			double dh = caveCeil-caveFloor;
			double d = ReikaMathLibrary.normalizeToBounds(Math.abs(hval-LOWER_CAVE_MAX_THRESHOLD), 0, 1, 0, LOWER_CAVE_MAX_THRESHOLD-LOWER_CAVE_MIN_THRESHOLD);
			caveCeil = caveFloor+(int)(d*dh);
			 */

			double dh = caveCeil-caveFloor;
			dh *= ReikaMathLibrary.normalizeToBounds(caveCeilNoise.getValue(x, z), 0.8, 1.2);
			//double d = Math.abs(hval-caveThresh);
			//d = ReikaMathLibrary.normalizeToBounds(d, 0.125, 1, 0, caveThresh-thresh);
			//d = ReikaMathLibrary.ellipticalInterpolation(d, 0, caveThresh-thresh, 0.125, 1);
			caveCeil = caveFloor+4+(int)ReikaMathLibrary.ellipticalInterpolation(Math.abs(hval-thresh), 0, caveThresh-thresh, 0, dh);
			//ReikaJavaLibrary.pConsole("["+caveFloor+", "+baseCeil+"] ("+dh+") @ "+hval+" of ["+thresh+", "+caveThresh+"] {"+Math.abs(hval-thresh)+", "+(caveThresh-thresh)+"} > "+caveCeil);
			//caveCeil = caveFloor+(int)(d*dh);

			dh = top-baseCeil;
			//d = hval-thresh;
			//d = ReikaMathLibrary.normalizeToBounds(d, 0.125, 1, 0, caveThresh-thresh);
			//d = ReikaMathLibrary.ellipticalInterpolation(d, 0, caveThresh-thresh, 0.125, 1);
			top = baseCeil+2+(int)ReikaMathLibrary.ellipticalInterpolation(hval-caveThresh, 0, caveThresh-thresh, 0, dh);
			//top = caveCeil+(int)(d*dh);
		}
		this.generateLandColumn(world, x, z, biome, rand, dirt, top, caveFloor, caveCeil);
	}

	private int getMidPlateauHeight(int x, int z) {
		return (int)ReikaMathLibrary.normalizeToBounds(middlePlateauTop.getValue(x, z), MIN_MIDDLE_TOP_Y, MAX_MIDDLE_TOP_Y);
	}

	private void generateUpperPlateau(World world, int x, int z, BiomeGenBase biome, Random rand, boolean cave, double thresh, double caveThresh, double hval, int dirt) {
		int top = this.getTopPlateauHeight(x, z);
		int caveFloor = Integer.MAX_VALUE;
		int caveCeil = Integer.MIN_VALUE;
		if (cave) {
			caveFloor = (int)ReikaMathLibrary.normalizeToBounds(middlePlateauTop.getValue(x, z), MIN_MIDDLE_TOP_Y, MAX_MIDDLE_TOP_Y);
			caveCeil = (int)ReikaMathLibrary.normalizeToBounds(upperPlateauBottom.getValue(x, z), MIN_UPPER_BOTTOM_Y, MAX_UPPER_BOTTOM_Y);

			int baseCeil = caveCeil;

			double dh = caveCeil-caveFloor;
			dh *= ReikaMathLibrary.normalizeToBounds(caveCeilNoise.getValue(x, z), 0.8, 1.2);
			//double d = Math.abs(hval-/*MIDDLE_CAVE_MAX_THRESHOLD*/caveThresh);
			//d = ReikaMathLibrary.normalizeToBounds(d, 0.125, 1, 0, /*MIDDLE_CAVE_MAX_THRESHOLD*/caveThresh-/*MIDDLE_CAVE_MIN_THRESHOLD*/thresh);
			caveCeil = caveFloor+4+(int)ReikaMathLibrary.ellipticalInterpolation(Math.abs(hval-thresh), 0, caveThresh-thresh, 0, dh);
			//double d = ReikaMathLibrary.linterpolate(hval, MIDDLE_CAVE_MIN_THRESHOLD, MIDDLE_CAVE_MAX_THRESHOLD, 0, 1);
			//caveCeil = caveFloor+(int)(d*dh);

			//double d = MIDDLE_CAVE_MAX_THRESHOLD-hval;
			//caveCeil = (int)ReikaMathLibrary.linterpolate(d, MIDDLE_CAVE_MIN_THRESHOLD, MIDDLE_CAVE_MAX_THRESHOLD, caveFloor, caveCeil);

			dh = top-baseCeil;
			//d = hval-thresh;
			//d = ReikaMathLibrary.normalizeToBounds(d, 0.125, 1, 0, caveThresh-thresh);
			top = baseCeil+2+(int)ReikaMathLibrary.ellipticalInterpolation(hval-caveThresh, 0, caveThresh-thresh, 0, dh);
			//top = caveCeil+(int)(d*dh);
		}
		this.generateLandColumn(world, x, z, biome, rand, dirt, top, caveFloor, caveCeil);
	}

	private int getTopPlateauHeight(int x, int z) {
		return (int)ReikaMathLibrary.normalizeToBounds(upperPlateauTop.getValue(x, z), MIN_UPPER_TOP_Y, MAX_UPPER_TOP_Y);
	}

	private void generateWater(World world, int x, int z, BiomeGenBase biome, Random rand, double hval, int dirtt) {
		int floor = this.getOceanFloor(x, z, hval);
		BlockKey stone = this.getStone(biome);
		BlockKey dirt = this.getDirt(biome);
		for (int i = 0; i < floor; i++) {
			this.setBlock(x, i, z, stone.blockID, stone.hasMetadata() ? stone.metadata : 0);
		}
		for (int i = floor; i <= SEA_LEVEL; i++) {
			this.setBlock(x, i, z, Blocks.water);
		}
		for (int i = SEA_LEVEL+1; i < 256; i++) {
			this.setBlock(x, i, z, Blocks.air);
		}
		for (int h = 1; h <= dirtt; h++) {
			this.setBlock(x, floor-h, z, dirt.blockID, dirt.hasMetadata() ? dirt.metadata : 0);
		}
	}

	/** Is water at y=floor */
	private int getOceanFloor(int x, int z, double hval) {
		int min = (int)ReikaMathLibrary.normalizeToBounds(oceanDepth.getValue(x, z), MIN_OCEAN_FLOOR_Y, MAX_OCEAN_FLOOR_Y);
		int floor = (int)ReikaMathLibrary.linterpolate(hval, 0, SHORELINE_THRESHOLD, min, SEA_LEVEL+1);
		return floor;
	}

	private void generateLandColumn(World world, int x, int z, BiomeGenBase biome, Random rand, int dirt, int top, int caveFloor, int caveCeil) {
		for (int i = 0; i < 256; i++) {
			BlockKey b = this.getStone(biome);
			if (i > top || (i > caveFloor && i < caveCeil))
				b = BlockKey.AIR;
			else if (i == top || (i == caveFloor && i < caveCeil)) {
				b = this.getGrass(biome);
			}
			else if (i >= top-dirt || (i >= caveFloor-dirt && i < caveCeil)) {
				b = this.getDirt(biome);
			}
			this.setBlock(x, i, z, b.blockID, b.hasMetadata() ? b.metadata : 0);
		}
		if (caveFloor > 0 && caveFloor < 256) {
			//if (ReikaBlockHelper.isDirtType(biome.topBlock, 0)) {
			if (ReikaRandomHelper.doWithChance(6)) {
				this.setBlock(x, caveFloor+1, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.GLOWDAISY.ordinal());
			}
			else if (ReikaRandomHelper.doWithChance(0.008)) {
				this.setBlock(x, caveFloor+1, z, Blocks.sapling);
			}

			if (ReikaRandomHelper.doWithChance(0.003)) {
				this.setBlock(x, caveCeil-1, z, ChromaBlocks.TIEREDPLANT.getBlockInstance(), TieredPlants.CAVE.ordinal());
			}
			else if (ReikaRandomHelper.doWithChance(0.008)) {
				int nt = Math.min(ReikaRandomHelper.getRandomBetween(1, 4), caveCeil-caveFloor-3);
				for (int i = 1; i <= nt; i++)
					this.setBlock(x, caveCeil-i, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.GLOWROOT.ordinal());
			}
			//}
		}
	}

	@Override
	protected BlockKey getStone(BiomeGenBase biome) {
		return BiomeGlowingCliffs.isGlowingCliffs(biome) ? BIOME_STONE : STONE;
	}

	@Override
	protected BlockKey getDirt(BiomeGenBase biome) {
		return BiomeGlowingCliffs.isGlowingCliffs(biome) ? BIOME_DIRT : DIRT;
	}

	@Override
	protected BlockKey getGrass(BiomeGenBase biome) {
		return BiomeGlowingCliffs.isGlowingCliffs(biome) ? BIOME_GRASS : GRASS;
	}

	@Override
	protected boolean isPlant(Block b) {
		return b == ChromaBlocks.DECOFLOWER.getBlockInstance() || b == ChromaBlocks.TIEREDPLANT.getBlockInstance() || b == Blocks.sapling;
	}

	public GlowCliffRegion getRegion(World world, int x, int z, BiomeGenBase b) {
		double hval = this.calcHval(world, x, z, b);
		if (hval < SHORELINE_THRESHOLD) {
			return GlowCliffRegion.WATER;
		}
		else {
			double middlethresh = this.calcMiddleThresh(x, z);
			double topthresh = this.calcTopThresh(x, z);
			if (hval < middlethresh) {
				return GlowCliffRegion.SHORES;
			}
			else if (hval < topthresh) {
				return GlowCliffRegion.PLATEAU;
			}
			else {
				return GlowCliffRegion.HIGH_PLATEAU;
			}
		}
	}

	private static class BlendPoint {

		private final int distance;
		private final double distanceFraction;
		private final BiomeGenBase biome;

		private final int xCoord;
		private final int zCoord;

		private BlendPoint(int d, double f, int x, int z, BiomeGenBase b) {
			distance = d;
			distanceFraction = f;
			biome = b;

			xCoord = x;
			zCoord = z;
		}

	}

	public static enum GlowCliffRegion {
		WATER(),
		SHORES(),
		PLATEAU(),
		HIGH_PLATEAU();
	}

}
