/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaPowered;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverInventoryBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;

public class TileEntityAutoEnchanter extends FluidReceiverInventoryBase implements ChromaPowered {

	private HashMap<Enchantment, Integer> selected = new HashMap();

	public static final int CHROMA_PER_LEVEL = 500;

	private StepTimer progress = new StepTimer(40);
	public int progressTimer;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ENCHANTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

		if (this.canProgress()) {
			progress.update();
			if (progress.checkCap()) {
				if (!world.isRemote)
					this.applyEnchants();
			}
		}
		else {
			progress.reset();
		}
		progressTimer = progress.getTick();
	}

	public int getProgressScaled(int a) {
		return a * progressTimer / progress.getCap();
	}

	private boolean canProgress() {
		return inv[0] != null && this.isValid(inv[0]) && this.getChroma() >= this.getConsumedChroma() && this.enchanting();
	}

	private boolean enchanting() {
		if (selected.isEmpty())
			return false;
		for (Enchantment e : selected.keySet()) {
			int level = selected.get(e);
			if (level > 0)
				return true;
		}
		return false;
	}

	public int getChroma() {
		return tank.getLevel();
	}

	public boolean addChroma(int amt) {
		if (tank.canTakeIn(amt)) {
			tank.addLiquid(amt, FluidRegistry.getFluid("chroma"));
			return true;
		}
		return false;
	}

	private boolean isValid(ItemStack is) {
		return (is.getItem().getItemEnchantability() > 0 || is.getItem() == Items.book) && !ReikaEnchantmentHelper.hasEnchantments(is);
	}

	private void applyEnchants() {
		if (inv[0].getItem() == Items.book)
			inv[0] = new ItemStack(Items.enchanted_book);
		ReikaEnchantmentHelper.applyEnchantments(inv[0], selected);
		tank.removeLiquid(this.getConsumedChroma());
	}

	private int getConsumedChroma() {
		int total = 0;
		Collection<Integer> levels = selected.values();
		Iterator<Integer> it = levels.iterator();
		while (it.hasNext()) {
			int level = it.next();
			total += level;
		}
		return total*CHROMA_PER_LEVEL;
	}

	public boolean setEnchantment(Enchantment e, int level) {
		level = Math.min(this.getMaxEnchantmentLevel(e), level);
		if (level <= 0) {
			this.removeEnchantment(e);
			return true;
		}
		else {
			if (this.getEnchantment(e) == 0) {
				if (!ReikaEnchantmentHelper.isCompatible(selected.keySet(), e)) {
					return false;
				}
			}
			selected.put(e, level);
			return true;
		}
	}

	public int getMaxEnchantmentLevel(Enchantment e) {
		if (e == Enchantment.fortune)
			return 5;
		if (e == Enchantment.looting)
			return 5;
		if (e == Enchantment.respiration)
			return 5;
		return e.getMaxLevel();
	}

	public void removeEnchantment(Enchantment e) {
		selected.remove(e);
	}

	public boolean incrementEnchantment(Enchantment e) {
		int level = this.getEnchantment(e);
		return this.setEnchantment(e, level+1);
	}

	public void decrementEnchantment(Enchantment e) {
		int level = this.getEnchantment(e);
		int newlevel = Math.max(level-1, 0);
		this.setEnchantment(e, newlevel);
	}

	public void clearEnchantments() {
		selected.clear();
	}

	public int getEnchantment(Enchantment e) {
		return selected.containsKey(e) ? selected.get(e) : 0;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return Collections.unmodifiableMap(selected);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return this.isValid(itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return ReikaEnchantmentHelper.hasEnchantments(itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				int lvl = this.getEnchantment(Enchantment.enchantmentsList[i]);
				NBT.setInteger(Enchantment.enchantmentsList[i].getName(), lvl);
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		selected = new HashMap();
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				int lvl = NBT.getInteger(Enchantment.enchantmentsList[i].getName());
				if (lvl > 0)
					selected.put(Enchantment.enchantmentsList[i], lvl);
			}
		}
	}

	@Override
	public int getCapacity() {
		return 6000;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("chroma");
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return true;
	}

}
