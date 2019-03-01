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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.enchantment.Enchantment;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class EnchantmentBlacklist {

	private static Class tile;
	private static Method add;

	static {
		try {
			tile = Class.forName("Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter");
			add = tile.getMethod("blacklistEnchantment", Enchantment.class);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("Could not load ChromatiCraft class!");
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			ReikaJavaLibrary.pConsole("Could not read ChromatiCraft class!");
			e.printStackTrace();
		}
		catch (SecurityException e) {
			ReikaJavaLibrary.pConsole("Could not read ChromatiCraft class!");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("Could not read ChromatiCraft class!");
			e.printStackTrace();
		}
	}

	/** Use this to blacklist an enchantment from the Chromatic Enchanter. */
	public static void addBlacklist(Enchantment en) {
		try {
			add.invoke(null, en);
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("Error adding Accelerator Blacklist:");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("Error adding Accelerator Blacklist:");
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			ReikaJavaLibrary.pConsole("Error adding Accelerator Blacklist:");
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ReikaJavaLibrary.pConsole("Error adding Accelerator Blacklist: Class not loaded properly!");
			e.printStackTrace();
		}
	}

}
