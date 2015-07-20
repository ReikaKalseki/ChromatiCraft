package Reika.ChromatiCraft.World.Dimension.Structure.DataStorage;

import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;

public class MusicStructureData extends StructureData {

	public MusicStructureData(DimensionStructureGenerator gen) {
		super(gen);
	}

	@Override
	public void load() {
		MusicPuzzleGenerator mus = (MusicPuzzleGenerator)generator;

	}

}
