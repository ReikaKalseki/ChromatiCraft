/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Interfaces.Item.CustomEnchantingCategory;

import cpw.mods.fml.common.eventhandler.Event.Result;


public interface EnchantableItem extends CustomEnchantingCategory {

	public Result getEnchantValidity(Enchantment e, ItemStack is);

}
