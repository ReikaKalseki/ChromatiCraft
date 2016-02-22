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


public class EnchantmentWeaponAOE extends ChromaticEnchantment {

	public EnchantmentWeaponAOE(int id) {
		super(id, EnumEnchantmentType.weapon);
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	public static double getRadius(int level) {
		return 1.5D+level/2D;
	}

	public static double getDamageFactor(int level, double dist, double radius) {
		return Math.min(1, 1D-Math.pow(dist/radius-level/40D, level));
	}

}
