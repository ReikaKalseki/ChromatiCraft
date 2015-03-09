/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Interfaces.ToolSprite;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class ItemOrePick extends ItemChromaTool implements ToolSprite {

	public ItemOrePick(int index) {
		super(index);
		this.setMaxDamage(720);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (held && e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			int type = -1;
			for (float i = 0; i <= 5; i += 0.2) {
				int[] xyz = ReikaVectorHelper.getPlayerLookBlockCoords(ep, i);
				Block id = world.getBlock(xyz[0], xyz[1], xyz[2]);
				int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				ReikaOreHelper ore = ReikaOreHelper.getFromVanillaOre(id);
				ModOreList mod = ModOreList.getModOreFromOre(id, meta);
				if (ore != null) {
					type = ore.ordinal();
					break;
				}
				else if (mod != null) {
					type = 4096+mod.ordinal();
					break;
				}
			}
			if (is.stackTagCompound == null)
				is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("ore", type);
		}
		else
			is.stackTagCompound = null;
	}

	public static OreType getOreTypeByMetadata(ItemStack is) {
		int meta = is.stackTagCompound != null ? is.stackTagCompound.getInteger("ore") : -1;
		if (meta == -1)
			return null;
		return meta >= 4096 ? ModOreList.oreList[meta-4096] : ReikaOreHelper.oreList[meta];
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return toolClass.toLowerCase().contains("pick") ? 2 : super.getHarvestLevel(stack, toolClass);
	}

	@Override
	public boolean canHarvestBlock(Block b, ItemStack is) {
		return Items.iron_pickaxe.canHarvestBlock(b, is);
	}

	@Override
	public final IIcon getIconFromDamage(int dmg) { //To get around a bug in backtools
		return Items.stone_pickaxe.getIconFromDamage(0);
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return ForgeHooks.isToolEffective(stack, block, meta) ? 4 : super.getDigSpeed(stack, block, meta);
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		Set set = new HashSet();
		set.add("pickaxe");
		set.add("pickax");
		return set;
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return super.getItemSpriteIndex(is)+(is.stackTagCompound != null && is.stackTagCompound.getInteger("ore") >= 0 ? 1 : 0);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack is, World world, Block b, int x, int y, int z, EntityLivingBase elb) {
		if (b.getBlockHardness(world, x, y, z) > 0) {
			is.damageItem(1, elb);
		}
		return true;
	}

}
