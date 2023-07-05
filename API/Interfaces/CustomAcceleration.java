/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI;

/** Supply an instance of this to the {@link AdjacencyUpgradeAPI} to specify custom accelerator behavior on your TileEntity.
<b>Do not use this for blacklisting.</b> Use the dedicated handler for that. */
public interface CustomAcceleration extends CustomAdjacencyHandler {

	/** This will be called once per tick, with 'factor' being the acceleration factor of the core, eg 16 or 64. */
	public void tick(int factor);

	/** Is this handler being registered for any parent classes; basically does the accelerator need to recursively call super() to get
	 * the class object on which it needs to operate when starting with the "direct" child class */
	public boolean usesParentClasses();

}
