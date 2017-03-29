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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import Reika.ChromatiCraft.Base.ChromaticEnchantment;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class EnchantmentPhasingSequence extends ChromaticEnchantment {

	private static final float[] REDUCTION_FACTORS = {1, 0.9375F, 0.875F, 0.75F, 0.667F};

	public EnchantmentPhasingSequence(int id) {
		super(id, EnumEnchantmentType.weapon);
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	public boolean canEnchantItem(Item item)  {
		return item instanceof ItemSword || item instanceof ItemBow;
	}

	private static float getActualDamage(float baseDmg, int level) {
		return (float)ReikaMathLibrary.roundDownToFraction(baseDmg*REDUCTION_FACTORS[level], 0.125);
	}

	public static float getPenetratingDamage(float baseDmg, int level) {
		return (float)ReikaMathLibrary.roundUpToFraction(0.1F*level*getActualDamage(baseDmg, level), 0.25);
	}

	public static float getSpilloverDamage(float baseDmg, int level) {
		return getActualDamage(baseDmg, level)-getPenetratingDamage(baseDmg, level);
	}

}
