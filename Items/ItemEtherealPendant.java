/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.Worldgen.BlockEtherealLuma;

public class ItemEtherealPendant extends ItemChromaTool {

	public ItemEtherealPendant(int tex) {
		super(tex);
		this.setHasSubtypes(true);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			if (world.getTotalWorldTime()%20 == 0) {
				BlockEtherealLuma.addPotions(ep);
			}
		}
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return true;
	}

}
