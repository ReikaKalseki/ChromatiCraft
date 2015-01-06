/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Magic.ElementTagCompound;

public interface AuraSource {

	public ElementTagCompound getAuras();

	public double getDistancePower();

	public double getCoefficient();

}
