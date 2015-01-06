/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;

/** Use this class to add new rituals or test the activity state on existing ones. */
public class RitualAPI {

	/** Call this to determine if a player is currently undergoing a ritual and should be blocked from things like using jetpacks and teleporting. */
	public static boolean isPlayerUndergoingRitual(EntityPlayer ep) {
		for (int i = 0; i < RitualType.list.length; i++) {
			RitualType r = RitualType.list[i];
			if (r.isPlayerUndergoingRitual(ep))
				return true;
		}
		return false;
	}

	/** Call this to add an ability ritual to the table. Args: Ability given by completing the ritual, then a map of the required crystal
	 * energy to perform this ritual. Consult {@link=CrystalElementProxy} for what elements may be appropriate; typical energy magnitudes
	 * range from 2000 to 50000. Like the casting table, excessively large costs may render the recipe impossible due to their costs being
	 * larger than the storage capacity of the table itself. */
	public static void addRitual(Ability a, HashMap<CrystalElementProxy, Integer> elements) {
		try {
			RitualType.ABILITY.addRitual.invoke(null, a, elements);
		}
		catch (Exception e) {
			System.out.println("Could not add ability "+a+"!");
			e.printStackTrace();
		}
	}

	/** Internal helper enum; do not touch this */
	private static enum RitualType {
		ABILITY("Reika.ChromatiCraft.Auxiliary.RecipeManagers.AbilityRituals", "addRitual", "isPlayerUndergoingRitual", Ability.class, HashMap.class),
		;

		private Class type;
		private Method addRitual;
		private Method testPlayer;

		private static final RitualType[] list = values();

		private RitualType(String cl, String add, String test, Class... args) {
			try {
				type = Class.forName(cl);
				addRitual = type.getDeclaredMethod(add, args);
				testPlayer = type.getDeclaredMethod(test, EntityPlayer.class);
			}
			catch (Exception e) {
				System.out.println("Could not construct ritual type "+this+"!");
				e.printStackTrace();
			}
		}

		private boolean isPlayerUndergoingRitual(EntityPlayer ep) {
			try {
				return (Boolean)testPlayer.invoke(null, ep);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		static {

		}
	}
}
