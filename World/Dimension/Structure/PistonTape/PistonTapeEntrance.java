package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class PistonTapeEntrance extends DynamicStructurePiece<PistonTapeGenerator> {

	private final Coordinate start;
	private final ForgeDirection dir;

	public PistonTapeEntrance(PistonTapeGenerator s, Coordinate c, ForgeDirection d) {
		super(s);
		start = c;
		dir = d;
	}

	@Override
	public void generate(World world, int x, int z) {
		int y1 = start.yCoord-2;
		int y2 = world.getTopSolidOrLiquidBlock(x, z);
		for (int y = y1; y <= y2; y++) {
			int r = this.getR(y, y1);
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					boolean wall = i == r || i == -r || k == -r || k == r || y == y1;
					if (wall) {
						int m = world.rand.nextInt(20) == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
						if (this.isEdge(y-1, y1)) {
							m = BlockType.COBBLE.metadata;
						}
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					}
					else if (this.isEdge(y, y1)) {
						world.setBlock(dx, y, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta(), 3);
					}
					else {
						world.setBlock(dx, y, dz, Blocks.air);
					}
				}
			}
		}
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+3*dir.offsetX+left.offsetX*i;
				int dz = z+3*dir.offsetZ+left.offsetZ*i;
				world.setBlock(dx, start.yCoord+k, dz, Blocks.air);
			}
		}
	}

	private boolean isEdge(int y, int y1) {
		return y > y1+1 && this.getR(y, y1) != this.getR(y+1, y1);
	}

	private int getR(int y, int y1) {
		return 4-(int)(2*Math.cos((y-y1)/4D));
	}

}
