/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class MusicPuzzle extends StructurePiece {

	private final LinkedList<MusicKey> melody = new LinkedList();

	public final int length;

	public MusicPuzzle(DimensionStructureGenerator s, int len) {
		super(s);
		length = len;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	public void initialize(Random rand) {
		for (int i = 0; i < length; i++) {
			MusicKey key = this.randomKey(rand);
			melody.add(key);
		}
	}

	public List<MusicKey> getMelody() {
		return Collections.unmodifiableList(melody);
	}

	public boolean compare(ArrayList<MusicKey> li) {
		return li.equals(melody);
	}

	private MusicKey randomKey(Random rand) {
		CrystalElement e = CrystalElement.randomElement();
		List<MusicKey> keys = CrystalMusicManager.instance.getKeys(e);
		return keys.get(rand.nextInt(keys.size()));
	}

}
