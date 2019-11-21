package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ColoredStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class PylonStructure extends ColoredStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		y -= 9;
		FilledBlockArray array = new FilledBlockArray(world);
		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			Block b2 = n == 0 ? b : Blocks.air;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					if (b2 == Blocks.air)
						array.setEmpty(dx, dy, dz, false, false);
					else
						array.setBlock(dx, dy, dz, b2, 0);
					if (dir.offsetX == 0) {
						if (b2 == Blocks.air) {
							array.setEmpty(dx+dir.offsetZ, dy, dz, false, false);
							array.setEmpty(dx-dir.offsetZ, dy, dz, false, false);
						}
						else {
							array.setBlock(dx+dir.offsetZ, dy, dz, b2, 0);
							array.setBlock(dx-dir.offsetZ, dy, dz, b2, 0);
						}
					}
					else if (dir.offsetZ == 0) {
						if (b2 == Blocks.air) {
							array.setEmpty(dx, dy, dz+dir.offsetX, false, false);
							array.setEmpty(dx, dy, dz-dir.offsetX, false, false);
						}
						else {
							array.setBlock(dx, dy, dz+dir.offsetX, b2, 0);
							array.setBlock(dx, dy, dz-dir.offsetX, b2, 0);
						}
					}
				}
			}
		}

		for (int i = 1; i <= 5; i++) {
			int dy = y+i;
			Block b2 = i < 5 ? b : ChromaBlocks.RUNE.getBlockInstance();
			int meta = (i == 2 || i == 3) ? 2 : (i == 4 ? 7 : 8);
			if (i == 5) //rune
				meta = this.getCurrentColor().ordinal();
			array.setBlock(x-3, dy, z+1, b2, meta);
			array.setBlock(x-3, dy, z-1, b2, meta);

			array.setBlock(x+3, dy, z+1, b2, meta);
			array.setBlock(x+3, dy, z-1, b2, meta);

			array.setBlock(x-1, dy, z+3, b2, meta);
			array.setBlock(x-1, dy, z-3, b2, meta);

			array.setBlock(x+1, dy, z+3, b2, meta);
			array.setBlock(x+1, dy, z-3, b2, meta);
		}

		for (int n = 1; n <= 7; n++) {
			int dy = y+n;
			for (int i = -1; i <= 1; i += 2) {
				int dx = x+i;
				for (int k = -1; k <= 1; k += 2) {
					int dz = z+k;
					int meta = n == 5 ? 3 : (n == 7 ? 5 : 2);
					array.setBlock(dx, dy, dz, b, meta);
				}
			}
		}

		array.setBlock(x-3, y+4, z, b, 4);
		array.setBlock(x+3, y+4, z, b, 4);
		array.setBlock(x, y+4, z-3, b, 4);
		array.setBlock(x, y+4, z+3, b, 4);


		array.setBlock(x-2, y+3, z+1, b, 1);
		array.setBlock(x-2, y+3, z-1, b, 1);

		array.setBlock(x+2, y+3, z+1, b, 1);
		array.setBlock(x+2, y+3, z-1, b, 1);

		array.setBlock(x-1, y+3, z+2, b, 1);
		array.setBlock(x-1, y+3, z-2, b, 1);

		array.setBlock(x+1, y+3, z+2, b, 1);
		array.setBlock(x+1, y+3, z-2, b, 1);

		array.remove(x, y+9, z);

		array.remove(x-3, y+6, z-1);
		array.remove(x-1, y+6, z-3);

		array.remove(x+3, y+6, z-1);
		array.remove(x+1, y+6, z-3);

		array.remove(x-3, y+6, z+1);
		array.remove(x-1, y+6, z+3);

		array.remove(x+3, y+6, z+1);
		array.remove(x+1, y+6, z+3);

		array.addBlock(x, y, z, b, StoneTypes.STABILIZER.ordinal());
		for (int i = 1; i <= 2; i++) {
			array.addBlock(x+i, y, z, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x-i, y, z, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x, y, z+i, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x, y, z-i, b, StoneTypes.RESORING.ordinal());
		}

		this.addTile(array, x, y, z, ChromaTiles.PYLONLINK);

		array.setEmpty(x, y+1, z, false, false);
		this.addTile(array, x, y+1, z, ChromaTiles.PYLONTURBO);
		array.setPlacementOverride(x, y, z, b, 0);
		array.setPlacementOverride(x, y+1, z, Blocks.air, 0);

		return array;
	}

}
