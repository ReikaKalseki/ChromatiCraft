package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaCheck;
import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityMeteorTower;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class MeteorTowerStructure extends ChromaStructureBase {

	public final int tier;

	public MeteorTowerStructure(int t) {
		tier = t;
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int j = 12; j <= 14; j++) {
			int dy = y-j;
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					array.setBlock(x+i, dy, z+k, crystalstone, 0);
				}
			}
			for (int i = -2; i <= 2; i++) {
				int ml = j == 13 ? StoneTypes.RESORING.ordinal() : StoneTypes.GROOVE1.ordinal();
				int mc = j == 13 ? StoneTypes.COLUMN.ordinal() : StoneTypes.CORNER.ordinal();
				array.setBlock(x-2, dy, z+i, crystalstone, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+2, dy, z+i, crystalstone, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+i, dy, z-2, crystalstone, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+i, dy, z+2, crystalstone, Math.abs(i) == 2 ? mc : ml);
			}
		}

		int[][] cols = {{-2, -1}, {-2, 1}, {-1, 2}, {-1, -2}, {2, -1}, {2, 1}, {1, -2}, {1, 2}};

		for (int j = 2; j <= 11; j++) {
			int dy = y-j;
			for (int a = 0; a < cols.length; a++) {
				int[] col = cols[a];
				int dx = x+col[0];
				int dz = z+col[1];
				int m = j == 4 || j == 7 || j == 11 ? StoneTypes.BRICKS.ordinal() : StoneTypes.COLUMN.ordinal();
				if (j == 9 && tier == 2)
					m = StoneTypes.GLOWCOL.ordinal();
				array.setBlock(dx, dy, dz, crystalstone, m);
			}
		}

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x-2, y-1, z+i, crystalstone, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+2, y-1, z+i, crystalstone, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y-1, z-2, crystalstone, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y-1, z+2, crystalstone, StoneTypes.BRICKS.ordinal());
		}

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+i;
				int dz = z+k;
				if (i != 0 || k != 0) {
					array.setBlock(dx, y, dz, crystalstone, StoneTypes.BRICKS.ordinal());
				}
			}
		}

		TileEntityMeteorTower te = new TileEntityMeteorTower();
		ItemStack is = ChromaTiles.METEOR.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		te.setDataFromItemStackTag(is);
		array.setTile(x, y, z, ChromaTiles.METEOR.getBlock(), ChromaTiles.METEOR.getBlockMetadata(), te, "tier");

		for (int j = 1; j <= 2; j++) {
			int dy = y+j;
			for (int i = -1; i <= 1; i += 2) {
				for (int k = -1; k <= 1; k += 2) {
					int dx = x+i;
					int dz = z+k;
					int m = j == 1 ? StoneTypes.COLUMN.ordinal() : (tier == 0 ? StoneTypes.SMOOTH.ordinal() : StoneTypes.FOCUS.ordinal());
					array.setBlock(dx, dy, dz, crystalstone, m);
				}
			}
		}

		int[] h = {4, 7};

		for (int a = 0; a < h.length; a++) {
			int dy = y-h[a];
			int m = 0;
			switch(tier) {
				case 0:
				default:
					m = StoneTypes.SMOOTH.ordinal();
					break;
				case 1:
					m = StoneTypes.BEAM.ordinal();
					break;
				case 2:
					m = StoneTypes.GLOWBEAM.ordinal();
					break;
			}
			array.setBlock(x-2, dy, z, crystalstone, m);
			array.setBlock(x+2, dy, z, crystalstone, m);
			array.setBlock(x, dy, z-2, crystalstone, m);
			array.setBlock(x, dy, z+2, crystalstone, m);
		}

		if (tier > 0) {
			for (int j = h[0]; j <= h[1]; j++) {
				int dy = y-j;
				int m = j == h[0] || j == h[1] ? StoneTypes.SMOOTH.ordinal() : StoneTypes.STABILIZER.ordinal();
				array.setBlock(x-2, dy, z-2, crystalstone, m);
				array.setBlock(x+2, dy, z+2, crystalstone, m);
				if (tier == 2) {
					array.setBlock(x+2, dy, z-2, crystalstone, m);
					array.setBlock(x-2, dy, z+2, crystalstone, m);
				}
			}
		}

		CrystalElement e = null;
		switch(tier) {
			case 0:
			default:
				e = CrystalElement.LIME;
				break;
			case 1:
				e = CrystalElement.YELLOW;
				break;
			case 2:
				e = CrystalElement.RED;
				break;
		}

		ChromaCheck check = new ChromaCheck(e);
		array.setBlock(x, y-12, z, check);
		array.setBlock(x-1, y-12, z, check);
		array.setBlock(x+1, y-12, z, check);
		array.setBlock(x, y-12, z-1, check);
		array.setBlock(x, y-12, z+1, check);

		return array;
	}

}
