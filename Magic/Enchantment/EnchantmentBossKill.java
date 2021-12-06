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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ChromaticEnchantment;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;


public class EnchantmentBossKill extends ChromaticEnchantment {

	private static DamageProfile[] damageFraction = {
			new DamageProfile(16, 50, 500),
			new DamageProfile(4, 80, 300),
			new DamageProfile(2, 100, 200),
			new DamageProfile(2, 250, 100),
	};

	public EnchantmentBossKill(int id) {
		super(id, EnumEnchantmentType.weapon);
	}

	@Override
	public int getMaxLevel() {
		return damageFraction.length;
	}

	@Override
	public boolean canApply(ItemStack is) {
		return EnumEnchantmentType.weapon.canEnchantItem(is.getItem()) || EnumEnchantmentType.bow.canEnchantItem(is.getItem());
	}

	@Override
	public boolean isVisibleToPlayer(EntityPlayer ep, int level) {
		return ProgressStage.KILLDRAGON.isPlayerAtStage(ep) || ProgressStage.KILLWITHER.isPlayerAtStage(ep);
	}

	public static float getDamageDealt(EntityLivingBase e, int lvl) {
		float max = e.getMaxHealth();
		DamageProfile dp = damageFraction[lvl-1];
		if (max >= dp.damageThreshold) {
			return Math.min(max*dp.damageFraction, dp.damageCap);
		}
		else {
			return 0;
		}
	}

	private static class DamageProfile {

		private final float damageFraction;
		private final int damageCap;
		private final int damageThreshold;

		private DamageProfile(int nhits, int cap, int thresh) {
			damageFraction = 1F/nhits;
			damageCap = cap;
			damageThreshold = thresh;
		}

	}

}
