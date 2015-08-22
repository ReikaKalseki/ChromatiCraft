package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.MeteorCraft.MeteorGenerator;
import Reika.MeteorCraft.MeteorGenerator.MeteorType;


public class WorldGenChromaMeteor extends ChromaWorldGenerator {

	private static final WeightedRandom<BlockKey> basicOres = new WeightedRandom();

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.002F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		y--;

		Block b = world.getBlock(x, y, z);
		if (!this.canGenerateOn(b))
			return false;

		int d = 1+rand.nextInt(4);

		y -= d;

		int r = 3+rand.nextInt(rand.nextBoolean() ? 5 : 4);

		double e = rand.nextDouble()*0.5;
		int ra = (int)(r*(1-e));
		double ang = rand.nextDouble()*360;

		if (r > 4 && rand.nextInt(12) == 0)
			this.generateCrater(world, x, y, z, rand, r);
		else
			this.generateTrack(world, x, y, z, rand, r, ra, ang);

		this.generateMeteor(world, x, y, z, rand, r, ra);

		return true;
	}

	private boolean canGenerateOn(Block b) {
		return b == Blocks.grass || b == Blocks.stone || b == Blocks.sand;
	}

	private void generateMeteor(World world, int x, int y, int z, Random rand, int r, int ra) {
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int h = -ra; h <= ra; h++) {
					if (ReikaMathLibrary.isPointInsideEllipse(i, h, k, r, ra, r)) {
						BlockKey bk = this.getMeteorBlock();
						int dx = x+i;
						int dy = y+h-3;
						int dz = z+k;
						world.setBlock(dx, dy, dz, bk.blockID, bk.metadata, 3);
					}
				}
			}
		}

	}

	private BlockKey getMeteorBlock() {
		return ModList.METEORCRAFT.isLoaded() ? new BlockKey(MeteorGenerator.instance.getBlock(MeteorType.STONE, true)) : basicOres.getRandomEntry();
	}

	private void generateTrack(World world, int x, int y, int z, Random rand, int r, int ra, double ang) {

		int rc = (int)(r*(1.125+rand.nextDouble()*0.125));

		double f = ReikaRandomHelper.getRandomPlusMinus(4D, 2D);

		for (int o = 0; o < 18; o++) {
			int ox = MathHelper.floor_double(x+o*Math.sin(Math.toRadians(ang)));
			int oz = MathHelper.floor_double(z+o*Math.cos(Math.toRadians(ang)));
			int oy = MathHelper.floor_double(y+o/f);

			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					for (int h = -ra; h <= ra; h++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, h, k, rc, ra, rc)) {
							int dx = ox+i;
							int dy = oy+h;
							int dz = oz+k;
							world.setBlock(dx, dy, dz, Blocks.air);
							Block b = world.getBlock(dx, dy-1, dz);
							Block b2 = b == Blocks.stone ? rand.nextInt(40) == 0 ? Blocks.obsidian : Blocks.cobblestone : b == Blocks.dirt || b == Blocks.grass ? Blocks.dirt : Blocks.gravel;
							//world.setBlock(dx, dy-1, dz, b2);
						}
					}
				}
			}
		}
	}

	private void generateCrater(World world, int x, int y, int z, Random rand, int r) {
		//int rc = (int)(r*(1.5+rand.nextDouble()*0.5));
		//int ra = (int)(rc*0.75);

		int rc = (int)(r*(1+rand.nextDouble()*2));
		int ra = (int)(rc*0.5);

		y += ra/2;

		for (int i = -rc; i <= rc; i++) {
			for (int k = -rc; k <= rc; k++) {
				for (int h = ra; h >= -ra; h--) {
					int dx = x+i;
					int dy = y+h;
					int dz = z+k;

					if (ReikaMathLibrary.isPointInsideEllipse(i, h, k, rc, ra, rc)) {
						world.setBlock(dx, dy, dz, Blocks.air);

						Block b = world.getBlock(dx, dy-1, dz);
						Block b2 = b == Blocks.stone ? rand.nextInt(40) == 0 ? Blocks.obsidian : Blocks.cobblestone : b == Blocks.dirt || b == Blocks.grass ? Blocks.dirt : Blocks.gravel;
						//world.setBlock(dx, dy-1, dz, b2);
					}
				}
			}
		}

		for (double a = 0; a < 360; a += 1) {
			int t = 3+rand.nextInt(5);
			for (int hy = -3; hy <= t; hy++) {
				int cx = MathHelper.floor_double(x+(rc+hy)*Math.cos(Math.toRadians(a)));
				int cz = MathHelper.floor_double(z+(rc+hy)*Math.sin(Math.toRadians(a)));
				int cy = y+hy-3;
				for (int h = 0; h < 16; h++) {
					Block b = world.getBlock(cx, cy+h, cz);
					if (!b.isAir(world, cx, cy+h, cz))
						world.setBlock(cx, cy+h, cz, h == 0 ? (rand.nextInt(18) == 0 ? Blocks.quartz_block : Blocks.stone) : Blocks.air);
				}
			}
		}

		int n = 8+rand.nextInt(24);
		for (int i = 0; i < n; i++) {
			double a = rand.nextDouble()*360;
			double dmin = rc+4+rand.nextDouble();
			double dmax = rc+12+rand.nextDouble()*8;
			for (double d = dmin; d <= dmax; d++) {
				double da = Math.toRadians(ReikaRandomHelper.getRandomPlusMinus(a, 5));
				int dx = MathHelper.floor_double(x+d*Math.cos(da));
				int dz = MathHelper.floor_double(z+d*Math.sin(da));
				int dy = world.getTopSolidOrLiquidBlock(dx, dz);
				BlockKey bk = this.getMeteorBlock();
				world.setBlock(dx, dy, dz, bk.blockID, bk.metadata, 3);
			}
		}
	}

	static {
		basicOres.addEntry(new BlockKey(Blocks.stone), 50);

		basicOres.addEntry(new BlockKey(Blocks.coal_ore), 20);
		basicOres.addEntry(new BlockKey(Blocks.iron_ore), 12);
		basicOres.addEntry(new BlockKey(Blocks.redstone_ore), 10);
		basicOres.addEntry(new BlockKey(Blocks.diamond_ore), 4);
		basicOres.addEntry(new BlockKey(Blocks.emerald_ore), 2);
		basicOres.addEntry(new BlockKey(Blocks.lapis_ore), 6);
		basicOres.addEntry(new BlockKey(Blocks.gold_ore), 8);
	}

}
