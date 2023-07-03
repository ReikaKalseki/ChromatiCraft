/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.DataStorage.MusicStructureData;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicLoot;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class MusicPuzzleGenerator extends DimensionStructureGenerator {

	private static final int LENGTH = 4+2*ChromaOptions.getStructureDifficulty();

	private static final ArrayList<MelodyPrefab> prefabs = new ArrayList();

	private final ArrayList<MusicPuzzle> puzzles = new ArrayList();

	private HashSet<MelodyPrefab> usedPrefabs = new HashSet();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		this.generatePuzzles(rand);

		posY = 20+rand.nextInt(80);

		int x = chunkX;
		int z = chunkZ;
		int y = posY;

		this.addDynamicStructure(new MusicEntrance(this), x, z);

		z += 3;

		for (MusicPuzzle p : puzzles) {
			p.generate(world, x, y, z);

			z += 20;
		}

		new MusicLoot(this).generate(world, x, y, z);
	}

	private void generatePuzzles(Random rand) {
		for (int i = 0; i < LENGTH; i++) {
			int len = this.getPuzzleLength(i);
			MusicPuzzle m = new MusicPuzzle(this, len, i);
			MelodyPrefab p = null;
			if (usedPrefabs.size() < prefabs.size() && rand.nextInt(5) == 0) {
				p = getRandomPrefab(rand, len*5/2, usedPrefabs);
			}
			if (p != null) {
				m.loadPrefab(p);
				usedPrefabs.add(p);
			}
			else {
				m.initialize(rand);
			}
			puzzles.add(m);
		}
	}

	private int getPuzzleLength(int step) {
		return Math.max(6, (2*ChromaOptions.getStructureDifficulty())+(step-9+ChromaOptions.getStructureDifficulty()));
	}

	public static MelodyPrefab getRandomPrefab(Random rand, int maxlen, HashSet<MelodyPrefab> exclusions) {
		HashSet<MelodyPrefab> validPrefabs = new HashSet(prefabs);
		if (exclusions != null)
			validPrefabs.removeAll(exclusions);
		Iterator<MelodyPrefab> it = validPrefabs.iterator();
		while (it.hasNext()) {
			MelodyPrefab pre = it.next();
			if (pre.notes.size() > maxlen)
				it.remove();
		}
		return validPrefabs.isEmpty() ? null : ReikaJavaLibrary.getRandomCollectionEntry(rand, validPrefabs);
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
		usedPrefabs.clear();
	}

	@Override
	public StructureData createDataStorage() {
		return new MusicStructureData(this);
	}

	@Override
	public boolean hasBeenSolved(World world) {
		for (MusicPuzzle m : puzzles) {
			if (!m.isSolved) {
				return false;
			}
		}
		return true;
	}

	public void completePuzzle(int structureIndex) {
		puzzles.get(structureIndex).isSolved = true;
	}

	@Override
	public void openStructure(World world) {
		for (int i = 0; i < puzzles.size(); i++) {
			((TileEntityChromaDoor)puzzles.get(i).controllerLocation.offset(0, 0, 13).getTileEntity(world)).open(-1);
			this.completePuzzle(i);
		}
	}

	static {
		addPrefabMelody(8, MusicKey.C5, null, MusicKey.G4, null, MusicKey.A4, null, MusicKey.E4, null, MusicKey.F4, null, MusicKey.C4, null, MusicKey.F4, null, MusicKey.G4, null);
		addPrefabMelody(8, MusicKey.G5, MusicKey.A5, MusicKey.B5, MusicKey.D6, MusicKey.C6, MusicKey.C6, MusicKey.E6, MusicKey.D6, MusicKey.D6, MusicKey.G6, MusicKey.Fs6, MusicKey.G6, MusicKey.D6, MusicKey.B5, MusicKey.G5, MusicKey.A5, MusicKey.B5, MusicKey.C6, MusicKey.D6, MusicKey.E6, MusicKey.D6, MusicKey.C6, MusicKey.B5, MusicKey.A5, MusicKey.B5, MusicKey.G5, MusicKey.Fs5, MusicKey.G5, MusicKey.A5, MusicKey.D5);

		addPrefabMelody(8, MusicKey.F5, MusicKey.C5, MusicKey.F5, MusicKey.C5, MusicKey.F5, MusicKey.Cs5, MusicKey.Eb5, MusicKey.G5, MusicKey.Ab5, MusicKey.G5, MusicKey.Eb5, MusicKey.F5);
		addPrefabMelody(8, MusicKey.A4, MusicKey.A4, MusicKey.C5, MusicKey.G4, MusicKey.E4, MusicKey.A4, MusicKey.C5, MusicKey.G4);
		addPrefabMelody(8, MusicKey.A4, MusicKey.B4, MusicKey.Cs5, MusicKey.E5, MusicKey.A4, MusicKey.B4, MusicKey.Cs5, MusicKey.E5, MusicKey.A4, MusicKey.B4, MusicKey.C5, MusicKey.D5, MusicKey.B4);
		addPrefabMelody(8, MusicKey.A4, MusicKey.B4, MusicKey.D5, MusicKey.B4, MusicKey.E5, MusicKey.Fs5, MusicKey.D5, MusicKey.B4, MusicKey.D5, MusicKey.B4, MusicKey.Cs5);
		addPrefabMelody(8, MusicKey.G5, MusicKey.E5, MusicKey.D5, MusicKey.B4, MusicKey.E5, MusicKey.C5, MusicKey.G5, null, MusicKey.B4, MusicKey.D5, MusicKey.G5, MusicKey.A5, MusicKey.Fs5);
		addPrefabMelody(8, MusicKey.G4, MusicKey.D4, MusicKey.A4, MusicKey.D4, MusicKey.Bb4, MusicKey.D4, MusicKey.C5, MusicKey.D5, MusicKey.Bb4, MusicKey.Eb4, MusicKey.C5, MusicKey.Eb4, MusicKey.A4, MusicKey.Eb4, MusicKey.F4, MusicKey.D4);
		addPrefabMelody(8, MusicKey.C5, MusicKey.D5, MusicKey.Eb5, MusicKey.C5, MusicKey.G4, MusicKey.Ab4, MusicKey.Bb4, MusicKey.G4, MusicKey.F4, MusicKey.G4, MusicKey.Ab4, MusicKey.Bb4, MusicKey.F4);
		addPrefabMelody(8, MusicKey.Ab5, MusicKey.Eb5, MusicKey.Ab5, MusicKey.Eb6, MusicKey.B5, MusicKey.Ab5, MusicKey.B5, MusicKey.Ab5, MusicKey.B5, MusicKey.Eb5, MusicKey.Eb6, MusicKey.Ab5, MusicKey.Eb5, MusicKey.B5, MusicKey.Eb6, MusicKey.B5, MusicKey.B5, MusicKey.Ab5, MusicKey.B5, MusicKey.E6, MusicKey.Fs5, MusicKey.E6, MusicKey.B5, MusicKey.B5, MusicKey.Fs5, MusicKey.Bb5, MusicKey.Eb5, MusicKey.Cs6, MusicKey.B5);

		addPrefabMelody(8, MusicKey.G5, MusicKey.D5, MusicKey.G5, MusicKey.D5, MusicKey.G5, MusicKey.Eb5, MusicKey.G5, MusicKey.Eb5, MusicKey.A5, MusicKey.Eb5, MusicKey.Bb5, MusicKey.Eb5, MusicKey.F5, MusicKey.D5, MusicKey.F5, MusicKey.D5, MusicKey.G5, MusicKey.D5, MusicKey.A5, MusicKey.D5, MusicKey.A5, MusicKey.D5, MusicKey.A5, MusicKey.F5, MusicKey.G5);

		addPrefabMelody(8, MusicKey.E5, MusicKey.E5, MusicKey.F5, MusicKey.G5, MusicKey.G5, MusicKey.F5, MusicKey.E5, MusicKey.D5, MusicKey.C5, MusicKey.C5, MusicKey.D5, MusicKey.E5, MusicKey.E5, null, MusicKey.D5, MusicKey.D5, null, MusicKey.E5, MusicKey.E5, MusicKey.F5, MusicKey.G5, MusicKey.G5, MusicKey.F5, MusicKey.E5, MusicKey.D5, MusicKey.C5, MusicKey.C5, MusicKey.D5, MusicKey.E5, MusicKey.D5, null, MusicKey.C5, MusicKey.C5);

		addPrefabMelody(7, MusicKey.D5, null, MusicKey.A5, null, MusicKey.Cs6, null, null, MusicKey.A5, MusicKey.D6, MusicKey.Cs6, MusicKey.B5, MusicKey.A5, MusicKey.B5, MusicKey.Cs6, MusicKey.A5, MusicKey.E5, null, MusicKey.A5, null, MusicKey.Cs6, null, null, MusicKey.A5, MusicKey.D6, MusicKey.Cs6, MusicKey.B5, MusicKey.A5, MusicKey.B5, MusicKey.Cs6, MusicKey.E6, MusicKey.Cs6, MusicKey.A5);
		addPrefabMelody(5, MusicKey.G5, null, null, MusicKey.D5, null, null, MusicKey.G5, null, null, MusicKey.D5, null, null, MusicKey.G5, null, MusicKey.A5, null, MusicKey.G5, null, null, MusicKey.Fs5, null, null, MusicKey.G5, null, null, MusicKey.Fs5, null, null, MusicKey.C6, null, MusicKey.B5, null, MusicKey.G5, null, null, MusicKey.D5, null, null, MusicKey.B4, null, null, MusicKey.G5, null, null, MusicKey.C5, null, MusicKey.B4, null, MusicKey.A5, null, null, MusicKey.B5, null, null, MusicKey.A5, null, null, MusicKey.B5, null, null, MusicKey.A5, MusicKey.B5, MusicKey.D6, MusicKey.B5, MusicKey.G5);
	}

	private static void addPrefabMelody(int pace, MusicKey... notes) {
		ArrayList<MusicKey> li = ReikaJavaLibrary.makeListFrom(notes);
		int shift = verify(li);
		if (shift > 0) {
			for (int i = 0; i < li.size(); i++) {
				MusicKey mus = li.get(i);
				if (mus != null)
					li.set(i, mus.getInterval(shift));
			}
		}
		prefabs.add(new MelodyPrefab(pace, li));
	}

	private static int verify(ArrayList<MusicKey> li) {
		for (int d = 0; d < 12; d++) {
			boolean flag = true;
			for (MusicKey key : li) {
				if (key == null)
					continue;
				if ((d != 0 && key.getInterval(d) == key) || !CrystalMusicManager.instance.canPlayKey(key.getInterval(d))) {
					flag = false;
					break;
				}
			}
			if (flag)
				return d;
		}
		for (int d = -1; d > -12; d--) {
			boolean flag = true;
			for (MusicKey key : li) {
				if (key == null)
					continue;
				if ((d != 0 && key.getInterval(d) == key) || !CrystalMusicManager.instance.canPlayKey(key.getInterval(d))) {
					flag = false;
					break;
				}
			}
			if (flag)
				return d;
		}
		throw new RegistrationException(ChromatiCraft.instance, "Invalid prefab melody!");
	}

	public static final class MelodyPrefab {

		private final ArrayList<MusicKey> notes;
		public final CrystalElement center;
		public final KeySignature key;
		public final int playbackRate;

		private MelodyPrefab(int rate, ArrayList<MusicKey> li) {
			playbackRate = rate;
			notes = li;
			key = ReikaMusicHelper.KeySignature.findSignature(li);
			center = key != null ? CrystalMusicManager.instance.getColorForKeySignature(key) : null;
		}

		public List<MusicKey> getNotes() {
			return Collections.unmodifiableList(notes);
		}

		@Override
		public String toString() {
			return key+" @ "+center+" $ "+notes.toString();
		}

		@Override
		public int hashCode() {
			return notes.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof MelodyPrefab && ((MelodyPrefab)o).notes.equals(notes);
		}

	}

}
