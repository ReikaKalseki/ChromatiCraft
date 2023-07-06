package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;


public interface LumenConsumer extends LumenTile, NBTTile, AdjacentUpdateWatcher {

	public int getEfficiencyBoost();

	public boolean allowsEfficiencyBoost();
}
