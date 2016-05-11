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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockCrystalHive extends ItemBlock {

	public ItemBlockCrystalHive(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 2; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.HIVE.getMultiValuedName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public IIcon getIconFromDamage(int dmg) {
		return field_150939_a.getIcon(0, dmg);
	}
}
