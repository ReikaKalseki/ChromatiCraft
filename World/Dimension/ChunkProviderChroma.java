/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Entity.EntitySkyRiver;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.TieredWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.SkyRiverGenerator.RiverPoint;
import Reika.ChromatiCraft.World.Dimension.MapGen.MapGenCanyons;
import Reika.ChromatiCraft.World.Dimension.MapGen.MapGenTendrils;
import Reika.ChromatiCraft.World.Dimension.Structure.MonumentGenerator;
import Reika.DragonAPI.Instantiable.Data.BumpMap;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class ChunkProviderChroma implements IChunkProvider {

	/*
	private static BumpMap[] bumpMaps = new BumpMap[16];

	static {
		for (int i = 0; i < 16; i++) {
			bumpMaps[i] = new BumpMap(ChromatiCraft.class, "Textures/Dimension Bump Maps/"+CrystalElement.elements[i].name().toLowerCase()+".png");
		}
	}
	 */

	/** RNG. */
	private Random rand;
	private final long overWorldSeed;
	private final long randomSeed;

	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorPerlin noiseGen4;
	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen5;
	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen6;
	private NoiseGeneratorOctaves mobSpawnerNoise;

	/** Reference to the World object. */
	private World worldObj;
	/** are map structures going to be generated (e.g. strongholds) */
	private WorldType worldType;

	private final double[] field_147434_q;
	private final float[] parabolicField;
	private double[] stoneNoise = new double[256];
	/** The biomes that are used to generate the chunk */
	private BiomeGenBase[] biomesForGeneration;
	private double[] noiseData3;
	private double[] noiseData1;
	private double[] noiseData2;
	private double[] noiseData6;
	private int[][] field_73219_j = new int[32][32];

	private final MapGenCanyons canyonGen = new MapGenCanyons();
	private final MapGenTendrils caveGenerator = new MapGenTendrils();

	private final ChromaChunkManager chunkManager;
	private final BiomeTerrainProvider terrainManager;

	private final OneWayList<ChromaWorldGenerator> decorators = new OneWayList();

	public static final int VERTICAL_OFFSET = 40;

	//private final HashSet<ChunkCoordIntPair> populatedChunks = new HashSet();

	//private final HashSet<CrystalElement> unusedColors = new HashSet();
	//private final HashSet<DimensionStructureType> unusedTypes = new HashSet();
	static final HashSet<StructurePair> structures = new HashSet();
	static final MonumentGenerator monument = new MonumentGenerator();

	private static int generationFlags = 0;

	public void clearCaches() {
		//populatedChunks.clear();
		/*
		for (int i = 0; i < 16; i++) {
			unusedColors.add(CrystalElement.elements[i]);
		}
		for (int i = 0; i < DimensionStructureType.types.length; i++) {
			unusedTypes.add(DimensionStructureType.types[i]);
		}*/
		this.regenerateGenerators();
	}

	public static void triggerGenerator(ThreadedGenerators gen) {
		regenerateGenerators(~gen.getBit());
	}

	public static void regenerateGenerators() {
		regenerateGenerators(0);
	}

	private static void regenerateGenerators(int invflags) {
		long seed = System.currentTimeMillis();
		generationFlags = ReikaMathLibrary.getNBitflags(ThreadedGenerators.generators.length) & ~invflags;
		if ((invflags & ThreadedGenerators.STRUCTURE.getBit()) == 0) {
			for (StructurePair s : structures)
				s.generator.clear();
			structures.clear();
			monument.clear();
		}
		for (int i = 0; i < ThreadedGenerators.generators.length; i++) {
			ThreadedGenerators gen = ThreadedGenerators.generators[i];
			if ((invflags & gen.getBit()) == 0)
				gen.run(seed);
		}
	}

	static void finishGeneration(ThreadedGenerators gen) {
		generationFlags = generationFlags & ~gen.getBit();
		if (generationFlags == 0)
			ThreadedGenerators.reset();
	}

	public static boolean isGeneratorReady(ThreadedGenerators g) {
		return (generationFlags & g.getBit()) == 0;
	}

	public static boolean areGeneratorsReady() {
		return generationFlags == 0;
	}

	public static Set<StructurePair> getStructures() {
		return Collections.unmodifiableSet(structures);
	}

	public static MonumentGenerator getMonumentGenerator() {
		return monument;
	}

	public ChunkProviderChroma(World world)
	{
		worldObj = world;
		randomSeed = System.currentTimeMillis();
		chunkManager = new ChromaChunkManager(world);
		worldType = WorldType.DEFAULT;//world.getWorldInfo().getTerrainType(); //not that it matters
		overWorldSeed = world.getSeed();
		rand = new Random(randomSeed); //make independent of world seed
		noiseGen1 = new NoiseGeneratorOctaves(rand, 16); //16
		noiseGen2 = new NoiseGeneratorOctaves(rand, 16); //16
		noiseGen3 = new NoiseGeneratorOctaves(rand, 96); //8 //smoothness factor
		noiseGen4 = new NoiseGeneratorPerlin(rand, 4); //4 //grass gen?
		noiseGen5 = new NoiseGeneratorOctaves(rand, 10); //10
		noiseGen6 = new NoiseGeneratorOctaves(rand, 16); //16
		mobSpawnerNoise = new NoiseGeneratorOctaves(rand, 8); //8
		field_147434_q = new double[825];
		parabolicField = new float[25];
		terrainManager = new BiomeTerrainProvider(randomSeed);

		for (int j = -2; j <= 2; ++j)
		{
			for (int k = -2; k <= 2; ++k)
			{
				float f = 10.0F / MathHelper.sqrt_float(j * j + k * k + 0.2F);
				parabolicField[j + 2 + (k + 2) * 5] = f;
			}
		}

		//this.clearCaches();
		this.createDecorators();
	}

	private void createDecorators() {
		decorators.addAll(DimensionGenerators.getSortedList(rand, randomSeed));
	}

	static StructurePair getNearestStructure(int x, int z) {
		double d = Double.POSITIVE_INFINITY;
		StructurePair ret = null;
		for (StructurePair s : structures) {
			ChunkCoordIntPair p = s.generator.getCentralLocation();
			double dx = x-(p.chunkXPos << 4);
			double dz = z-(p.chunkZPos << 4);
			double dd = Math.sqrt(dx*dx+dz*dz);
			if (dd < d) {
				d = dd;
				ret = s;
			}
		}
		return ret;
	}

	static double getDistanceToNearestStructureBlockCoords(int x, int z) {
		double dx = monument.getPosX()-x;
		double dz = monument.getPosZ()-z;
		double d = Math.sqrt(dx*dx+dz*dz);
		for (StructurePair s : structures) {
			ChunkCoordIntPair p = s.generator.getCentralLocation();
			dx = x-(p.chunkXPos << 4);
			dz = z-(p.chunkZPos << 4);
			double dd = Math.sqrt(dx*dx+dz*dz);
			d = Math.min(d, dd);
		}
		return d;
	}

	static double getDistanceToNearestStructureChunkCoords(int chunkX, int chunkZ) {
		double dx = (monument.getPosX() >> 4)-chunkX;
		double dz = (monument.getPosZ() >> 4)-chunkZ;
		double d = Math.sqrt(dx*dx+dz*dz);
		for (StructurePair s : structures) {
			ChunkCoordIntPair p = s.generator.getCentralLocation();
			dx = chunkX-p.chunkXPos;
			dz = chunkZ-p.chunkZPos;
			double dd = Math.sqrt(dx*dx+dz*dz);
			d = Math.min(d, dd);
		}
		return d;
	}

	public DimensionStructureType getStructureType(CrystalElement e) {
		for (StructurePair s : structures) {
			if (s.color == e)
				return s.generator.getType();
		}
		return null;
	}

	public void generateColumnData(int chunkX, int chunkZ, Block[] columnData)
	{
		byte b0 = 63;//32;//63;
		//biomesForGeneration = chunkManager.getBiomesForGeneration(biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
		this.applyNoiseLayers(chunkX * 4, chunkZ * 4);

		for (int k = 0; k < 4; k++) {
			int l = k * 5;
			int i1 = (k + 1) * 5;

			for (int j1 = 0; j1 < 4; j1++) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;

				for (int k2 = 0; k2 < 32; k2++) {
					double d0 = 0.125D;
					double d1 = field_147434_q[k1 + k2];
					double d2 = field_147434_q[l1 + k2];
					double d3 = field_147434_q[i2 + k2];
					double d4 = field_147434_q[j2 + k2];
					double d5 = (field_147434_q[k1 + k2 + 1] - d1) * d0;
					double d6 = (field_147434_q[l1 + k2 + 1] - d2) * d0;
					double d7 = (field_147434_q[i2 + k2 + 1] - d3) * d0;
					double d8 = (field_147434_q[j2 + k2 + 1] - d4) * d0;

					for (int l2 = 0; l2 < 8; l2++) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i3 = 0; i3 < 4; i3++) {
							int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
							short short1 = 256;
							j3 -= short1;
							double d14 = 0.25D;
							double d16 = (d11 - d10) * d14;
							double d15 = d10 - d16;

							for (int k3 = 0; k3 < 4; k3++) {
								if ((d15 += d16) > 0.0D) {
									columnData[j3 += short1] = Blocks.stone;
								}
								else if (k2 * 8 + l2 < b0)
								{
									columnData[j3 += short1] = Blocks.water;
								}
								//else if (k2 * 8 + l2 < b0+1)
								//{
								//	columnData[j3 += short1] = Blocks.sand; //sand beaches
								//}
								else
								{
									columnData[j3 += short1] = null;
								}
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}

		//this.applyRawBumpMapping(chunkX, chunkZ, columnData);
	}

	private void applyRawBumpMapping(int cx, int cz, Block[] data) {
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int d = (dx*16+dz);
				int posIndex = d*data.length/256;
				int x = cx*16+dx;
				int z = cz*16+dz;
				//int bump = this.calculateBlendedBump(x, z, bumpMaps[CrystalElement.CYAN.ordinal()]);
				int bump = -(int)(0.25*Math.abs(Math.tan((x%256)*(x%256)+(z%256)*(z%256))));//(int)(3*Math.sin(x/64D)+4*Math.sin(z/512D)+1*Math.cos(x/16D+z/32D+x*z/128D));

				if (bump != 0) {
					int sy = -1;
					for (int y = 255; y > 0; y--) {
						if (data[y+posIndex] == Blocks.stone) {
							sy = y;
							break;
						}
					}
					//ReikaJavaLibrary.pConsole(x+", "+z+" > "+sy+" by "+bump);
					if (sy >= 0) {
						if (bump > 0) {
							for (int i = 1; i <= bump; i++) {
								data[sy+posIndex+i] = Blocks.stone;
							}
						}
						else {
							bump = -bump;
							for (int i = 0; i < bump; i++) {
								data[sy+posIndex-i] = Blocks.air;//null;
								//ReikaJavaLibrary.pConsole("Setting "+x+", "+(sy-i)+", "+z);
							}
						}
					}
				}
				/*
				for (int y = 0; y < 256; y++) {
					data[posIndex+y] = y > bump ? Blocks.air : Blocks.stone;
				}
				 */
			}
		}
	}

	private int calculateBlendedBump(int x, int z, BumpMap bp) {
		int mx = ((x%512)+512)%512;
		int mz = ((z%512)+512)%512;

		int mxp = (((x+1)%512)+512)%512;
		int mzp = (((z+1)%512)+512)%512;

		int mxm = (((x-1)%512)+512)%512;
		int mzm = (((z-1)%512)+512)%512;

		return 127 + (bp.getBump(mx, mz)+bp.getBump(mxp, mz)+bp.getBump(mxm, mz)+bp.getBump(mx, mzp)+bp.getBump(mx, mzm)) / 5 / 16;
	}

	public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] columnData, byte[] metaData, BiomeGenBase[] biomeData, int dy) {
		double d0 = 0.03125D;
		stoneNoise = noiseGen4.func_151599_a(stoneNoise, chunkX * 16, chunkZ * 16, 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int x = chunkX*16+dx;
				int z = chunkZ*16+dz;
				int d = (dx*16+dz);
				BiomeGenBase biome = biomeData[d];
				int posIndex = d*columnData.length/256;

				this.generateBedrockLayer(x, z, posIndex, columnData, metaData);
				this.generateSandBeaches(x, z, posIndex, columnData, metaData, dy, biome);
				this.generateSurfaceGrass(x, z, posIndex, columnData, metaData, dy, biome);
			}
		}
	}

	private void generateSandBeaches(int x, int z, int posIndex, Block[] columnData, byte[] metaData, int dy, BiomeGenBase biome) {
		Block b = columnData[62+dy+posIndex];
		Block bb = columnData[61+dy+posIndex];
		Block ba = columnData[63+dy+posIndex];
		if (b == Blocks.stone && ba == null && bb == Blocks.stone) {
			columnData[62+dy+posIndex] = Blocks.sand;
		}

		for (int y = 66+dy; y > 0; y--) {
			b = columnData[y+posIndex];
			ba = columnData[y+1+posIndex];
			if (b == Blocks.stone && ba == Blocks.water) {
				columnData[y+posIndex] = Blocks.sand;
			}
		}
	}

	private void generateSurfaceGrass(int x, int z, int posIndex, Block[] columnData, byte[] metaData, int dy, BiomeGenBase biome) {
		int surface = 0;
		int filler = 0;
		int maxSurface = 1;
		int maxFiller = 3+(int)(2*Math.sin(x/4D)+3*Math.sin(z/16D));

		Block surf = Blocks.grass;
		Block fill = Blocks.dirt;

		for (int y = 254; y > 0; y--) {
			Block b = columnData[y+posIndex];
			Block ba = columnData[y+1+posIndex];
			if (b == Blocks.stone && ba == null) {
				if (surface < maxSurface) {
					surface++;
					columnData[y+posIndex] = surf;

					//columnData[y+posIndex] = Blocks.wool;
					//metaData[y+posIndex] = (byte)((biome.biomeID-100)%16);
				}
				else if (filler < maxFiller) {
					filler++;
					columnData[y+posIndex] = fill;
				}
			}
		}
	}

	private void generateBedrockLayer(int x, int z, int posIndex, Block[] columnData, byte[] metaData) {
		double d1 = 1.5-0.1875+rand.nextDouble()*0.1875;
		double d2 = 1-0.125+rand.nextDouble()*0.125;
		double d3 = 0.5-0.0625+rand.nextDouble()*0.0625;
		double d4 = 0.25-0.0625+rand.nextDouble()*0.0625;
		double sx = d1*Math.sin(Math.toRadians((x%64/64D)*360D));
		double sz = d2*Math.cos(Math.PI*1.5+Math.toRadians((z%128/128D)*360D));
		double sxz1 = d3*Math.sin(Math.toRadians(x*z/8D))*Math.cos(Math.toRadians(x*z/16D));
		double sxz2 = d4*Math.cos(Math.toRadians(x*z*z/128D))*Math.sin(Math.toRadians(x*x*z/256D));
		double maxBedrockY = 2.25+sx+sz+sxz1+sxz2;
		if (maxBedrockY > 0)
			columnData[posIndex] = Blocks.bedrock;
		for (int i = 0; i < 4; i++) {
			if (1+i <= maxBedrockY && (rand.nextInt(4) > 0 || rand.nextInt(4-i) > 0))
				columnData[posIndex+1+i] = Blocks.bedrock;
		}
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int x, int p_73158_2_)
	{
		return this.provideChunk(x, p_73158_2_);
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
	 * specified chunk from the map seed and chunk seed
	 */
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
		Block[] ablock = new Block[65536];
		byte[] abyte = new byte[65536];
		biomesForGeneration = new BiomeGenBase[256];//chunkManager.loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int x = chunkX*16+dx;
				int z = chunkZ*16+dz;
				int i = dz*16+dx;
				biomesForGeneration[i] = BiomeDistributor.getBiome(x, z);
			}
		}
		this.generateColumnData(chunkX, chunkZ, ablock);
		ablock = this.shiftTerrainGen(ablock, VERTICAL_OFFSET);
		this.replaceBlocksForBiome(chunkX, chunkZ, ablock, abyte, biomesForGeneration, VERTICAL_OFFSET);

		this.runGenerators(chunkZ, chunkZ, ablock, abyte);

		Chunk chunk = new Chunk(worldObj, ablock, abyte, chunkX, chunkZ);
		byte[] biomeData = chunk.getBiomeArray();

		for (int k = 0; k < biomeData.length; ++k) {
			biomeData[k] = (byte)biomesForGeneration[k].biomeID;
		}

		chunk.generateSkylightMap();

		this.populate(null, chunkX, chunkZ);

		//chunk.isTerrainPopulated = true; //use this to disable all populators

		return chunk;
	}

	private Block[] shiftTerrainGen(Block[] ablock, int dy) {
		Block[] temp = new Block[ablock.length];
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int d = (dx*16+dz);
				int posIndex = d*ablock.length/256;
				for (int j = 255-dy; j >= 0; j--) {
					temp[posIndex+j+dy] = ablock[posIndex+j];
				}
				for (int j = 0; j < dy; j++) {
					temp[posIndex+j] = Blocks.stone;
				}
			}
		}
		return temp;
	}

	private void runGenerators(int chunkX, int chunkZ, Block[] ablock, byte[] abyte) {
		//canyonGen.func_151539_a(this, worldObj, chunkX, chunkZ, ablock);
	}

	private void applyNoiseLayers(int chunkX, int chunkZ)
	{
		double d0 = 684.412D;
		double d1 = 684.412D;
		double d2 = 512.0D;
		double d3 = 512.0D;
		noiseData6 = noiseGen6.generateNoiseOctaves(noiseData6, chunkX, chunkZ, 5, 5, 200.0D, 200.0D, 0.5D);
		noiseData3 = noiseGen3.generateNoiseOctaves(noiseData3, chunkX, 0, chunkZ, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		noiseData1 = noiseGen1.generateNoiseOctaves(noiseData1, chunkX, 0, chunkZ, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		noiseData2 = noiseGen2.generateNoiseOctaves(noiseData2, chunkX, 0, chunkZ, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		boolean flag1 = false;
		boolean flag = false;
		int idx = 0;
		int i1 = 0;
		double d4 = 8.5D;

		for (int j1 = 0; j1 < 5; j1++) {
			for (int k1 = 0; k1 < 5; k1++) {
				float f = 0.0F;
				float f1 = 0.0F;
				float f2 = 0.0F;
				byte b0 = 2;

				for (int l1 = -b0; l1 <= b0; l1++) {
					for (int i2 = -b0; i2 <= b0; i2++) {

						/*
						//normally biome dependent
						float f3 = 0.5F; //avg height
						float f4 = 0.0625F; //height variation

						//These two gives a map that is half plains, half mountains - desirable
						f4 *= 8;
						f3 = 0.125F;

						//V2ery flat, below sea level
						f4 = 0;
						f3 = -0.5F;

						//Low plains, with sizeable hills
						f3 = -1F;
						f4 = 0.125F;

						//very low plains around 33, lots of tall mountains
						f3 = -1F;
						f4 = 1F;
						 */

						//float f3 = -0.25F+0.75F*MathHelper.sin(chunkX/32F)*MathHelper.cos(chunkZ/48F);
						//float f4 = 0.0625F*2*(2+1F*MathHelper.cos(chunkX/96F)+1F*MathHelper.sin(chunkZ/64F));

						//float f3 = -0.25F+(float)Math.sqrt(Math.sin(chunkX/8D)*Math.sin(chunkX/8D)+Math.cos(chunkZ/8D)*Math.cos(chunkZ/8D));
						//float f4 = 0.0625F*(float)Math.sqrt((chunkX*chunkX+chunkZ*chunkZ)/128D);


						//FINAL GENERATION
						float f0 = (float)Math.sqrt((chunkX*chunkX+chunkZ*chunkZ)/(65536D*32));

						//New from BiomeDistributor
						f0 *= 0.03125;

						double dd = this.getDistanceToNearestStructureChunkCoords(chunkX, chunkZ);
						if (dd <= 8) {
							f0 *= dd/8D;
						}
						float f3 = Math.max(-0.25F, 0.125F-f0*0.125F);
						float f4 = 0.5F*f0;

						/*
						//New from BiomeDistributor
						int r = 3;
						WeightedAverage avg = new WeightedAverage();
						for (int i = -r; i <= r; i++) {
							for (int k = -r; k <= r; k++) {
								int dx = (chunkX/4+i)*16+8;
								int dz = (chunkZ/4+k)*16+8;
								ChromaDimensionBiome biome = BiomeDistributor.getBiome(dx, dz);
								int base = biome.getExactType().getBaseHeightDelta();
								avg.addValue(base, 1D/(1D+ReikaMathLibrary.py3d(i, 0, k)));
							}
						}
						f3 *= avg.getAverageValue();
						 */

						//Math.sqrt(chunkX*chunkX+chunkZ*chunkZ)/32F;
						//8*Math.sin(chunkX/256D)+32*Math.cos(chunkZ/512D); - large mountains, interesting terrain
						//4*Math.sin(chunkX/256)+8*Math.cos(chunkZ/512)-4 ^ 2
						//8*Math.sin(chunkX/32D)*Math.cos(chunkX/4D)+8*Math.cos(chunkZ/16D)*Math.sin(chunkZ/8D);
						double offset = 0;
						if (offset > 0)
							f4 *= 1+offset;

						float f5 = parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

						f += f4 * f5;
						f1 += f3 * f5;
						f2 += f5;
					}
				}

				f /= f2;
				f1 /= f2;
				f = f * 0.9F + 0.1F;
				f1 = (f1 * 4.0F - 1.0F) / 8.0F;
				double d12 = noiseData6[i1] / 8000.0D;

				if (d12 < 0.0D)
				{
					d12 = -d12 * 0.3D;
				}

				d12 = d12 * 3.0D - 2.0D;

				if (d12 < 0.0D)
				{
					d12 /= 2.0D;

					if (d12 < -1.0D)
					{
						d12 = -1.0D;
					}

					d12 /= 1.4D;
					d12 /= 2.0D;
				}
				else
				{
					if (d12 > 1.0D)
					{
						d12 = 1.0D;
					}

					d12 /= 8.0D;
				}

				++i1;
				double d13 = f1;
				double d14 = f;
				d13 += d12 * 0.2D;
				d13 = d13 * 8.5D / 8.0D;
				double d5 = 8.5D + d13 * 4.0D;

				for (int j2 = 0; j2 < 33; ++j2) {
					double d6 = (j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

					if (d6 < 0.0D) {
						d6 *= 4.0D;
					}

					double d7 = noiseData1[idx] / 512.0D;
					double d8 = noiseData2[idx] / 512.0D;
					double d9 = (noiseData3[idx] / 10.0D + 1.0D) / 2.0D;
					double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

					if (j2 > 29) {
						double d11 = (j2 - 29) / 3.0F;
						d10 = d10 * (1.0D - d11) + -10.0D * d11;
					}

					//d10 = Math.max(d10-2.5, (d10-5)*8);
					//d10 *= 1+0.5*Math.sin(chunkX*chunkX*chunkZ*chunkZ/256D);

					field_147434_q[idx] = d10;
					++idx;
				}
			}
		}
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int p_73149_1_, int p_73149_2_)
	{
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	public void populate(IChunkProvider loader, int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;
		int k = chunkX * 16;
		int l = chunkZ * 16;
		BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(k + 16, l + 16);
		rand.setSeed(worldObj.getSeed());
		long i1 = rand.nextLong() / 2L * 2L + 1L;
		long j1 = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed(chunkX * i1 + chunkZ * j1 ^ worldObj.getSeed());
		boolean flag = false;

		int k1;
		int l1;
		int i2;

		//controls vanilla ores and things like dirt patches
		//biomegenbase.decorate(worldObj, rand, k, l);

		//if (TerrainGen.populate(loader, worldObj, rand, chunkX, chunkZ, flag, ANIMALS)) {
		//	SpawnerAnimals.performWorldGenSpawning(worldObj, biomegenbase, k + 8, l + 8, 16, 16, rand);
		//}
		k += 8;
		l += 8;

		BlockFalling.fallInstantly = false;
	}

	private void runDecorators(int x, int z) {
		for (ChromaWorldGenerator wg : decorators) {
			ChromaDimensionBiome b = BiomeDistributor.getBiome(x, z);
			if (wg.type.generateIn(b)) {
				float f = wg.getGenerationChance(worldObj, x, z, b);
				int n = (int)f;
				if (ReikaRandomHelper.doWithChance(f-n))
					n++;
				for (int i = 0; i < n; i++) {
					int dx = x + rand.nextInt(16) + 8;
					int dz = z + rand.nextInt(16) + 8;
					int y = worldObj.getTopSolidOrLiquidBlock(dx, dz);
					wg.generate(worldObj, rand, dx, y, dz);
				}
			}
		}
	}

	/*
	private StructurePair getGenStructure() {
		CrystalElement e = unusedColors.isEmpty() ? null : ReikaJavaLibrary.getRandomCollectionEntry(unusedColors);
		unusedColors.remove(e);

		DimensionStructureType gen = unusedTypes.isEmpty() ? null : ReikaJavaLibrary.getRandomCollectionEntry(unusedTypes);
		unusedTypes.remove(gen);

		return e != null && gen != null ? new StructurePair(gen, e) : null;
	}
	 */

	public void onPopulationHook(IChunkProvider gen, IChunkProvider loader, int x, int z) {

		terrainManager.generateChunk(worldObj, x, z, rand);

		this.runDecorators(x*16, z*16);

		this.generateExtraChromaOre(worldObj, gen, loader, x, z);

		ChunkCoordIntPair cp = new ChunkCoordIntPair(x, z);
		for (StructurePair s : structures) {
			//ReikaJavaLibrary.pConsole("Generating chunk "+x+", "+z+" for a "+s.color+" "+s.generator);
			s.generator.generateChunk(worldObj, cp);
		}
		monument.generateChunk(worldObj, cp);
		//for (int i = 0; i < 256; i++) {
		//	worldObj.setBlock(x+8, i, z+8, Blocks.glass);

		Collection<RiverPoint> c = SkyRiverGenerator.getPointsForChunk(x, z);
		if (c != null) {
			for (RiverPoint p : c) {
				EntitySkyRiver e = new EntitySkyRiver(worldObj, p);
				e.setPosition(p.position.xCoord, p.position.yCoord, p.position.zCoord);
				worldObj.spawnEntityInWorld(e);
			}
		}
	}

	private void generateExtraChromaOre(World world, IChunkProvider gen, IChunkProvider loader, int x, int z) {
		for (int i = 0; i < 6; i++) {
			if (i < 3)
				TieredWorldGenerator.instance.skipPlants = true;
			TieredWorldGenerator.instance.generate(rand, x, z, world, gen, loader);
		}
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave()
	{
		return false;
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
	 * Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate ips)
	{
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
	 * unimplemented.
	 */
	public void saveExtraData() {}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString()
	{
		return "RandomLevelSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given location.
	 */
	public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z)
	{
		//BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(x, z);
		//return type == EnumCreatureType.monster && scatteredFeatureGenerator.func_143030_a(x, y, z) ? scatteredFeatureGenerator.getScatteredFeatureSpawnList() : biomegenbase.getSpawnableList(type);
		return new ArrayList();
	}

	public ChunkPosition func_147416_a(World world, String type, int x, int y, int z)
	{
		return null;//"Stronghold".equals(type) && strongholdGenerator != null ? strongholdGenerator.func_151545_a(world, x, y, z) : null;
	}

	public int getLoadedChunkCount()
	{
		return 0;
	}

	public void recreateStructures(int p_82695_1_, int p_82695_2_)
	{
		//mineshaftGenerator.func_151539_a(this, worldObj, p_82695_1_, p_82695_2_, (Block[])null);
		//villageGenerator.func_151539_a(this, worldObj, p_82695_1_, p_82695_2_, (Block[])null);
		//strongholdGenerator.func_151539_a(this, worldObj, p_82695_1_, p_82695_2_, (Block[])null);
		//scatteredFeatureGenerator.func_151539_a(this, worldObj, p_82695_1_, p_82695_2_, (Block[])null);
	}

}
