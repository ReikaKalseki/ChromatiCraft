package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class BoostedRelayStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				array.setBlock(x+i, y-2, z+k, b, StoneTypes.SMOOTH.ordinal());
			}
		}

		array.setBlock(x-1, y-2, z, b, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+1, y-2, z, b, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x, y-2, z-1, b, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x, y-2, z+1, b, StoneTypes.GROOVE2.ordinal());

		array.setBlock(x+1, y-2, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y-2, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y-2, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y-2, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);

		array.setBlock(x+1, y-3, z+1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-3, z+1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+1, y-3, z-1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-3, z-1, b, StoneTypes.SMOOTH.ordinal());

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				array.setEmpty(x+i, y-1, z+k, false, false);
			}
		}

		array.setBlock(x, y-1, z, b, StoneTypes.FOCUSFRAME.ordinal());

		array.setBlock(x-2, y-1, z, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-1, z, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-1, z-2, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-1, z+2, b, StoneTypes.BRICKS.ordinal());

		array.setBlock(x+2, y-1, z+2, b, StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x-2, y-1, z+2, b, StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x+2, y-1, z-2, b, StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x-2, y-1, z-2, b, StoneTypes.EMBOSSED.ordinal());

		TileEntityRelaySource te = new TileEntityRelaySource();
		if (this.isDisplay())
			te.setEnhanced(true);
		array.setBlock(x, y, z, ChromaTiles.RELAYSOURCE.getBlock(), ChromaTiles.RELAYSOURCE.getBlockMetadata(), te, "enhance");
		return array;
	}

}
