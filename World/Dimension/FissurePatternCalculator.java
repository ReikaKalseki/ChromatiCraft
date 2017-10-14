package Reika.ChromatiCraft.World.Dimension;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenFissure;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;


public class FissurePatternCalculator extends ThreadedGenerator {

	private static final int PATTERN_COUNT = 500;

	private static final ArrayList<FissurePattern> patterns = new ArrayList();

	public FissurePatternCalculator(long seed) {
		super(seed);
	}

	@Override
	public void run() throws Throwable {
		patterns.clear();
		for (int i = 0; i < PATTERN_COUNT; i++) {
			FissurePattern fp = new FissurePattern();
			fp.calculate(rand);
			patterns.add(fp);
		}
	}

	public static FissurePattern getRandomFissure(World world, int x, int y, int z) {
		return patterns.get(world.rand.nextInt(PATTERN_COUNT));
	}

	public static void generateRandomFissure(World world, int x, int y, int z) {
		getRandomFissure(world, x, y, z).generate(world, x, y, z);
	}

	@Override
	public String getStateMessage() {
		return patterns.size()+" fissure layouts calculated.";
	}

	public static class FissurePattern {

		private final HashMap<Coordinate, BlockKey> blockData = new HashMap();
		private final HashMap<Point, Integer> shape = new HashMap();

		private FissurePattern() {

		}

		private void calculate(Random rand) {
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
				this.cut(rand, 0, 0, 0, w, my, 0, l, li, columns, color);
			}

			for (Coordinate c : columns.keySet()) {
				int h = columns.get(c);
				if (this.getBlock(c.xCoord, c.yCoord+h-1, c.zCoord) == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
					this.setBlock(c.xCoord, c.yCoord+h, c.zCoord, ChromaBlocks.VOIDRIFT.getBlockInstance(), color);
				}
			}

			for (Coordinate c : blockData.keySet()) {
				shape.put(new Point(c.xCoord, c.zCoord), 0);
			}
		}

		public Map<Point, Integer> getDepthMap() {
			return Collections.unmodifiableMap(shape);
		}

		public void generate(World world, int x, int y, int z) {
			for (Coordinate c : blockData.keySet()) {
				Coordinate c2 = c.offset(x, y, z);
				Block b = c2.getBlock(world);
				if (b.isAir(world, c2.xCoord, c2.yCoord, c2.zCoord))
					continue;
				if (b == ChromaBlocks.DIMGEN.getBlockInstance() || ReikaBlockHelper.isLiquid(b))
					continue;
				if (!WorldGenFissure.canCutInto(world, c2.xCoord, c2.yCoord, c2.zCoord))
					continue;
				BlockKey bk = blockData.get(c);
				if (b.getMaterial() != Material.rock && bk.blockID == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
					continue;
				c2.setBlock(world, bk.blockID, bk.metadata, 2);
			}
		}

		private void cut(Random rand, int x, int y, int z, double w, int my, int dist, int len, ArrayList<ForgeDirection> follow, HashMap<Coordinate, Integer> columns, int color) {
			for (int dy = my; dy <= y+12; dy++) {

				int r = (int)(w*Math.sqrt(1+(dy-my)/4D));

				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						int dx = x+i;
						int dz = z+k;
						this.setBlock(dx, dy, dz, Blocks.air, 0);
						for (int d = 0; d < 6; d++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
							int dx2 = dx+dir.offsetX;
							int dy2 = dy+dir.offsetY;
							int dz2 = dz+dir.offsetZ;
							this.setBlock(dx2, dy2, dz2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal());
							Coordinate c = new Coordinate(dx2, 0, dz2);
							Integer get = columns.get(c);
							int h = get != null ? get.intValue() : 0;
							columns.put(c, Math.max(dy2+1, h));
						}

						int gy = my-6;
						for (int d = 0; d < 6; d++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
							int dx2 = dx+dir.offsetX;
							int dy2 = gy+dir.offsetY;
							int dz2 = dz+dir.offsetZ;
							if (dir == ForgeDirection.UP) {
								for (int h = 1; h < my; h++) {
									dy2 = gy+dir.offsetY*h;
									this.setBlock(dx2, dy2, dz2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal());
								}
							}
							else {
								//if (world.getBlock(dx2, dy2, dz2) != ChromaBlocks.DIMGEN.getBlockInstance())
								this.setBlock(dx2, dy2, dz2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
							}
						}
						this.setBlock(dx, gy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LIFEWATER.ordinal());
					}
				}
			}

			if (dist > 1) {
				if (rand.nextInt(2) == 0) {
					ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(follow.get(follow.size()-1));
					if (rand.nextBoolean())
						dir = dir.getOpposite();
					follow.add(dir);
					this.cut(rand, x+dir.offsetX, y, z+dir.offsetZ, w, my, 0, len-1, follow, columns, color);
					follow.remove(follow.size()-1);
				}
			}

			if (len > 0 && rand.nextInt(6+len) > 0) {
				ForgeDirection dir = follow.get(rand.nextInt(follow.size()));
				this.cut(rand, x+dir.offsetX, y, z+dir.offsetZ, w, my, dist+1, len-1, follow, columns, color);
			}

		}

		private Block getBlock(int x, int y, int z) {
			BlockKey get = blockData.get(new Coordinate(x, y, z));
			return get != null ? get.blockID : null;
		}

		private void setBlock(int x, int y, int z, Block b, int meta) {
			blockData.put(new Coordinate(x, y, z), new BlockKey(b, meta));
		}

	}

}
