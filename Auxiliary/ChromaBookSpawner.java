/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker.PlayerTracker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public class ChromaBookSpawner implements PlayerTracker {

	@Override
	public void onNewPlayer(EntityPlayer ep) {
		if (ReikaInventoryHelper.checkForItemStack(this.getItem(), ep.inventory, false))
			return;
		if (!ep.inventory.addItemStackToInventory(this.getItem()))
			ep.dropPlayerItemWithRandomChoice(this.getItem(), true);
	}

	public ItemStack getItem() {
		return ChromaItems.HELP.getStackOf();
	}

	@Override
	public String getID() {
		return "ChromatiCraft_Handbook";
	}

}
