/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemChromaMulti extends ItemChromaBasic {

	public ItemChromaMulti(int tex) {
		super(tex);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	public int getNumberTypes() {
		return ChromaItems.getEntryByID(this).getNumberMetadatas();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < this.getNumberTypes(); i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}
}
