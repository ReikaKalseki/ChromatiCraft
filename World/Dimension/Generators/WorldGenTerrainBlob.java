/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class WorldGenTerrainBlob extends ChromaWorldGenerator {

	public WorldGenTerrainBlob(DimensionGenerators g, Random r, long s) {
		super(g, r, s);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.25F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		y = rand.nextInt(64+ChunkProviderChroma.VERTICAL_OFFSET);
		Blob b = new Blob(rand.nextDouble()*360, 8+rand.nextDouble()*24, 2+rand.nextDouble()*6);
		b.curvePower = 1.75+rand.nextDouble()*1.25;
		b.hasDirtTop = rand.nextInt(4) > 0;
		switch(rand.nextInt(24)) {
			case 0:
			case 1:
			case 2:
				b.innerBlock = new BlockKey(Blocks.lava);
				break;
			case 3:
				b.innerBlock = new BlockKey(Blocks.obsidian);
				break;
			case 4:
				b.innerBlock = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal());
				break;
			case 5:
				b.outerBlock = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal());
				b.innerBlock = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal());
				break;
			case 6:
				b.innerBlock = new BlockKey(Blocks.iron_ore);
				break;
			case 7:
				b.innerBlock = new BlockKey(Blocks.redstone_ore);
				break;
			case 8:
				b.innerBlock = new BlockKey(Blocks.lapis_ore);
				break;
		}
		switch(rand.nextInt(20)) {
			case 0:
			case 6:
				b.centerBlock = new BlockKey(Blocks.diamond_ore);
			case 1:
			case 7:
				b.centerBlock = new BlockKey(Blocks.emerald_ore);
			case 2:
			case 3:
			case 8:
			case 9:
				b.centerBlock = new BlockKey(Blocks.gold_ore);
				break;
			case 4:
				if (ModOreList.PLATINUM.existsInGame())
					b.centerBlock = new BlockKey(ModOreList.PLATINUM.getRandomOreBlock());
				break;
			case 5:
				if (ModOreList.IRIDIUM.existsInGame())
					b.centerBlock = new BlockKey(ModOreList.IRIDIUM.getRandomOreBlock());
				break;
			case 10:
				b.centerBlock = new BlockKey(ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.OCEANSTONE.ordinal());
				break;
			case 11:
				break;
		}
		b.calculate(x, y, z, rand);
		b.generate(world, rand);
		return true;
	}

	private static class Blob {

		private BlockKey centerBlock = new BlockKey(Blocks.stone);
		private BlockKey innerBlock = new BlockKey(Blocks.stone);
		private BlockKey outerBlock = new BlockKey(Blocks.stone);
		private boolean hasDirtTop = false;

		private final double compassAngle;
		private final double length;
		private final double maxWidth;

		private double curvePower = 2;

		private final HashSet<Coordinate> coords = new HashSet();
		private final HashSet<Coordinate> centercoords = new HashSet();

		private Blob(double a, double l, double w) {
			compassAngle = a;
			length = l;
			maxWidth = w;
		}

		private void calculate(int x, int y, int z, Random rand) {
			double x1 = x+0.5;
			double y1 = y+0.5;
			double z1 = z+0.5;
			double[] dp = ReikaPhysicsHelper.polarToCartesian(length, 0, compassAngle);
			double x2 = x1+dp[0];
			double y2 = y1+dp[1];
			double z2 = z1+dp[2];
			for (double d = 0; d <= 1; d += 0.5D/length) {
				double dx = x1+dp[0]*d;
				double dy = y1+dp[1]*d;
				double dz = z1+dp[2]*d;
				double r = maxWidth*Math.pow(0.25+3*(0.5-Math.abs(d-0.5)/2D), /*curvePower*/1);
				//ReikaJavaLibrary.pConsole("Placing sphere R="+r+"/ "+d+" @ "+x+","+y+","+z);
				this.placeSphere(rand, dx, dy, dz, r);
			}
		}

		private void placeSphere(Random rand, double x, double y, double z, double r) {
			double pow = 1.75+rand.nextDouble()*2.25;
			for (double i = -r; i <= r; i += 0.5) {
				for (double j = -r; j <= r; j += 0.5) {
					for (double k = -r; k <= r; k += 0.5) {
						if (Math.pow(Math.abs(i), pow)+Math.pow(Math.abs(j), pow)+Math.pow(Math.abs(k), pow) <= Math.pow(Math.abs(r), pow)) {
							Coordinate c = new Coordinate(x+i, y+j, z+k);
							coords.add(c);
							if (Math.abs(i) <= 1 && Math.abs(j) <= 1 && Math.abs(k) <= 1)
								centercoords.add(c);
						}
					}
				}
			}
		}

		private void generate(World world, Random rand) {
			for (Coordinate c : coords) {
				if (!hasDirtTop || coords.contains(c.offset(0, 1, 0))) {
					BlockKey bk = outerBlock;
					//if (!innerBlock.equals(outerBlock)) {
					if (centercoords.contains(c)) {
						bk = centerBlock;
					}
					else {
						boolean flag = true;
						for (int i = 0; i < 6; i++) {
							if (!coords.contains(c.offset(ForgeDirection.VALID_DIRECTIONS[i], 1))) {
								flag = false;
								break;
							}
						}
						if (flag)
							bk = innerBlock;
					}
					//}
					c.setBlock(world, bk.blockID, bk.metadata);
				}
				else {
					Block b = c.offset(0, 1, 0).getBlock(world);
					c.setBlock(world, b.isAir(world, c.xCoord, c.yCoord+1, c.zCoord) ? Blocks.grass : Blocks.dirt);
				}
			}
		}

	}

}
