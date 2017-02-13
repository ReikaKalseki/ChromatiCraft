/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ModSpawnEntry;
import Reika.DragonAPI.Instantiable.Worldgen.ModifiableBigTree;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BiomeGlowingCliffs extends BiomeGenBase {

	private static final SimplexNoiseGenerator hueShift = new SimplexNoiseGenerator(System.currentTimeMillis()).setFrequency(1/8D);
	private static final SimplexNoiseGenerator lumShift = new SimplexNoiseGenerator(-System.currentTimeMillis()).setFrequency(1/6D);
	private static final SimplexNoiseGenerator waterColorMix = new SimplexNoiseGenerator(~System.currentTimeMillis()).setFrequency(1/10D);
	//private final SimplexNoiseGenerator fogDensityXZ = new SimplexNoiseGenerator(2*System.currentTimeMillis()).setFrequency(1/24D);
	//private final SimplexNoiseGenerator fogDensityXY = new SimplexNoiseGenerator(-4*System.currentTimeMillis()).setFrequency(1/18D);
	//private final SimplexNoiseGenerator fogDensityYZ = new SimplexNoiseGenerator(5*System.currentTimeMillis()).setFrequency(1/18D);

	private static GlowingCliffsColumnShaper terrain;
	private static long worldSeed;

	private final GlowingTreeGenerator glowTree = new GlowingTreeGenerator();

	public BiomeGlowingCliffs(int id) {
		super(id);

		biomeName = "Luminous Cliffs";

		theBiomeDecorator = new GlowingCliffsDecorator();

		this.setTemperatureRainfall(0.75F, 0.85F);
		enableRain = false;

		for (int i = 0; i < BlockFlower.field_149859_a.length; i++) {
			flowers.add(new FlowerEntry(Blocks.red_flower, i, 20));
		}
		for (int i = 0; i < BlockFlower.field_149858_b.length; i++) {
			flowers.add(new FlowerEntry(Blocks.yellow_flower, i, 20));
		}

		this.setHeight(new Height(BiomeGenBase.extremeHillsPlus.rootHeight, BiomeGenBase.extremeHillsPlus.heightVariation));

		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();

		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 5, 4, 4)); //0.5x
		//this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4)); //0x
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 2, 1, 4)); //2x
		//spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1)); //0x
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityGlowCloud.class, 30, 1, 4));

		spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
		spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityGlowCloud.class, 30, 1, 1));

		if (ModList.THAUMCRAFT.isLoaded())
			spawnableMonsterList.add(new ModSpawnEntry(ModList.THAUMCRAFT, "thaumcraft.common.entities.monster.EntityWisp", 5, 1, 1).getEntry());
	}
	/*
	public float getFogDensity(double x, double y, double z) {
		double fxz = fogDensityXZ.getValue(x, z);
		double fxy = fogDensityXY.getValue(x, y);
		double fyz = fogDensityYZ.getValue(y, z);
		if (fxz <= 0 || fxy <= 0 || fyz <= 0)
			return 0;
		double f = ReikaMathLibrary.py3d(fxz, fxy, fyz);
		return (float)f;
	}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float temp)  {
		return 0xd0a0ff;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z) {
		int base = BiomeGenBase.forest.getBiomeGrassColor(x, y, z);
		int c = this.shiftHue(base, x, z);
		c = this.shiftBrightness(c, x, y, z);
		return c;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeFoliageColor(int x, int y, int z) {
		int base = BiomeGenBase.forest.getBiomeFoliageColor(x, y, z);
		int c = this.shiftHue(base, x, z);
		c = this.shiftBrightness(c, x, y, z);
		return c;
	}

	@Override
	public int getWaterColorMultiplier() {
		return 0x22ffbb;//0xff70e0;
	}

	public int getWaterColor(IBlockAccess world, int x, int y, int z, int l) {
		float f = (float)ReikaMathLibrary.normalizeToBounds(waterColorMix.getValue(x, z), 0, 1);
		return ReikaColorAPI.mixColors(0x22ffbb, 0xff50d0, f);//this.getWaterColorMultiplier();
	}

	private int shiftBrightness(int base, int x, int y, int z) {
		float f = (float)ReikaMathLibrary.normalizeToBounds(lumShift.getValue(x, z), 0.05F, 0.6F);
		f *= MathHelper.clamp_float((y-64F)/125F, 0, 1);
		return ReikaColorAPI.mixColors(base, 0xffffff, 1-f);
	}

	private int shiftHue(int base, int x, int z) {
		float h = (float)ReikaMathLibrary.normalizeToBounds(hueShift.getValue(x, z), 0, 50);
		//ReikaJavaLibrary.pConsole(x+", "+z+" > "+hueShift.getValue(x, z)+" > "+h);
		return ReikaColorAPI.getShiftedHue(base, h);
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, Block[] arr, byte[] m, int x, int z, double stoneNoise) {
		super.genTerrainBlocks(world, rand, arr, m, x, z, stoneNoise);
		//this.initTerrain(world);
		//terrain.generateColumn(world, x, z, rand, arr, m, this);
	}

	public static void blendTerrainEdges(World world, int chunkX, int chunkZ, Block[] blockArray, byte[] metaArray) {
		initTerrain(world);

		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int x = chunkX*16+i;
				int z = chunkZ*16+k;
				BiomeGenBase b = world.getWorldChunkManager().getBiomeGenAt(x, z);
				if (!BiomeGlowingCliffs.isGlowingCliffs(b))
					terrain.blendEdge(world, x, z, blockArray, metaArray);
				else
					terrain.generateColumn(world, x, z, new Random(chunkX*341873128712L+chunkZ*132897987541L), blockArray, metaArray, b);
			}
		}
	}

	private static void initTerrain(World world) {
		long seed = world.getSeed();
		if (seed != worldSeed || terrain == null) {
			terrain = new GlowingCliffsColumnShaper(seed);
			worldSeed = seed;
		}
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		if (rand.nextInt(20) == 0) {
			return glowTree;
		}
		else {
			return super.func_150567_a(rand);//rand.nextInt(10) == 0 ? worldGeneratorBigTree : worldGeneratorTrees;
		}
	}

	public static boolean isGlowingCliffs(BiomeGenBase b) {
		return b instanceof BiomeGlowingCliffs;
	}

	private static class GlowingTreeGenerator extends ModifiableBigTree {

		public GlowingTreeGenerator() {
			super(false);
		}

		@Override
		public Block getLeafBlock(int x, int y, int z) {
			return rand.nextInt(16) == 0 ? ChromaBlocks.GLOWLEAF.getBlockInstance() : super.getLeafBlock(x, y, z);
		}

		@Override
		public int getLeafMetadata(int x, int y, int z) {
			return super.getLeafMetadata(x, y, z);
		}

	}

}
