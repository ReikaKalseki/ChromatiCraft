/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.AngleMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;


public class WarpNetwork {

	private static final String NBT_TAG = "warpnodenet";

	private final HashMap<WorldLocation, AngleMap<WorldLocation>> data = new HashMap();

	public static final WarpNetwork instance = new WarpNetwork();

	private WarpNetwork() {

	}

	public void addLocation(WorldLocation loc) {
		AngleMap<WorldLocation> map = this.calculateLinks(loc);
		data.put(loc, map);
		WarpNetworkData.initNetworkData(loc.getWorld()).setDirty(true);
	}

	public WorldLocation getLink(WorldLocation loc, double angle, double tolerance) {
		AngleMap<WorldLocation> map = data.get(loc);
		if (map == null || map.isEmpty())
			return null;
		if (map.size() == 1) {
			Entry<Double, WorldLocation> e = map.firstEntry();
			return Math.abs(ReikaVectorHelper.getAngleDifference(e.getKey(), angle)) <= tolerance ? e.getValue() : null;
		}
		Entry<Double, WorldLocation> e1 = map.floorEntry(angle);
		Entry<Double, WorldLocation> e2 = map.ceilingEntry(angle);
		//ReikaJavaLibrary.pConsole(e1+", "+e2+", "+angle+" of "+map);
		if (e1 == null) {
			return Math.abs(ReikaVectorHelper.getAngleDifference(e2.getKey(), angle)) <= tolerance ? e2.getValue() : null;
		}
		else if (e2 == null) {
			//ReikaJavaLibrary.pConsole(e1+" @ "+ReikaVectorHelper.getAngleDifference(e1.getKey(), angle));
			return Math.abs(ReikaVectorHelper.getAngleDifference(e1.getKey(), angle)) <= tolerance ? e1.getValue() : null;
		}
		else {
			double d1 = Math.abs(ReikaVectorHelper.getAngleDifference(e1.getKey(), angle));
			double d2 = Math.abs(ReikaVectorHelper.getAngleDifference(e2.getKey(), angle));
			boolean left = d1 < d2;
			return (left ? d1 : d2) <= tolerance ? (left ? e1.getValue() : e2.getValue()) : null;
		}
	}

	private AngleMap<WorldLocation> calculateLinks(WorldLocation loc) {
		AngleMap<WorldLocation> ret = new AngleMap();
		for (WorldLocation key : data.keySet()) {
			if (key.dimensionID == loc.dimensionID && !loc.equals(key)) {
				AngleMap<WorldLocation> map = data.get(key);
				double phi = ReikaPhysicsHelper.cartesianToPolar(loc.xCoord-key.xCoord, 0, loc.zCoord-key.zCoord)[2];
				map.put(phi, loc);
				ret.put((phi+180)%360, key);
			}
		}
		//ReikaJavaLibrary.pConsole("Calculated "+ret+" for "+loc);
		return ret;
	}

	public void clear() {
		data.clear();
	}

	public void load(NBTTagCompound NBT) {
		data.clear();

		NBTTagList data = NBT.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : data.tagList) {
			NBTTagCompound entry = (NBTTagCompound)o;
			WorldLocation key = WorldLocation.readFromNBT("location", entry);
			AngleMap<WorldLocation> map = new AngleMap();
			NBTTagList li = entry.getTagList("map", NBTTypes.COMPOUND.ID);
			for (Object o2 : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o2;
				WorldLocation loc = WorldLocation.readFromNBT(tag);
				double d = tag.getDouble("angle");
				map.put(d, loc);
			}
			this.data.put(key, map);
		}
	}

	public void save(NBTTagCompound NBT) {
		NBTTagList data = new NBTTagList();
		for (WorldLocation key : this.data.keySet()) {
			NBTTagCompound entry = new NBTTagCompound();
			entry.setTag("location", key.writeToTag());
			AngleMap<WorldLocation> map = this.data.get(key);
			NBTTagList li = new NBTTagList();
			for (double d : map.keySet()) {
				WorldLocation loc = map.get(d);
				NBTTagCompound tag = loc.writeToTag();
				tag.setDouble("angle", d);
				li.appendTag(tag);
			}
			entry.setTag("map", li);
			data.appendTag(entry);
		}
		NBT.setTag("data", data);
	}

	public static class WarpNetworkData extends WorldSavedData {

		private static final String IDENTIFIER = NBT_TAG;

		public WarpNetworkData() {
			super(IDENTIFIER);
		}

		public WarpNetworkData(String s) {
			super(s);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			instance.load(NBT);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			instance.save(NBT);
		}

		private static WarpNetworkData initNetworkData(World world) {
			WarpNetworkData data = (WarpNetworkData)world.loadItemData(WarpNetworkData.class, IDENTIFIER);
			if (data == null) {
				data = new WarpNetworkData();
				world.setItemData(IDENTIFIER, data);
			}
			return data;
		}
	}

}
