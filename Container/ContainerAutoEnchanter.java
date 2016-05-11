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
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class ContainerAutoEnchanter extends CoreContainer {

	private TileEntityAutoEnchanter tile;

	public ContainerAutoEnchanter(EntityPlayer player, TileEntityAutoEnchanter te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 80, 35);

		this.addPlayerInventoryWithOffset(player, 0, 15);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, tile.progressTimer);
		}

		ReikaPacketHelper.sendTankSyncPacket(ChromatiCraft.packetChannel, tile, "tank");
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0) {
			tile.progressTimer = par2;
		}
	}


}
