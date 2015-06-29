/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChromaBook extends ItemChromaTool {

	public ItemChromaBook(int index) {
		super(index);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs cr, List li) {
		li.add(new ItemStack(this));
		ItemStack is = new ItemStack(this);
		is.stackTagCompound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			list.appendTag(new NBTTagString(r.name()));
		}
		is.stackTagCompound.setTag("pages", list);
		is.stackTagCompound.setBoolean("creative", true);
		li.add(is);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
			if (!this.isCreative(is))
			ep.openGui(ChromatiCraft.instance, ChromaGuis.BOOKPAGES.ordinal(), null, 0, 0, 0);
		}
		else {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				this.resetGuis();
			}
			ep.openGui(ChromatiCraft.instance, ChromaGuis.BOOKNAV.ordinal(), world, 0, 0, 0);
		}
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (this.isCreative(is)) {
			li.add("Creative Spawned");
		}
		else {
			li.add(String.format("Has %d of %d pages.", this.getNumberPages(is), ChromaResearch.getAllNonParents().size()));
		}
	}

	private static boolean isCreative(ItemStack is) {
		return ChromaItems.HELP.matchWith(is) && is.stackTagCompound != null && is.stackTagCompound.getBoolean("creative");
	}

	@SideOnly(Side.CLIENT)
	private void resetGuis() {
		GuiNavigation.resetOffset();
	}

	public void setItems(ItemStack is, ArrayList<ItemStack> li) {
		if (is == null || is.getItem() != this)
			return;
		NBTTagList list = new NBTTagList();
		for (ItemStack in : li) {
			ChromaResearch r = ItemInfoFragment.getResearch(in);
			if (r != null) {
				NBTTagString tag = new NBTTagString(r.name());
				list.appendTag(tag);
			}
		}
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("pages", list);
	}

	public ArrayList<ItemStack> getItemList(ItemStack tool) {
		ArrayList<ItemStack> li = new ArrayList();
		if (tool == null || tool.getItem() != this)
			return li;
		if (tool.stackTagCompound != null) {
			NBTTagList list = tool.stackTagCompound.getTagList("pages", NBTTypes.STRING.ID);
			for (Object o : list.tagList) {
				NBTTagString tag = (NBTTagString)o;
				li.add(ItemInfoFragment.getItem(ChromaResearch.valueOf(tag.func_150285_a_())));
			}
		}
		return li;
	}

	public static boolean hasPage(ItemStack is, ChromaResearch b) {
		if (isCreative(is))
			return true;
		if (b == ChromaResearch.START)
			return true;
		if (b == ChromaResearch.PACKCHANGES)
			return true;
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("pages"))
			return false;
		return is.stackTagCompound.getTagList("pages", NBTTypes.STRING.ID).tagList.contains(new NBTTagString(b.name()));
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		int base = super.getItemSpriteIndex(is);
		return this.isCreative(is) || this.hasAllPages(is) ? base+1 : base;
	}

	private static boolean hasAllPages(ItemStack is) {
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			if (!hasPage(is, r))
				return false;
		}
		return true;
	}

	public static int getNumberPages(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("pages"))
			return 0;
		return is.stackTagCompound.getTagList("pages", NBTTypes.STRING.ID).tagCount();
	}

	public static HashMap<String, PlayerNote> getNotes(ItemStack is) {
		return null;
	}

	public static void setNotes(ItemStack is, HashMap<String, PlayerNote> notes) {

	}

	public static class PlayerNote {

		public final String title;
		public final String note;

		private PlayerNote(String t, String n) {
			title = t;
			note = n;
		}

	}

}
