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
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** Use this to add custom recipe objects that ChromatiCraft can use for item element value calculation. */
public class ItemElementAPI {

	private static Class main;
	private static Field field;
	private static List list;

	static {
		try {
			main = Class.forName("Reika.ChromatiCraft.Registry.ItemElementCalculator");
			field = main.getDeclaredField("handlers");
			field.setAccessible(true);
			Object instance = main.getField("instance").get(null);
			list = (List)field.get(instance);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("Could not load ChromatiCraft class!");
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
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
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("Could not read ChromatiCraft class!");
			e.printStackTrace();
		}
	}

	/** Use this to register a custom recipe handler. */
	public static void addHandler(ItemInOutHandler handler) {
		try {
			list.add(handler);
		}
		catch (NullPointerException e) {
			ReikaJavaLibrary.pConsole("Error adding Item Element API handler: Class not loaded properly!");
			e.printStackTrace();
		}
		ReikaJavaLibrary.pConsole("CHROMATICRAFT: Added item element API handler "+handler+".");
	}

	public static interface ItemInOutHandler {

		/** All items that go into making the given item, through this recipe handler. Return null or empty if the handler cannot make this item. */
		public Collection<ItemStack> getInputItemsFor(ItemStack out);

		/** These represent the elements added by the process itself, eg smelting adds orange, for fire. Return null or empty for none. */
		public Collection<CrystalElementProxy> getBonusElements();

	}

}
