package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Structure.Worldgen.PylonStructure;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityPylonTurboCharger.Location;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class PylonTurboStructure extends PylonStructure {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = super.getArray(world, x, y, z);
		y -= 9;

		array.setBlock(x, y+9, z,  ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata());

		BlockKey[] col = new BlockKey[]{
				new BlockKey(b, StoneTypes.COLUMN.ordinal()),
				new BlockKey(b, StoneTypes.FOCUS.ordinal()),
				new BlockKey(ChromaTiles.PYLONTURBO.getBlock(), ChromaTiles.PYLONTURBO.getBlockMetadata()),
		};

		for (int l = 0; l < Location.list.length; l++) {
			Location loc = Location.list[l];
			Coordinate c = loc.position;
			for (int i = 0; i < col.length; i++) {
				array.setBlock(x+c.xCoord, y+1+i, z+c.zCoord, col[i].blockID, col[i].metadata);
			}
		}

		for (Coordinate c : TileEntityCrystalPylon.getPowerCrystalLocations()) {
			this.setTile(array, x+c.xCoord, y+9+c.yCoord, z+c.zCoord, ChromaTiles.CRYSTAL);
		}

		this.setTile(array, x, y+1, z, ChromaTiles.PYLONTURBO);

		return array;
	}

}
