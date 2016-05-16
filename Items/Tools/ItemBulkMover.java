/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;

public class ItemBulkMover extends ItemChromaTool {

	public ItemBulkMover(int index) {
		super(index);
	}

	public static int getStoredItems(ItemStack tool) {
		return tool.stackTagCompound != null ? tool.stackTagCompound.getInteger("count") : 0;
	}

	public static ItemStack getStoredItem(ItemStack tool) {
		return tool.stackTagCompound != null ? ItemStack.loadItemStackFromNBT(tool.stackTagCompound.getCompoundTag("item")) : null;
	}

	public static void setStoredItem(ItemStack tool, ItemStack store) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		if (store != null)
			store.writeToNBT(tag);
		tool.stackTagCompound.setTag("item", tag);
	}

	public static int getNumberToCarry(ItemStack tool) {
		return tool.stackTagCompound != null ? tool.stackTagCompound.getInteger("space") : 0;
	}

	public static void setNumberToCarry(ItemStack tool, int amt) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		tool.stackTagCompound.setInteger("space", amt);
	}

	private static boolean hasItems(ItemStack tool) {
		return getStoredItems(tool) > 0;
	}

	private static void removeItems(ItemStack tool, int rem) {
		int has = tool.stackTagCompound.getInteger("count");
		tool.stackTagCompound.setInteger("count", has-rem);
	}

	private static void addItems(ItemStack tool, int add) {
		int has = tool.stackTagCompound.getInteger("count");
		tool.stackTagCompound.setInteger("count", has+add);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.BULKMOVER.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (world.isRemote) {
			return true;
		}

		if (this.getStoredItem(is) != null) {
			InventoryInteraction ii = this.getInteraction(world, x, y, z, s, ep);
			if (ii != null) {
				if (this.hasItems(is)) {
					int added = ii.addItems(this.getStoredItem(is), this.getStoredItems(is));
					removeItems(is, added);
				}
				else {
					int rem = ii.drawItems(this.getStoredItem(is), this.getNumberToCarry(is));
					addItems(is, rem);
				}
			}
		}

		return false;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		int amt = this.getStoredItems(is);
		ItemStack has = this.getStoredItem(is);
		if (has != null) {
			String s = String.format("Has %d of %s; set to move up to %d", amt, has.getDisplayName(), this.getNumberToCarry(is));
			li.add(s);
		}
		else {
			li.add("No stored items.");
		}
	}

	private InventoryInteraction getInteraction(World world, int x, int y, int z, int side, EntityPlayer ep) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (InterfaceCache.GRIDHOST.instanceOf(te)) {
			return new AEInteraction(((IGridHost)te).getGridNode(ForgeDirection.VALID_DIRECTIONS[side]), ep);
		}
		else if (te instanceof IInventory) {
			return new IInvInteraction((IInventory)te);
		}
		else
			return null;
	}

	private static class AEInteraction implements InventoryInteraction {

		private final MESystemReader me;

		private AEInteraction(IGridNode ign, EntityPlayer ep) {
			me = new MESystemReader(ign, ep);
		}

		@Override
		public int addItems(ItemStack is, int max) {
			return (int)(max-me.addItem(ReikaItemHelper.getSizedItemStack(is, max), false));
		}

		@Override
		public int drawItems(ItemStack is, int max) {
			return (int)me.removeItem(ReikaItemHelper.getSizedItemStack(is, max), false, true);
		}

	}

	private static class IInvInteraction implements InventoryInteraction {

		private final IInventory inventory;

		private IInvInteraction(IInventory ii) {
			inventory = ii;
		}

		@Override
		public int addItems(ItemStack is, int max) {
			return max-ReikaInventoryHelper.addToInventoryWithLeftover(ReikaItemHelper.getSizedItemStack(is, max), inventory, false);
		}

		@Override
		public int drawItems(ItemStack is, int max) {
			return ReikaInventoryHelper.drawFromInventory(is, max, inventory);
		}

	}

	private static interface InventoryInteraction {

		/** Returns number successfully added. */
		public int addItems(ItemStack is, int max);

		/** Returns number successfully removed. */
		public int drawItems(ItemStack is, int max);

	}

}
