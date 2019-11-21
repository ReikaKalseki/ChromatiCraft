package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class GateStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -3; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				for (int k = -3; k <= 3; k++) {
					array.setEmpty(x+i, y+j, z+k, false, false);
				}
			}
		}

		int mb = StoneTypes.BRICKS.ordinal();
		int mc = StoneTypes.CORNER.ordinal();
		int mr = StoneTypes.RESORING.ordinal();
		int ms = StoneTypes.SMOOTH.ordinal();
		int ma = StoneTypes.STABILIZER.ordinal();
		int[][] metas = {
				{-2, -2, mb, mb, mb, mb, mb, -2, -2},
				{-2, mb, mb, ms, ms, ms, mb, mb, -2},
				{mb, mb, -1, -1, mr, -1, -1, mb, mb},
				{mb, ms, -1, mc, mr, mc, -1, ms, mb},
				{mb, ms, mr, mr, ma, mr, mr, ms, mb},
				{mb, ms, -1, mc, mr, mc, -1, ms, mb},
				{mb, mb, -1, -1, mr, -1, -1, mb, mb},
				{-2, mb, mb, ms, ms, ms, mb, mb, -2},
				{-2, -2, mb, mb, mb, mb, mb, -2, -2},
		};

		for (int i = 0; i < metas.length; i++) {
			for (int k = 0; k < metas.length; k++) {
				int m = metas[i][k];
				int dx = x+i-metas.length/2;
				int dz = z+k-metas.length/2;
				if (m >= 0) {
					array.setBlock(dx, y-1, dz, crystalstone, m);
				}
				else if (m == -1) {
					array.setBlock(dx, y-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal());
				}
			}
		}

		int[] m = {StoneTypes.COLUMN.ordinal(), StoneTypes.COLUMN.ordinal(), StoneTypes.GLOWCOL.ordinal(), StoneTypes.COLUMN.ordinal(), StoneTypes.CORNER.ordinal()};
		for (int i = 0; i < m.length; i++) {
			int meta = m[i];
			int dy = y+i;
			array.setBlock(x+4, dy, z, crystalstone, meta);
			array.setBlock(x-4, dy, z, crystalstone, meta);
			array.setBlock(x, dy, z+4, crystalstone, meta);
			array.setBlock(x, dy, z-4, crystalstone, meta);
		}

		array.setBlock(x+3, y+4, z, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x-3, y+4, z, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x, y+4, z-3, crystalstone, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x, y+4, z+3, crystalstone, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x+2, y+4, z, crystalstone, StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x-2, y+4, z, crystalstone, StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x, y+4, z-2, crystalstone, StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x, y+4, z+2, crystalstone, StoneTypes.ENGRAVED.ordinal());

		array.setBlock(x, y, z, ChromaTiles.TELEPORT.getBlock(), ChromaTiles.TELEPORT.getBlockMetadata());

		return array;
	}

}
