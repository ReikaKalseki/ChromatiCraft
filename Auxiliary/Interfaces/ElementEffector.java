/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.Collection;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface ElementEffector {

	public Collection<CrystalElement> getCurrentElements();

	public int getElement(CrystalElement e);

}
