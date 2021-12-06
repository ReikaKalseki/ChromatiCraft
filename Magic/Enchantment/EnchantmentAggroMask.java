/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ChromaticEnchantment;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.Chromabilities;


public class EnchantmentAggroMask extends ChromaticEnchantment {

	public EnchantmentAggroMask(int id) {
		super(id, EnumEnchantmentType.weapon);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean canApply(ItemStack is) {
		return EnumEnchantmentType.weapon.canEnchantItem(is.getItem()) || EnumEnchantmentType.bow.canEnchantItem(is.getItem());
	}

	public static boolean hidePigmanSpreadDamage(int level) {
		return level >= 1;
	}

	public static boolean hideDirectDamage(int level) {
		return level >= 2;
	}

	public static boolean preservePeace(int level) {
		return level >= 3;
	}

	@Override
	public boolean isVisibleToPlayer(EntityPlayer ep, int level) {
		return level == 3 ? ProgressStage.DIMENSION.playerHasPrerequisites(ep) && Chromabilities.COMMUNICATE.isAvailableToPlayer(ep) : ProgressStage.KILLMOB.isPlayerAtStage(ep);
	}

}
