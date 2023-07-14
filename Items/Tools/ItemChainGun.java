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

import Reika.ChromatiCraft.Base.ItemProjectileFiringTool;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;

public class ItemChainGun extends ItemProjectileFiringTool {

	public ItemChainGun(int index) {
		super(index);
	}

	@Override
	protected Entity createProjectile(ItemStack is, World world, EntityPlayer ep, boolean randomVec) {
		return new EntityChainGunShot(world, ep, randomVec);
	}

	@Override
	public int getAutofireRate() {
		return 20;
	}

}
