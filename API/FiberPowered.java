/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface FiberPowered {

	/** Returns the amount successfully added. Args: Color, maxAdd */
	public int addEnergy(CrystalElement e, int amt);

	/** Self-explanatory. */
	public boolean isAcceptingColor(CrystalElement e);

}
