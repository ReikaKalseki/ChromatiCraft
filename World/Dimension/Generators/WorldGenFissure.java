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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class WorldGenFissure extends ChromaWorldGenerator {

	public WorldGenFissure(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (world.getBlock(x, y, z) == Blocks.water)
			return false;
		if (world.getBlock(x, y+1, z) == Blocks.water)
			return false;

		int my = 8+rand.nextInt(16);
		double w = rand.nextDouble();

		Collection<ForgeDirection> dirs = new ArrayList();
		for (int i = 2; i < 6; i++) {
			if (rand.nextInt(3) > 0) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				dirs.add(dir);
			}
		}

		int color = rand.nextInt(16);
		HashMap<Coordinate, Integer> columns = new HashMap();

		for (ForgeDirection dir : dirs) {
			int l = 12;
			ArrayList<ForgeDirection> li = new ArrayList();
			li.add(dir);
			this.cut(rand, world, x, y, z, w, my, 0, l, li, columns, color);
		}

		for (Coordinate c : columns.keySet()) {
			int h = columns.get(c);
			if (c.offset(0, h-1, 0).getBlock(world) == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
				c.offset(0, h, 0).setBlock(world, ChromaBlocks.VOIDRIFT.getBlockInstance(), color, 3);
			}
		}

		return true;
	}

	private void cut(Random rand, World world, int x, int y, int z, double w, int my, int dist, int len, ArrayList<ForgeDirection> follow, HashMap<Coordinate, Integer> columns, int color) {
		for (int dy = my; dy <= y+12; dy++) {

			int r = (int)(w*Math.sqrt(1+(dy-my)/4D));

			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					if (this.canCutInto(world, dx, dy, dz)) {
						world.setBlock(dx, dy, dz, Blocks.air);
						for (int d = 0; d < 6; d++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
							int dx2 = dx+dir.offsetX;
							int dy2 = dy+dir.offsetY;
							int dz2 = dz+dir.offsetZ;
							Block b = world.getBlock(dx2, dy2, dz2);
							int m = world.getBlockMetadata(dx2, dy2, dz2);
							if (b.getMaterial() == Material.rock && this.canCutInto(world, dx2, dy2, dz2)) {
								world.setBlock(dx2, dy2, dz2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), 0, 3);
								Coordinate c = new Coordinate(dx2, 0, dz2);
								Integer get = columns.get(c);
								int h = get != null ? get.intValue() : 0;
								columns.put(c, Math.max(dy2+1, h));
							}
						}
					}
				}
			}
		}

		if (dist > 1) {
			if (rand.nextInt(2) == 0) {
				ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(follow.get(follow.size()-1));
				if (rand.nextBoolean())
					dir = dir.getOpposite();
				follow.add(dir);
				this.cut(rand, world, x+dir.offsetX, y, z+dir.offsetZ, w, my, 0, len-1, follow, columns, color);
				follow.remove(follow.size()-1);
			}
		}

		if (len > 0 && rand.nextInt(6+len) > 0) {
			ForgeDirection dir = follow.get(rand.nextInt(follow.size()));
			this.cut(rand, world, x+dir.offsetX, y, z+dir.offsetZ, w, my, dist+1, len-1, follow, columns, color);
		}

	}

	public static boolean canCutInto(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b instanceof BlockStructureShield && meta >= 8)
			return false;
		if (b.getBlockHardness(world, x, y, z) < 0)
			return false;
		if (b instanceof SemiUnbreakable) {
			if (((SemiUnbreakable)b).isUnbreakable(world, x, y, z, meta))
				return false;
		}
		if (b == ChromaBlocks.RUNE.getBlockInstance())
			return false;
		ChromaBlocks cb = ChromaBlocks.getEntryByID(b);
		if (cb != null && cb.isDimensionStructureBlock()) {
			return false;
		}
		return true;
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.01F;
	}

}
