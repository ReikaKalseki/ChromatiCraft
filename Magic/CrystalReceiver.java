package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalReceiver {

	public void receiveElement(CrystalElement e, int amt);

	public void onPathBroken();

}
