/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import Reika.ChromatiCraft.Base.ChromaPotion;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PotionCustomRegen extends ChromaPotion {

	public PotionCustomRegen(int id) {
		super(id, false, Potion.regeneration.getLiquidColor(), 0);
	}

	@Override
	public void performEffect(EntityLivingBase elb, int level)  {
		if (elb.getHealth() < elb.getMaxHealth()) {
			elb.heal(1.0F);
		}
	}

	@Override
	public String getName() {
		return Potion.regeneration.getName();
	}

	@Override
	public boolean isReady(int dura, int level) {
		int d = level > 0 ? 1+(120 >> (6*level)) : 50;
		return dura%d == 0 && (level > 0 || ReikaRandomHelper.doWithChance(25));
	}

	@Override
	public int getStatusIconIndex() {
		return Potion.regeneration.getStatusIconIndex();
	}

	@Override
	public boolean hasStatusIcon() {
		return Potion.regeneration.hasStatusIcon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		Potion.regeneration.renderInventoryEffect(x, y, effect, mc);
	}

}
