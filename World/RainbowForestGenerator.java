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
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.IWG.ColorTreeGenerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.Simplex3DGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;

public class RainbowForestGenerator extends WorldGenerator {

	private static final boolean GENERATE_SMALL_RAINBOW_TREES = true;

	private static final float FERTILE_VINE_CHANCE_FACTOR = 0.12F;//0.1F;

	//private static Simplex3DGenerator colorOffsetNoise;
	private static final long root = 34897534781342377L;
	private static final NoiseGeneratorBase extraX = new Simplex3DGenerator(4587 ^ root).setFrequency(1/8D);
	private static final NoiseGeneratorBase extraY = new Simplex3DGenerator(-3735 ^ root).setFrequency(1/8D);
	private static final NoiseGeneratorBase extraZ = new Simplex3DGenerator(823741 ^ root).setFrequency(1/8D);
	private static final VoronoiNoiseGenerator colorNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(83745 ^ root).setFrequency(1/20D).setDisplacement(extraX, extraY, extraZ, 6);

	private static final NoiseGeneratorBase woodGlowNoise = new Simplex3DGenerator(191327 ^ root).setFrequency(0.8);

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (ColorTreeGenerator.canGenerateTree(world, x, z) && BlockDyeSapling.canGrowAt(world, x, y, z, true)) {
			//ColorTreeGenerator.growTree(world, x, y, z, 5+random.nextInt(3), random, this.getColor(x, y, z));
			//TreeShaper.getInstance().generateTallTree(world, x, y, z);
			ReikaDyeHelper color = getColor(x, y, z);
			if (random.nextInt(10) == 0) {
				boolean generated = false;
				if (RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z)) {
					RainbowTreeGenerator.getInstance().generateLargeRainbowTree(world, x, y, z, random);
					generated = true;
					boolean tryEthereal = ModList.THAUMCRAFT.isLoaded() && ChromaOptions.ETHEREAL.getState();
					int max = tryEthereal ? 18 : 10;
					for (int i = 0; i < max; i++) {
						int dx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
						int dz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
						while (!world.checkChunksExist(dx, 0, dz, dx, world.provider.getActualHeight(), dz)) {
							dx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
							dz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
						}
						int dy = world.getTopSolidOrLiquidBlock(dx, dz);
						if (tryEthereal && i >= 10) {
							this.tryGenerateEthereal(world, dx, dy, dz);
						}
						else {
							this.tryGenerateMud(world, dx, dy, dz);
							for (int d = 2; d < 6; d++) {
								if (random.nextFloat() < 0.67F) {
									ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
									this.tryGenerateMud(world, dx+dir.offsetX, dy, dz+dir.offsetZ);
								}
							}
						}
					}
				}
				else if (GENERATE_SMALL_RAINBOW_TREES && random.nextInt(5) == 0) {
					generated = RainbowTreeGenerator.getInstance().tryGenerateSmallRainbowTree(world, x, y, z, random);
				}
				if (!generated) {
					TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, random, color, false, this.getVineChance(random), FERTILE_VINE_CHANCE_FACTOR);
				}
			}
			else {
				TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, random, color, false, this.getVineChance(random), FERTILE_VINE_CHANCE_FACTOR);
			}
			return true;
		}
		return false;
	}

	private boolean tryGenerateMud(World world, int x, int y, int z) {
		Block bd = world.getBlock(x, y-1, z);
		while (y > 0 && (bd == ChromaBlocks.DYEVINE.getBlockInstance() || bd == ChromaBlocks.FERTILEDYEVINE.getBlockInstance() || bd == ChromaBlocks.TIEREDPLANT.getBlockInstance() || bd.isAir(world, x, y-1, z) || bd.isLeaves(world, x, y-1, z))) {
			y--;
			bd = world.getBlock(x, y-1, z);
		}
		if (bd == Blocks.grass) {
			if (!ReikaWorldHelper.softBlocks(world, x-1, y-1, z) && !ReikaWorldHelper.softBlocks(world, x+1, y-1, z)) {
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z-1) && !ReikaWorldHelper.softBlocks(world, x, y-1, z+1)) {
					if (world.getBlock(x, y, z) == ChromaBlocks.DYEFLOWER.getBlockInstance() || ReikaWorldHelper.softBlocks(world, x, y, z)) {
						world.setBlockToAir(x, y, z);
						world.setBlock(x, y-1, z, ChromaBlocks.MUD.getBlockInstance());
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean tryGenerateEthereal(World world, int dx, int dy, int dz) {
		if (ReikaWorldHelper.softBlocks(world, dx, dy, dz) && world.getBlock(dx, dy-1, dz) == Blocks.grass) {
			Block id = ThaumItemHelper.BlockEntry.ETHEREAL.getBlock();
			int meta = ThaumItemHelper.BlockEntry.ETHEREAL.metadata;
			world.setBlock(dx, dy, dz, id, meta, 3);
			world.func_147451_t(dx, dy, dz);
			world.func_147479_m(dx, dy, dz);
			return true;
		}
		return false;
	}

	private float getVineChance(Random rand) {
		return rand.nextInt(5) <= 1 ? 0.6F+rand.nextFloat()*0.4F : 0;
	}

	public static ReikaDyeHelper getColor(/*World world, */int x, int y, int z) {
		/*
		if (colorOffsetNoise == null || colorOffsetNoise.seed != world.getSeed()) {
			colorOffsetNoise = (Simplex3DGenerator)new Simplex3DGenerator(world.getSeed()).setFrequency(1/60D);
		}*/
		//int idx = (Math.abs(x/16)+y+Math.abs(z/16));
		//idx += ReikaMathLibrary.normalizeToBounds(colorOffsetNoise.getValue(idx, y, z), 0, 16);
		//return ReikaDyeHelper.dyes[(idx+16)%16];
		colorNoise.randomFactor = 0.45;
		return ReikaDyeHelper.dyes[(colorNoise.getClosestRoot(x, y, z).hashCode()%16+16)%16];
	}

	public static IIcon getWoodGlow(int x, int y, int z, IIcon[] icons) {
		return icons[getWoodGlowIndex(x, y, z, icons.length)];
	}

	public static int getWoodGlowIndex(int x, int y, int z, int icons) {
		return MathHelper.clamp_int((int)Math.round(ReikaMathLibrary.normalizeToBounds(woodGlowNoise.getValue(x, y, z), -0.6, icons-0.4)), 0, icons-1);
	}

}
