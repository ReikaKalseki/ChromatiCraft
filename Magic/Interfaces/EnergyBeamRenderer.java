/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import java.util.Collection;

import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public interface EnergyBeamRenderer {

	public Collection<CrystalTarget> getTargets();

	public void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w);

	public void addSelfTickingTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w, int ticks);

	public void removeTarget(WorldLocation loc, CrystalElement e);

	public void clearTargets(boolean unload);

	public double getOutgoingBeamRadius();

}
