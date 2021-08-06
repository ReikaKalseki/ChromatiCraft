/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class RitualCompletionEvent extends PlayerEvent {

	public final String abilityID;

	public RitualCompletionEvent(EntityPlayer ep, String id) {
		super(ep);

		abilityID = id;
	}

}
