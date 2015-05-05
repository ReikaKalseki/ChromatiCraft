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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class AcceleratorBlacklist {

	private static Class tile;
	private static Method add;

	static {
		try {
			tile = Class.forName("Reika.ChromatiCraft.TileEntity.AOE.TileEntityAccelerator");
			add = tile.getMethod("blacklistTile", Class.class);
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

	/** Use this to blacklist your TileEntity class from being accelerated with the TileEntity acclerator.
	 * You must specify a reason (from the {@link BlacklistReason} enum) which will be put into the loading log.
	 * Arguments: TileEntity class, Reason.
	 * Sample log message:<br>
	 * <i> CHROMATICRAFT:
	 * "TileEntity "Miner" has been blacklisted from the TileEntity Accelerator, because the creator finds it unbalanced or overpowered."
	 * </i>*/
	public static void addBlacklist(Class<? extends TileEntity> cl, String name, BlacklistReason r) {
		try {
			add.invoke(null, cl);
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
		ReikaJavaLibrary.pConsole("CHROMATICRAFT: TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
	}

	public static void addBlacklist(Class<? extends TileEntity> cl, BlacklistReason r) {
		addBlacklist(cl, cl.getSimpleName(), r);
	}

	public static enum BlacklistReason {
		BUGS("it will cause bugs or other errors."),
		CRASH("it would cause a crash."),
		BALANCE("the creator finds it unbalanced or overpowered."),
		EXPLOIT("it creates an exploit."),
		OPINION("the creator wishes it to be disabled.");

		public final String message;

		private BlacklistReason(String msg) {
			message = msg;
		}
	}

}
