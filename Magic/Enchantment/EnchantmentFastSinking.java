/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import Reika.ChromatiCraft.Base.ChromaticEnchantment;


public class EnchantmentFastSinking extends ChromaticEnchantment {

	public EnchantmentFastSinking(int id) {
		super(id, EnumEnchantmentType.armor_feet);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

}
