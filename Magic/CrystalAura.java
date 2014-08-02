/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Aura.AuraSource;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalAura implements AuraSource {

	private final CrystalElement element;
	private final int size;

	//Create in onBlockAdded() if (!world.isRemote) for CrystalBlock
	public CrystalAura(CrystalElement e, int size) {
		this.size = size;
		element = e;
	}

	public CrystalAura(int meta, int size) {
		this(CrystalElement.elements[meta], size);
	}

	@Override
	public ElementTagCompound getAuras() {
		ElementTagCompound e = new ElementTagCompound();
		e.addTag(element, size);
		return e;
	}

	@Override
	public double getDistancePower() {
		return 2;
	}

	@Override
	public double getCoefficient() {
		return 1;
	}

}
