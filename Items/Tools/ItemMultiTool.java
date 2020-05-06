/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.EnchantableItem;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.Interfaces.Item.ToolSprite;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class ItemMultiTool extends ItemChromaTool implements ToolSprite, EnchantableItem {

	public ItemMultiTool(int index) {
		super(index);
		this.setMaxDamage(900); //was 400
	}

	@Override
	public boolean canHarvestBlock(Block b, ItemStack is) {
		return Items.diamond_pickaxe.canHarvestBlock(b, is);
	}

	@Override
	public int getItemEnchantability()
	{
		return Items.golden_pickaxe.getItemEnchantability();
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass)
	{
		return 3;
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return 4;
	}

	@Override
	public final IIcon getIconFromDamage(int dmg) { //To get around a bug in backtools
		return Items.stone_pickaxe.getIconFromDamage(0);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack is, World world, Block b, int x, int y, int z, EntityLivingBase elb) {
		if (b.getBlockHardness(world, x, y, z) > 0) {
			is.damageItem(1, elb);
		}
		return true;
	}

	@Override
	public Result getEnchantValidity(Enchantment e, ItemStack is) {
		return e == Enchantment.unbreaking || e == Enchantment.efficiency || e == Enchantment.silkTouch || e == Enchantment.fortune || e.getName().toLowerCase(Locale.ENGLISH).contains("soulbound") ? Result.ALLOW : Result.DENY;
	}

}
