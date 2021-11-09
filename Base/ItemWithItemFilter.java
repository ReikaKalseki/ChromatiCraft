package Reika.ChromatiCraft.Base;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public abstract class ItemWithItemFilter extends ItemChromaTool {

	public ItemWithItemFilter(int index) {
		super(index);
	}

	@Override
	public final ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
			Mode m = this.getMode(is).next();
			if (m == Mode.REVERSED && !this.canBeReversed(ep, is))
				m = m.next();
			this.setMode(is, m);
		}
		else {
			ep.openGui(ChromatiCraft.instance, ChromaGuis.ITEMWITHFILTER.ordinal(), world, 0, 0, 0);
		}
		return is;
	}

	@Override
	public final int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		return this.getMode(item) == Mode.REVERSED ? base+1 : base;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			Mode m = this.getMode(is);
			String s = this.getActionName(ep, is);
			switch(m) {
				case EVERYTHING:
					li.add(s+" all items");
					break;
				case NOTHING:
					li.add(s+" no items");
					break;
				case BLACKLIST:
					li.add(s+" everything except:");
					break;
				case WHITELIST:
					li.add(s+":");
					break;
				case REVERSED:
					li.add(s);
					break;
			}
			if (m.usesInventory() && is.stackTagCompound.hasKey("items")) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					for (KeyedItemStack in : this.getItemList(is)) {
						li.add(">>"+in.getCriteriaAsChatFormatting()+in.getItemStack().getDisplayName());
					}
				}
				else {
					li.add(EnumChatFormatting.GREEN+"Hold shift for item data");
				}
			}
		}
	}

	public final boolean matchesItem(EntityPlayer ep, ItemStack tool, ItemStack is) {
		return this.isCurrentlyEnabled(ep, tool) && this.getMode(tool).matchesItem(tool, is);
	}

	public abstract boolean isCurrentlyEnabled(EntityPlayer ep, ItemStack tool);

	public abstract boolean canBeReversed(EntityPlayer ep, ItemStack tool);

	public abstract String getActionName(EntityPlayer ep, ItemStack tool);

	public final Mode getMode(ItemStack tool) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		return Mode.list[tool.stackTagCompound.getInteger("mode")];
	}

	private final void setMode(ItemStack tool, Mode mode) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		tool.stackTagCompound.setInteger("mode", mode.ordinal());
	}

	public final ArrayList<Filter> getFilters(ItemStack is) {
		ArrayList<Filter> li = new ArrayList();
		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("filter")) {
			NBTTagList items = is.stackTagCompound.getTagList("filter", NBTTypes.COMPOUND.ID);
			for (NBTTagCompound nbt : ((List<NBTTagCompound>)items.tagList)) {
				String s = nbt.getString("filterType");
				Filter f = this.constructFilter(s);
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

	public final HashSet<KeyedItemStack> getItemList(ItemStack tool) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		if (tool.stackTagCompound.hasKey("items")) {
			return this.loadItemList(tool.stackTagCompound.getTagList("items", NBTTypes.COMPOUND.ID));
		}
		ArrayList<Filter> li = this.getFilters(tool);
		HashSet<KeyedItemStack> set = new HashSet();
		for (Filter f : li) {
			KeyedItemStack ks = f.getKey();
			if (!set.contains(ks))
				set.add(ks);
		}
		return set;
	}

	private final HashSet<KeyedItemStack> loadItemList(NBTTagList tag) {
		HashSet<KeyedItemStack> set = new HashSet();
		for (NBTTagCompound nbt : ((List<NBTTagCompound>)tag.tagList)) {
			KeyedItemStack item = KeyedItemStack.readFromNBT(nbt);
			if (!set.contains(item))
				set.add(item);
		}
		return set;
	}

	private final void saveItemList(NBTTagCompound tag, HashSet<KeyedItemStack> set) {
		NBTTagList li = new NBTTagList();
		for (KeyedItemStack is : set) {
			NBTTagCompound nbt = new NBTTagCompound();
			is.writeToNBT(nbt);
			li.appendTag(nbt);
		}
		tag.setTag("items", li);
	}

	private final Filter constructFilter(String s) {
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

	private final void setFilters(ItemStack is, ArrayList<Filter> li) {
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

	public final void setItems(ItemStack is, ArrayList<ItemStack> li) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		ArrayList<Filter> li2 = new ArrayList();
		for (ItemStack in : li) {
			Filter f = this.constructFilter(in);
			if (f != null)
				li2.add(f);
		}
		this.setFilters(is, li2);
		this.saveItemList(is.stackTagCompound, this.getItemList(is));
	}

	private final Filter constructFilter(ItemStack in) {
		if (in == null)
			return null;
		//		if (in.getItem() == ChromaItems.INVFILTER.getItemInstance())
		//			return ?;
		return new ItemFilter(in);
	}

	public static enum Mode {
		WHITELIST(),
		BLACKLIST(),
		EVERYTHING(),
		NOTHING(),
		REVERSED();

		private static final Mode[] list = values();

		private boolean matchesItem(ItemStack tool, ItemStack is) {
			switch(this) {
				case BLACKLIST:
					return !WHITELIST.matchesItem(tool, is);
				case WHITELIST:
					ItemWithItemFilter bag = ((ItemWithItemFilter)tool.getItem());
					KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true).setSized(false);
					if (!bag.checkNBT(tool, is))
						ks.setIgnoreNBT(true);
					return bag.getItemList(tool).contains(ks);
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

	public boolean checkNBT(ItemStack tool, ItemStack is) {
		return true;
	}

}
