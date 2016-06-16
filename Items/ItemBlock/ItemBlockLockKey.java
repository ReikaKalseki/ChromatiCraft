/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockLockKey;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class ItemBlockLockKey extends ItemBlockMultiType {

	public ItemBlockLockKey(Block b) {
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add("Channel: "+BlockLockKey.LockChannel.lockList[is.getItemDamage()].name);
		if (is.stackTagCompound != null && ReikaObfuscationHelper.isDeObfEnvironment())
			li.add("UUID: "+is.stackTagCompound.getString("uid"));
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
	{
		return super.onDroppedByPlayer(item, player);
	}

}
