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

import Reika.ChromatiCraft.Magic.Network.CrystalLink;

public interface LinkWatchingRepeater extends CrystalRepeater {

	public void onLinkRecalculated(CrystalLink l);

}
