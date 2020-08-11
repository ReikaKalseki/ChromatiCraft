package Reika.ChromatiCraft.Magic.Network;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class PathNode {

	public final WorldLocation location;
	public final Class<? extends CrystalNetworkTile> tileClass;

	public final int stepIndex;
	public final boolean isFinalTarget;

	private CrystalNetworkTile cachedTile;

	PathNode(WorldLocation loc, int i, boolean end) {
		location = loc;
		CrystalNetworkTile te = PylonFinder.getNetTileAt(location, false);
		if (te == null) {
			throw new IllegalStateException("CC Crystal Network: Created a path node from null?!");
		}
		tileClass = te != null ? te.getClass() : null;
		stepIndex = i;
		isFinalTarget = end;
	}

	PathNode(CrystalNetworkTile te) {
		this(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()), 0, false);
	}

	void cacheTile() {
		cachedTile = this.getTile(true);
	}

	void flush() {
		cachedTile = null;
	}

	public boolean isSource() {
		return CrystalSource.class.isAssignableFrom(tileClass);
	}

	public boolean isRepeater() {
		return CrystalRepeater.class.isAssignableFrom(tileClass);
	}

	CrystalNetworkTile getTile(boolean exception) {
		if (cachedTile != null)
			return cachedTile;
		if (this.isSource())
			return PylonFinder.getSourceAt(location, exception);
		else if (CrystalTransmitter.class.isAssignableFrom(tileClass))
			return PylonFinder.getTransmitterAt(location, exception);
		else if (CrystalReceiver.class.isAssignableFrom(tileClass))
			return PylonFinder.getReceiverAt(location, exception);
		else
			return PylonFinder.getNetTileAt(location, exception);
	}

	@Override
	public String toString() {
		return "#"+stepIndex+": "+tileClass+" @ "+location;
	}

	@Override
	public int hashCode() {
		return location.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PathNode && location.equals(((PathNode)o).location);
	}

}
