/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WarpPointData {

	public static final String TAG = "CHROMAWARPPOINT";

	public WarpPointData() {

	}

	public static void readFromNBT(EntityPlayer ep) {
		AbilityHelper.instance.teleports.clear();
		NBTTagCompound NBT = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		NBTTagCompound nbt = NBT.getCompoundTag(TAG);
		NBTTagList points = nbt.getTagList("points", NBTTypes.COMPOUND.ID);
		HashMap<String, WarpPoint> map = new HashMap();
		for (Object o2 : points.tagList) {
			NBTTagCompound pt = (NBTTagCompound)o2;
			String label = pt.getString("label");
			WorldLocation pos = WorldLocation.readFromNBT("pos", pt);
			if (pos != null) {
				map.put(label, new WarpPoint(label, pos));
			}
		}
		AbilityHelper.instance.teleports.put(ep, map);
		//ReikaJavaLibrary.pConsole(ep.getCommandSenderName()+": "+map);
	}

	public static void writeToNBT(EntityPlayer ep) {
		NBTTagCompound data = new NBTTagCompound();
		HashMap<String, WarpPoint> map = AbilityHelper.instance.teleports.get(ep);
		NBTTagList points = new NBTTagList();
		for (String label : map.keySet()) {
			WarpPoint wp = map.get(label);
			NBTTagCompound pt = new NBTTagCompound();
			pt.setString("label", label);
			wp.location.writeToNBT("pos", pt);
			points.appendTag(pt);
		}
		data.setTag("points", points);
		//ReikaJavaLibrary.pConsole(ep.getCommandSenderName()+": "+data);
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setTag(TAG, data);
	}

	@SideOnly(Side.CLIENT)
	public static Collection<WarpPoint> loadMiniMaps() {
		Collection<WarpPoint> c = new HashSet();
		c.addAll(readVoxelMap());
		c.addAll(readJourneyMap());
		c.addAll(readMapWriter());
		return c;
	}

	@SideOnly(Side.CLIENT)
	private static Collection<WarpPoint> readMapWriter() {
		HashSet<WarpPoint> map = new HashSet();
		File f = new File(DragonAPICore.getMinecraftDirectory(), "saves/mapwriter_mp_worlds"); //just read all files for now
		if (f.exists() && f.isDirectory()) {
			for (File in : ReikaFileReader.getAllFilesInFolder(f, ".cfg")) {
				readMapWriterFile(in, map);
			}
		}
		return map;
	}

	@SideOnly(Side.CLIENT)
	private static Collection<WarpPoint> readJourneyMap() {
		HashSet<WarpPoint> map = new HashSet();
		File f = new File(DragonAPICore.getMinecraftDirectory(), "journeymap/data/mp"); //just read all files for now
		if (f.exists() && f.isDirectory()) {
			for (File in : ReikaFileReader.getAllFilesInFolder(f, ".json")) {
				readJourneyMapFile(in, map);
			}
		}
		return map;
	}

	@SideOnly(Side.CLIENT)
	private static Collection<WarpPoint> readMapwriter() {
		HashSet<WarpPoint> map = new HashSet();
		return map;
	}

	@SideOnly(Side.CLIENT)
	private static Collection<WarpPoint> readVoxelMap() {
		HashSet<WarpPoint> map = new HashSet();
		File f = new File(DragonAPICore.getMinecraftDirectory(), "mods/VoxelMods/voxelMap"); //just read all files for now
		if (f.exists() && f.isDirectory()) {
			for (File in : ReikaFileReader.getAllFilesInFolder(f, ".points")) {
				readVoxelMapFile(in, map);
			}
		}
		return map;
	}

	@SideOnly(Side.CLIENT)
	private static void readMapWriterFile(File f, HashSet<WarpPoint> map) {
		for (String s : ReikaFileReader.getFileAsLines(f, true)) {
			if (s.startsWith("S:marker")) {
				s = s.substring(s.indexOf('=')+1);
				String[] parts = s.split(":");
				String label = parts[0];
				String x = parts[1];
				String y = parts[2];
				String z = parts[3];
				String dim = parts[4];
				WarpPoint p = new WarpPoint(label, new WorldLocation(Integer.parseInt(dim), Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
				map.add(p);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void readJourneyMapFile(File f, HashSet<WarpPoint> map) {
		JsonElement e = new JsonParser().parse(ReikaFileReader.getReader(f));
		if (e instanceof JsonObject) {
			JsonObject j = (JsonObject)e;
			JsonArray dims = j.getAsJsonArray("dimensions");
			int x = j.getAsJsonPrimitive("x").getAsInt();
			int y = j.getAsJsonPrimitive("y").getAsInt();
			int z = j.getAsJsonPrimitive("z").getAsInt();
			String id = j.getAsJsonPrimitive("name").getAsString();
			WarpPoint p = new WarpPoint(id, new WorldLocation(dims.get(0).getAsInt(), x, y, z));
			map.add(p);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void readVoxelMapFile(File f, HashSet<WarpPoint> map) {
		for (String s : ReikaFileReader.getFileAsLines(f, true)) {
			if (s.startsWith("name")) {
				String[] parts = s.split(",");
				HashMap<String, String> dat = new HashMap();
				for (int i = 0; i < parts.length; i++) {
					String[] data = parts[i].split(":");
					if (data.length == 2) {
						String name = data[0];
						String val = data[1];
						if (name.equals("dimensions"))
							val = val.substring(0, val.length()-1); //remove trailing '#'
						dat.put(name, val);
					}
				}
				String label = dat.get("name");
				label = label.replaceAll("~comma~", ",");
				label = label.replaceAll("~colon~", ":");
				WarpPoint p = new WarpPoint(label, new WorldLocation(Integer.parseInt(dat.get("dimensions")), Integer.parseInt(dat.get("x")), Integer.parseInt(dat.get("y")), Integer.parseInt(dat.get("z"))));
				map.add(p);
			}
		}
	}

}
