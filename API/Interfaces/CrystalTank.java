package Reika.ChromatiCraft.API.Interfaces;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;


public interface CrystalTank extends IFluidHandler {

	/** The controller TileEntity. Will return self for the controller itself, so just call this every time you want to "do anything". */
	public CrystalTank getController();

	public boolean isController();

	/** Only valid on controllers. May be null even then, for empty tanks. */
	public Fluid getCurrentFluid();

	/** Only valid on controllers. */
	public int getCurrentFluidLevel();

	/** Only valid on controllers. Total capacity in mB - this is <i>distinct</i> from the IFluidHandler capacity, and is the <i>actual</i> capacity. */
	public int getCapacity();

	/** Only valid on controllers. A shorthand for IFluidHandler code. Returns how much was added. */
	public int addFluid(Fluid f, int amt);

}
