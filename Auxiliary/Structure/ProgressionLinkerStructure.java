package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class ProgressionLinkerStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		this.setTile(array, x, y, z, ChromaTiles.PROGRESSLINK);

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				for (int j = 1; j <= 2; j++) {
					StoneTypes s = StoneTypes.SMOOTH;
					if (j == 1) {
						if (i == 0 || k == 0) {
							if (i != 0 || k != 0) {
								s = StoneTypes.BRICKS;
							}
							else {
								s = null;
							}
						}
						else {
							s = StoneTypes.CORNER;
						}

					}
					if (j == 2) {
						if (i == 0 || k == 0) {

						}
						else {
							s = StoneTypes.COLUMN;
						}
					}
					if (s == null)
						array.setBlock(x+i, y-j, z+k, Blocks.glowstone);
					else
						array.setBlock(x+i, y-j, z+k, crystalstone, s.ordinal());
				}
			}
		}

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection dir2 = ReikaDirectionHelper.getRightBy90(dir);
			int dx = x+dir.offsetX*2;
			int dy = y-2;
			int dz = z+dir.offsetZ*2;
			array.setBlock(dx, dy, dz, crystalstone, StoneTypes.BEAM.ordinal());
			array.setBlock(dx+dir2.offsetX, dy, dz+dir2.offsetZ, crystalstone, StoneTypes.BEAM.ordinal());
		}

		return array;
	}

}
