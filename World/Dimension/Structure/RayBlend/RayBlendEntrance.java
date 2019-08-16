package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class RayBlendEntrance extends DynamicStructurePiece<RayBlendGenerator> {

	public RayBlendEntrance(RayBlendGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y1 = parent.getPosY();
		int y2 = world.getTopSolidOrLiquidBlock(x, z);
		int y = y1;
		int sX = 0;
		int sZ = 0;
		int dx = x+sX*EntranceLevel.CELL;
		int dz = z+sZ*EntranceLevel.CELL;
		if (y+EntranceLevel.HEIGHT < y2) {
			EntranceLevel el = new EntranceLevel(sX, sZ, EntranceLevel.rand(), EntranceLevel.rand());
			el.generate(world, x, y, z);
			y += EntranceLevel.HEIGHT;
			sX = el.shaftUpX;
			sZ = el.shaftUpZ;
			dx = x+sX*EntranceLevel.CELL;
			dz = z+sZ*EntranceLevel.CELL;
		}
		else {
			while (y <= y2) {
				EntranceLevel.generateShaft(world, dx, y, dz);
				y++;
			}
		}
		for (int i = 0; i < 6; i++) {
			world.setBlock(dx-EntranceLevel.CELL/2-1, y+i, z+EntranceLevel.CELL/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
			world.setBlock(dx-EntranceLevel.CELL/2-1, y+i, z-EntranceLevel.CELL/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
			world.setBlock(dx+EntranceLevel.CELL/2+1, y+i, z+EntranceLevel.CELL/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
			world.setBlock(dx+EntranceLevel.CELL/2+1, y+i, z-EntranceLevel.CELL/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
		}
	}

	private static class EntranceLevel {

		private static final int HEIGHT = 5;
		private static final int GRID = 1;

		private static final int CELL = 3;

		private final int shaftDownX;
		private final int shaftDownZ;
		private final int shaftUpX;
		private final int shaftUpZ;

		private EntranceLevel(int xd, int zd, int xu, int zu) {
			shaftDownX = xd;
			shaftDownZ = zd;
			shaftUpX = xu;
			shaftUpZ = zu;
		}

		private void generate(World world, int x, int y, int z) {
			int r = CELL/2+CELL+3;
			int fx1 = x+shaftDownX*(CELL+1)-1;
			int fx2 = x+shaftDownX*(CELL+1)+1;
			int fz1 = z+shaftDownZ*(CELL+1)-1;
			int fz2 = z+shaftDownZ*(CELL+1)+1;
			int cx1 = x+shaftUpX*(CELL+1)-1;
			int cx2 = x+shaftUpX*(CELL+1)+1;
			int cz1 = z+shaftUpZ*(CELL+1)-1;
			int cz2 = z+shaftUpZ*(CELL+1)+1;

			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					int m = BlockType.STONE.metadata;
					boolean airFloor = dx >= fx1 && dx <= fx2 && dz >= fz1 && dz <= fz2;
					boolean airCeil = dx >= cx1 && dx <= cx2 && dz >= cz1 && dz <= cz2;
					if (airFloor)
						world.setBlock(dx, y, dz, Blocks.air);
					else
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					if (airCeil)
						world.setBlock(dx, y+HEIGHT, dz, Blocks.air);
					else
						world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				}
			}
			for (int i = 1; i < HEIGHT; i++) {
				int m = BlockType.STONE.metadata;
				//if (i == (x+z)%HEIGHT)
				if (world.rand.nextInt(12) == 0)
					m = BlockType.LIGHT.metadata;
				world.setBlock(x-r, y+i, z-r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x+r, y+i, z-r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x-r, y+i, z+r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(x+r, y+i, z+r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			}
		}

		private static int rand() {
			return ReikaRandomHelper.getRandomPlusMinus(0, GRID);
		}

		private static void generateShaft(World world, int x, int y, int z) {
			int r = CELL/2+1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					if (Math.abs(i) == r || Math.abs(k) == r) {
						world.setBlock(x+i, y, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
					}
					else {
						world.setBlock(x+i, y, z+k, Blocks.air);
					}
				}
			}
		}

	}

}
