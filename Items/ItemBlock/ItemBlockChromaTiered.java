/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.BlockTieredResource;

public class ItemBlockChromaTiered extends ItemBlock {

	public ItemBlockChromaTiered(Block b) {
		super(b);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		BlockTieredResource block = (BlockTieredResource)field_150939_a;
		if (!block.isPlayerSufficientTier(world, x, y, z, ep)) {
			;//return false;
		}
		return super.onItemUse(is, ep, world, x, y, z, s, a, b, c);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

}
