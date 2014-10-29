/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class PlayerElementBuffer {

	public static final PlayerElementBuffer instance = new PlayerElementBuffer();

	//private final HashMap<String, ElementTagCompound> data = new HashMap();

	private static final String NBT_TAG = "CrystalBuffer";

	private PlayerElementBuffer() {

	}

	private NBTTagCompound getTag(EntityPlayer ep) {
		NBTTagCompound tag = ep.getEntityData().getCompoundTag(NBT_TAG);
		ep.getEntityData().setTag(NBT_TAG, tag);
		return tag;
	}

	public ElementTagCompound getPlayerBuffer(EntityPlayer ep) {
		NBTTagCompound tag = this.getTag(ep);
		return ElementTagCompound.createFromNBT(tag);
	}

	public int getPlayerContent(EntityPlayer ep, CrystalElement e) {
		NBTTagCompound tag = this.getTag(ep);
		return tag.getInteger(e.name());
	}

	public boolean playerHas(EntityPlayer ep, CrystalElement e, int amt) {
		return ep.capabilities.isCreativeMode || this.getPlayerContent(ep, e) >= amt;
	}

	public boolean addToPlayer(EntityPlayer ep, CrystalElement e, int amt) {
		NBTTagCompound tag = this.getTag(ep);
		int has = tag.getInteger(e.name());
		int val = Math.min(has+amt, this.getElementCap(ep));
		tag.setInteger(e.name(), val);
		return val > has;
	}

	public boolean addToPlayer(EntityPlayer ep, ElementTagCompound tag) {
		boolean flag = false;
		for (CrystalElement e : tag.elementSet()) {
			flag |= this.addToPlayer(ep, e, tag.getValue(e));
		}
		return flag;
	}

	public void removeFromPlayer(EntityPlayer ep, CrystalElement e, int amt) {
		if (ep.capabilities.isCreativeMode)
			return;
		NBTTagCompound tag = this.getTag(ep);
		int has = tag.getInteger(e.name());
		has -= amt;
		tag.setInteger(e.name(), Math.max(0, has));
	}

	public int getElementCap(EntityPlayer ep) {
		NBTTagCompound tag = this.getTag(ep);
		return Math.max(24, tag.getInteger("cap"));
	}

	public double getPlayerFraction(EntityPlayer ep, CrystalElement e) {
		return (double)this.getPlayerContent(ep, e)/this.getElementCap(ep);
	}

	public boolean upgradeCap(EntityPlayer ep) {
		NBTTagCompound tag = this.getTag(ep);
		int prev = this.getElementCap(ep);
		int val = Math.min(prev*4, 240000);
		tag.setInteger("cap", val);
		boolean flag = val > prev;
		if (flag) {
			ChromaSounds.CAST.playSound(ep.worldObj, ep.posX, ep.posY, ep.posZ, 1, 1);
		}
		return flag;
	}

	public boolean canPlayerAccept(EntityPlayer ep, CrystalElement e, int amt) {
		return this.getPlayerContent(ep, e)+amt <= this.getElementCap(ep);
	}

	public boolean isMaxed(EntityPlayer player, CrystalElement e) {
		return this.getPlayerContent(player, e) == this.getElementCap(player);
	}

	public boolean checkUpgrade(EntityPlayer player, boolean upgrade) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (!this.isMaxed(player, e))
				return false;
		}
		return upgrade ? this.upgradeCap(player) : true;
	}

	public boolean hasElement(EntityPlayer ep, CrystalElement e) {
		return this.getPlayerContent(ep, e) > 0;
	}

}
