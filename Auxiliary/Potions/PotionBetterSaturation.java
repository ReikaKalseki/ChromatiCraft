/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import Reika.ChromatiCraft.Base.ChromaPotion;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ModInteract.ItemHandlers.HungerOverhaulHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PotionBetterSaturation extends ChromaPotion {

	public PotionBetterSaturation(int par1) {
		super(par1, false, 0xA55926, 0);
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		if (!e.worldObj.isRemote && e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			if (level > 0 || ep.getFoodStats().getFoodLevel() < this.getMaxBaseFoodLevel())
				ep.getFoodStats().addStats(level + 1, 1.0F);
		}
	}

	private int getMaxBaseFoodLevel() {
		return ModList.HUNGEROVERHAUL.isLoaded() ? HungerOverhaulHandler.getInstance().regenHungerValue : 17;
	}

	@Override
	public String getName() {
		return Potion.field_76443_y.getName();//StatCollector.translateToLocal("chromapotion.sat");
	}

	@Override
	public boolean isReady(int time, int amp) {
		return amp > 0 || time == 5;
	}

	@Override
	public int getStatusIconIndex() {
		return Potion.field_76443_y.getStatusIconIndex();
	}

	@Override
	public boolean hasStatusIcon() {
		return Potion.field_76443_y.hasStatusIcon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		Potion.field_76443_y.renderInventoryEffect(x, y, effect, mc);
	}

}
