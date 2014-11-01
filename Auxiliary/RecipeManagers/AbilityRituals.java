/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.EnumMap;

import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AbilityRituals {

	private final EnumMap<Chromabilities, AbilityRitual> data = new EnumMap(Chromabilities.class);

	public static final AbilityRituals instance = new AbilityRituals();

	private AbilityRituals() {

		AbilityRitual rit = new AbilityRitual(Chromabilities.REACH);
		rit.addAura(CrystalElement.LIME, 20000);
		rit.addAura(CrystalElement.PURPLE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.MAGNET);
		rit.addAura(CrystalElement.LIME, 5000);
		rit.addAura(CrystalElement.WHITE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SONIC);
		rit.addAura(CrystalElement.BLACK, 5000);
		rit.addAura(CrystalElement.YELLOW, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SHIFT);
		rit.addAura(CrystalElement.LIME, 25000);
		rit.addAura(CrystalElement.YELLOW, 25000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.HEAL);
		rit.addAura(CrystalElement.MAGENTA, 50000);
		rit.addAura(CrystalElement.LIGHTBLUE, 10000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.SHIELD);
		rit.addAura(CrystalElement.RED, 20000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.FIREBALL);
		rit.addAura(CrystalElement.ORANGE, 10000);
		rit.addAura(CrystalElement.PINK, 4000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.COMMUNICATE);
		rit.addAura(CrystalElement.BLACK, 10000);
		rit.addAura(CrystalElement.RED, 4000);
		rit.addAura(CrystalElement.LIGHTGRAY, 4000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.HEALTH);
		rit.addAura(CrystalElement.MAGENTA, 25000);
		rit.addAura(CrystalElement.PURPLE, 5000);
		this.addRitual(rit);

		rit = new AbilityRitual(Chromabilities.PYLON);
		rit.addAura(CrystalElement.BLACK, 2000);
		rit.addAura(CrystalElement.YELLOW, 5000);
		rit.addAura(CrystalElement.RED, 25000);
		this.addRitual(rit);
	}

	private void addRitual(AbilityRitual ar) {
		data.put(ar.ability, ar);
	}

	public boolean hasRitual(Chromabilities c) {
		return data.containsKey(c);
	}

	public ElementTagCompound getAura(Chromabilities c) {
		return this.hasRitual(c) ? data.get(c).getRequiredAura() : new ElementTagCompound();
	}

	public int getDuration(Chromabilities c) {
		return this.hasRitual(c) ? data.get(c).duration : 0;
	}

	private static class AbilityRitual {

		private final ElementTagCompound energy = new ElementTagCompound();
		public final int duration;
		public final Chromabilities ability;

		private AbilityRitual(Chromabilities c) {
			ability = c;
			duration = 950;
		}

		private void addAura(CrystalElement e, int amt) {
			energy.addTag(e, amt);
		}

		public ElementTagCompound getRequiredAura() {
			return energy.copy();
		}

	}
}
