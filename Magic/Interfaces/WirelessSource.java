package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Registry.CrystalElement;


public interface WirelessSource extends CrystalReceiver, LumenTile {

	boolean canTransmitTo(TileEntityWirelessPowered te);

	boolean request(CrystalElement e, int amt);

}
