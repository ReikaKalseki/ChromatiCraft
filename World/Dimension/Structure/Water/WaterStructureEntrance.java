package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;


public class WaterStructureEntrance extends DynamicStructurePiece {

	public WaterStructureEntrance(WaterPuzzleGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y = parent.getPosY()+WaterFloor.HEIGHT;

		y += 5;

		int top = world.getTopSolidOrLiquidBlock(x, z);
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int d = Math.abs(i)+Math.abs(k);
				if (d <= 5) {
					int dx = x+i;
					int dz = z+k;
					for (int dy = y; dy <= top; dy++) {
						if (Math.abs(i) == r || Math.abs(k) == r || d == 5) {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 2);
						}
						else if (dy == y) {
							world.setBlock(dx, dy, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta(), 2);
						}
						else {
							world.setBlock(dx, dy, dz, Blocks.air);
						}
					}
				}
			}
		}
		r += 6;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int d = Math.abs(i)+Math.abs(k);
				if ((d > 5 || Math.abs(i) > 4 || Math.abs(k) > 4) && d <= 11) {
					int dx = x+i;
					int dz = z+k;
					if (d < 11 && world.rand.nextInt(3) == 0) {
						world.setBlock(dx, top-1, dz, Blocks.water);
						world.setBlock(dx, top-2, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(4) == 0 ? BlockType.LIGHT.metadata : world.rand.nextInt(3) == 0 ? BlockType.STONE.metadata : BlockType.MOSS.metadata, 2);
					}
					else {
						world.setBlock(dx, top-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(3) == 0 ? BlockType.STONE.metadata : BlockType.MOSS.metadata, 2);
						world.setBlock(dx, top-2, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(3) == 0 ? BlockType.STONE.metadata : BlockType.MOSS.metadata, 2);
					}
				}
			}
		}
	}

}
