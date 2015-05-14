package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class LocksEntrance extends StructurePiece {

	public final int radius;
	public final ForgeDirection facing;
	private final ForgeDirection left;

	public LocksEntrance(DimensionStructureGenerator s, ForgeDirection dir, int r) {
		super(s);
		radius = r;
		facing = dir;
		left = ReikaDirectionHelper.getLeftBy90(facing);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block bk = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		for (int i = -radius; i <= radius; i++) {
			int dx = x+i;
			for (int k = -radius; k <= radius; k++) {
				int dz = z+k;

				int in = Math.abs(radius-i);
				int out = Math.abs(radius/2-(radius-i));
				int h = in <= 1 ? 2 : in <= 3 ? 3 : out <= 1 ? 5 : 4;
				for (int j = 0; j < h; j++) {
					int dy = y+j;

					boolean wall = Math.abs(i) == radius || Math.abs(k) == radius || j == 0 || j == h;
					world.setBlock(dx, dy, dz, wall ? bk : Blocks.air, wall ? BlockType.STONE.metadata : 0);
				}
			}
		}

		int dx = x+facing.offsetX*radius;
		int dz = z+facing.offsetZ*radius;
		int w = 3;
		for (int a = -w; a <= w; a++) {
			int dx2 = dx+left.offsetX*a;
			int dz2 = dz+left.offsetZ*a;
			int h = a == w ? 2 : 3;
			for (int b = 0; b < h; b++) {
				int dy = y+b;
				world.setBlock(dx2, dy, dz2, Blocks.air);
			}
		}
	}

}
