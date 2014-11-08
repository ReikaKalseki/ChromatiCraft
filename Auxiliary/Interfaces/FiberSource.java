package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberTransmitter;

public interface FiberSource extends FiberIO {

	public void removeTerminus(TileEntityFiberTransmitter te);
	public void addTerminus(TileEntityFiberTransmitter te);

	public void onTransmitTo(TileEntityFiberTransmitter te, CrystalElement e, int energy);

}
