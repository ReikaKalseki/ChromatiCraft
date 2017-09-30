/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaItems;

public class ItemPylonFinder extends ItemChromaTool {

	public ItemPylonFinder(int index) {
		super(index);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean selected) {
		if (e instanceof EntityPlayer && ChromaItems.AURAPOUCH.matchWith(((EntityPlayer)e).inventory.mainInventory[slot]) && world.getTotalWorldTime()%8 == 0)
			e.getEntityData().setLong("pylonoverlay", world.getTotalWorldTime());
	}

}
