/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;

import java.util.Collection;

public interface ElementEffector {

	public Collection<CrystalElement> getCurrentElements();

	public int getElement(CrystalElement e);

}
