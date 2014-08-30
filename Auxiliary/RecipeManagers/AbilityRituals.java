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

import java.util.HashMap;

import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AbilityRituals {

	private final HashMap<Chromabilities, AbilityRitual> data = new HashMap();

	public static final AbilityRituals instance = new AbilityRituals();

	private AbilityRituals() {

		AbilityRitual rit = new AbilityRitual();
		rit.addAura(CrystalElement.LIME, 20000);
		rit.addAura(CrystalElement.PURPLE, 5000);
		data.put(Chromabilities.REACH, rit);

		rit = new AbilityRitual();
		rit.addAura(CrystalElement.LIME, 5000);
		rit.addAura(CrystalElement.WHITE, 5000);
		data.put(Chromabilities.MAGNET, rit);

		rit = new AbilityRitual();
		rit.addAura(CrystalElement.BLACK, 5000);
		rit.addAura(CrystalElement.YELLOW, 5000);
		data.put(Chromabilities.SONIC, rit);
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

		private AbilityRitual() {

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
