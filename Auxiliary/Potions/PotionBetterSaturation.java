/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionBetterSaturation extends Potion {

	public PotionBetterSaturation(int par1) {
		super(par1, false, 0xA55926);
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		if (!e.worldObj.isRemote && e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			if (level > 0 || ep.getFoodStats().getFoodLevel() < 17)
				ep.getFoodStats().addStats(level + 1, 1.0F);
		}
	}

	@Override
	public String getName()
	{
		return Potion.field_76443_y.getName();//StatCollector.translateToLocal("chromapotion.sat");
	}

	@Override
	public boolean isReady(int time, int amp)
	{
		return amp > 0 || time == 5;
	}

}
