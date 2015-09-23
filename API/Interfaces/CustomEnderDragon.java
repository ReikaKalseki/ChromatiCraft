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


public interface CustomEnderDragon {

	/** Return true if the difficulty to find and kill this entity is at least as much as that of the vanilla dragon */
	public boolean isDifficultyComparable();

	/** Return true iff the player need not go to the end to kill this mob. */
	public boolean isAvailableOutsideEnd();

	/** Return the main colors of the dragon. You can return any array length, as long as there is at least one element.
	Vanilla values would be {0x1A1A1A, 0x858585, 0xCC00FA, 0xE079FA} for skin, "frame", and two eye/mouth colors. */
	public int[] getMainColors();

}
