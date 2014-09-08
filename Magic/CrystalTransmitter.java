/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public interface CrystalTransmitter extends CrystalNetworkTile {

	public int getSendRange();

	public void addTarget(WorldLocation loc, CrystalElement e);

	public void removeTarget(WorldLocation loc, CrystalElement e);

	public void clearTargets();
}
