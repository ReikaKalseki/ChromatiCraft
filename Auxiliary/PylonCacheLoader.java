/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Network.PylonLinkNetwork;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler.PlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;


public class PylonCacheLoader implements PlayerTracker {

	public static final PylonCacheLoader instance = new PylonCacheLoader();

	private PylonCacheLoader() {

	}

	@Override
	public void onPlayerLogin(EntityPlayer ep) {
		if (ep instanceof EntityPlayerMP) {
			PylonGenerator.instance.sendDimensionCacheToPlayer((EntityPlayerMP)ep, ep.worldObj.provider.dimensionId);
			PylonLinkNetwork.instance.sync((EntityPlayerMP)ep);
			LoreManager.instance.sendTowersToClient((EntityPlayerMP)ep);
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
			PylonLinkNetwork.instance.sync(ep);
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {

	}

}
