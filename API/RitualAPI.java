/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

/** Use this class to add new rituals or test the activity state on existing ones. */
public interface RitualAPI {

	/** Call this to determine if a player is currently undergoing a ritual and should be blocked from things like using jetpacks and teleporting. */
	public boolean isPlayerUndergoingRitual(EntityPlayer ep);

	/** Call this to add an ability ritual to the table. Args: Ability given by completing the ritual, then a map of the required crystal
	 * energy to perform this ritual. Consult {@link=CrystalElementProxy} for what elements may be appropriate; typical energy magnitudes
	 * range from 2000 to 50000. Like the casting table, excessively large costs may render the recipe impossible due to their costs being
	 * larger than the storage capacity of the table itself. */
	public void addRitual(Ability a, HashMap<CrystalElementProxy, Integer> elements);
}
