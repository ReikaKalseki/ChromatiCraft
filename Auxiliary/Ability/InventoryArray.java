package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

class InventoryArray {

	private LinkedList<Inventory> inventories = new LinkedList();

	InventoryArray() {

	}

	public void addPage() {
		inventories.addLast(new Inventory());
	}

	void shift(EntityPlayer ep, boolean up) {
		if (inventories.isEmpty())
			return;

		Inventory inv = new Inventory();
		inv.populate(ep);

		if (up) {
			Inventory i = inventories.getFirst();
			inventories.removeFirst();
			i.load(ep);

			inventories.addLast(inv);
		}
		else {
			Inventory i = inventories.getLast();
			inventories.removeLast();
			i.load(ep);

			inventories.addFirst(inv);
		}
	}

	void writeToNBT(NBTTagList NBT) {
		for (Inventory i : inventories) {
			NBTTagCompound tag = new NBTTagCompound();
			i.writeToNBT(tag);
			NBT.appendTag(tag);
		}
	}

	void readFromNBT(NBTTagList NBT) {
		for (Object tag : NBT.tagList) {
			Inventory i = new Inventory();
			i.readFromNBT((NBTTagCompound)tag);
			inventories.addLast(i);
		}
	}

	@Override
	public String toString() {
		return inventories.size()+": "+inventories.toString();
	}

	//private void addPage() {
	//	inventories.addLast(new Inventory(inventories.size()));
	//}

}

class Inventory extends BasicInventory {

	private static final int SIZE = 36;

	public Inventory() {
		super("Ability Page", SIZE);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}

	void writeToNBT(NBTTagCompound NBT) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

	void readFromNBT(NBTTagCompound NBT) {
		NBTTagList nbttaglist = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
		inv = new ItemStack[SIZE];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length) {
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	void load(EntityPlayer ep) {
		for (int i = 0; i < SIZE; i++) {
			ep.inventory.mainInventory[i] = inv[i];
		}
	}

	void populate(EntityPlayer ep) {
		for (int i = 0; i < SIZE; i++) {
			inv[i] = ep.inventory.mainInventory[i];
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(inv);
	}

}

class InventoryArrayData extends WorldSavedData {

	public static final String TAG = "CHROMAINVARRAY";

	public InventoryArrayData() {
		super(TAG);
	}

	public InventoryArrayData(String s) {
		super(s);
	}

	static InventoryArrayData initArrayData(World world) {
		InventoryArrayData data = (InventoryArrayData)world.loadItemData(InventoryArrayData.class, TAG);
		if (data == null) {
			data = new InventoryArrayData();
			world.setItemData(TAG, data);
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for (UUID s : AbilityHelper.instance.inventories.keySet()) {
			NBTTagList tag = nbt.getTagList(s.toString(), NBTTypes.COMPOUND.ID);
			AbilityHelper.instance.inventories.get(s.toString()).readFromNBT(tag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		for (UUID s : AbilityHelper.instance.inventories.keySet()) {
			NBTTagList tag = new NBTTagList();
			AbilityHelper.instance.inventories.get(s.toString()).writeToNBT(tag);
			nbt.setTag(s.toString(), tag);
		}
	}

}
