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

import java.util.Random;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;

public class PotionGrowthHormone extends Potion {

	private static final Random rand = new Random();

	public PotionGrowthHormone(int par1) {
		super(par1, false, 0x00aaff);
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		if (e instanceof EntityAgeable) {
			EntityAgeable a = (EntityAgeable)e;
			a.addGrowth(level);

			if (e instanceof EntitySheep) {
				EntitySheep es = (EntitySheep)e;
				if (rand.nextInt(10) == 0)
					es.setSheared(false);
			}
		}
	}

	@Override
	public String getName()
	{
		return StatCollector.translateToLocal("chromapotion.growth");
	}

	@Override
	public boolean isReady(int time, int amp)
	{
		return true;
	}

}
