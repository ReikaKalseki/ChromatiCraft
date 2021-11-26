/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Magic.ToolChargingSystem.EntityChargingTool;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class ItemPoweredChromaTool extends ItemChromaTool implements PoweredItem {

	public ItemPoweredChromaTool(int index) {
		super(index);
		//this.setMaxDamage(getChargeStates()-1);
	}


	public abstract CrystalElement getColor(ItemStack is);
	public abstract int getMaxCharge();
	public abstract int getChargeStates();

	protected abstract boolean isActivated(EntityPlayer e, ItemStack is, boolean held);
	public abstract int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is);
	protected abstract boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held);

	public boolean canChargeWhilePlayerCharges() {
		return false;
	}

	public int getChargeRate(ItemStack is, int base) {
		return base;
	}

	public int getChargeState(float frac) {
		return Math.round((this.getChargeStates()-1)*frac);
	}

	@Override
	public final void getSubItems(Item i, CreativeTabs c, List li) {
		li.add(ToolChargingSystem.instance.getChargedItem(this, 0));
		li.add(ToolChargingSystem.instance.getChargedItem(this, this.getMaxCharge()));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add(String.format("Energy: %.2f%s", 100F*ToolChargingSystem.instance.getCharge(is)/this.getMaxCharge(), "%"));
	}

	@Override
	public final void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer) {
			if (this.isActivated((EntityPlayer)e, is, held)) {
				if (ToolChargingSystem.instance.getCharge(is) > 0) {
					if (this.doTick(is, world, (EntityPlayer)e, held) && !((EntityPlayer)e).capabilities.isCreativeMode) {
						ToolChargingSystem.instance.removeCharge(is, this.getChargeConsumptionRate((EntityPlayer)e, world, is), (EntityPlayer)e);
					}
				}
			}
		}
	}

	@Override
	public final int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public final boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public final Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityChargingTool(world, (EntityItem)location, itemstack);
	}

	@Override
	public final boolean onEntityItemUpdate(EntityItem ei) {
		return ToolChargingSystem.instance.tickItem(ei);
	}

	@SideOnly(Side.CLIENT)
	public void doChargeFX(EntityItem ei, int charge) {
		doStandardChargeFX(ei, charge);
	}

	@SideOnly(Side.CLIENT)
	public static void doStandardChargeFX(EntityItem ei, int charge) {
		ItemStack is = ei.getEntityItem();
		PoweredItem pi = (PoweredItem)is.getItem();
		if (charge == pi.getMaxCharge()) {
			double ang = 360*Math.sin(ei.age/50D);
			for (int i = 0; i < 360; i += 60) {
				double v = 0.125+0.0625*Math.sin(ei.age/250D);
				double vx = v*Math.cos(Math.toRadians(ang+i));
				double vz = v*Math.sin(Math.toRadians(ang+i));
				EntityBlurFX fx = new EntityCCBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vx, 0, vz).setLife(40).setRapidExpand().setScale(1.5F);
				fx.setColor(pi.getColor(is).getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, itemRand.nextDouble()*360, itemRand.nextDouble()*360);
			float s = 1+itemRand.nextFloat();
			EntityBlurFX fx = new EntityCCBlurFX(ei.worldObj, ei.posX, (int)ei.posY+0.25, ei.posZ, vel[0], vel[1], vel[2]).setLife(40).setScale(s).setColliding();
			fx.setColor(pi.getColor(is).getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

	protected void renderExtraIcons(RenderItem ri, ItemStack is, ItemRenderType type) {

	}

	public boolean hasChargeStates() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("tooltip"))
			return false;
		this.renderExtraIcons(ri, is, type);
		ToolChargingSystem.instance.renderItemAux(ri, is, type);
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

	@Override
	public String getNotes(int subpage) {
		return "Requires charging with "+this.getColor(new ItemStack(this)).displayName+" ambient energy to be used";
	}

	@Override
	public int getMaxSubpage() {
		return 1;
	}

	public boolean replaceOriginal() {
		return false;
	}

}
