package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityWirelessSource extends CrystalReceiverBase implements WirelessSource {

	public static final int TRANSMIT_RANGE = 18;

	public static final double LOSS_PER_LUMEN = 0.2;
	//public static final int LOSS_PER_OCCLUSION = 120;

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 1000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 120000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WIRELESS;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canTransmitTo(TileEntityWirelessPowered te) {
		return false;
	}

	@Override
	public boolean request(CrystalElement e, int amt) {
		return false;
	}

}
