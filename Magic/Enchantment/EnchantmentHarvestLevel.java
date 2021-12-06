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

import Reika.ChromatiCraft.Base.ChromaticEnchantment;


public class EnchantmentHarvestLevel extends ChromaticEnchantment {

	public EnchantmentHarvestLevel(int id) {
		super(id, EnumEnchantmentType.digger);
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public boolean isVisibleToPlayer(EntityPlayer ep, int level) {
		return true;
	}

}
