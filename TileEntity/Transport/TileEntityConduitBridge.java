package Reika.ChromatiCraft.TileEntity.Transport;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import cofh.api.energy.IEnergyHandler;


public class TileEntityConduitBridge extends TileEntityChromaticBase implements IFluidHandler, IEnergyHandler {

	private static final int FLUID_CAPACITY = 2000;
	private static final int RF_CAPACITY = 60000;

	private int rfLevel;
	private final HybridTank tank = new HybridTank("bridge", FLUID_CAPACITY);

	private Coordinate otherBridge;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BRIDGE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void link(TileEntityConduitBridge te) {

	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return this.connective(from);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return this.insert(Interaction.RF, from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return (int)this.remove(Interaction.RF, from, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return (int)this.amount(Interaction.RF, from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return this.capacity(Interaction.RF, from);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.insert(Interaction.FLUID, from, resource, !doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return (FluidStack)this.remove(Interaction.FLUID, from, resource, !doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return (FluidStack)this.remove(Interaction.FLUID, from, maxDrain, !doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.connective(from);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return this.connective(from);
	}

	public boolean connective(ForgeDirection from) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{new FluidTankInfo((FluidStack)this.amount(Interaction.FLUID, from), this.capacity(Interaction.FLUID, from))};
	}

	private int insert(Interaction a, ForgeDirection dir, Object amt, boolean simulate) {
		switch(a) {
			case FLUID:
				FluidStack fs = (FluidStack)amt;
				return tank.isEmpty() || fs.getFluid() == tank.getActualFluid() ? tank.fill(fs, !simulate) : 0;
			case RF:
				int val = (int)amt;
				int add = Math.min(RF_CAPACITY-rfLevel, val);
				if (!simulate)
					rfLevel += add;
				return add;
		}
		return 0;
	}

	private Object remove(Interaction a, ForgeDirection dir, Object amt, boolean simulate) {
		switch(a) {
			case FLUID:
				if (amt instanceof FluidStack) {
					FluidStack fs = (FluidStack)amt;
					return fs.getFluid() == tank.getActualFluid() ? tank.drain(fs.amount, !simulate) : null;
				}
				else if (amt instanceof Integer) {
					Fluid f = tank.getActualFluid();
					if (f == null)
						return null;
					int val = (int)amt;
					int rem = Math.min(val, tank.getLevel());
					if (!simulate)
						tank.removeLiquid(rem);
					return new FluidStack(f, rem);
				}
				else {
					return null;
				}
			case RF:
				int val = (int)amt;
				int rem = Math.min(rfLevel, val);
				if (!simulate)
					rfLevel -= rem;
				return rem;
		}
		return null;
	}

	private Object amount(Interaction a, ForgeDirection dir) {
		switch(a) {
			case FLUID:
				return tank.getFluid();
			case RF:
				return rfLevel;
		}
		return null;
	}

	private int capacity(Interaction a, ForgeDirection dir) {
		switch(a) {
			case FLUID:
				return FLUID_CAPACITY;
			case RF:
				return RF_CAPACITY;
		}
		return 0;
	}

	public static enum Interaction {
		FLUID(),
		RF();
	}

}
