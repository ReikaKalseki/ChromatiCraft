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

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.AnimatedSpritesheet;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public class ItemCrystalShard extends ItemCrystalBasic implements AnimatedSpritesheet {

	public ItemCrystalShard(int tex) {
		super(tex);
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)-item.getItemDamage()+item.getItemDamage()%16;
	}

	@Override
	public boolean isPotionIngredient(ItemStack is)
	{
		return false;
	}

	@Override
	public int getNumberTypes() {
		return CrystalElement.elements.length*2;
	}

	//@Override
	//public boolean hasEffect(ItemStack is) {
	//	return is.getItemDamage() >= 16;
	//}

	@Override
	public String getPotionEffect(ItemStack is)
	{
		String ret = "";
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
		switch(dye) {
		case BLACK:
			ret += PotionHelper.fermentedSpiderEyeEffect;
		case BLUE:
			ret += PotionHelper.goldenCarrotEffect;
		case BROWN:
			ret += PotionHelper.redstoneEffect;
		case CYAN: //water breathing
			ret += "";
		case GRAY: //slowness
			ret += PotionHelper.sugarEffect;
		case GREEN:
			ret += PotionHelper.spiderEyeEffect;
		case LIGHTBLUE:
			ret += PotionHelper.sugarEffect;
		case LIGHTGRAY: //weakness
			ret += PotionHelper.blazePowderEffect;
		case LIME: //jump boost
			ret += "";
		case MAGENTA:
			ret += PotionHelper.ghastTearEffect;
		case ORANGE:
			ret += PotionHelper.magmaCreamEffect;
		case PINK:
			ret += PotionHelper.blazePowderEffect;
		case PURPLE: //xp -> level2?
			ret += PotionHelper.glowstoneEffect;
		case RED: //resistance
			ret += "";
		case WHITE:
			ret += PotionHelper.goldenCarrotEffect;
		case YELLOW: //haste
			ret += "";
		default:
			ret += "";
		}
		if (is.getItemDamage() >= 16) {
			ret += "+5+6-7"; //level II and extended
		}
		return ret;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Good for ");
		CrystalElement color = CrystalElement.elements[is.getItemDamage()%16];
		sb.append(CrystalPotionController.getPotionName(color));
		sb.append(" Potions");
		li.add(sb.toString());
		if (is.getItemDamage() >= 16)
			li.add(EnumChatFormatting.LIGHT_PURPLE.toString()+"Gives level II enhanced potions");
		else
			li.add(EnumChatFormatting.GOLD.toString()+"Gives ordinary potions");
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return is.getItemDamage() >= 16;
	}

	@Override
	public int getFrameSpeed() {
		return 3;
	}

	@Override
	public int getColumn(ItemStack is) {
		return is.getItemDamage()%16;
	}

	@Override
	public int getFrameCount() {
		return 16;
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return 0;
	}

	@Override
	public String getTexture(ItemStack is) {
		return is.getItemDamage() >= 16 ? "/Reika/ChromatiCraft/Textures/Items/shardglow.png" : super.getTexture(is);
	}
}
