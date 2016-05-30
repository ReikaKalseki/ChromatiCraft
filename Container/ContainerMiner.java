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
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerMiner extends CoreContainer {

	private final TileEntityMiner tile;

	public ContainerMiner(EntityPlayer player, TileEntityMiner te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 152, 16);

		//this.addSlot(1, 152, 92);
		//this.addSlot(2, 152, 74);
		//this.addSlot(3, 152, 56);

		this.addPlayerInventoryWithOffset(player, 0, 40);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, tile.progress);
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0) {
			tile.progress = par2;
		}
	}

}
