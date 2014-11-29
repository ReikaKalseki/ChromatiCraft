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
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.SlotXItems;

public class ContainerItemFabricator extends CoreContainer {

	private final TileEntityItemFabricator tile;

	public ContainerItemFabricator(EntityPlayer player, TileEntityItemFabricator te) {
		super(player, te);
		tile = te;

		this.addSlotToContainer(new SlotXItems(te, 0, 142, 11, 1));
		this.addSlotNoClick(1, 142, 35);

		this.addPlayerInventory(player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int k = 0; k < crafters.size(); k++) {
			ICrafting icrafting = (ICrafting)crafters.get(k);
			icrafting.sendProgressBarUpdate(this, 20, tile.progress);
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		super.updateProgressBar(par1, par2);

		if (par1 == 20) {
			tile.progress = par2;
		}
		else {
			CrystalElement e = CrystalElement.elements[par1];
			tile.setEnergy(e, par2);
		}
	}

}
