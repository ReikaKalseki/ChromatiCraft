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

public class ItemEtherealPendant extends ItemChromaTool {

	private static final String TAG = "last_lumapendant";

	public ItemEtherealPendant(int tex) {
		super(tex);
		this.setHasSubtypes(true);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		e.getEntityData().setLong(TAG, world.getTotalWorldTime());/*
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			if (world.getTotalWorldTime()%10 == 0) {
				BlockEtherealLuma.addPotions(ep);
			}
		}*/
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return true;
	}

	public static boolean isActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG) < 20;
	}

}
