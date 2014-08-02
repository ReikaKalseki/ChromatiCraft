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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromatiGuis;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemInventoryLinker extends ItemChromaTool implements AuraPowered {

	public ItemInventoryLinker(int id, int index) {
		super(id, index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory) {
			this.link(is, te);
			return true;
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
			is.setItemDamage(1-is.getItemDamage());
		}
		else {
			ep.openGui(ChromatiCraft.instance, ChromatiGuis.LINK.ordinal(), world, 0, 0, 0);
		}
		return is;
	}

	public void setItems(ItemStack is, ArrayList<ItemStack> li) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < li.size(); i++) {
			ItemStack in = li.get(i);
			NBTTagCompound nbt = new NBTTagCompound();
			in.writeToNBT(nbt);
			items.appendTag(nbt);
		}
		is.stackTagCompound.setTag("items", items);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			if (is.stackTagCompound.hasKey("link")) {
				NBTTagCompound nbt = is.stackTagCompound.getCompoundTag("link");
				int x = nbt.getInteger("x");
				int y = nbt.getInteger("y");
				int z = nbt.getInteger("z");
				if (ep.worldObj != null) {
					int id = ep.worldObj.getBlockId(x, y, z);
					if (id != 0 && ep.worldObj.getBlockTileEntity(x, y, z) instanceof IInventory)
						li.add("Linked to "+Block.blocksList[id].getLocalizedName()+" at "+x+", "+y+", "+z);
					else if (id != 0) {
						li.add("Linked block "+Block.blocksList[id].getLocalizedName());
						li.add("at "+x+", "+y+", "+z+" is invalid.");
					}
					else {
						li.add("Linked block at "+x+", "+y+", "+z);
						li.add("is missing.");
					}
				}
				else {
					li.add("Linked to "+x+", "+y+", "+z);
				}
			}
			else {
				li.add("No link");
			}
			if (is.getItemDamage() == 1) {
				li.add("Sending all items");
			}
			else if (is.stackTagCompound.hasKey("items")) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					ArrayList<ItemStack> items = this.getItemList(is);
					for (int i = 0; i < items.size(); i++) {
						ItemStack in = items.get(i);
						li.add(">>"+in.getDisplayName());
					}
				}
				else {
					li.add(EnumChatFormatting.GREEN+"Hold shift for item data");
				}
			}
		}
	}

	@Override
	public ElementTagCompound getRequirements(ItemStack is) {
		return new ElementTagCompound();
	}

	private boolean canLinkItems() {
		return true;
	}

	public boolean linksItem(ItemStack tool, ItemStack is) {
		return this.canLinkItems() && (tool.getItemDamage() == 1 || ReikaItemHelper.listContainsItemStack(this.getItemList(tool), is));
	}

	public ArrayList<ItemStack> getItemList(ItemStack is) {
		ArrayList<ItemStack> li = new ArrayList();
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("items")) {
			NBTTagList items = is.stackTagCompound.getTagList("items");
			for (int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound nbt = (NBTTagCompound)items.tagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbt);
				li.add(item);
			}
		}
		return li;
	}

	public boolean processItem(World world, ItemStack tool, ItemStack is) {
		IInventory ii = this.getInventory(world, tool);
		if (ii != null) {
			return ReikaInventoryHelper.addToIInv(is.copy(), ii);
		}
		return false;
	}

	private IInventory getInventory(World world, ItemStack is) {
		if (is.stackTagCompound == null)
			return null;
		if (!is.stackTagCompound.hasKey("link"))
			return null;
		NBTTagCompound link = is.stackTagCompound.getCompoundTag("link");
		if (link != null) {
			int x = link.getInteger("x");
			int y = link.getInteger("y");
			int z = link.getInteger("z");
			TileEntity te = world.getBlockTileEntity(x, y, z);
			return te instanceof IInventory ? (IInventory)te : null;
		}
		return null;
	}

	private void link(ItemStack is, TileEntity te) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound link = new NBTTagCompound();
		link.setInteger("x", te.xCoord);
		link.setInteger("y", te.yCoord);
		link.setInteger("z", te.zCoord);
		is.stackTagCompound.setCompoundTag("link", link);
	}

}
