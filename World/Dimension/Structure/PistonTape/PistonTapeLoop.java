package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class PistonTapeLoop extends StructurePiece<PistonTapeGenerator> {

	private final ForgeDirection facing;
	public final int busWidth;

	public final LoopDimensions dimensions;
	private final TapeStage level;
	private final PistonTapeSlice[] bits;

	protected PistonTapeLoop(PistonTapeGenerator s, ForgeDirection dir, TapeStage t) {
		super(s);
		level = t;
		facing = dir;

		busWidth = t.bitsPerDoor;
		bits = new PistonTapeSlice[busWidth];
		dimensions = LoopDimensions.createFor(level.doorCount);
		for (int i = 0; i < bits.length; i++) {
			bits[i] = new PistonTapeSlice(facing, s, this, dimensions);
		}
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(facing);
		for (int i = 0; i < bits.length; i++) {
			PistonTapeSlice p = bits[i];
			p.generate(world, x+dir.offsetX*i, y, z+dir.offsetZ*i);
		}
	}

	public int doorCount() {
		return level.doorCount;
	}

	public void cycle() {
		for (PistonTapeSlice s : bits) {
			s.cycle();
		}
	}

	static class LoopDimensions {

		private static final int MIN_HEIGHT = 3;
		private static final int MAX_HEIGHT = 5;

		public final int totalHeight;
		public final int totalDepth;

		public final int bitLength;

		private LoopDimensions(int d, int h) {
			totalHeight = h;
			totalDepth = d;

			bitLength = (totalHeight-2)*2+(totalDepth-2)*2+2;
		}

		private static LoopDimensions createFor(int stages) {
			int l = stages*2;
			int bestH = -1;
			int bestD = -1;
			int best = l+5;
			for (int h = MIN_HEIGHT; h <= MAX_HEIGHT; h++) {
				int d = MathHelper.ceiling_double_int(l/(float)h);
				int excess = d*h-l;
				if (excess < best) {
					bestH = h;
					bestD = d;
					best = excess;
				}
			}
			return new LoopDimensions(bestD, bestH);
		}

	}

}
