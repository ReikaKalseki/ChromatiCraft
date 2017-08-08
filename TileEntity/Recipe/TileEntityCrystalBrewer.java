/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class TileEntityCrystalBrewer extends InventoriedChromaticBase implements OperationInterval {

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
			if (CrystalPotionController.isPotionModifier(CrystalElement.elements[zd%16]))
				return ReikaPotionHelper.isActualPotion(is.getItemDamage());
			else
				return !ReikaPotionHelper.isActualPotion(is.getItemDamage());
		}
		else if (is.getItem() == ChromaItems.POTION.getItemInstance()) {
			return CrystalPotionController.isPotionModifier(CrystalElement.elements[zd%16]);
		}
		return false;
	}

	private void brew() {
		CrystalElement color = CrystalElement.elements[inv[0].getItemDamage()%16];
		boolean boost = inv[0].getItemDamage() >= 16;
		inv[0] = null;

		for (int i = 1; i < 4; i++) {
			if (inv[i] != null) {
				inv[i] = this.getPotionStackFromColor(inv[i].getItemDamage(), color, boost);
			}
		}
	}

	public static ItemStack getPotionStackFromColor(int dmg, CrystalElement color, boolean boost) {
		ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
		String eff = shard.getItem().getPotionEffect(shard);
		boolean custom = CrystalPotionController.requiresCustomPotion(color);
		ItemStack is = new ItemStack(Items.potionitem, 1, ReikaPotionHelper.AWKWARD_META);

		if (CrystalPotionController.isPotionModifier(color)) {
			is.setItemDamage(dmg);
		}

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
		if (boost) {
			is.setItemDamage(is.getItemDamage() | ReikaPotionHelper.BOOST_BIT | ReikaPotionHelper.EXTENDED_BIT);
		}
		return is;
	}
	/*
	public static List<ItemStack> getPotionsForColor(CrystalElement color) {
		if (CrystalPotionController.isPotionModifier(color)) {
			Map<Potion, Integer> map = ReikaPotionHelper.getPotionValues();
			ArrayList<ItemStack> li = new ArrayList();
			for (Potion p : map.keySet()) {
				int meta = map.get(p);
				ItemStack is = getPotionStackFromColor(meta, color);
				li.add(is);
			}
			//ReikaJavaLibrary.pConsole(li);
			return li;
		}
		else {
			return ReikaJavaLibrary.makeListFrom(getPotionStackFromColor(ReikaPotionHelper.AWKWARD_META, color));
		}
	}*/

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

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

	@Override
	public float getOperationFraction() {
		return !this.canBrew() ? 0 : 1-time/400F;
	}

	@Override
	public OperationState getState() {
		return this.canBrew() ? OperationState.RUNNING : OperationState.INVALID;
	}

}
