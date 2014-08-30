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

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.HybridTank;

public class TileEntityCrystalTank extends TileEntityChromaticBase implements IFluidHandler {

	private final HybridTank tank = new HybridTank("crystaltank", 1000000000);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int add = Math.min(this.getCapacity()-this.getLevel(), resource.amount);
		if (add > 0) {
			FluidStack fs = new FluidStack(resource.getFluid(), add);
			return tank.fill(fs, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TANK;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private int getCapacity() {
		return 0;
	}

	public int getLevel() {
		return tank.getLevel();
	}

}
