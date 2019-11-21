package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class CompoundRepeaterStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		this.setTile(array, x, y, z, ChromaTiles.COMPOUND);
		array.setBlock(x, y-1, z, crystalstone, 12);
		array.setBlock(x, y-2, z, crystalstone, 2);
		array.setBlock(x, y-3, z, crystalstone, 13);
		array.setBlock(x, y-4, z, crystalstone, 2);
		array.setBlock(x, y-5, z, crystalstone, 12);
		return array;
	}

}
