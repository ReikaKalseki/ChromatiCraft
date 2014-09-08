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
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class TileEntityAuraProcessor extends InventoriedChromaticBase {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	private boolean canProcess() {
		return false;
	}

	private void process() {

	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i == 2;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return (i == 0 && ChromaItems.SHARD.matchWith(is)) || (i == 1 && ChromaItems.BERRY.matchWith(is));
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PROCESSOR;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
