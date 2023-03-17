/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

public interface RegionalSensitiveRepeater extends CrystalRepeater {

	public void onRegionUpdated();

	public int getSensitivityRadius();

}
