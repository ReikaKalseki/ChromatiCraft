package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;


public class WaterStructureEntrance extends DynamicStructurePiece {

	public WaterStructureEntrance(WaterPuzzleGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y = parent.getPosY();


		y += 4;

		int top = world.getTopSolidOrLiquidBlock(x, z);
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				for (int dy = y; dy <= top; dy++) {
					if (Math.abs(i) == r || Math.abs(k) == r) {
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 2);
					}
					else {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}
	}

}
