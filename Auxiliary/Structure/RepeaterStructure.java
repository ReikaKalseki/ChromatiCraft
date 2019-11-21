package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ColoredStructureBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class RepeaterStructure extends ColoredStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		this.setTile(array, x, y, z, ChromaTiles.REPEATER);
		array.setBlock(x, y-1, z, ChromaBlocks.RUNE.getBlockInstance(), this.getCurrentColor().ordinal());
		array.setBlock(x, y-2, z, crystalstone, 0);
		array.setBlock(x, y-3, z, crystalstone, 0);
		return array;
	}

}
