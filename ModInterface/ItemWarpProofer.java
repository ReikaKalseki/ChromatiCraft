/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;

public class ItemWarpProofer extends ItemChromaTool { //maybe make ability instead?

	public ItemWarpProofer(int tex) {
		super(tex);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			ReikaThaumHelper.giveWarpProtection(ep, 20);
		}
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return true;
	}

}
