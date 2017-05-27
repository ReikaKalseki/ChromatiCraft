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

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.DimDecoTileTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.DoublePolygon;
import Reika.DragonAPI.Instantiable.Math.DoubleRectangle;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class WorldGenGlowingCracks extends ChromaWorldGenerator {

	private final WeightedRandom<OreType> oreRand = new WeightedRandom();

	public WorldGenGlowingCracks(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);

		oreRand.addEntry(ReikaOreHelper.EMERALD, 10);
		oreRand.addEntry(ReikaOreHelper.DIAMOND, 20);
		oreRand.addEntry(ReikaOreHelper.REDSTONE, 50);
		oreRand.addEntry(ReikaOreHelper.GOLD, 40);
		if (ModOreList.SAPPHIRE.existsInGame())
			oreRand.addEntry(ModOreList.SAPPHIRE, 30);
		if (ModOreList.PLATINUM.existsInGame())
			oreRand.addEntry(ModOreList.PLATINUM, 20);
		if (ModOreList.AMETHYST.existsInGame())
			oreRand.addEntry(ModOreList.AMETHYST, 30);
		if (ModOreList.MANA.existsInGame())
			oreRand.addEntry(ModOreList.MANA, 20);
		if (ModOreList.MOONSTONE.existsInGame())
			oreRand.addEntry(ModOreList.MOONSTONE, 10);
		if (ModOreList.VINTEUM.existsInGame())
			oreRand.addEntry(ModOreList.VINTEUM, 30);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.25F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				if (world.getBlock(x+i, y-1, z+k) != Blocks.grass)
					return false;
			}
		}
		world.setBlock(x, y, z, ChromaBlocks.DIMGENTILE.getBlockInstance(), DimDecoTileTypes.GLOWCRACKS.ordinal(), 3);

		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				world.setBlockMetadataWithNotify(x+i, y-1, z+k, 1, 3); //unbreakable grass && stone
				world.setBlockMetadataWithNotify(x+i, y-2, z+k, 1, 3);
			}
		}

		int h = 8+rand.nextInt(9);//6+rand.nextInt(7);
		Crystal cry = new Crystal(x, y-4-h/2, z);
		cry.rootHeight = h;
		cry.rootSize = 2+rand.nextInt(4);
		cry.rotX = rand.nextDouble()*10-5;
		cry.rotY = rand.nextDouble()*360;
		cry.rotZ = rand.nextDouble()*10-5;
		cry.calculate();
		cry.randomize(rand);
		Collection<Coordinate> li = cry.getCoordinates();
		oreRand.setSeed(rand.nextLong());
		OreType ore = oreRand.getRandomEntry();
		ItemStack is = ore.getFirstOreBlock();
		BlockKey bk = rand.nextInt(2) == 0 ? new BlockKey(ChromaBlocks.TIEREDORE.getBlockInstance(), this.getRandomOre(rand)) : new BlockKey(Block.getBlockFromItem(is.getItem()), is.getItemDamage());
		for (Coordinate c : li) {
			if (c.getBlock(world) == Blocks.stone || c.getBlock(world) == ChromaBlocks.TIEREDORE.getBlockInstance())
				c.setBlock(world, bk.blockID, bk.metadata, 2);
		}
		return true;
	}

	private int getRandomOre(Random rand) {
		TieredOres ore = TieredOres.list[rand.nextInt(TieredOres.list.length)];
		while (ore.genBlock != Blocks.stone) {
			ore = TieredOres.list[rand.nextInt(TieredOres.list.length)];
		}
		return ore.ordinal();
	}

	private static class Crystal {

		private final int centerX;
		private final int centerY;
		private final int centerZ;

		private double rootHeight = 8; //radius, not -ve to +ve
		private double rootSize = 3; //radius, not -ve to +ve

		private double rotX;
		private double rotY;
		private double rotZ;

		private DecimalPosition lowerPoint;
		private DecimalPosition upperPoint;
		private DecimalPosition[] edgePoints;

		private Crystal(int x, int y, int z) {
			centerX = x;
			centerY = y;
			centerZ = z;
		}

		private void calculate() {
			Vec3 lowerPointVec = Vec3.createVectorHelper(0, 0-rootHeight, 0);
			Vec3 upperPointVec = Vec3.createVectorHelper(0, 0+rootHeight, 0);
			Vec3[] edgePointsVec = new Vec3[4];
			edgePointsVec[0] = Vec3.createVectorHelper(0+rootSize, 0, 0);
			edgePointsVec[1] = Vec3.createVectorHelper(0, 			0, 0+rootSize);
			edgePointsVec[2] = Vec3.createVectorHelper(0-rootSize, 0, 0);
			edgePointsVec[3] = Vec3.createVectorHelper(0, 			0, 0-rootSize);

			lowerPointVec = ReikaVectorHelper.rotateVector(lowerPointVec, rotX, rotY, rotZ);
			upperPointVec = ReikaVectorHelper.rotateVector(upperPointVec, rotX, rotY, rotZ);
			for (int i = 0; i < 4; i++) {
				edgePointsVec[i] = ReikaVectorHelper.rotateVector(edgePointsVec[i], rotX, rotY, rotZ);
			}

			edgePoints = new DecimalPosition[4];
			lowerPoint = new DecimalPosition(lowerPointVec).offset(centerX, centerY, centerZ);
			upperPoint = new DecimalPosition(upperPointVec).offset(centerX, centerY, centerZ);
			for (int i = 0; i < 4; i++) {
				edgePoints[i] = new DecimalPosition(edgePointsVec[i]).offset(centerX, centerY, centerZ);
			}
		}

		private void randomize(Random rand) {
			lowerPoint = lowerPoint.offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			upperPoint = upperPoint.offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			for (int i = 0; i < 4; i++) {
				edgePoints[i] = edgePoints[i].offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			}
		}

		private Collection<Coordinate> getCoordinates() {
			HashSet<Coordinate> set = new HashSet();
			AxisAlignedBB box = ReikaAABBHelper.fromPoints(upperPoint, lowerPoint, edgePoints[0], edgePoints[1], edgePoints[2], edgePoints[3]);
			int y0 = MathHelper.floor_double(box.minY);
			int y1 = MathHelper.ceiling_double_int(box.maxY);
			for (int y = y0; y < y1; y++) {
				double dy = y+0.5;
				DoublePolygon slice = new DoublePolygon();
				for (int i = 0; i < 4; i++) {
					DecimalPosition p = edgePoints[i];
					double f = dy < centerY ? (centerY-dy)/(centerY-lowerPoint.yCoord) : 1-((dy-centerY)/(upperPoint.yCoord-centerY));
					double cx = dy < centerY ? lowerPoint.xCoord : upperPoint.xCoord;
					double cz = dy < centerY ? lowerPoint.zCoord : upperPoint.zCoord;
					double x = cx+(p.xCoord-cx)*(dy < centerY ? 1-f : f);
					double z = cz+(p.zCoord-cz)*(dy < centerY ? 1-f : f);
					slice.addPoint(x, z);
				}
				DoubleRectangle r = slice.getBounds();
				int x0 = MathHelper.floor_double(r.x);
				int x1 = MathHelper.ceiling_double_int(r.x+r.width);
				int z0 = MathHelper.floor_double(r.y);
				int z1 = MathHelper.ceiling_double_int(r.y+r.height);
				for (int x = x0; x <= x1; x++) {
					for (int z = z0; z <= z1; z++) {
						double dx = x+0.5;
						double dz = z+0.5;
						if (slice.contains(dx, dz) || slice.contains(dx-0.5, dz-0.5) || slice.contains(dx+0.5, dz+0.5)) {
							set.add(new Coordinate(x, y, z));
						}
					}
				}
			}
			return set;
		}

	}

}
