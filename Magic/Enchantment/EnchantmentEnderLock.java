/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import Reika.ChromatiCraft.Base.ChromaticEnchantment;


public class EnchantmentEnderLock extends ChromaticEnchantment {

	public EnchantmentEnderLock(int id) {
		super(id, EnumEnchantmentType.bow);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

}
