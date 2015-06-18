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

import java.lang.reflect.Method;

import net.minecraft.entity.EntityLiving;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** Use this to blacklist entity types from the spawner reprogrammer, to prevent progression skips, rendering glitches, crashes, or similar.
 * It is strongly discouraged to use this to blacklist mobs that can be safely made into spawners.
 * */

public class SpawnerBlacklist {

	private static Class tile;
	private static Method call;
	private static Method callClass;

	/** Call this to blacklist an entity with this registration name. */
	public static void blacklistEntityType(String name) {
		try {
			call.invoke(name);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not blacklist entity type '"+name+"' from the spawner reprogrammer!");
			e.printStackTrace();
		}
	}

	/** Call this to blacklist an entity with this entity class. */
	public static void blacklistEntityType(Class<? extends EntityLiving> c) {
		try {
			callClass.invoke(c);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not blacklist entity class '"+c+"' from the spawner reprogrammer!");
			e.printStackTrace();
		}
	}

	static {
		try {
			tile = Class.forName("Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer");
			call = tile.getDeclaredMethod("addDisallowedMob", String.class);
			callClass = tile.getDeclaredMethod("addDisallowedMob", Class.class);
			call.setAccessible(true);
			callClass.setAccessible(true);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not initialize spawner reprogrammer blacklist API!");
			e.printStackTrace();
		}
	}

}
