package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalBattery extends CrystalReceiver, CrystalSource {

	public int getMaxStorage(CrystalElement e);
	public float getFillFraction(CrystalElement e);

}
