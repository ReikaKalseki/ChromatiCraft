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

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.Simplex3DGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Interfaces.CustomMapColorBiome;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeEnderForest extends BiomeGenForest implements CustomMapColorBiome {

	private static final long root = 127645902378217L;
	private final NoiseGeneratorBase extraX = new Simplex3DGenerator(3571 ^ root).setFrequency(1/4D);
	private final NoiseGeneratorBase extraY = new Simplex3DGenerator(-8247 ^ root).setFrequency(1/4D);
	private final NoiseGeneratorBase extraZ = new Simplex3DGenerator(5648723 ^ root).setFrequency(1/4D);
	private final VoronoiNoiseGenerator colorNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(23657 ^ root).setFrequency(1/12D).setDisplacement(extraX, extraY, extraZ, 6);

	private final WorldGenAbstractTree enderOakLarge = new EnderOakGenerator(3, 7, 5, 12, 3, 5, 0.15F, 6, 0.15F);
	private final WorldGenAbstractTree enderOakSmall = new EnderOakGenerator(2, 4, 3, 5, 2, 3, 0, 0, 0.1F);
	private final WorldGenAbstractTree enderOakNarrow = new EnderOakGenerator(6, 12, 6, 15, 1, 2, 0.35F, 4, 0F);

	private final WeightedRandom<WorldGenAbstractTree> treeTypes = new WeightedRandom();
	private static final Random colorRand = new Random();

	//private HashMap<Coordinate, Integer> colorMap = new HashMap();

	public BiomeEnderForest(int id) {
		super(id, 0);
		//thin the trees a little
		theBiomeDecorator.treesPerChunk *= 0.7;

		this.setDisableRain();

		colorNoise.randomFactor = 0.55;

		biomeName = "Ender Forest";

		spawnableMonsterList.clear();

		//boost Enderman spawn rates relative
		spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 1, 1, 4));
		spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 1, 1, 4));

		treeTypes.addEntry(worldGeneratorTrees, 20);
		treeTypes.addEntry(worldGeneratorBigTree, 4);
		treeTypes.addEntry(enderOakSmall, 50);
		treeTypes.addEntry(enderOakLarge, 10);
		treeTypes.addEntry(enderOakNarrow, 6);
	}

	@Override
	public boolean canSpawnLightningBolt()
	{
		return true;
	}

	@Override
	public float getSpawningChance()
	{
		return 0.1F;
	}

	@Override
	public int getBiomeGrassColor(int x, int y, int z) 	{
		if (ChromaOptions.ENDERCOLORING.getState()) {
			return ReikaColorAPI.RGBtoHex(255, 200, 255);
		}
		else {
			return ReikaColorAPI.mixColors(BiomeGenBase.forest.getBiomeGrassColor(x, y, z), BiomeGenBase.icePlains.getBiomeGrassColor(x, y, z), this.getMix(x, y, z));
		}
	}

	@Override
	public int getBiomeFoliageColor(int x, int y, int z) {
		if (ChromaOptions.ENDERCOLORING.getState()) {
			return ReikaColorAPI.RGBtoHex(255, 150, 255);
		}
		else {
			/*
			Coordinate loc = new Coordinate(x, y, z);
			Integer c = colorMap.get(loc);
			if (c == null) {
				c = ReikaColorAPI.mixColors(BiomeGenBase.forest.getBiomeFoliageColor(x, y, z), BiomeGenBase.icePlains.getBiomeFoliageColor(x, y, z), this.getMix(x, y, z));
				colorMap.put(loc, c);
			}
			return c;
			 */
			return ReikaColorAPI.mixColors(BiomeGenBase.forest.getBiomeFoliageColor(x, y, z), BiomeGenBase.icePlains.getBiomeFoliageColor(x, y, z), this.getMix(x, y, z));
		}
	}

	public void clearColorCache() {
		//colorMap.clear();
	}

	private float getMix(int x, int y, int z) {
		colorRand.setSeed(new Coordinate(x/6+extraX.getValue(x, z)*4, y/4+extraY.getValue(x, z)*2, z/6+extraZ.getValue(x, z)*4).hashCode());
		colorRand.nextBoolean();
		colorRand.nextBoolean();
		return colorRand.nextFloat();
		/*
		DecimalPosition root = colorNoise.getClosestRoot(x, y, z);
		int hash = root.hashCode();
		return ((hash%10+10)%10)/10F;
		 */
	}

	@Override
	public int getWaterColorMultiplier() {
		if (!ChromaOptions.ENDERCOLORING.getState())
			return BiomeGenBase.forest.getWaterColorMultiplier();
		return ReikaColorAPI.RGBtoHex(195, 0, 105);
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new DecoratorEnderForest();
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		if (rand.nextInt(4) > 0) {
			switch(rand.nextInt(2)) {
				case 0:
					if (Flowers.ENDERFLOWER.canPlantAt(world, x, y, z)) {
						world.setBlock(x, y, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.ENDERFLOWER.ordinal(), 3);
					}
					break;
				case 1:
					if (Flowers.RESOCLOVER.canPlantAt(world, x, y, z)) {
						world.setBlock(x, y, z, ChromaBlocks.DECOFLOWER.getBlockInstance(), Flowers.RESOCLOVER.ordinal(), 3);
					}
					break;
			}
		}
		else {
			super.plantFlower(world, rand, x, y, z);
		}
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		treeTypes.setRNG(rand);
		return treeTypes.getRandomEntry();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMapColor(World world, int x, int z) {
		return 0xC872DB;
	}

}
