/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;


public interface EnchantableItem {

	public boolean isEnchantValid(Enchantment e, ItemStack is);

}
