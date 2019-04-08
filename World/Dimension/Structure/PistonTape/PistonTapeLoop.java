package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class PistonTapeLoop extends StructurePiece<PistonTapeGenerator> {

	private final MultiMap<ForgeDirection, Coordinate> pistons = new MultiMap(CollectionType.HASHSET);

	public PistonTapeLoop(PistonTapeGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int l = parent.getLength()+3;
		while (l%4 != 2) {
			l++;
		}
		int d1 = (l-2)/4;
		int d2 = d1+1;
		for (int i = 0; i < 9; i++) {
			this.addPiston(world, x, y, z+i, ForgeDirection.UP);
		}
	}

	private void addPiston(ChunkSplicedGenerationCache world, int x, int y, int z, ForgeDirection dir) {
		pistons.addValue(dir, new Coordinate(x, y, z));
		world.setBlock(x, y, z, Blocks.piston, dir.ordinal());
	}

}
