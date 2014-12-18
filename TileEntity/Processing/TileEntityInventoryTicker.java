/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedFiberPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityInventoryTicker extends InventoriedFiberPowered {

	public int ticks = 1;

	public static final int MAX_RATE = 8;

	private boolean hotbar = true;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.LIGHTBLUE, 25);
		required.addTag(CrystalElement.GREEN, 10);
	}

	public static ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == ForgeDirection.DOWN.ordinal();
	}

	@Override
	public int getSizeInventory() {
		return 36;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TICKER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		this.updateItems();
	}

	private void updateItems() {
		EntityPlayer ep = this.getPlacer();
		if (ep != null) {
			for (int i = 0; i < this.getSizeInventory(); i++) {
				ItemStack is = inv[i];
				if (is != null) {
					for (int k = 0; k < ticks && energy.containsAtLeast(required); k++) {
						is.getItem().onUpdate(is, worldObj, ep, hotbar ? 0 : 9, hotbar);
						this.drainEnergy(required);
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 4000;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		ticks = NBT.getInteger("ticks");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("ticks", ticks);
	}

}
