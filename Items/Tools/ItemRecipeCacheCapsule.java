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
import java.util.Map.Entry;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public class ItemRecipeCacheCapsule extends ItemChromaTool {

	public ItemRecipeCacheCapsule(int index) {
		super(index);

		maxStackSize = 4;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return false;
		if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.TABLE) {
			TileEntityCastingTable te = (TileEntityCastingTable)world.getTileEntity(x, y, z);
			if (te.isOwnedByPlayer(player)) {
				CachedRecipe cr = this.getRecipe(stack);
				if (cr != null) {
					if (cr.applyToTable(te)) {
						this.setRecipe(stack, null);
						ChromaSounds.CAST.playSoundAtBlock(te, 0.5F, 2F);
						if (!player.capabilities.isCreativeMode)
							stack.stackSize--;
					}
					else {
						ChromaSounds.ERROR.playSoundAtBlock(te);
					}
				}
				else {
					this.setRecipe(stack, new CachedRecipe(findClosestRecipe(te), te));
					ChromaSounds.USE.playSoundAtBlock(te);
				}
			}
		}
		return true;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		CachedRecipe pos = this.getRecipe(is);
		if (pos != null) {
			li.add("Storing recipe: "+(pos.recipe != null ? pos.recipe.getDisplayName() : "Unknown")+" at table "+pos.tableLoc);
		}
		else {
			li.add("Contains no recipe.");
		}
	}

	private static CachedRecipe getRecipe(ItemStack is) {
		return is.stackTagCompound != null && is.stackTagCompound.hasKey("recipe") ? CachedRecipe.loadRecipe(is.stackTagCompound.getCompoundTag("recipe")) : null;
	}

	private static void setRecipe(ItemStack is, CachedRecipe cr) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		if (cr != null) {
			NBTTagCompound tag = new NBTTagCompound();
			cr.writeToNBT(tag);
			is.stackTagCompound.setTag("recipe", tag);
		}
		else {
			is.stackTagCompound.removeTag("recipe");
		}
	}

	private static MultiBlockCastingRecipe findClosestRecipe(TileEntityCastingTable te) {
		MultiBlockCastingRecipe ret = null;
		int diffs = Integer.MAX_VALUE;
		for (CastingRecipe r : RecipesCastingTable.instance.getAllRecipes()) {
			if (r instanceof MultiBlockCastingRecipe) {
				int diff = getRecipeDifference(te, (MultiBlockCastingRecipe)r);
				if (ret == null || diff < diffs) {
					ret = (MultiBlockCastingRecipe)r;
					diffs = diff;
				}
			}
		}
		return ret;
	}

	private static int getRecipeDifference(TileEntityCastingTable te, MultiBlockCastingRecipe r) {
		int ret = 0;
		for (Entry<List<Integer>, TileEntityItemStand> e : te.getOtherStands().entrySet()) {
			ItemMatch at = r.getAuxItem(e.getKey());
			ItemStack has = e.getValue().getStackInSlot(0);
			if (at != null ? !at.match(has) : has != null)
				ret++;
		}
		return ret;
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		return this.getRecipe(item) == null ? base : base+1;
	}

	private static class CachedRecipe {

		private final MultiBlockCastingRecipe recipe;
		private final Coordinate tableLoc;
		private final HashMap<Coordinate, ItemStack> standContents = new HashMap();

		private CachedRecipe(String id, Coordinate c) {
			recipe = Strings.isNullOrEmpty(id) ? null : (MultiBlockCastingRecipe)RecipesCastingTable.instance.getRecipeByStringID(id);
			tableLoc = c;
		}

		private CachedRecipe(MultiBlockCastingRecipe rec, TileEntityCastingTable table) {
			tableLoc = new Coordinate(table);
			recipe = rec;
			for (TileEntityItemStand te : table.getOtherStands().values()) {
				ItemStack is = te.getStackInSlot(0);
				standContents.put(new Coordinate(te), is != null ? is.copy() : null);
				te.setInventorySlotContents(0, null);
				te.syncAllData(true);
			}
			table.syncAllData(true);
		}

		public boolean applyToTable(TileEntityCastingTable te) {
			if (!tableLoc.equals(te)) {
				return false;
			}
			for (Coordinate c : standContents.keySet()) {
				if (ChromaTiles.getTile(te.worldObj, c.xCoord, c.yCoord, c.zCoord) != ChromaTiles.STAND) {
					return false;
				}
			}
			te.dumpAllStands();
			for (Entry<Coordinate, ItemStack> e : standContents.entrySet()) {
				TileEntityItemStand te2 = (TileEntityItemStand)e.getKey().getTileEntity(te.worldObj);
				te2.setInventorySlotContents(0, e.getValue());
				te2.syncAllData(true);
			}
			te.syncAllData(true);
			return true;
		}

		private void writeToNBT(NBTTagCompound tag) {
			tag.setString("recipeID", recipe != null ? recipe.getIDString() : "");
			tableLoc.writeToNBT("location", tag);
			NBTTagList li = new NBTTagList();
			for (Entry<Coordinate, ItemStack> e : standContents.entrySet()) {
				NBTTagCompound at = new NBTTagCompound();
				e.getKey().writeToNBT("pos", at);
				if (e.getValue() != null)
					e.getValue().writeToNBT(at);
				li.appendTag(at);
			}
			tag.setTag("items", li);
		}

		private static CachedRecipe loadRecipe(NBTTagCompound tag) {
			String id = tag.getString("recipeID");
			Coordinate loc = Coordinate.readFromNBT("location", tag);
			NBTTagList li = tag.getTagList("items", NBTTypes.COMPOUND.ID);
			HashMap<Coordinate, ItemStack> map = new HashMap();
			for (Object o : li.tagList) {
				NBTTagCompound at = (NBTTagCompound)o;
				Coordinate c = Coordinate.readFromNBT("pos", at);
				ItemStack item = ItemStack.loadItemStackFromNBT(at);
				map.put(c, item);
			}
			CachedRecipe ret = new CachedRecipe(id, loc);
			ret.standContents.putAll(map);
			return ret;
		}

	}

}
