/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;


public abstract class ChromaticEnchantment extends Enchantment {

	protected ChromaticEnchantment(int id, EnumEnchantmentType type) {
		super(id, 0, type);
		this.setName("chroma."+this.getClass().getSimpleName().toLowerCase().substring("Enchantment".length()));
	}

	@Override
	public final boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

}
