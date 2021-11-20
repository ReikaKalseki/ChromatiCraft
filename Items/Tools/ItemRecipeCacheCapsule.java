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

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class ItemRecipeCacheCapsule extends ItemChromaTool {

	public ItemRecipeCacheCapsule(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		CachedRecipe pos = this.getRecipe(is);
		if (pos != null) {

		}
	}

	private static CachedRecipe getRecipe(ItemStack is) {
		return is.stackTagCompound != null ? loadRecipe(is.stackTagCompound.getCompoundTag("recipe")) : null;
	}

	private static CachedRecipe loadRecipe(NBTTagCompound tag) {
		return null;
	}

	private static void setRecipe(TileEntityCastingTable te) {

	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		return this.getRecipe(item) == null ? base : base+1;
	}

	private static class CachedRecipe {

		//private final MultiBlockCastingRecipe recipe;
		private final Coordinate tableLoc;
		private final HashMap<Coordinate, ItemStack> standContents = new HashMap();

		private CachedRecipe(/*MultiBlockCastingRecipe rec, */TileEntityCastingTable table) {
			tableLoc = new Coordinate(table);
			//recipe = rec;
			for (TileEntityItemStand te : table.getOtherStands().values()) {
				ItemStack is = te.getStackInSlot(0);
				standContents.put(new Coordinate(te), is != null ? is.copy() : null);
				te.setInventorySlotContents(0, null);
			}
		}

	}

}
