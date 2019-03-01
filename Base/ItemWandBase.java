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

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class ItemWandBase extends ItemChromaTool {

	private final ElementTagCompound energy = new ElementTagCompound();

	public ItemWandBase(int index) {
		super(index);
	}

	protected final void addEnergyCost(CrystalElement e, int amt) {
		energy.addValueToColor(e, amt);
	}

	protected final void drainPlayer(EntityPlayer ep, float factor) {
		PlayerElementBuffer.instance.removeFromPlayer(ep, energy.copy().scale(factor));
	}

	protected final void drainPlayer(EntityPlayer ep) {
		PlayerElementBuffer.instance.removeFromPlayer(ep, energy);
	}

	public final boolean sufficientEnergy(EntityPlayer ep) {
		return this.sufficientEnergy(ep, 1);
	}

	public final boolean sufficientEnergy(EntityPlayer ep, float f) {
		return ep.capabilities.isCreativeMode || PlayerElementBuffer.instance.playerHas(ep, energy.copy().scale(f));
	}

	public ElementTagCompound getEnergy(float scale) {
		return energy.copy().scale(scale);
	}

	//@SideOnly(Side.CLIENT)
	public String generateUsageData() {
		StringBuilder sb = new StringBuilder();
		sb.append("Base usage cost: \n");
		for (CrystalElement e : energy.elementSet()) {
			sb.append(e.displayName+": "+energy.getValue(e)+"\n");
		}
		return sb.toString();
	}

	@Override
	public final int getItemEnchantability() {
		return 0;//Items.golden_pickaxe.getItemEnchantability();
	}

	protected static boolean canUseBoostedEffect(EntityPlayer ep) {
		return ProgressStage.CTM.isPlayerAtStage(ep);
	}

}
