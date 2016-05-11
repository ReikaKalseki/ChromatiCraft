/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Registry.CrystalElement;


public interface WirelessSource extends CrystalReceiver, LumenTile {

	boolean canTransmitTo(TileEntityWirelessPowered te);

	boolean request(CrystalElement e, int amt);

}
