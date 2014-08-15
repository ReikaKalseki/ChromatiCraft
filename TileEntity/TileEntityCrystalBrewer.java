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

import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalPotionController;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.ChromatiCraft.Registry.ChromaItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;

public class TileEntityCrystalBrewer extends InventoriedChromaticBase {

	private int time = 400;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.canBrew()) {
			time--;
			if (time <= 0) {
				this.brew();
				time = 400;
			}
		}
		else {
			time = 400;
		}
	}

	private boolean canBrew() {
		if (inv[0] == null)
			return false;
		if (inv[0].getItem() != ChromaItems.SHARD.getItemInstance())
			return false;
		if (!this.isSlotModifiable(1) && !this.isSlotModifiable(2) && !this.isSlotModifiable(3))
			return false;

		return true;
	}

	private boolean isSlotModifiable(int i) {
		ItemStack is = inv[i];
		ItemStack z = inv[0];
		int zd = z.getItemDamage();
		if (is == null)
			return false;
		if (is.getItem() == Items.potionitem) {
			if (zd == ReikaDyeHelper.BLACK.ordinal() || zd == ReikaDyeHelper.BROWN.ordinal() || zd == ReikaDyeHelper.PURPLE.ordinal())
				return ReikaPotionHelper.isActualPotion(is.getItemDamage());
			else
				return true;
		}
		else if (is.getItem() == ChromaItems.POTION.getItemInstance()) {
			return zd == ReikaDyeHelper.BLACK.ordinal() || zd == ReikaDyeHelper.BROWN.ordinal() || zd == ReikaDyeHelper.PURPLE.ordinal();
		}
		return false;
	}

	private void brew() {
		ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(inv[0].getItemDamage());
		boolean custom = CrystalPotionController.requiresCustomPotion(color);
		inv[0] = null;

		for (int i = 1; i < 4; i++) {
			if (inv[i] != null) {
				inv[i] = this.getPotionStackFromColor(color);
			}
		}
	}

	public static ItemStack getPotionStackFromColor(ReikaDyeHelper color) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
		String eff = shard.getItem().getPotionEffect(shard);
		boolean custom = CrystalPotionController.requiresCustomPotion(color);
		ItemStack is = new ItemStack(Items.potionitem, 1, ReikaPotionHelper.AWKWARD_META);
		if (custom) {
			is = new ItemStack(ChromaItems.POTION.getItemInstance(), 1, color.ordinal());
		}
		else {
			if (CrystalPotionController.isCorruptedPotion(color)) {
				int newmeta = PotionHelper.applyIngredient(is.getItemDamage(), eff);
				is = new ItemStack(is.getItem(), 1, newmeta);
				int cmeta = PotionHelper.applyIngredient(is.getItemDamage(), PotionHelper.fermentedSpiderEyeEffect);
				is = new ItemStack(is.getItem(), 1, cmeta);
			}
			else {
				int newmeta = PotionHelper.applyIngredient(is.getItemDamage(), eff);
				is = new ItemStack(is.getItem(), 1, newmeta);
			}
		}
		return is;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {
		return ReikaMathLibrary.py3d(ep.posX-xCoord-0.5, ep.posY-yCoord-0.5, ep.posZ-zCoord-0.5) <= 8;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (is.getItem() == ChromaItems.POTION.getItemInstance())
			return i != 0;
		if (is.getItem() == ChromaItems.SHARD.getItemInstance())
			return i == 0;
		if (is.getItem() == Items.potionitem)
			return i != 0;
		if (is.getItem().getPotionEffect(is) != null)
			return i == 0;
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i != 0;
	}

	public int getBrewTime() {
		return time;
	}

	public void setBrewTime(int par2) {
		time = par2;
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		time = NBT.getInteger("BrewTime");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);
		NBT.setInteger("BrewTime", time);
	}

	@Override
	public int getRedstoneOverride() {
		return this.canBrew() ? 0 : 15;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BREWER;
	}

}