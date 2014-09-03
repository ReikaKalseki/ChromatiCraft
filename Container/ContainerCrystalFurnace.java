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
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFurnace;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerCrystalFurnace extends CoreContainer {

	private final TileEntityCrystalFurnace tile;

	public ContainerCrystalFurnace(EntityPlayer player, TileEntityCrystalFurnace te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 56, 35);
		this.addSlotNoClick(1, 116, 35);

		this.addPlayerInventory(player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int k = 0; k < crafters.size(); k++) {
			ICrafting icrafting = (ICrafting)crafters.get(k);
			for (CrystalElement e : tile.smeltTags().elementSet()) {
				icrafting.sendProgressBarUpdate(this, e.ordinal(), tile.getEnergy(e));
			}
			icrafting.sendProgressBarUpdate(this, 20, tile.smeltTimer);
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		super.updateProgressBar(par1, par2);

		if (par1 == 20) {
			tile.smeltTimer = par2;
		}
		else {
			CrystalElement e = CrystalElement.elements[par1];
			tile.setEnergy(e, par2);
		}
	}

}
