package Reika.ChromatiCraft.API;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface FiberPowered {

	/** Returns the amount successfully added. Args: Color, maxAdd */
	public int addEnergy(CrystalElement e, int amt);

	/** Self-explanatory. */
	public boolean isAcceptingColor(CrystalElement e);

}
