/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.entity.player.EntityPlayer;

import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;

import cpw.mods.fml.relauncher.Side;

class ResetWalkSpeedEvent implements ScheduledEvent {

	private final EntityPlayer player;
	//private final float walkSpeed;
	private final float prevHeight;

	ResetWalkSpeedEvent(EntityPlayer ep) {
		player = ep;
		//walkSpeed = ep.capabilities.getWalkSpeed();
		prevHeight = ep.stepHeight;
	}

	@Override
	public void fire() {
		//ReikaPlayerAPI.setPlayerWalkSpeed(player, walkSpeed);
		player.stepHeight = prevHeight;
	}

	@Override
	public boolean runOnSide(Side s) {
		return true;
	}

}
