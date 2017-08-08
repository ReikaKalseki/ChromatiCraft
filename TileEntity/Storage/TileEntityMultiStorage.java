/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class TileEntityMultiStorage extends TileEntityChromaticBase implements ItemOnRightClick, BreakAction {

	private HashMap<KeyedItemStack, Integer> items = new HashMap();

	private BlockArray blocks = new BlockArray();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.STORAGE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public int getSize() {
		return 2;//blocks.getSize();
	}

	public int getCapacity() {
		return 16*64*this.getSize()*this.getSize();
	}

	public int getNumberTypesPermitted() {
		return (int)Math.sqrt(this.getSize());
	}

	private int getTotalStoredCount() {
		int num = 0;
		for (KeyedItemStack ks : items.keySet()) {
			num += items.get(ks);
		}
		return num;
	}

	public ItemStack addItem(ItemStack is) {
		if (this.containsItem(is)) {
			return this.doAddItem(is);
		}
		else if (this.canAcceptNewType(is)) {
			return this.doAddItem(is);
		}
		else {
			return is;
		}
	}

	private ItemStack doAddItem(ItemStack is) {
		int space = this.getCapacity()-this.getTotalStoredCount();
		if (space >= is.stackSize) {
			this.putItem(is);
			return null;
		}
		else {
			this.putItem(ReikaItemHelper.getSizedItemStack(is, space));
			is.stackSize -= space;
			return is;
		}
	}

	private void putItem(ItemStack is) {
		KeyedItemStack ks = this.getKey(is);
		Integer get = items.get(ks);
		int num = get != null ? get.intValue() : 0;
		items.put(ks, num+is.stackSize);
	}

	private void doRemoveItem(ItemStack is, int num) {
		KeyedItemStack ks = this.getKey(is);
		int get = items.get(ks);
		int res = get-num;
		if (res > 0)
			items.put(ks, res);
		else
			items.remove(ks);
	}

	public ItemStack removeItem(ItemStack is) {
		int rem = Math.min(is.getMaxStackSize(), this.countItem(is));
		if (rem > 0) {
			this.doRemoveItem(is, rem);
			return ReikaItemHelper.getSizedItemStack(is, rem);
		}
		return null;
	}

	public Collection<KeyedItemStack> getItemTypes() {
		return Collections.unmodifiableCollection(items.keySet());
	}

	public int countItem(ItemStack is) {
		KeyedItemStack ks = this.getKey(is);
		return items.containsKey(ks) ? items.get(ks) : 0;
	}

	private boolean canAcceptNewType(ItemStack is) {
		return items.keySet().size() < this.getNumberTypesPermitted();
	}

	private boolean containsItem(ItemStack is) {
		return items.containsKey(this.getKey(is));
	}

	private KeyedItemStack getKey(ItemStack is) {
		return new KeyedItemStack(ReikaItemHelper.getSizedItemStack(is, 1)).setIgnoreNBT(false).setIgnoreMetadata(false).setSimpleHash(true).lock();
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (item == null) {
			this.dropItem(ep);
			return null;
		}
		else {
			return this.addItem(item);
		}
	}

	private void dropItem(EntityPlayer ep) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBTTagList nbttaglist = new NBTTagList();

		for (KeyedItemStack ls : items.keySet()) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("count", items.get(ls));
			ls.getItemStack().writeToNBT(nbttagcompound);
			nbttaglist.appendTag(nbttagcompound);
		}

		NBT.setTag("Items", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		items.clear();

		NBTTagList nbttaglist = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int amt = nbttagcompound.getInteger("count");

			if (amt >= 0) {
				ItemStack is = ItemStack.loadItemStackFromNBT(nbttagcompound);
				items.put(this.getKey(is), amt);
			}
		}
	}

	@Override
	public void breakBlock() {
		for (KeyedItemStack ls : items.keySet()) {
			int num = items.get(ls);
			ItemStack is = ls.getItemStack();
			while (num > 0) {
				int drop = Math.min(num, is.getMaxStackSize());
				ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ReikaItemHelper.getSizedItemStack(is, drop));
				num -= drop;
			}
		}
	}

}
