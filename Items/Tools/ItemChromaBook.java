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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
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
		for (int i = 1; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			if (!r.isParent())
				list.appendTag(new NBTTagInt(i));
		}
		is.stackTagCompound.setTag("pages", list);
		li.add(is);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking()) {
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

	@SideOnly(Side.CLIENT)
	private void resetGuis() {
		GuiNavigation.resetOffset();
	}

	public void setItems(ItemStack is, ArrayList<ItemStack> li) {
		NBTTagList list = new NBTTagList();
		for (ItemStack in : li) {
			NBTTagInt tag = new NBTTagInt(in.getItemDamage());
			list.appendTag(tag);
		}
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("pages", list);
	}

	public ArrayList<ItemStack> getItemList(ItemStack tool) {
		ArrayList<ItemStack> li = new ArrayList();
		if (tool.stackTagCompound != null) {
			NBTTagList list = tool.stackTagCompound.getTagList("pages", NBTTypes.INT.ID);
			for (Object o : list.tagList) {
				NBTTagInt tag = (NBTTagInt)o;
				li.add(ChromaItems.FRAGMENT.getStackOfMetadata(tag.func_150287_d()));
			}
		}
		return li;
	}

	public static boolean hasPage(ItemStack is, ChromaResearch b) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("pages"))
			return false;
		return is.stackTagCompound.getTagList("pages", NBTTypes.INT.ID).tagList.contains(new NBTTagInt(b.ordinal()));
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		int base = super.getItemSpriteIndex(is);
		return this.hasAllPages(is) ? base+1 : base;
	}

	private boolean hasAllPages(ItemStack is) {
		for (int i = 1; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			if (!r.isParent()) {
				if (!this.hasPage(is, r))
					return false;
			}
		}
		return true;
	}

}
