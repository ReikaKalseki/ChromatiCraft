/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.Comparator;

import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Registry.Chromabilities;

public final class AbilitySorter implements Comparator<Ability> {

	public static final AbilitySorter sorter = new AbilitySorter();

	private AbilitySorter() {

	}

	@Override
	public int compare(Ability o1, Ability o2) {
		if (o1 instanceof Chromabilities && o2 instanceof Chromabilities) {
			return ((Chromabilities)o1).ordinal()-((Chromabilities)o2).ordinal();
		}
		else if (o1 instanceof Chromabilities) {
			return Integer.MIN_VALUE;
		}
		else if (o2 instanceof Chromabilities) {
			return Integer.MAX_VALUE;
		}
		return Chromabilities.getAbilityInt(o1)-Chromabilities.getAbilityInt(o2);
	}

}
