/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class ItemWarpProofer extends ItemChromaTool {

	public ItemWarpProofer(int tex) {
		super(tex);
		this.setHasSubtypes(true);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		if (e instanceof EntityPlayer && is.getItemDamage() == 1) {
			EntityPlayer ep = (EntityPlayer) e;
			if (world.getTotalWorldTime()%240 == 0) {
				ReikaThaumHelper.removeWarp(ep, 1);
			}
		}
	}

	@Override
	public void getSubItems(Item i, CreativeTabs c, List li) {
		li.add(new ItemStack(this, 1, 0));
		li.add(new ItemStack(this, 1, 1));
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return is.getItemDamage() == 1;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return item.getItemDamage() == 1 ? super.getItemSpriteIndex(item)+16 : super.getItemSpriteIndex(item);
	}

}
