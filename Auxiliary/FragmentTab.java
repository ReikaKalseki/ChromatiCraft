/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.GUI.SortedCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FragmentTab extends SortedCreativeTab {

	public FragmentTab(String name) {
		super(name);
		this.setNoTitle();
	}

	@Override
	public boolean hasSearchBar()
	{
		return true;
	}

	@Override
	public int getSearchbarWidth()
	{
		return 73;//super.getSearchbarWidth();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return ChromaItems.FRAGMENT.getStackOf();
	}

	@Override
	protected Comparator<ItemStack> getComparator() {
		return comparator;
	}

	private static final FragmentComparator comparator = new FragmentComparator();

	private static final class FragmentComparator implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			ChromaResearch r1 = ItemInfoFragment.getResearch(o1);
			ChromaResearch r2 = ItemInfoFragment.getResearch(o2);
			if (r1 != null && r2 != null) {
				return r1.ordinal()-r2.ordinal();
			}
			else if (r1 != null) { //blank first
				return Integer.MAX_VALUE;
			}
			else if (r2 != null) {
				return Integer.MIN_VALUE;
			}
			else
				return 0;
		}

	}

}
