package Reika.ChromatiCraft.Magic.Interfaces;

import java.util.Collection;

import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public interface EnergyBeamRenderer {

	public Collection<CrystalTarget> getTargets();

	public void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz);

	public void removeTarget(WorldLocation loc, CrystalElement e);

	public void clearTargets();

}
