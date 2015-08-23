package Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class NonEuclidEntrance extends DynamicStructurePiece {

	public NonEuclidEntrance(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int maxy = world.getTopSolidOrLiquidBlock(x, z)+1;
		int miny = parent.getPosY()+1;
		int h = maxy-miny;
		int l = 4;
		int dh = 6;
		int localmax = maxy;

		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();//ChromaBlocks.SPECIALSHIELD.getBlockInstance();
		int m1 = BlockType.STONE.metadata;
		int m2 = BlockType.CLOAK.metadata;

		int dd = 0;
		for (int d = 0; d <= h; d++) {
			int dy = miny+d;
			int dz = z+l+d;

			for (int i = -2; i <= 2; i++) {
				int dx = x+i;
				world.setBlock(dx, dy, dz, b, m2, 3);
				world.setBlock(dx, dy+dh, dz, b, m1, 3);
				boolean wall = Math.abs(i) == 2;
				for (int f = 1; f < dh; f++)
					world.setBlock(dx, dy+f, dz, wall ? b : Blocks.air, wall ? m1 : 0, 3);
			}

			localmax = world.getTopSolidOrLiquidBlock(x, dz)-1;
			Block bb = world.getBlock(x, localmax, dz);
			if (!(bb instanceof BlockStructureShield)) {
				h = localmax-miny;
				dd = d-h;
			}
		}

		parent.offsetEntry(0, dd);

		for (int k = 1; k < l; k++) {
			int dz = z+k;
			int dy = miny-1;
			for (int i = -2; i <= 2; i++) {
				int dx = x+i;
				world.setBlock(dx, dy, dz, b, m2, 3);
				world.setBlock(dx, dy+dh, dz, b, m1, 3);
				boolean wall = Math.abs(i) == 2;
				for (int f = 1; f < dh; f++)
					world.setBlock(dx, dy+f, dz, wall ? b : Blocks.air, wall ? m1 : 0, 3);
			}

			dz = z+k+h+dd+10+1;
			dy = maxy-2;
			for (int i = -2; i <= 2; i++) {
				int dx = x+i;
				world.setBlock(dx, dy, dz, b, m2, 3);
				world.setBlock(dx, dy+dh, dz, b, m1, 3);
				boolean wall = Math.abs(i) == 2;
				for (int f = 1; f < dh; f++)
					world.setBlock(dx, dy+f, dz, wall ? b : Blocks.air, wall ? m1 : 0, 3);
			}
		}

		for (int i = -1; i <= 1; i++) {
			int dx = x+i;

			int dz = z+1;
			int dy = miny+3;
			world.setBlock(dx, dy, dz, b, m1, 3);
			world.setBlock(dx, dy+1, dz, b, m1, 3);
			world.setBlock(dx, dy+1, dz+1, b, m1, 3);

			/*
			dz = z+l+h;
			dy = maxy+5;
			world.setBlock(dx, dy, dz, b, BlockType.LIGHT.metadata, 3);
			 */
		}
	}



}
