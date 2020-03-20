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

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** Use this to add custom recipe objects that ChromatiCraft can use for item element value calculation. */
public class PlayerBufferAPI {

	private static Class main;
	private static Object instance;
	private static Method drain;

	static {
		try {
			main = Class.forName("Reika.ChromatiCraft.Magic.PlayerElementBuffer");
			instance = main.getField("instance").get(null);
			drain = main.getMethod("removeFromPlayer", EntityPlayer.class, CrystalElementProxy.class, int.class);
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
		catch (NoSuchMethodException e) {
			ReikaJavaLibrary.pConsole("Could not read ChromatiCraft class!");
			e.printStackTrace();
		}
	}

	/** Subtracts the specified amount of (or, if the player has less, all of their) energy of a given color from a player's buffer. */
	public static void drainEnergy(EntityPlayer ep, CrystalElementProxy color, int amt) {
		try {
			drain.invoke(instance, ep, color, amt);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not drain "+amt+" of "+color+" from "+ep+" via API!");
			e.printStackTrace();
		}
	}

}
