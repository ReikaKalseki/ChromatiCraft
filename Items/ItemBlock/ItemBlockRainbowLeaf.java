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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockRainbowLeaf extends ItemBlock {

	public ItemBlockRainbowLeaf(Block b) {
		super(b);
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(id, 1, 1));
	}

	@Override
	public int getMetadata(int dmg)
	{
		return dmg;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2)
	{
		return 0xffffff;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.RAINBOWLEAF.getBasicName();
	}

}
