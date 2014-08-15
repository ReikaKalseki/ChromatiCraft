/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.TileEntityCollector;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerCollector extends CoreContainer {

	private TileEntityCollector tile;

	public ContainerCollector(EntityPlayer player, TileEntityCollector te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 17, 16);
		this.addSlotNoClick(1, 17, 54);

		this.addPlayerInventory(player);
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

		ReikaPacketHelper.sendTankSyncPacket(ChromatiCraft.packetChannel, tile, "input");
		ReikaPacketHelper.sendTankSyncPacket(ChromatiCraft.packetChannel, tile, "output");
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0) {
			//	tile.progressTimer = par2;
		}
	}


}