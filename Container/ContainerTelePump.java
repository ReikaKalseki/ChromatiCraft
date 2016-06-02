/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class ContainerTelePump extends CoreContainer {

	private TileEntityTeleportationPump tile;

	public ContainerTelePump(EntityPlayer player, TileEntityTeleportationPump te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 152, 35);
		this.addSlot(1, 152, 54);

		//this.addSlot(2, 170, 54);
		//this.addSlot(3, 170, 35);

		this.addPlayerInventoryWithOffset(player, 9, 2);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
			//icrafting.sendProgressBarUpdate(this, 0, tile.progressTimer);
		}

		ReikaPacketHelper.sendTankSyncPacket(ChromatiCraft.packetChannel, tile, "tank");
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0) {
			//	tile.progressTimer = par2;
		}
	}


}
