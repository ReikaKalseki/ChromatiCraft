/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;



public class ProgressionEvent extends PlayerEvent {

	public final String researchKey;
	public final ResearchType type;

	public ProgressionEvent(EntityPlayer player, String research, ResearchType type) {
		super(player);
		researchKey = research;
		this.type = type;
	}

	public static enum ResearchType {
		PROGRESS(),
		FRAGMENT(),
		COLOR(),
		DIMSTRUCT();
	}

}
