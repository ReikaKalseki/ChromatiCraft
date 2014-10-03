/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityItemFabricator extends InventoriedCrystalReceiver {

	private static class Recipe {

		private final ItemStack output;
		private final ElementTagCompound energy;

		private Recipe(ElementTagCompound tag, ItemStack is) {
			energy = tag;
			output = is;
		}
	}

	private Recipe recipe = null;
	private int craftingTick;

	public void setRecipe(ItemStack out) {
		ElementTagCompound tag = FabricationRecipes.recipes().getItemCost(out);
		if (tag != null) {
			recipe = new Recipe(tag, out);
		}
	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (recipe != null) {

		}

		if (craftingTick > 0) {
			this.onCraftingTick(world, x, y, z);
		}
	}

	private void onCraftingTick(World world, int x, int y, int z) {

		craftingTick--;
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && ReikaItemHelper.matchStacks(is, ChromaStacks.chromaDust);
	}

	@Override
	public int getMaxStorage() {
		return 8000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FABRICATOR;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		craftingTick = NBT.getInteger("craft");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("craft", craftingTick);
	}

}
