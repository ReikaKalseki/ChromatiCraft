/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;

public class CrystalMusicManager {

	public static final CrystalMusicManager instance = new CrystalMusicManager();

	private final EnumMap<CrystalElement, MusicKey> baseKeys = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, ArrayList<MusicKey>> allKeys = new EnumMap(CrystalElement.class);
	private final MultiMap<MusicKey, CrystalElement> sourceElements = new MultiMap(CollectionType.HASHSET);

	private static final Random rand = new Random();

	private CrystalMusicManager() {
		this.addTonic(CrystalElement.BLACK, MusicKey.C4);
		this.addTonic(CrystalElement.BROWN, MusicKey.D4);
		this.addTonic(CrystalElement.BLUE, MusicKey.E4);
		this.addTonic(CrystalElement.GREEN, MusicKey.F4);
		this.addTonic(CrystalElement.RED, MusicKey.G4);
		this.addTonic(CrystalElement.PURPLE, MusicKey.A4);
		this.addTonic(CrystalElement.MAGENTA, MusicKey.B4);
		this.addTonic(CrystalElement.CYAN, MusicKey.A4);
		this.addTonic(CrystalElement.LIGHTGRAY, MusicKey.D5);
		this.addTonic(CrystalElement.GRAY, MusicKey.C5);
		this.addTonic(CrystalElement.LIME, MusicKey.E5);
		this.addTonic(CrystalElement.PINK, MusicKey.F5);
		this.addTonic(CrystalElement.YELLOW, MusicKey.G5);
		this.addTonic(CrystalElement.LIGHTBLUE, MusicKey.A5);
		this.addTonic(CrystalElement.ORANGE, MusicKey.E4);
		this.addTonic(CrystalElement.WHITE, MusicKey.C6);

		/*
		baseKeys.put(CrystalElement.BLACK, MusicKey.C4);
		baseKeys.put(CrystalElement.RED, MusicKey.G4);
		baseKeys.put(CrystalElement.GREEN, MusicKey.F4);*
		baseKeys.put(CrystalElement.BROWN, MusicKey.D4);
		baseKeys.put(CrystalElement.BLUE, MusicKey.E4);
		baseKeys.put(CrystalElement.PURPLE, MusicKey.A4);
		baseKeys.put(CrystalElement.CYAN, MusicKey.A4);
		baseKeys.put(CrystalElement.LIGHTGRAY, MusicKey.D5);*
		baseKeys.put(CrystalElement.GRAY, MusicKey.C5);*
		baseKeys.put(CrystalElement.PINK, MusicKey.F5);
		baseKeys.put(CrystalElement.LIME, MusicKey.E5);
		baseKeys.put(CrystalElement.YELLOW, MusicKey.G5);
		baseKeys.put(CrystalElement.LIGHTBLUE, MusicKey.A5);
		baseKeys.put(CrystalElement.MAGENTA, MusicKey.B4);
		baseKeys.put(CrystalElement.ORANGE, MusicKey.E4);
		baseKeys.put(CrystalElement.WHITE, MusicKey.C6);

		 */
	}

	private void addTonic(CrystalElement e, MusicKey m) {
		baseKeys.put(e, m);

		ArrayList<MusicKey> li = new ArrayList();
		li.add(m);
		li.add(this.isMinorKey(e) ? m.getMinorThird() : m.getMajorThird());
		li.add(m.getFifth());
		li.add(m.getOctave());
		allKeys.put(e, li);

		sourceElements.addValue(m, e);
		sourceElements.addValue(this.isMinorKey(e) ? m.getMinorThird() : m.getMajorThird(), e);
		sourceElements.addValue(m.getFifth(), e);
		sourceElements.addValue(m.getOctave(), e);
		//ReikaJavaLibrary.pConsole("Loaded Notes "+m+"/"+(this.isMinorKey(e) ? m.getMinorThird() : m.getMajorThird())+"/"+m.getFifth()+"/"+m.getOctave()+" for color "+e);
	}

	public int getBasePitch(CrystalElement e) {
		return baseKeys.get(e).pitch;
	}

	public double getPitchFactor(MusicKey key) {
		return key.getRatio(MusicKey.C5);
	}

	public double getThird(CrystalElement e) {
		MusicKey key = baseKeys.get(e);
		double base = this.getDingPitchScale(e);
		return this.isMinorKey(e) ? base*key.getMinorThird().getRatio(key) : base*key.getMajorThird().getRatio(key);
	}

	public double getFifth(CrystalElement e) {
		MusicKey key = baseKeys.get(e);
		double base = this.getDingPitchScale(e);
		return base*key.getFifth().getRatio(key);
	}

	public double getOctave(CrystalElement e) {
		MusicKey key = baseKeys.get(e);
		double base = this.getDingPitchScale(e);
		return base*key.getOctave().getRatio(key);
	}

	public int getIntervalFor(CrystalElement e, MusicKey key) {
		MusicKey base = baseKeys.get(e);
		if (base == key) {
			return 0;
		}
		else if ((this.isMinorKey(e) ? base.getMinorThird() : base.getMajorThird()) == key) {
			return 1;
		}
		else if (base.getFifth() == key) {
			return 2;
		}
		else if (base.getOctave() == key) {
			return 3;
		}
		else {
			return -1;
		}
	}

	public double getDingPitchScale(CrystalElement e) {
		return this.getPitchFactor(baseKeys.get(e));
	}

	public float getRandomScaledDing(CrystalElement e) {
		int n = rand.nextInt(4);
		return this.getScaledDing(e, n);
	}

	public float getScaledDing(CrystalElement e, int n) {
		MusicKey key = baseKeys.get(e);
		double base = this.getDingPitchScale(e);
		switch(n) {
			case 0:
				;//base *= 1;
				break;
			case 1:
				base *= this.isMinorKey(e) ? key.getMinorThird().getRatio(key) : key.getMajorThird().getRatio(key);
				break;
			case 2:
				base *= key.getFifth().getRatio(key);
				break;
			case 3:
				base *= key.getOctave().getRatio(key);
				break;
		}
		return (float)base;
	}

	public boolean isMinorKey(CrystalElement e) {
		if (e == CrystalElement.CYAN || e == CrystalElement.ORANGE || e == CrystalElement.GREEN || e == CrystalElement.LIGHTGRAY || e == CrystalElement.GRAY)
			return true;
		return CrystalPotionController.isBadPotion(e);
	}

	public List<MusicKey> getKeys(CrystalElement e) {
		return Collections.unmodifiableList(allKeys.get(e));
	}

	public KeySignature getSignature(CrystalElement e) {
		if (this.isMinorKey(e)) {
			return KeySignature.getByMinorTonic(baseKeys.get(e));
		}
		else {
			return KeySignature.getByTonic(baseKeys.get(e));
		}
	}

	public HashSet<CrystalElement> getFullChordMixes(KeySignature ks) {
		HashSet<CrystalElement> set = new HashSet();
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			boolean flag = true;
			for (MusicKey ms : this.getKeys(e)) {
				Note n = ms.getNote();
				if (!ks.isNoteValid(n)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				set.add(e);
			}
		}
		return set;
	}

	public HashSet<MusicKey> getValidNotesToMixWith(CrystalElement main) {
		HashSet<MusicKey> set = new HashSet();
		KeySignature ks = this.getSignature(main);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			for (MusicKey ms : this.getKeys(e)) {
				Note n = ms.getNote();
				if (ks.isNoteValid(n)) {
					Collection<MusicKey> keys = MusicKey.getAllOf(n);
					for (MusicKey key : keys) {
						if (this.canPlayKey(key))
							set.add(key);
					}
				}
			}
		}
		return set;
	}

	public CrystalElement getColorForKeySignature(KeySignature ks) {
		ArrayList<CrystalElement> li = new ArrayList(this.getFullChordMixes(ks));
		return !li.isEmpty() ? li.get(0) : null;
	}

	public Set<CrystalElement> getColorsWithKey(MusicKey key) {
		return (Set<CrystalElement>)sourceElements.get(key);
	}

	public Set<CrystalElement> getColorsWithKeyAnyOctave(MusicKey key) {
		Set<CrystalElement> set = this.getColorsWithKey(key);
		if (set.isEmpty()) {
			set = this.getColorsWithKey(key.getOctave());
		}
		if (set.isEmpty()) {
			set = this.getColorsWithKey(key.getInterval(-12));
		}
		if (set.isEmpty()) {
			set = this.getColorsWithKey(key.getInterval(-24));
		}
		if (set.isEmpty()) {
			set = this.getColorsWithKey(key.getInterval(-36));
		}
		if (set.isEmpty()) {
			set = this.getColorsWithKey(key.getInterval(24));
		}
		return set;
	}

	public boolean canPlayKey(MusicKey key) {
		return sourceElements.containsKey(key);
	}

	public CrystalNote getNote(CrystalElement e, int step) {
		return new CrystalNote(e, allKeys.get(e).get(step));
	}

	public static class CrystalNote {

		public final CrystalElement color;
		public final MusicKey key;

		private CrystalNote(CrystalElement e, MusicKey m) {
			color = e;
			key = m;
		}

	}

}
