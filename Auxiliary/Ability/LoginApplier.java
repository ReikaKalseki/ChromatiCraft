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
import net.minecraft.entity.player.EntityPlayerMP;

import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;

class LoginApplier implements PlayerTracker {

	public static final LoginApplier instance = new LoginApplier();

	private LoginApplier() {

	}

	@Override
	public void onPlayerLogin(EntityPlayer ep) {
		if (Chromabilities.REACH.enabledOn(ep)) {
			Chromabilities.triggerAbility(ep, Chromabilities.REACH, 0, true);
		}
		if (Chromabilities.HEALTH.enabledOn(ep)) {
			if (ep instanceof EntityPlayerMP) {
				AbilityHelper.instance.syncHealth((EntityPlayerMP)ep);
			}
		}
		WarpPointData.readFromNBT(ep);
		//WarpPointData.initWarpData(ep.worldObj).setDirty(true);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		AbilityHelper.instance.isNoClipEnabled = false;
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo) {
		if (!player.worldObj.isRemote && Chromabilities.HEALTH.enabledOn(player)) {
			AbilityHelper.instance.syncHealth((EntityPlayerMP)player);
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {

	}

}
