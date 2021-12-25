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

import java.util.List;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Magic.ToolChargingSystem.EntityChargingTool;
import Reika.ChromatiCraft.Magic.Interfaces.PoweredItem;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBoostedPendant extends ItemPendant implements PoweredItem {

	private static final int MAX_CHARGE = 20*3600*60; //60h

	public ItemBoostedPendant(int tex) {
		super(tex);
		hasSubtypes = true;
		maxStackSize = 1;
		this.setNoRepair();
	}

	@Override
	protected boolean isEnhanced(ItemStack is) {
		return !ChromaOptions.POWEREDPENDANTS.getState() || ToolChargingSystem.instance.getCharge(is) > 0;
	}

	@Override
	protected void onTick(ItemStack is, World world, EntityPlayer ep, int slot) {
		if (ChromaOptions.POWEREDPENDANTS.getState()) {
			ToolChargingSystem.instance.tryChargeFromPlayer(is, ep);
			ToolChargingSystem.instance.removeCharge(is, this.getChargeConsumptionRate(ep, world, is), ep);
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (ChromaOptions.POWEREDPENDANTS.getState())
			li.add(String.format("Energy: %.2f%s", 100F*ToolChargingSystem.instance.getCharge(is)/this.getMaxCharge(), "%"));
	}

	@Override
	protected void addNBTForCreative(ItemStack item) {
		if (ChromaOptions.POWEREDPENDANTS.getState()) {
			ToolChargingSystem.instance.addCharge(item, MAX_CHARGE);
		}
	}

	@Override
	public final boolean onEntityItemUpdate(EntityItem ei) {
		return ChromaOptions.POWEREDPENDANTS.getState() ? ToolChargingSystem.instance.tickItem(ei) : super.onEntityItemUpdate(ei);
	}

	@Override
	public final int getEntityLifespan(ItemStack is, World world) {
		return ChromaOptions.POWEREDPENDANTS.getState() ? Integer.MAX_VALUE : super.getEntityLifespan(is, world);
	}

	@Override
	public final boolean hasCustomEntity(ItemStack stack) {
		return ChromaOptions.POWEREDPENDANTS.getState();
	}

	@Override
	public final Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return ChromaOptions.POWEREDPENDANTS.getState() ? new EntityChargingTool(world, (EntityItem)location, itemstack) : super.createEntity(world, location, itemstack);
	}

	@Override
	public int getMaxCharge() {
		return MAX_CHARGE;
	}

	@Override
	public boolean hasChargeStates() {
		return false;
	}

	@Override
	public int getChargeStates() {
		return 0;
	}

	@Override
	public int getChargeState(float frac) {
		return 0;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 1;
	}

	@Override
	public int getChargeRate(ItemStack is, int base) {
		return base*3/2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doChargeFX(EntityItem ei, int charge) {
		ItemPoweredChromaTool.doStandardChargeFX(ei, charge);
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.elements[is.getItemDamage()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("tooltip"))
			return false;
		ToolChargingSystem.instance.renderItemAux(ri, is, type);
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

	@Override
	public float getPlayerChargeCoefficient(ItemStack is) {
		return 3.6F;
	}

	@Override
	public float getPlayerBufferExtractionValue(ItemStack is) {
		return 40;
	}

	@Override
	public String getNotes(int subpage) {
		return ChromaOptions.POWEREDPENDANTS.getState() ? "Enhanced pendants require charging with the corresponding crystal element" : "";
	}

	@Override
	public int getMaxSubpage() {
		return 1;
	}

	public boolean replaceOriginal() {
		return false;
	}

}
