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

/** Implement this on TileEntities that have a range of effect (beyond one block) and which can have their range increased by an appropriate booster
 * block. */
public interface RangeUpgradeable {

	/** Called every tick; implementation is left up to you, whether it be setting a flag or increasing the range each tick to the maximum, or
	 * some other entirely different approach. Args: Maximum increase factor. */
	public void upgradeRange(double r);

	public int getRange();

}
