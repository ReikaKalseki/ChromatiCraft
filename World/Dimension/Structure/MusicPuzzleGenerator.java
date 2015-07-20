/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.DataStorage.MusicStructureData;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;

public class MusicPuzzleGenerator extends DimensionStructureGenerator {

	private static final int LENGTH = 4+2*ChromaOptions.getStructureDifficulty();

	private final ArrayList<MusicPuzzle> puzzles = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {
		this.generatePuzzles(rand);
	}

	private void generatePuzzles(Random rand) {
		for (int i = 0; i < LENGTH; i++) {
			MusicPuzzle m = new MusicPuzzle(this, Math.max(3, 3+(i-3)));
			m.initialize(rand);
			puzzles.add(m);
		}
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	protected void clearCaches() {
		puzzles.clear();
	}

	@Override
	public StructureData createDataStorage() {
		return new MusicStructureData(this);
	}

}
