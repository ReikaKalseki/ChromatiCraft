package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

public interface CrystalTransmitter extends CrystalNetworkTile {

	public int getSendRange();

	public void markTarget(WorldLocation loc, CrystalElement e);

	public void clearTarget();
}
