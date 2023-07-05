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

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import Reika.ChromatiCraft.Auxiliary.Interfaces.DynamicallyGeneratedSubpage;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class ItemWandBase extends ItemChromaTool implements DynamicallyGeneratedSubpage {

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

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (!energy.isEmpty()) {
			if (GuiScreen.isShiftKeyDown()) {
				String[] parts = this.generateUsageData(true).split("\\n");
				for (String s : parts) {
					li.add(s);
				}
			}
			else {
				StringBuilder sb = new StringBuilder();
				sb.append("Hold ");
				sb.append(EnumChatFormatting.GREEN.toString());
				sb.append("Shift");
				sb.append(EnumChatFormatting.GRAY.toString());
				sb.append(" for cost data");
				li.add(sb.toString());
			}
		}
	}

	//@SideOnly(Side.CLIENT)
	public String generateUsageData(boolean color) {
		StringBuilder sb = new StringBuilder();
		sb.append("Base usage requires: \n");
		for (CrystalElement e : energy.elementSet()) {
			sb.append((color ? e.getChatColorString() : "")+" "+e.displayName/*+": "+energy.getValue(e)*/+"\n");
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

	public String getNotes(int subpage) {
		return this.generateUsageData(false);
	}

	public int getMaxSubpage() {
		return 1;
	}

	public boolean replaceOriginal() {
		return true;
	}

}
