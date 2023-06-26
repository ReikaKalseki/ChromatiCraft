/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class ItemWarpProofer extends ItemChromaTool {

	private static final String PLAYER_TAG = "owner_player";
	private static final String ACTIVITY_TAG = "last_dewarp";

	public ItemWarpProofer(int tex) {
		super(tex);
		this.setHasSubtypes(true);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		if (e instanceof EntityPlayer && is.getItemDamage() == 1) {
			if (is.stackTagCompound == null) {
				is.stackTagCompound = new NBTTagCompound();
			}
			EntityPlayer ep = (EntityPlayer) e;
			if (!is.stackTagCompound.hasKey(PLAYER_TAG)) {
				is.stackTagCompound.setString(PLAYER_TAG, ep.getCommandSenderName());
			}
			if (!ep.getCommandSenderName().equals(is.stackTagCompound.getString(PLAYER_TAG))) {
				if (world.getTotalWorldTime()-e.getEntityData().getLong(ACTIVITY_TAG) >= 240) {
					ReikaThaumHelper.removeWarp(ep, 1);
					e.getEntityData().setLong(ACTIVITY_TAG, world.getTotalWorldTime());
				}
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
