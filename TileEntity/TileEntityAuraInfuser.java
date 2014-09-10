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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverInventoryBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;

//Infuses shards with activated chroma
public class TileEntityAuraInfuser extends FluidReceiverInventoryBase {

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
		return ChromaTiles.INFUSER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getCapacity() {
		return 8000;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("active chroma");
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return true;
	}

}
