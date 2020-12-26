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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

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
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
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

	public static void clearNoteTexts(ItemStack is) {
		if (is.stackTagCompound != null)
			is.stackTagCompound.removeTag("notes");
	}

	public static void addNoteText(ItemStack is, String s) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagList li = is.stackTagCompound.getTagList("notes", NBTTypes.STRING.ID);
		li.appendTag(new NBTTagString(s));
		is.stackTagCompound.setTag("notes", li);
		//ReikaJavaLibrary.pConsole("Appended "+s+" to "+li);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (this.isCreative(is)) {
			li.add("Creative Spawned");
		}
		else {
			li.add(String.format("Has %d of %d pages.", this.getNumberPages(is), ChromaResearch.getAllObtainableFragments().size()));
			int blanks = this.getBlanksStored(is);
			li.add(String.format("Has %d extra blank fragment%s.", blanks, blanks == 1 ? "" : "s"));
		}
	}

	public static boolean isCreative(ItemStack is) {
		return ChromaItems.HELP.matchWith(is) && is.stackTagCompound != null && is.stackTagCompound.getBoolean("creative");
	}

	public static int getBlanksStored(ItemStack is) {
		return ChromaItems.HELP.matchWith(is) && is.stackTagCompound != null ? is.stackTagCompound.getInteger("blanks") : 0;
	}

	public static void addBlanks(ItemStack is, int amt) {
		if (ChromaItems.HELP.matchWith(is)) {
			if (is.stackTagCompound == null) {
				is.stackTagCompound = new NBTTagCompound();
			}
			int get = is.stackTagCompound.getInteger("blanks");
			is.stackTagCompound.setInteger("blanks", get+amt);
		}
	}

	@SideOnly(Side.CLIENT)
	private void resetGuis() {
		GuiNavigation.resetOffset();
	}

	public void setItems(ItemStack is, ArrayList<ChromaResearch> li) {
		if (is == null || is.getItem() != this)
			return;
		NBTTagList list = new NBTTagList();
		for (ChromaResearch r : li) {
			if (r != null) {
				NBTTagString tag = new NBTTagString(r.name());
				list.appendTag(tag);
			}
		}
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("pages", list);
	}

	public ArrayList<ChromaResearch> getItemList(ItemStack tool) {
		ArrayList<ChromaResearch> li = new ArrayList();
		if (tool == null || tool.getItem() != this)
			return li;
		if (tool.stackTagCompound != null) {
			NBTTagList list = tool.stackTagCompound.getTagList("pages", NBTTypes.STRING.ID);
			Iterator<NBTTagString> it = list.tagList.iterator();
			while (it.hasNext()) {
				NBTTagString tag = it.next();
				String s = tag.func_150285_a_();
				ChromaResearch r = ChromaResearch.getByName(s);
				if (r == null) {
					ChromatiCraft.logger.logError("Null research item {"+s+"} in the book?!");
					it.remove();
					continue;
				}
				int idx = ChromaResearch.getAllObtainableFragments().indexOf(r);
				if (idx < 0) {
					it.remove();
					throw new WTFException("How did you get a parent (OR NONEXISTENT) fragment '"+r+"' in the book!?!", true);
				}
				li.add(r);
			}
		}
		return li;
	}

	public static boolean hasPage(ItemStack is, ChromaResearch b) {
		if (isCreative(is))
			return true;
		if (b.isAlwaysPresent())
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
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
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

	public static void recoverFragment(EntityPlayer ep, ChromaResearch r, ItemStack book) {
		ReikaInventoryHelper.findAndDecrStack(Items.paper, -1, ep.inventory.mainInventory);
		ReikaInventoryHelper.findAndDecrStack(ReikaItemHelper.inksac, ep.inventory.mainInventory);
		ItemChromaBook item = (ItemChromaBook)book.getItem();
		ArrayList<ChromaResearch> li = item.getItemList(book);
		li.add(r);
		item.setItems(book, li);
	}

	public static int checkForInk(ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (ReikaItemHelper.matchStacks(in, ReikaItemHelper.inksac))
					return i;
				else if (ReikaItemHelper.isInOreTag(in, "dyeBlack"))
					return i;
			}
		}
		return -1;
	}

}
