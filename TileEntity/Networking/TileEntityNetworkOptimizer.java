package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.HashSet;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class TileEntityNetworkOptimizer extends CrystalReceiverBase {

	private final PathData[] data = new PathData[16];

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		for (int i = 0; i < 16; i++) {
			data[i] = new PathData();
		}
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 1;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 1;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.OPTIMIZER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static class PathData {

		private final HashSet<Coordinate> receivers = new HashSet();
		private final HashSet<Coordinate> sources = new HashSet();

	}

}
