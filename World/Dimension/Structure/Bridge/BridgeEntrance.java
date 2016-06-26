package Reika.ChromatiCraft.World.Dimension.Structure.Bridge;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;


public class BridgeEntrance extends DynamicStructurePiece {

	public BridgeEntrance(BridgeGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int top = world.getTopSolidOrLiquidBlock(x, z);
		int y = parent.getPosY()+1;
		while (world.getBlock(x, y, z) == Blocks.air) {
			y++;
		}
		y -= 2;
		for (int dy = y; dy <= top; dy++) {
			int r = dy%8 < 4 ? 4 : 3;
			int r2 = r-1;
			int m = r == 3 ? BlockType.CLOAK.metadata : BlockType.STONE.metadata;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					if (Math.abs(i) <= r2 && Math.abs(k) <= r2) {
						if (Math.abs(i) != r || Math.abs(k) != r) {
							world.setBlock(dx, dy, dz, dy <= y+1 ? ChromaBlocks.HOVER.getBlockInstance() : Blocks.air, dy <= y+1 ? HoverType.DAMPER.getPermanentMeta() : 0, 3);
						}
						else {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
						}
					}
					else {
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					}
				}
			}
		}
	}

}
