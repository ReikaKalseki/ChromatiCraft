/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Powered;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import Reika.ChromatiCraft.Auxiliary.Render.OreOverlayRenderer;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.Item.ToolSprite;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class ItemOrePick extends ItemPoweredChromaTool implements ToolSprite {

	private static final int SONAR_COST = 90;
	private static final int SCAN_COST = 10;
	private static final int CHARGE_TIME = 30;

	public ItemOrePick(int index) {
		super(index);
		this.setMaxDamage(720);
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer ep, boolean held) {
		int type = -1;
		float r = 8;//5;
		for (float i = 0; i <= r; i += 0.2) {
			int[] xyz = ReikaVectorHelper.getPlayerLookBlockCoords(ep, i);
			int at = this.getOreType(world, xyz[0], xyz[1], xyz[2]);
			if (at >= 0) {
				type = at;
				break;
			}
		}
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("ore", type);
		return type >= 0;
	}

	public static OreType getOreTypeByMetadata(ItemStack is) {
		int meta = is.stackTagCompound != null ? is.stackTagCompound.getInteger("ore") : -1;
		if (meta == -1)
			return null;
		return meta >= 4096 ? ModOreList.oreList[meta-4096] : ReikaOreHelper.oreList[meta];
	}

	@Override
	public void onUsingTick(ItemStack is, EntityPlayer ep, int count) {
		count = this.getMaxItemUseDuration(is)-count;
		count = MathHelper.clamp_int(count, 0, CHARGE_TIME);
		if (ep.worldObj.isRemote) {
			//this.doChargingParticles(ep, count);
		}
		else if (count > 1) {
			ChromaSounds.NETWORKOPTCHARGE.playSound(ep, 0.25F+2F*count/CHARGE_TIME, MathHelper.clamp_float(0.5F, 2F*count/CHARGE_TIME, 2F));
		}
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer ep) {
		return is;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is) {
		return 7200;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is) {
		return EnumAction.bow;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!this.handleUseAllowance(ep) && this.isActivated(ep, is, true))
			ep.setItemInUse(is, this.getMaxItemUseDuration(is));
		return is;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack is, World world, EntityPlayer ep, int count) {
		count = MathHelper.clamp_int(this.getMaxItemUseDuration(is)-count, 0, CHARGE_TIME);
		//ReikaChatHelper.write(power+"  ->  "+charge);
		//ReikaJavaLibrary.pConsole(count);
		if (count >= CHARGE_TIME && this.getCharge(is) >= SONAR_COST) {
			this.fire(is, world, ep);
		}
		else if (count > 5) {
			ChromaSounds.ERROR.playSound(ep);
		}
		ep.setItemInUse(null, 0);
	}

	private void fire(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote) {
			this.removeCharge(is, SONAR_COST);int x = MathHelper.floor_double(ep.posX);
			int y = MathHelper.floor_double(ep.posY);
			int z = MathHelper.floor_double(ep.posZ);
			OreOverlayRenderer.instance.startPing(world, x, y, z, ep);
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (this.getCharge(is) < SCAN_COST)
			return false;
		int ore = this.getOreType(world, x, y, z);
		if (ore >= 0) {
			if (!world.isRemote) {
				this.removeCharge(is, SCAN_COST);
				OreOverlayRenderer.instance.startScan(world, x, y, z, ep);
			}
			return true;
		}
		return false;
	}

	private int getOreType(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaOreHelper ore = ReikaOreHelper.getFromVanillaOre(id);
		ModOreList mod = ModOreList.getModOreFromOre(id, meta);
		if (ore != null) {
			return ore.ordinal();
		}
		else if (mod != null) {
			return 4096+mod.ordinal();
		}
		else {
			return -1;
		}
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return toolClass.toLowerCase(Locale.ENGLISH).contains("pick") ? 2 : super.getHarvestLevel(stack, toolClass);
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
	public boolean onBlockDestroyed(ItemStack is, World world, Block b, int x, int y, int z, EntityLivingBase elb) {
		if (b.getBlockHardness(world, x, y, z) > 0) {
			is.damageItem(1, elb);
			this.addCharge(is, this.getMaxCharge()*4/this.getMaxDamage()); //so 25% durability for 1 full recharge
		}
		return true;
	}

	@Override
	protected CrystalElement getColor() {
		return CrystalElement.BROWN;
	}

	@Override
	public int getMaxCharge() {
		return 7200;
	}

	@Override
	public int getChargeStates() {
		return 1;
	}

	@Override
	protected boolean hasChargeStates() {
		return false;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		if (item.stackTagCompound != null && item.stackTagCompound.getBoolean("ore"))
			base++;
		return base;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return held;
	}

	@Override
	protected int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return world.rand.nextInt(20) == 0 ? 1 : 0;
	}

}
