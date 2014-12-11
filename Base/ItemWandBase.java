/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.entity.player.EntityPlayer;
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
		return ep.capabilities.isCreativeMode || PlayerElementBuffer.instance.playerHas(ep, energy);
	}

	public ElementTagCompound getEnergy(float scale) {
		return energy.copy().scale(scale);
	}

}
