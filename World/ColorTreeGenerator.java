/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import cpw.mods.fml.common.IWorldGenerator;

public class ColorTreeGenerator implements IWorldGenerator {

	public static final int CHANCE = 128;

	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider cg, IChunkProvider cp) {
		chunkX *= 16;
		chunkZ *= 16;
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		int trees = this.getTreeCount(biome);
		int x = chunkX+r.nextInt(16);
		int z = chunkZ+r.nextInt(16);
		if (this.canGenerateTree(world, x, z)) {
			for (int i = 0; i < trees; i++) {
				if (r.nextInt(CHANCE) == 0) {
					int y = world.getTopSolidOrLiquidBlock(x, z);
					Block b = world.getBlock(x, y, z);
					if (r.nextInt(this.getRainbowChance(world)) == 0) {
						if (RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z)) {
							RainbowTreeGenerator.getInstance().generateRainbowTree(world, x, y, z, r);
						}
						else {
							TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[r.nextInt(16)], false);
						}
					}
					else {
						TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[r.nextInt(16)], false);
					}
				}
			}
		}
	}

	private int getRainbowChance(World world) {
		return world.provider.dimensionId == ReikaTwilightHelper.getDimensionID() ? 16 : 32;
	}

	public static int getTreeCount(BiomeGenBase biome) {
		BiomeDecorator dec = biome.theBiomeDecorator;
		int trees = Math.max(0, dec.treesPerChunk);

		if (biome == BiomeGenBase.plains)
			trees += 2;
		if (biome == BiomeGenBase.forest || biome == BiomeGenBase.forestHills)
			trees += 6;
		if (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHillsEdge)
			trees += 3;
		if (biome == BiomeGenBase.iceMountains || biome == BiomeGenBase.icePlains)
			trees += 3;
		if (biome == BiomeGenBase.jungle)
			trees += 3;
		if (biome == BiomeGenBase.swampland)
			trees += 3;

		if (trees > 0)
			return trees;

		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.FOREST) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.forest), "max");
			}
			if (types[i] == BiomeDictionary.Type.MOUNTAIN) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.extremeHills), "max");
			}
			if (types[i] == BiomeDictionary.Type.JUNGLE) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.jungle), "max");
			}
			if (types[i] == BiomeDictionary.Type.HILLS) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.forestHills), "max");
			}
			if (types[i] == BiomeDictionary.Type.SNOWY) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.icePlains), "max");
			}
			if (types[i] == BiomeDictionary.Type.PLAINS) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.plains), "max");
			}
			if (types[i] == BiomeDictionary.Type.SWAMP) {
				trees = ReikaMathLibrary.extrema(trees, getTreeCount(BiomeGenBase.swampland), "max");
			}
		}
		return trees;
	}

	public static boolean canGenerateTree(World world, int x, int z) {
		if (world.isRemote)
			return false;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (Math.abs(world.provider.dimensionId) == ReikaTwilightHelper.getDimensionID())
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
			return false;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		BiomeDecorator dec = biome.theBiomeDecorator;
		if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean)
			return false;
		if (biome == BiomeGenBase.desert || biome == BiomeGenBase.desertHills || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY))
			return false;
		if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MUSHROOM))
			return false;
		int y = world.getTopSolidOrLiquidBlock(x, z);
		//ReikaJavaLibrary.pConsole(world.getBlock(x, y, z)+","+world.getBlock(x, y-1, z)+","+world.getBlock(x, y-2, z)+":"+BlockDyeSapling.canGrowAt(world, x, y, z));
		return BlockDyeSapling.canGrowAt(world, x, y, z, true);
	}

}
