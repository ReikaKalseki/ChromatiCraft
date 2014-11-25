package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;

public interface LumenTile {

	public int getEnergy(CrystalElement e);

	public ElementTagCompound getEnergy();

	public int getMaxStorage(CrystalElement e);

}
