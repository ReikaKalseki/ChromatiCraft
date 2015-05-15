package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class LocksEntrance extends DynamicStructurePiece {

	public final int radius;
	public final ForgeDirection facing;
	private final ForgeDirection left;

	public LocksEntrance(DimensionStructureGenerator s, ForgeDirection dir, int r, int y) {
		super(s, y);
		radius = r;
		facing = dir;
		left = ReikaDirectionHelper.getLeftBy90(facing);
	}

	@Override
	public void generate(World world, int x, int z) {
		Block bk = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		for (int i = -radius; i <= radius; i++) {
			int dx = x+i;
			for (int k = -radius; k <= radius; k++) {
				int dz = z+k;

				int sign = (int)Math.signum(facing.offsetX+facing.offsetZ);
				int step = facing.offsetX != 0 ? i : k;
				int d = Math.abs(facing.offsetX == 0 ? i : k);
				int d2 = Math.abs(step);
				int in = radius-d;
				int h = (int)(2+Math.sqrt(2.5*in));
				for (int j = 0; j <= h; j++) {
					int dy = posY+j;

					boolean enter = step*sign == -radius && j > 0 && j < h-1 && d < radius-1;
					boolean wall = !enter && (Math.abs(i) == radius || Math.abs(k) == radius || j == 0 || j == h);
					boolean window = j == h && d == (radius > 10 ? 4 : radius > 8 ? 3 : radius > 6 ? 2 : 1) && d2 > 0 && d2 < radius-1;
					world.setBlock(dx, dy, dz, wall ? bk : Blocks.air, wall ? window ? BlockType.GLASS.metadata : BlockType.STONE.metadata : 0, 3);
				}
			}
		}

		int dx = x+facing.offsetX*radius;
		int dz = z+facing.offsetZ*radius;
		int w = 2;
		for (int a = -w; a <= w; a++) {
			int dx2 = dx+left.offsetX*a;
			int dz2 = dz+left.offsetZ*a;
			int h = Math.abs(a) == w ? 3 : 4;
			for (int b = 1; b <= h; b++) {
				int dy = posY+b;
				world.setBlock(dx2, dy, dz2, Blocks.air);
			}
		}
	}

}
