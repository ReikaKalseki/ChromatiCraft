/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface EnergyBeamReceiver {

	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e);

	public double getIncomingBeamRadius();

}
