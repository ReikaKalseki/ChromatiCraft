package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class WirelessPedestalStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		this.setTile(array, x, y, z, ChromaTiles.WIRELESS);


		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			array.setEmpty(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, false, false);
		}

		/*
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				for (int k = -1; k <= 1; k += 2) {
					array.setBlock(x+i, y+j, z+k, Blocks.glowstone);
					if (ModList.MYSTCRAFT.isLoaded())
						array.addBlock(x+i, y+j, z+k, MystCraftHandler.getInstance().crystalID);
				}
			}
		}
		 */

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				array.setEmpty(x+i, y-1, z+k, false, false);

				StoneTypes s = i == 0 || k == 0 ? StoneTypes.BRICKS : StoneTypes.CORNER;
				if (i == 0 && k == 0)
					s = StoneTypes.MULTICHROMIC;
				array.setBlock(x+i, y-2, z+k, crystalstone, s.ordinal());

				s = i == 0 || k == 0 ? StoneTypes.SMOOTH : StoneTypes.COLUMN;
				array.setBlock(x+i, y-3, z+k, crystalstone, s.ordinal());
				if (s == StoneTypes.COLUMN) {
					array.addBlock(x+i, y-3, z+k, crystalstone, StoneTypes.GLOWCOL.ordinal());
				}
			}
		}

		for (int i = -3; i <= 3; i++) {
			for (int k = -3; k <= 3; k++) {
				int m = StoneTypes.SMOOTH.ordinal();
				if (Math.abs(k) == 3)
					m = StoneTypes.GROOVE1.ordinal();
				if (Math.abs(i) == 3)
					m = StoneTypes.GROOVE2.ordinal();
				if (Math.abs(k) == 3 && Math.abs(i) == 3)
					m = StoneTypes.BRICKS.ordinal();
				array.setBlock(x+i, y-4, z+k, crystalstone, m);
				if (m != StoneTypes.SMOOTH.ordinal()) {
					int m2 = m == StoneTypes.BRICKS.ordinal() ? StoneTypes.STABILIZER.ordinal() : StoneTypes.RESORING.ordinal();
					array.addBlock(x+i, y-4, z+k, crystalstone, m2);
				}
				if (Math.abs(i) <= 2 && Math.abs(k) <= 2) {
					if (Math.abs(i) > 1 || Math.abs(k) > 1) {
						array.setBlock(x+i, y-5, z+k, crystalstone, StoneTypes.SMOOTH.ordinal());
						array.setBlock(x+i, y-4, z+k, ChromaBlocks.LUMA.getBlockInstance());
					}
				}
			}
		}

		for (int i = 0; i <= 1; i++) {
			array.setBlock(x-2, y-3, z-i, crystalstone, StoneTypes.BEAM.ordinal());
			array.setBlock(x+2, y-3, z+i, crystalstone, StoneTypes.BEAM.ordinal());
			array.setBlock(x+i, y-3, z-2, crystalstone, StoneTypes.BEAM.ordinal());
			array.setBlock(x-i, y-3, z+2, crystalstone, StoneTypes.BEAM.ordinal());
		}

		return array;
	}

}
