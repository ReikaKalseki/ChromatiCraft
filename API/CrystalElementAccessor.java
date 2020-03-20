/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.Locale;

public class CrystalElementAccessor {

	private static Class elementClass;

	/** An interface for the internal CrystalElement enum, to which (or just {@link Enum}) you can cast it; consult that enum for thematic meanings. */
	public static interface CrystalElementProxy {

		public String displayName();
		public int getColor();
		public int ordinal();

	}

	static {
		try {
			elementClass = Class.forName("Reika.ChromatiCraft.Registry.CrystalElement");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static CrystalElementProxy getByEnum(String name) {
		return (CrystalElementProxy)Enum.valueOf(elementClass, name.toUpperCase(Locale.ENGLISH));
	}

	public static CrystalElementProxy getByIndex(int idx) {
		return (CrystalElementProxy)elementClass.getEnumConstants()[idx];
	}

}
