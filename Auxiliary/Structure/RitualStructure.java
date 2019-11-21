package Reika.ChromatiCraft.Auxiliary.Structure;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class RitualStructure extends ChromaStructureBase {

	public final boolean isEnhanced;

	private boolean allowEnhance;
	private boolean requireEnhance;

	public RitualStructure(boolean enhance) {
		isEnhanced = enhance;
		this.initializeEnhance(enhance, enhance);
	}

	public void initializeEnhance(boolean allow, boolean require) {
		requireEnhance = require;
		allowEnhance = allow;
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				for (int j = 1; j <= 4; j++)
					array.setEmpty(x+i, y+j, z+k, true, true);
			}
		}

		for (int i = -5; i <= 5; i++) {
			for (int k = -5; k <= 5; k++) {
				array.setBlock(x+i, y, z+k, b, 0);
			}
		}

		for (int i = -4; i <= 4; i++) {
			for (int k = -4; k <= 4; k++) {
				array.setBlock(x+i, y+1, z+k, b, 0);
			}
		}

		for (int i = -4; i <= 4; i++) {
			if (requireEnhance) {
				array.setBlock(x-4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+i, y+1, z-4, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+i, y+1, z+4, b, StoneTypes.GLOWBEAM.ordinal());
			}
			else {
				array.setBlock(x-4, y+1, z+i, b, 1);
				array.setBlock(x+4, y+1, z+i, b, 1);
				array.setBlock(x+i, y+1, z-4, b, 1);
				array.setBlock(x+i, y+1, z+4, b, 1);
				if (allowEnhance) {
					array.addBlock(x-4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+i, y+1, z-4, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+i, y+1, z+4, b, StoneTypes.GLOWBEAM.ordinal());
				}
			}
		}

		for (int i = -3; i <= 3; i++) {
			array.setBlock(x-3, y+2, z+i, b, 1);
			array.setBlock(x+3, y+2, z+i, b, 1);
			array.setBlock(x+i, y+2, z-3, b, 1);
			array.setBlock(x+i, y+2, z+3, b, 1);
		}

		if (requireEnhance) {
			array.setBlock(x+2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x-2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x+2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x-2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
		}
		else {
			array.setBlock(x+2, y+2, z+2, b, 2);
			array.setBlock(x-2, y+2, z+2, b, 2);
			array.setBlock(x+2, y+2, z-2, b, 2);
			array.setBlock(x-2, y+2, z-2, b, 2);
			if (allowEnhance) {
				array.addBlock(x+2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x-2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x+2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x-2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
			}
		}

		array.setBlock(x+2, y+3, z+2, b, 7);
		array.setBlock(x-2, y+3, z+2, b, 7);
		array.setBlock(x+2, y+3, z-2, b, 7);
		array.setBlock(x-2, y+3, z-2, b, 7);

		array.setBlock(x+3, y+2, z+3, b, 8);
		array.setBlock(x-3, y+2, z+3, b, 8);
		array.setBlock(x+3, y+2, z-3, b, 8);
		array.setBlock(x-3, y+2, z-3, b, 8);

		array.setBlock(x+4, y+1, z+4, b, 8);
		array.setBlock(x-4, y+1, z+4, b, 8);
		array.setBlock(x+4, y+1, z-4, b, 8);
		array.setBlock(x-4, y+1, z-4, b, 8);

		array.setBlock(x-1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance(), 0);

		array.setBlock(x, y+2, z, ChromaTiles.RITUAL.getBlock(), ChromaTiles.RITUAL.getBlockMetadata());

		array.remove(x, y, z);

		return array;
	}

}
