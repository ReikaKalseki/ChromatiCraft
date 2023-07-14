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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ModifiableBigTree;
import Reika.DragonAPI.Instantiable.Worldgen.ModifiableSmallTrees;
import Reika.DragonAPI.Interfaces.CustomMapColorBiome;
import Reika.DragonAPI.Interfaces.WinterBiomeStrengthControl;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BoPBlockHandler;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BiomeGlowingCliffs extends BiomeGenBase implements WinterBiomeStrengthControl, CustomMapColorBiome {

	private static final NoiseGeneratorBase hueShift = new SimplexNoiseGenerator(System.currentTimeMillis()).setFrequency(1/8D);
	private static final NoiseGeneratorBase lumShift = new SimplexNoiseGenerator(-System.currentTimeMillis()).setFrequency(1/6D);
	private static final NoiseGeneratorBase waterColorMix = new SimplexNoiseGenerator(~System.currentTimeMillis()).setFrequency(1/10D);
	private static final NoiseGeneratorBase skyColorMix = new SimplexNoiseGenerator(~System.currentTimeMillis()*2).setFrequency(1/20D);
	//private final SimplexNoiseGenerator fogDensityXZ = new SimplexNoiseGenerator(2*System.currentTimeMillis()).setFrequency(1/24D);
	//private final SimplexNoiseGenerator fogDensityXY = new SimplexNoiseGenerator(-4*System.currentTimeMillis()).setFrequency(1/18D);
	//private final SimplexNoiseGenerator fogDensityYZ = new SimplexNoiseGenerator(5*System.currentTimeMillis()).setFrequency(1/18D);

	private static GlowingCliffsColumnShaper terrain;
	private static long worldSeed;
	private static HashMap<Class, Boolean> generatorRules = new HashMap();

	/** Fades between zero and one as the biome is entered or left */
	@SideOnly(Side.CLIENT)
	public static float renderFactor;

	private final WorldGenAbstractTree basicTreeGen = new SlightlyFloweringSmallTree(false); //defaults to oak
	private final WorldGenAbstractTree bigTreeGen = new SlightlyFloweringBigTree(false); //defaults to oak
	private final GlowingTreeGenerator glowTree = new GlowingTreeGenerator();
	private final GlowingTreeGenerator smallGlowTrees = new GlowingTreeGenerator();

	private final List<SpawnListEntry> glowCloudList;

	public BiomeGlowingCliffs(int id, boolean register) {
		super(id, register);

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

		if (ChromaOptions.BIOMEBLEND.getState())
			this.setHeight(new Height(BiomeGenBase.extremeHillsPlus.rootHeight, BiomeGenBase.extremeHillsPlus.heightVariation));
		else
			this.setHeight(new Height(-0.75F, 0));

		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();

		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 5, 4, 4)); //0.5x
		//this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4)); //0x
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 10, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 2, 1, 4)); //2x
		//spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1)); //0x

		spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));

		glowCloudList = ReikaJavaLibrary.makeListFrom(new BiomeGenBase.SpawnListEntry(EntityGlowCloud.class, 30, 1, 1));

		//if (ModList.THAUMCRAFT.isLoaded())
		//	spawnableMonsterList.add(new ModSpawnEntry(ModList.THAUMCRAFT, "thaumcraft.common.entities.monster.EntityWisp", 5, 1, 1).getEntry());
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
	@SideOnly(Side.CLIENT)
	public static void updateRenderFactor(AbstractClientPlayer ep) {
		if (ep == null || ep.worldObj == null) {
			renderFactor = Math.max(0, renderFactor-0.1F);
		}
		else if (BiomeGlowingCliffs.isGlowingCliffs(ep.worldObj.getBiomeGenForCoords(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ)))) {
			renderFactor = Math.min(1, renderFactor+0.025F);
		}
		else {
			renderFactor = Math.max(0, renderFactor-0.0125F);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float temp)  {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		return ReikaColorAPI.getShiftedHue(0xd0a0ff, (float)(15*skyColorMix.getValue(ep.posX, ep.posZ)));
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

	@SideOnly(Side.CLIENT)
	public int getWaterColor(IBlockAccess world, int x, int y, int z, int l) {
		if (isClearWater(world, x, y, z))
			return 0xffffff;
		float f = (float)ReikaMathLibrary.normalizeToBounds(waterColorMix.getValue(x, z), 0, 1);
		return ReikaColorAPI.mixColors(0x22ffbb, 0xff50d0, f);//this.getWaterColorMultiplier();
	}

	@SideOnly(Side.CLIENT)
	public static boolean isClearWater(IBlockAccess world, int x, int y, int z) {
		World w = Minecraft.getMinecraft().theWorld;
		return (y > 65 && ReikaWorldHelper.getWaterDepth(world, x, y, z) <= 3) || world.getBlockMetadata(x, y, z) > 0 || w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) < 2 || ReikaWorldHelper.countAdjacentBlocks(world, x, y, z, Blocks.air, false) >= 2;
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
	public List getSpawnableList(EnumCreatureType type) {
		return type == ChromatiCraft.glowCloudType ? glowCloudList : super.getSpawnableList(type);
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, Block[] arr, byte[] m, int x, int z, double stoneNoise) {
		super.genTerrainBlocks(world, rand, arr, m, x, z, stoneNoise);
		//this.initTerrain(world);
		//terrain.generateColumn(world, x, z, rand, arr, m, this);
	}

	public static void blendTerrainEdgesAndGenCliffs(World world, int chunkX, int chunkZ, Block[] blockArray, byte[] metaArray) {
		initTerrain(world);

		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int x = chunkX*16+i;
				int z = chunkZ*16+k;
				BiomeGenBase b = world.getWorldChunkManager().getBiomeGenAt(x, z);
				if (!BiomeGlowingCliffs.isGlowingCliffs(b)) {
					if (ChromaOptions.BIOMEBLEND.getState()) {
						if (WorldgenProfiler.profilingEnabled())
							WorldgenProfiler.startGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
						terrain.blendEdge(world, x, z, blockArray, metaArray);
						if (WorldgenProfiler.profilingEnabled())
							WorldgenProfiler.onRunGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
					}
				}
				else {
					if (WorldgenProfiler.profilingEnabled())
						WorldgenProfiler.startGenerator(world, "Luminous cliff terrain shaping", chunkX, chunkZ);
					terrain.generateColumn(world, x, z, chunkX, chunkZ, blockArray, metaArray, b);
					if (WorldgenProfiler.profilingEnabled())
						WorldgenProfiler.onRunGenerator(world, "Luminous cliff terrain shaping", chunkX, chunkZ);
				}
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

	public static GlowingCliffsColumnShaper getTerrain(World world) {
		initTerrain(world);
		return terrain;
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		if (rand.nextInt(20) == 0) {
			return glowTree;
		}
		else {
			//return super.func_150567_a(rand);//rand.nextInt(10) == 0 ? worldGeneratorBigTree : worldGeneratorTrees;
			return rand.nextInt(10) == 0 ? bigTreeGen : basicTreeGen;
		}
	}

	public WorldGenAbstractTree getUndergroundTreeGen(Random rand, boolean construct, int bigChance) {
		return rand.nextInt(bigChance) == 0 ? construct ? new GlowingTreeGenerator() : glowTree : construct ? new SmallGlowingTreeGenerator() : smallGlowTrees;
	}

	@Override
	public float getWinterSkyStrength(World world, EntityPlayer ep) {
		return 0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMapColor(World world, int x, int z) {
		return 0x22aaff;
	}

	public static boolean isGlowingCliffs(BiomeGenBase b) {
		return b instanceof BiomeGlowingCliffs || (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.getMystParentBiome(b) instanceof BiomeGlowingCliffs);
	}

	/* No longer callable, nor was it a good idea
	public static void runIWGs(int cx, int cz, World world, IChunkProvider gen, IChunkProvider loader) {
		List<IWorldGenerator> iwgs = ReikaWorldHelper.getModdedGenerators();
		ResettableRandom rand = ReikaWorldHelper.getModdedGeneratorChunkRand(cx, cz, world);

		for (IWorldGenerator generator : iwgs) {
			if (canRunGenerator(generator)) {
				rand.resetSeed();
				generator.generate(rand, cx, cz, world, gen, loader);
			}
		}
	}
	 */

	public static boolean canRunGenerator(IWorldGenerator gen) {
		Class c = gen.getClass();
		Boolean flag = generatorRules.get(c);
		if (flag == null) {
			String s = c.getSimpleName().toLowerCase(Locale.ENGLISH);
			flag = true;
			if (s.contains("slimeisland"))
				flag = false;
			generatorRules.put(c, flag);
		}
		return flag.booleanValue();
	}

	private static class GlowingTreeGenerator extends ModifiableBigTree implements GlowingTreeGen {

		private static final int DEFAULT_GLOW_CHANCE = 16;
		private int glowChance = DEFAULT_GLOW_CHANCE;

		private boolean isGenerating;
		private LinkedList<Integer> glowChanceHistory = new LinkedList();

		public GlowingTreeGenerator() {
			super(false);
		}

		@Override
		public BlockKey getLeafBlock(int x, int y, int z) {
			return BiomeGlowingCliffs.selectGlowingTreeLeaf(rand, glowChance, super.getLeafBlock(x, y, z), true);
		}

		public void setGlowChance(int c) {
			if (isGenerating) {
				glowChanceHistory.addLast(glowChance);
			}
			glowChance = c;
		}

		public void resetGlowChance() {
			glowChance = DEFAULT_GLOW_CHANCE;
		}

		@Override
		public boolean generate(World world, Random rand, int x, int y, int z) {
			boolean wasGenerating = isGenerating;
			isGenerating = true;
			boolean flag = super.generate(world, rand, x, y, z);
			if (!glowChanceHistory.isEmpty()) {
				glowChance = glowChanceHistory.getLast();
			}
			isGenerating = wasGenerating;
			return flag;
		}

	}

	private static class SmallGlowingTreeGenerator extends ModifiableSmallTrees implements GlowingTreeGen {

		private static final int DEFAULT_GLOW_CHANCE = 16;
		private int glowChance = DEFAULT_GLOW_CHANCE;

		private boolean isGenerating;
		private LinkedList<Integer> glowChanceHistory = new LinkedList();

		public SmallGlowingTreeGenerator() {
			super(false);
		}

		public void setGlowChance(int c) {
			if (isGenerating) {
				glowChanceHistory.addLast(glowChance);
			}
			glowChance = c;
		}

		public void resetGlowChance() {
			glowChance = DEFAULT_GLOW_CHANCE;
		}

		@Override
		public BlockKey getLeafBlock(int x, int y, int z) {
			return BiomeGlowingCliffs.selectGlowingTreeLeaf(rand, glowChance, super.getLeafBlock(x, y, z), false);
		}

		@Override
		public boolean generate(World world, Random rand, int x, int y, int z) {
			boolean wasGenerating = isGenerating;
			isGenerating = true;
			boolean flag = super.generate(world, rand, x, y, z);
			if (!glowChanceHistory.isEmpty()) {
				glowChance = glowChanceHistory.getLast();
			}
			isGenerating = wasGenerating;
			return flag;
		}

	}

	private static class SlightlyFloweringSmallTree extends ModifiableSmallTrees {

		public SlightlyFloweringSmallTree(boolean updates) {
			super(updates);
		}

		@Override
		public BlockKey getLeafBlock(int x, int y, int z) {
			return BiomeGlowingCliffs.selectBasicTreeLeaf(rand, super.getLeafBlock(x, y, z), false);
		}
	}

	private static class SlightlyFloweringBigTree extends ModifiableBigTree {

		public SlightlyFloweringBigTree(boolean updates) {
			super(updates);
		}

		@Override
		public BlockKey getLeafBlock(int x, int y, int z) {
			return BiomeGlowingCliffs.selectBasicTreeLeaf(rand, super.getLeafBlock(x, y, z), true);
		}
	}

	public static interface GlowingTreeGen {

		public void setGlowChance(int chance);
		public void resetGlowChance();

	}

	public static BlockKey selectGlowingTreeLeaf(Random rand, int glowChance, BlockKey leafDefault, boolean isBig) {
		if (rand.nextInt(glowChance) == 0)
			return new BlockKey(ChromaBlocks.GLOWLEAF.getBlockInstance());
		return ModList.BOP.isLoaded() && rand.nextInt(isBig ? 20 : 25) == 0 ? BoPBlockHandler.LeafTypes.flowering.getBlock() : leafDefault;
	}

	public static BlockKey selectBasicTreeLeaf(Random rand, BlockKey leafDefault, boolean isBig) {
		return ModList.BOP.isLoaded() && rand.nextInt(isBig ? 15 : 10) == 0 ? BoPBlockHandler.LeafTypes.flowering.getBlock() : leafDefault;
	}

}
