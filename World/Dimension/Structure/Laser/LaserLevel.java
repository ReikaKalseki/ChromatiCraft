package Reika.ChromatiCraft.World.Dimension.Structure.Laser;

import java.io.IOException;

import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructureExport;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class LaserLevel extends StructurePiece {

	private final String name;
	private static final String PATH = "Structure Data/Laser";

	private StructureExport data;

	public boolean isSolved = false;

	public LaserLevel(LaserPuzzleGenerator s, String name) {
		super(s);

		data = new StructureExport(name, PATH, ChromatiCraft.class).addIgnoredBlock(new BlockKey(Blocks.stone)).addIgnoredBlock(new BlockKey(Blocks.air));
		this.name = name;
		try {
			data.load();
			BlockBox box = data.getBounds();
			Coordinate origin = new Coordinate(box.minX, box.minY, box.minZ).negate();
			data.offset(origin);
		}
		catch (IOException e) {
			throw new RuntimeException("Could not load structure data for laser level '"+name+"'", e);
		}
	}

	public int getLengthX() {
		return data.getBounds().getSizeX();
	}

	public int getLengthZ() {
		return data.getBounds().getSizeZ();
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Coordinate pos = new Coordinate(x, y, z);
		data.offset(pos);
		data.place(world);
	}

}
