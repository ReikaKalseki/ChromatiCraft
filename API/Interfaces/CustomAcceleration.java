/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

/** Implement this to override accelerator behavior on your TileEntity. <b>Do not use this for blacklisting.</b> Use the dedicated class for that. */
public interface CustomAcceleration {

	/** Return true to prevent further ticks (eg return true on the 30th tick to cap acceleration at 30x, or on the first but
	 * perform a 128x bigger operation for a factor of 128 without actually ticking 128 times). Args: Tick, Acceleration Factor */
	public void tick(int factor);

}
