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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Item.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class ItemInventoryLinker extends ItemChromaTool {

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
			this.setMode(is, this.getMode(is).next());
		}
		else {
			ep.openGui(ChromatiCraft.instance, ChromaGuis.LINK.ordinal(), world, 0, 0, 0);
		}
		return is;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		return getMode(item) == Mode.REVERSED ? base+1 : base;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (!world.isRemote && e instanceof EntityPlayer && is.stackTagCompound != null && getMode(is) == Mode.REVERSED && is.stackTagCompound.hasKey("link")) {
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
			Mode m = getMode(is);
			switch(m) {
				case EVERYTHING:
					li.add("Sending all items");
					break;
				case NOTHING:
					li.add("Sending no items");
					break;
				case BLACKLIST:
					li.add("Sending everything except:");
					break;
				case WHITELIST:
					li.add("Sending:");
					break;
				case REVERSED:
					li.add("Reversed flow direction");
					break;
			}
			if (m.usesInventory() && is.stackTagCompound.hasKey("items")) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					for (KeyedItemStack in : getItemList(is)) {
						li.add(">>"+in.getCriteriaAsChatFormatting()+in.getItemStack().getDisplayName());
					}
				}
				else {
					li.add(EnumChatFormatting.GREEN+"Hold shift for item data");
				}
			}
		}
	}

	private static boolean canLinkItems(EntityPlayer ep, ItemStack tool) {
		return true;
	}

	public static boolean linksItem(EntityPlayer ep, ItemStack tool, ItemStack is) {
		return canLinkItems(ep, tool) && getMode(tool).linksItem(tool, is);
	}

	public static Mode getMode(ItemStack tool) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		return Mode.list[tool.stackTagCompound.getInteger("mode")];
	}

	private static void setMode(ItemStack tool, Mode mode) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		tool.stackTagCompound.setInteger("mode", mode.ordinal());
	}

	public static ArrayList<Filter> getFilters(ItemStack is) {
		ArrayList<Filter> li = new ArrayList();
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("filter")) {
			NBTTagList items = is.stackTagCompound.getTagList("filter", NBTTypes.COMPOUND.ID);
			for (NBTTagCompound nbt : ((List<NBTTagCompound>)items.tagList)) {
				String s = nbt.getString("filterType");
				Filter f = constructFilter(s);
				if (f == null) {
					ChromatiCraft.logger.logError("Error reading item filter for "+is.getDisplayName()+"; unrecognized type "+s);
					continue;
				}
				f.readFromNBT(nbt);
				li.add(f);
			}
		}
		return li;
	}

	public static final HashSet<KeyedItemStack> getItemList(ItemStack tool) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		if (tool.stackTagCompound.hasKey("items")) {
			return loadItemList(tool.stackTagCompound.getTagList("items", NBTTypes.COMPOUND.ID));
		}
		ArrayList<Filter> li = getFilters(tool);
		HashSet<KeyedItemStack> set = new HashSet();
		for (Filter f : li) {
			KeyedItemStack ks = f.getKey();
			if (!set.contains(ks))
				set.add(ks);
		}
		return set;
	}

	private static HashSet<KeyedItemStack> loadItemList(NBTTagList tag) {
		HashSet<KeyedItemStack> set = new HashSet();
		for (NBTTagCompound nbt : ((List<NBTTagCompound>)tag.tagList)) {
			KeyedItemStack item = KeyedItemStack.readFromNBT(nbt);
			if (!set.contains(item))
				set.add(item);
		}
		return set;
	}

	private static void saveItemList(NBTTagCompound tag, HashSet<KeyedItemStack> set) {
		NBTTagList li = new NBTTagList();
		for (KeyedItemStack is : set) {
			NBTTagCompound nbt = new NBTTagCompound();
			is.writeToNBT(nbt);
			li.appendTag(nbt);
		}
		tag.setTag("items", li);
	}

	private static Filter constructFilter(String s) {
		try {
			Class c = Class.forName(s);
			Constructor<Filter> con = c.getDeclaredConstructor();
			con.setAccessible(true);
			return con.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void setFilters(ItemStack is, ArrayList<Filter> li) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.removeTag("items");
		NBTTagList items = new NBTTagList();
		for (Filter f : li) {
			NBTTagCompound nbt = new NBTTagCompound();
			f.writeToNBT(nbt);
			nbt.setString("filterType", f.getClass().getName());
			items.appendTag(nbt);
		}
		is.stackTagCompound.setTag("filter", items);
	}

	public static void setItems(ItemStack is, ArrayList<ItemStack> li) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		ArrayList<Filter> li2 = new ArrayList();
		for (ItemStack in : li) {
			Filter f = constructFilter(in);
			if (f != null)
				li2.add(f);
		}
		setFilters(is, li2);
		saveItemList(is.stackTagCompound, getItemList(is));
	}

	private static Filter constructFilter(ItemStack in) {
		if (in == null)
			return null;
		//		if (in.getItem() == ChromaItems.INVFILTER.getItemInstance())
		//			return ?;
		return new ItemFilter(in);
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

	public static enum Mode {
		WHITELIST(),
		BLACKLIST(),
		EVERYTHING(),
		NOTHING(),
		REVERSED();

		private static final Mode[] list = values();

		private boolean linksItem(ItemStack tool, ItemStack is) {
			switch(this) {
				case BLACKLIST:
					return !WHITELIST.linksItem(tool, is);
				case WHITELIST:
					return ItemInventoryLinker.getItemList(tool).contains(new KeyedItemStack(is).setSimpleHash(true));
				case EVERYTHING:
					return true;
				case NOTHING:
					return false;
				case REVERSED:
					return false;
			}
			return false;
		}

		public boolean usesInventory() {
			return this != EVERYTHING && this != NOTHING && this != REVERSED;
		}

		private Mode next() {
			return list[(this.ordinal()+1)%list.length];
		}
	}

	public static abstract class Filter {

		protected Filter() {

		}

		protected abstract void writeToNBT(NBTTagCompound tag);
		protected abstract void readFromNBT(NBTTagCompound tag);

		protected abstract KeyedItemStack getKey();

	}

	public static final class ItemFilter extends Filter {

		private ItemStack item;

		private ItemFilter() {
			super();
		}

		public ItemFilter(ItemStack is) {
			item = is.copy();
		}

		@Override
		protected void writeToNBT(NBTTagCompound tag) {
			item.writeToNBT(tag);
		}

		@Override
		protected void readFromNBT(NBTTagCompound tag) {
			item = ItemStack.loadItemStackFromNBT(tag);
		}

		@Override
		protected KeyedItemStack getKey() {
			return new KeyedItemStack(item).setIgnoreMetadata(false).setSized(false).setIgnoreNBT(false).setSimpleHash(true);
		}

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
					if (linksItem(ep, in, picked)) {
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

}
