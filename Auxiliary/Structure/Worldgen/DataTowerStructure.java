package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.Random;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockDummyAux.TileEntityDummyAux;
import Reika.ChromatiCraft.Block.BlockDummyAux.TileEntityDummyAux.Flags;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class DataTowerStructure extends ChromaStructureBase {

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Random r = DragonAPICore.rand;

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				array.setBlock(x+i, y, z+k, shield, r.nextInt(3) == 0 ? BlockType.MOSS.metadata : BlockType.STONE.metadata);
			}
		}

		array.setBlock(x+1, y+1, z, shield, r.nextInt(3) == 0 ? BlockType.MOSS.metadata : BlockType.STONE.metadata);
		array.setBlock(x-1, y+1, z, shield, r.nextInt(3) == 0 ? BlockType.MOSS.metadata : BlockType.STONE.metadata);
		array.setBlock(x, y+1, z+1, shield, r.nextInt(3) == 0 ? BlockType.MOSS.metadata : BlockType.STONE.metadata);
		array.setBlock(x, y+1, z-1, shield, r.nextInt(3) == 0 ? BlockType.MOSS.metadata : BlockType.STONE.metadata);

		array.setBlock(x, y+1, z, ChromaTiles.DATANODE.getBlock(), ChromaTiles.DATANODE.getBlockMetadata());

		for (int i = 0; i < 4; i++) {
			TileEntityDummyAux te = new TileEntityDummyAux();
			te.setFlag(Flags.HITBOX, true);
			te.setFlag(Flags.RENDER, false);
			te.setFlag(Flags.MOUSEOVER, false);
			te.link(new Coordinate(x, y+1, z));
			array.setBlock(x, y+2+i, z, ChromaBlocks.DUMMYAUX.getBlockInstance(), 0, te, "loc", "flags");
		}

		return array;
	}

}
