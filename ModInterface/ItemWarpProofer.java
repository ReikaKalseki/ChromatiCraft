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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;

public class ItemWarpProofer extends ItemChromaTool { //maybe make ability instead?

	public ItemWarpProofer(int tex) {
		super(tex);
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep) {
		ReikaThaumHelper.giveWarpProtection(ep, 1728000); //24h
		return new ItemStack(Items.glass_bottle);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is) {
		return EnumAction.drink;
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
	}

}
