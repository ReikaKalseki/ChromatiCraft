/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.Collection;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public interface FocusAcceleratable {

	public float getAccelerationFactor();

	public float getMaximumAcceleratability();

	public float getProgressToNextStep();

	public void recountFocusCrystals();

	public Collection<Coordinate> getRelativeFocusCrystalLocations();

}
