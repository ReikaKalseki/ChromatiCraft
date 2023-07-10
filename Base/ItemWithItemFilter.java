package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	@SideOnly(Side.CLIENT)
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
				if (GuiScreen.isShiftKeyDown()) {
					for (Filter in : this.getItemList(is)) {
						li.add(">>"+in.item.getCriteriaAsChatFormatting()+in.item.getItemStack().getDisplayName());
					}
				}
				else {
					li.add(EnumChatFormatting.GREEN+"Hold shift for item data");
				}
			}
		}
		li.add("Set filters with right-click");
		li.add("Cycle filter modes with shift right-click");
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

	public final ArrayList<Filter> getItemList(ItemStack tool) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		if (tool.stackTagCompound.hasKey("items")) {
			return this.loadItemList(tool.stackTagCompound.getTagList("items", NBTTypes.COMPOUND.ID));
		}
		return new ArrayList();
	}

	private final ArrayList<Filter> loadItemList(NBTTagList tag) {
		ArrayList<Filter> li = new ArrayList();
		for (NBTTagCompound nbt : ((List<NBTTagCompound>)tag.tagList)) {
			Filter f = new Filter();
			f.readFromNBT(nbt);
			li.add(f);
		}
		return li;
	}

	private final void saveItemList(NBTTagCompound tag, Filter[] set) {
		NBTTagList li = new NBTTagList();
		for (Filter is : set) {
			if (is == null)
				continue;
			NBTTagCompound nbt = new NBTTagCompound();
			is.writeToNBT(nbt);
			li.appendTag(nbt);
		}
		tag.setTag("items", li);
	}

	public final void setItems(ItemStack is, Filter[] filters) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		this.saveItemList(is.stackTagCompound, filters);
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
					for (Filter f : bag.getItemList(tool))
						if (f.match(is))
							return true;
					return false;
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

	public static final class Filter {

		private KeyedItemStack item;

		public Filter() {

		}

		public Filter(ItemStack is) {
			item = key(is);
		}

		private static KeyedItemStack key(ItemStack is) {
			return new KeyedItemStack(is).setIgnoreMetadata(false).setSized(false).setIgnoreNBT(false).setSimpleHash(true);
		}

		public void writeToNBT(NBTTagCompound tag) {
			if (item != null)
				item.writeToNBT(tag);
			tag.setString("filterType", this.getClass().getName());
		}

		public void readFromNBT(NBTTagCompound tag) {
			item = KeyedItemStack.readFromNBT(tag);
		}

		public void toggleNBT() {
			if (item != null)
				item.setIgnoreNBT(item.hasNBT());
		}

		public boolean hasNBT() {
			return item != null && item.hasNBT();
		}

		public ItemStack getDisplay() {
			return item == null ? null : item.getItemStack();
		}

		public boolean match(ItemStack is) {
			return item != null && item.match(is);
		}

	}

}
