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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;
import Reika.DragonAPI.Base.CoreContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCrystalBrewer extends CoreContainer {

	private int brewTime = 0;
	private TileEntityCrystalBrewer tile;

	public ContainerCrystalBrewer(EntityPlayer player, TileEntityCrystalBrewer te) {
		super(player, te);
		tile = te;

		this.addSlotToContainer(new Slot(te, 1, 56, 46));
		this.addSlotToContainer(new Slot(te, 2, 79, 53));
		this.addSlotToContainer(new Slot(te, 3, 102, 46));
		this.addSlotToContainer(new Slot(te, 0, 79, 17));

		this.addPlayerInventory(player);
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
	{
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, tile.getBrewTime());
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);

			if (brewTime != tile.getBrewTime())
			{
				icrafting.sendProgressBarUpdate(this, 0, tile.getBrewTime());
			}
		}

		brewTime = tile.getBrewTime();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
		{
			tile.setBrewTime(par2);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return tile.isUseableByPlayer(par1EntityPlayer);
	}
}
