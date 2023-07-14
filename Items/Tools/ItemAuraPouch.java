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

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.ProjectileFiringTool;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.ItemSpecificEffectDescription.ItemListEffectDescription;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Interfaces.Item.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public class ItemAuraPouch extends ItemChromaTool implements ActivatedInventoryItem {

	public static final int SIZE = 27;

	private static final TreeMap<String, AuraPouchEffectDescription> specialEffects = new TreeMap();
	public static final String FIRING_EFFECT_DESC = "Fires automatically";
	public static final String WORKS_IN_POUCH_EFFECT_DESC = "Keeps functionality active";

	public static void setSpecialEffect(String desc, ChromaItems i) {
		setSpecialEffect(desc, i.getStackOf());
	}

	public static void setSpecialEffect(String desc, Item i) {
		setSpecialEffect(desc, new ItemStack(i));
	}

	public static void setSpecialEffect(String desc, ItemStack is) {
		setSpecialEffect(desc, new GuiStackDisplay(is));
	}

	public static void setSpecialEffect(String desc, GuiItemDisplay d) {
		AuraPouchEffectDescription eff = specialEffects.get(desc);
		if (eff == null) {
			eff = new AuraPouchEffectDescription(desc);
			specialEffects.put(desc, eff);
		}
		eff.addDisplays(d);
	}

	public static void registerProjectileFiringEffect(Item item) {
		setSpecialEffect(FIRING_EFFECT_DESC, item);
	}

	private static class AuraPouchEffectDescription extends ItemListEffectDescription {

		public AuraPouchEffectDescription(String s) {
			super(s);
		}

		@Override
		public String getDescription(GuiItemDisplay i) {
			String ret = super.getDescription(i);
			if (i instanceof GuiStackDisplay) {
				GuiStackDisplay g = (GuiStackDisplay)i;
				ItemStack is = g.getItem();
				if (is != null && is.getItem() instanceof ProjectileFiringTool) {
					ProjectileFiringTool p = (ProjectileFiringTool)is.getItem();
					ret += String.format(" (every %.1fs)", p.getAutofireRate()/20F);
				}
			}
			return ret;
		}

	}

	public static void setDefaultSpecialEffects() {
		for (ChromaItems i : ChromaItems.itemList) {
			if (ProjectileFiringTool.class.isAssignableFrom(i.getObjectClass()))
				setSpecialEffect(FIRING_EFFECT_DESC, i);
		}
	}

	public static Collection<AuraPouchEffectDescription> getEffects() {
		return Collections.unmodifiableCollection(specialEffects.values());
	}

	public ItemAuraPouch(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.AURAPOUCH.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		ItemStack[] inv = this.getInventory(is);
		boolean[] active = this.getActiveSlots(is);
		for (int i = 0; i < inv.length; i++) {
			if (active[i] && inv[i] != null) {
				Item ii = inv[i].getItem();
				if (e instanceof EntityPlayer && ii instanceof ProjectileFiringTool) {
					ProjectileFiringTool p = (ProjectileFiringTool)ii;
					if (e.ticksExisted%p.getAutofireRate() == 0)
						p.fire(inv[i], world, (EntityPlayer)e, true);
				}
				ii.onUpdate(inv[i], world, e, slot, false);
				if (e instanceof EntityPlayer && ii instanceof ItemArmor)
					ii.onArmorTick(world, (EntityPlayer)e, inv[i]);
			}
		}
		this.setItems(is, inv);
	}

	public boolean[] getActiveSlots(ItemStack is) {
		boolean[] arr = new boolean[SIZE];
		if (is.getItem() == this && is.stackTagCompound != null) {
			NBTTagCompound actives = is.stackTagCompound.getCompoundTag("active");
			for (int i = 0; i < arr.length; i++) {
				arr[i] = actives.getBoolean("slot_"+i);
			}
		}
		return arr;
	}

	public ItemStack[] getInventory(ItemStack is) {
		ItemStack[] inv = new ItemStack[SIZE];
		if (is.getItem() == this && is.stackTagCompound != null) {
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("inventory");
			for (int i = 0; i < inv.length; i++) {
				inv[i] = tag.hasKey("slot_"+i) ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("slot_"+i)) : null;
			}
		}
		return inv;
	}

	public ItemStack getItem(ItemStack is, int slot) {
		if (is.getItem() == this && is.stackTagCompound != null) {
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("inventory");
			ItemStack ret = tag.hasKey("slot_"+slot) ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("slot_"+slot)) : null;
			return ret;
		}
		return null;
	}

	public int getInventorySize(ItemStack is) {
		return SIZE;
	}

	public void setSlotActive(ItemStack is, int slot, boolean active) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("inventory") || !is.stackTagCompound.hasKey("active")) {
			this.initNBT(is);
		}
		is.stackTagCompound.getCompoundTag("active").setBoolean("slot_"+slot, active);
	}

	private void initNBT(ItemStack is) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("inventory", new NBTTagCompound());
		NBTTagCompound act = new NBTTagCompound();
		is.stackTagCompound.setTag("active", act);
		for (int i = 0; i < SIZE; i++) {
			act.setBoolean("slot_"+i, true);
		}
	}

	public void setItems(ItemStack is, ItemStack[] items) {
		if (items.length != SIZE)
			throw new IllegalArgumentException("Wrong inventory array!");
		if (is.stackTagCompound == null) {
			this.initNBT(is);
		}
		NBTTagCompound inv = new NBTTagCompound();
		for (int i = 0; i < items.length; i++) {
			ItemStack in = items[i];
			if (in != null) {
				NBTTagCompound tag = new NBTTagCompound();
				in.writeToNBT(tag);
				inv.setTag("slot_"+i, tag);
			}
		}
		is.stackTagCompound.setTag("inventory", inv);
	}

	@Override
	public int getItemEnchantability() {
		return Items.leather_chestplate.getItemEnchantability();
	}

	@Override
	public void decrementSlot(ItemStack is, int slot, int amt) {
		ItemStack[] inv = this.getInventory(is);
		ReikaInventoryHelper.decrStack(slot, inv, amt);
		this.setItems(is, inv);
	}

	@Override
	public boolean isSlotActive(ItemStack is, int slot) {
		return this.getActiveSlots(is)[slot];
	}

}
