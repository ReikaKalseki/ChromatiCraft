package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;

public class ItemAuraPouch extends ItemChromaTool {

	public static final int SIZE = 27;

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
				inv[i].getItem().onUpdate(inv[i], world, e, slot, false);
			}
		}
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

	public void setSlotActive(ItemStack is, int slot, boolean active) {
		if (is.stackTagCompound == null) {
			this.initNBT(is);
		}
		is.stackTagCompound.getCompoundTag("active").setBoolean("slot_"+slot, active);
	}

	private void initNBT(ItemStack is) {
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

}
