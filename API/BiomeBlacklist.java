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
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


/** Use this to blacklist biomes from the ChromatiCraft Biome changer; to do this, specify "biome connections"; that is, a piece of code saying that
 * "biome X must only come from this set of biomes" or "biome Y must not come from this set of biomes". A few prefabs are provided for convenience,
 * including basic blacklisting/whitelisting features. */
public class BiomeBlacklist {

	private static Class tile;
	private static Method add;

	static {
		try {
			tile = Class.forName("Reika.ChromatiCraft.TileEntity.TileEntityBiomeChanger");
			add = tile.getMethod("addBiomeConnection", BiomeConnection.class);
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

	public static void addBlacklist(BiomeConnection bc) {
		try {
			add.invoke(null, bc);
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("Error adding Biome Changer Blacklist:");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("Error adding Biome Changer Blacklist:");
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			ReikaJavaLibrary.pConsole("Error adding Biome Changer Blacklist:");
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ReikaJavaLibrary.pConsole("Error adding Biome Changer Blacklist: Class not loaded properly!");
			e.printStackTrace();
		}
	}

	public static interface BiomeConnection {

		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out);

	}

	public static final class TotalDisallowConnection implements BiomeConnection {

		private final BiomeGenBase biome;

		public TotalDisallowConnection(BiomeGenBase b) {
			biome = b;
		}

		@Override
		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			return biome != in && biome != out;
		}
	}

	public static final class DisallowAsInputConnection implements BiomeConnection {

		private final BiomeGenBase biome;

		public DisallowAsInputConnection(BiomeGenBase b) {
			biome = b;
		}

		@Override
		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			return biome != in;
		}
	}

	public static final class DisallowAsOutputConnection implements BiomeConnection {

		private final BiomeGenBase biome;

		public DisallowAsOutputConnection(BiomeGenBase b) {
			biome = b;
		}

		@Override
		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			return biome != out;
		}
	}

	public static abstract class BlacklistBiomeConnection implements BiomeConnection {

		protected final HashMap<BiomeGenBase, Collection<BiomeGenBase>> blacklist = new HashMap();

		public BlacklistBiomeConnection() {

		}

		public BlacklistBiomeConnection(HashMap<BiomeGenBase, Collection<BiomeGenBase>> list) {
			for (BiomeGenBase b : list.keySet()) {
				blacklist.put(b, list.get(b));
			}
		}
	}

	public static final class BlacklistInputBiomeConnection extends BlacklistBiomeConnection {

		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			Collection<BiomeGenBase> list = blacklist.get(out);
			return list == null || !list.contains(in);
		}

	}

	public static final class BlacklistOutputBiomeConnection extends BlacklistBiomeConnection {

		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			Collection<BiomeGenBase> list = blacklist.get(in);
			return list == null || !list.contains(out);
		}

	}

}
