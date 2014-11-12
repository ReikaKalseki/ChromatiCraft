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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStorageCrystal extends ItemChromaTool {

	public ItemStorageCrystal(int tex) {
		super(tex);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ChromaItems.STORAGE.getNumberMetadatas(); i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
			ItemStack item2 = item.copy();
			for (int k = 0; k < CrystalElement.elements.length; k++)
				this.addEnergy(item2, CrystalElement.elements[k], this.getCapacity(item2));
			par3List.add(item2);
		}
	}

	@Override
	public void onUsingTick(ItemStack is, EntityPlayer ep, int count) {

	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ElementTagCompound tag = this.getTag(is);
		for (CrystalElement e : tag.elementSet()) {
			li.add(String.format("%s: %d", e.displayName, tag.getValue(e)));
		}
	}

	public int getCapacity(ItemStack is) {
		return (int)(1000*Math.pow(4, is.getItemDamage()-1));
	}

	public void addEnergy(ItemStack is, CrystalElement e, int value) {
		ElementTagCompound etg = this.getTag(is);
		int amt = this.getStoredEnergy(is, e);
		int sum = Math.min(this.getCapacity(is), amt+value);
		etg.setTag(e, sum);
		this.writeTag(is, etg);
	}

	public void removeEnergy(ItemStack is, CrystalElement e, int value) {
		ElementTagCompound etg = this.getTag(is);
		etg.subtract(e, value);
		this.writeTag(is, etg);
	}

	private ElementTagCompound getTag(ItemStack is) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		ElementTagCompound etg = new ElementTagCompound();
		etg.readFromNBT("energy", is.stackTagCompound);
		etg.clearEmptyKeys();
		return etg;
	}

	private void writeTag(ItemStack is, ElementTagCompound etg) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		etg.writeToNBT("energy", is.stackTagCompound);
	}


	public int getStoredEnergy(ItemStack is, CrystalElement e) {
		return this.getTag(is).getValue(e);
	}

	public int getTotalEnergy(ItemStack is) {
		return this.getTag(is).getTotalEnergy();
	}

	public ElementTagCompound getStoredTags(ItemStack is) {
		return this.getTag(is).copy();
	}

	public int getSpace(CrystalElement e, ItemStack is) {
		return this.getCapacity(is)-this.getStoredEnergy(is, e);
	}

	public boolean isFull(ItemStack is) {
		int max = this.getCapacity(is);
		ElementTagCompound tag = this.getTag(is);
		for (CrystalElement e : tag.elementSet()) {
			if (this.getSpace(e, is) > 0)
				return false;
		}
		return true;
	}

}
