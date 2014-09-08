/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import Reika.ChromatiCraft.Items.AuraPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemInventoryLinker extends ItemChromaTool implements AuraPowered {

	public ItemInventoryLinker(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		TileEntity te = world.getTileEntity(x, y, z);
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
			ep.openGui(ChromatiCraft.instance, ChromaGuis.LINK.ordinal(), world, 0, 0, 0);
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
				WorldLocation loc = WorldLocation.readFromNBT("link", is.stackTagCompound);
				if (loc != null) {
					if (loc.getWorld() != null) {
						Block id = loc.getBlock();
						if (id != Blocks.air && loc.getTileEntity() instanceof IInventory)
							li.add("Linked to "+id.getLocalizedName()+" at "+loc);
						else if (id != Blocks.air) {
							li.add("Linked block "+id.getLocalizedName());
							li.add("at "+loc+" is invalid.");
						}
						else {
							li.add("Linked block at "+loc);
							li.add("is missing.");
						}
					}
					else {
						li.add("Linked to "+loc);
					}
				}
				else {
					li.add("Invalid link");
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
			NBTTagList items = is.stackTagCompound.getTagList("items", is.stackTagCompound.getId());
			for (int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound nbt = items.getCompoundTagAt(i);
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
		WorldLocation loc = WorldLocation.readFromNBT("link", is.stackTagCompound);
		if (loc != null) {
			TileEntity te = loc.getTileEntity();
			return te instanceof IInventory ? (IInventory)te : null;
		}
		return null;
	}

	private void link(ItemStack is, TileEntity te) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		WorldLocation loc = new WorldLocation(te);
		loc.writeToNBT("link", is.stackTagCompound);
	}

}
