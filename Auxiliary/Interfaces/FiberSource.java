/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberTransmitter;

public interface FiberSource extends FiberIO {

	public void removeTerminus(TileEntityFiberTransmitter te);
	public void addTerminus(TileEntityFiberTransmitter te);

	public void onTransmitTo(TileEntityFiberTransmitter te, CrystalElement e, int energy);

}
