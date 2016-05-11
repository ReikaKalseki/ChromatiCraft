/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;


public class PylonCacheLoader implements PlayerTracker {

	public static final PylonCacheLoader instance = new PylonCacheLoader();

	private PylonCacheLoader() {

	}

	@Override
	public void onPlayerLogin(EntityPlayer ep) {
		if (ep instanceof EntityPlayerMP) {
			PylonGenerator.instance.sendDimensionCacheToPlayer((EntityPlayerMP)ep, ep.worldObj.provider.dimensionId);
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo) {
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)player;
			//ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PYLONCACHECLEAR.ordinal(), ep, dimFrom);
			PylonGenerator.instance.sendDimensionCacheToPlayer(ep, dimTo);
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {

	}

}
