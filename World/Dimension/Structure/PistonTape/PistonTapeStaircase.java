package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class PistonTapeStaircase extends StructurePiece<PistonTapeGenerator> {

	private static final int RADIUS = 2;

	private final Coordinate c1;
	private final Coordinate c2;
	private final Random rand;
	private final ForgeDirection main;

	public PistonTapeStaircase(PistonTapeGenerator gen, TapeStage s1, TapeStage s2, Random r) {
		super(gen);
		main = s1.mainDirection;
		c1 = s1.getExit().offset(main, RADIUS);
		c2 = s2.getEntrance().offset(main, RADIUS);
		rand = r;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		HashSet<Coordinate> set = new HashSet();
		double lx = c2.xCoord-c1.xCoord;
		double ly = c2.yCoord-c1.yCoord;
		double lz = c2.zCoord-c1.zCoord;
		for (double d = 0; d <= 1; d += 0.03125/4) {
			double dx = c1.xCoord+lx*d;
			double dy = c1.yCoord+ly*d;
			double dz = c1.zCoord+lz*d;
			int mx = MathHelper.floor_double(dx-RADIUS);
			int my = MathHelper.floor_double(dy-RADIUS);
			int mz = MathHelper.floor_double(dz-RADIUS);
			int px = MathHelper.floor_double(dx+RADIUS);
			int py = MathHelper.floor_double(dy+RADIUS);
			int pz = MathHelper.floor_double(dz+RADIUS);
			for (int i = mx; i <= px; i++) {
				for (int j = my; j <= py; j++) {
					for (int k = mz; k <= pz; k++) {
						set.add(new Coordinate(i, j, k));
					}
				}
			}
		}
		for (Coordinate c : set) {
			Block b = Blocks.air;
			int m = 0;
			for (Coordinate c2 : c.getAdjacentCoordinates()) {
				if (!set.contains(c2)) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = rand.nextInt(12) == 0 && set.contains(c.offset(0, 1, 0)) && set.contains(c.offset(0, -1, 0)) ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
					break;
				}
			}
			c.setBlock(world, b, m);
		}

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(main);
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = c1.xCoord+left.offsetX*i+main.offsetX*-2;
				int dz = c1.zCoord+left.offsetZ*i+main.offsetZ*-2;
				int dy = c1.yCoord+k;
				world.setBlock(dx, dy, dz, Blocks.air);

				dx = c2.xCoord+left.offsetX*i+main.offsetX*-2;
				dz = c2.zCoord+left.offsetZ*i+main.offsetZ*-2;
				dy = c2.yCoord+k;
				world.setBlock(dx, dy, dz, Blocks.air);
			}
		}
	}
}
