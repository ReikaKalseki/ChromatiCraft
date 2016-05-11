/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;

public final class ElementTag {

	public final int value;
	public final CrystalElement element;

	public ElementTag(CrystalElement e, int value) {
		element = e;
		this.value = value;
	}

	@Override
	public String toString() {
		return value+"x"+element.getEnglishName();
	}

	public ElementTag copy() {
		return new ElementTag(element, value);
	}

}
