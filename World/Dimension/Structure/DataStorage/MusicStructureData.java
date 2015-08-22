/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.DataStorage;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
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
