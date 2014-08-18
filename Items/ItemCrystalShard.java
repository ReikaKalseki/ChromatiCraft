/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;

public class ItemCrystalShard extends ItemCrystalBasic {

	public ItemCrystalShard(int tex) {
		super(tex);
	}

	@Override
	public boolean isPotionIngredient(ItemStack is)
	{
		return false;
	}

	@Override
	public String getPotionEffect(ItemStack is)
	{
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
		switch(dye) {
		case BLACK:
			return PotionHelper.fermentedSpiderEyeEffect;
		case BLUE:
			return PotionHelper.goldenCarrotEffect;
		case BROWN:
			return PotionHelper.redstoneEffect;
		case CYAN: //water breathing
			return "";
		case GRAY: //slowness
			return PotionHelper.sugarEffect;
		case GREEN:
			return PotionHelper.spiderEyeEffect;
		case LIGHTBLUE:
			return PotionHelper.sugarEffect;
		case LIGHTGRAY: //weakness
			return PotionHelper.blazePowderEffect;
		case LIME: //jump boost
			return "";
		case MAGENTA:
			return PotionHelper.ghastTearEffect;
		case ORANGE:
			return PotionHelper.magmaCreamEffect;
		case PINK:
			return PotionHelper.blazePowderEffect;
		case PURPLE: //xp -> level2?
			return PotionHelper.glowstoneEffect;
		case RED: //resistance
			return "";
		case WHITE:
			return PotionHelper.goldenCarrotEffect;
		case YELLOW: //haste
			return "";
		default:
			return "";
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Good for ");
		ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
		sb.append(CrystalPotionController.getPotionName(color));
		sb.append(" Potions");
		li.add(sb.toString());
	}
}
