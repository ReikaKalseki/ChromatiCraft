/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.potion.Potion;


public class CrystalPotionInterface {

	private static Set<Integer> ignored;

	public static void addBadPotionForIgnore(Potion p) {
		ignored.add(p.id);
	}

	static {
		try {
			Class c = Class.forName("Reika.ChromatiCraft.Magic.CrystalPotionController");
			Field f = c.getDeclaredField("ignoredPotions");
			f.setAccessible(true);
			ignored = (Set<Integer>)f.get(null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
