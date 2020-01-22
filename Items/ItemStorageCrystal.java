/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionLinking;
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
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
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
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer)
			ProgressionLinking.instance.attemptSyncTriggerProgressFor((EntityPlayer)e, ProgressStage.STORAGE);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ElementTagCompound tag = this.getTag(is);
		for (CrystalElement e : tag.elementSet()) {
			li.add(String.format("%s: %d", e.displayName, tag.getValue(e)));
		}
	}

	public static int getCapacity(ItemStack is) {
		return (int)(1000*Math.pow(8, is.getItemDamage()-1));
	}

	public static void addEnergy(ItemStack is, CrystalElement e, int value) {
		ElementTagCompound etg = getTag(is);
		int amt = getStoredEnergy(is, e);
		int sum = Math.min(getCapacity(is), amt+value);
		etg.setTag(e, sum);
		writeTag(is, etg);
	}

	public static void removeEnergy(ItemStack is, CrystalElement e, int value) {
		ElementTagCompound etg = getTag(is);
		etg.subtract(e, value);
		writeTag(is, etg);
	}

	private static ElementTagCompound getTag(ItemStack is) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		ElementTagCompound etg = new ElementTagCompound();
		etg.readFromNBT("energy", is.stackTagCompound);
		etg.clearEmptyKeys();
		return etg;
	}

	private static void writeTag(ItemStack is, ElementTagCompound etg) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		etg.writeToNBT("energy", is.stackTagCompound);
	}


	public static int getStoredEnergy(ItemStack is, CrystalElement e) {
		return getTag(is).getValue(e);
	}

	public static int getTotalEnergy(ItemStack is) {
		return getTag(is).getTotalEnergy();
	}

	public static ElementTagCompound getStoredTags(ItemStack is) {
		return getTag(is).copy();
	}

	public static int getSpace(CrystalElement e, ItemStack is) {
		return getCapacity(is)-getStoredEnergy(is, e);
	}

	public static boolean isFull(ItemStack is) {
		int max = getCapacity(is);
		ElementTagCompound tag = getTag(is);
		for (CrystalElement e : tag.elementSet()) {
			if (getSpace(e, is) > 0)
				return false;
		}
		return true;
	}

}
