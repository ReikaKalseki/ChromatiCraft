package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Magic.Network.CrystalPath;


public interface ConnectivityAction extends CrystalRepeater {

	public void notifySendingTo(CrystalPath p, CrystalReceiver r);
	public void notifyReceivingFrom(CrystalPath p, CrystalTransmitter t);

}
