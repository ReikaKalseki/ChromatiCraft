package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface ReactiveRepeater extends CrystalRepeater {

	public void onTransfer(CrystalSource src, CrystalReceiver r, CrystalElement element, int amt);

}
