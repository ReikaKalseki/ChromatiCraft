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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemWithItemFilter;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Item.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public class ItemInventoryLinker extends ItemWithItemFilter {

	public ItemInventoryLinker(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IInventory) {
			IInventory ii = (IInventory)te;
			if (ii.getSizeInventory() <= 0) {
				ChromaSounds.ERROR.playSoundAtBlock(te);
				ChromatiCraft.logger.logError("Cannot link to size-zero inventory!");
				return false;
			}
			this.link(is, te);
			return true;
		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (!world.isRemote && e instanceof EntityPlayer && is.stackTagCompound != null && this.getMode(is) == Mode.REVERSED && is.stackTagCompound.hasKey("link")) {
			EntityPlayer ep = (EntityPlayer)e;
			WorldLocation loc = WorldLocation.readFromNBT("link", is.stackTagCompound);
			if (loc != null) {
				if (loc.isChunkLoaded()) {
					Block id = loc.getBlock();
					if (id != Blocks.air) {
						TileEntity te = loc.getTileEntity();
						if (te instanceof IInventory) {
							IInventory ii = (IInventory)te;
							int look = (int)(world.getTotalWorldTime()%ii.getSizeInventory());
							ItemStack in = ii.getStackInSlot(look);
							if (in != null) {
								if (ReikaInventoryHelper.addToIInv(in, ep.inventory)) {
									ii.setInventorySlotContents(look, null);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		super.addInformation(is, ep, li, vb);
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
		}
	}

	@Override
	public boolean isCurrentlyEnabled(EntityPlayer ep, ItemStack tool) {
		return true;
	}

	@Override
	public boolean canBeReversed(EntityPlayer ep, ItemStack tool) {
		return true;
	}

	public static boolean processItem(World world, ItemStack tool, ItemStack is) {
		IInventory ii = getInventory(world, tool);
		if (ii != null) {
			return ReikaInventoryHelper.addToIInv(is.copy(), ii);
		}
		return false;
	}

	private static IInventory getInventory(World world, ItemStack is) {
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

	private static void link(ItemStack is, TileEntity te) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		WorldLocation loc = new WorldLocation(te);
		loc.writeToNBT("link", is.stackTagCompound);
	}

	public static boolean tryLinkItem(EntityPlayer ep, ItemStack is) {
		//return MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, new EntityItem(ep.worldObj, ep.posX, ep.posY, ep.posZ, is)));
		return parseInventoryForLinking(ep, is, ep.inventory.mainInventory, null);
	}

	private static boolean parseInventoryForLinking(EntityPlayer ep, ItemStack picked, ItemStack[] inv, ItemStack active) {
		for (int i = 0; i < inv.length; i++) {
			if (active == null || ((ActivatedInventoryItem)active.getItem()).isSlotActive(active, i)) {
				ItemStack in = inv[i];
				if (in != null && in.getItem() == ChromaItems.LINK.getItemInstance()) {
					if (((ItemInventoryLinker)in.getItem()).matchesItem(ep, in, picked)) {
						if (processItem(ep.worldObj, in, picked)) {
							ep.playSound("random.pop", 0.5F, 1);
							return true;
						}
					}
				}
				else if (in != null && in.getItem() instanceof ActivatedInventoryItem) {
					parseInventoryForLinking(ep, picked, ((ActivatedInventoryItem)in.getItem()).getInventory(in), in);
				}
			}
		}
		return false;
	}

	@Override
	public String getActionName(EntityPlayer ep, ItemStack tool) {
		return this.getMode(tool) == Mode.REVERSED ? "Reversed flow direction" : "Sending";
	}

}
