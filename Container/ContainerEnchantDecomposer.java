/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityEnchantDecomposer;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerEnchantDecomposer extends CoreContainer {

	private final TileEntityEnchantDecomposer tile;

	public ContainerEnchantDecomposer(EntityPlayer player, TileEntityEnchantDecomposer te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 72, 34);

		this.addPlayerInventory(player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int k = 0; k < crafters.size(); k++) {
			ICrafting icrafting = (ICrafting)crafters.get(k);
			for (CrystalElement e : tile.getTags().elementSet()) {
				icrafting.sendProgressBarUpdate(this, e.ordinal(), tile.getEnergy(e));
			}
			icrafting.sendProgressBarUpdate(this, 20, tile.processTimer);
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		super.updateProgressBar(par1, par2);

		if (par1 == 20) {
			tile.processTimer = par2;
		}
		else {
			CrystalElement e = CrystalElement.elements[par1];
			tile.setEnergyClient(e, par2);
		}
	}

}
