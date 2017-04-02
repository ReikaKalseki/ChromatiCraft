package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class WarpPointData extends WorldSavedData {

	public static final String TAG = "CHROMAWARPPOINT";

	public WarpPointData() {
		super(TAG);
	}

	public WarpPointData(String s) {
		super(s);
	}

	static WarpPointData initWarpData(World world) {
		WarpPointData data = (WarpPointData)world.loadItemData(WarpPointData.class, TAG);
		if (data == null) {
			data = new WarpPointData();
			world.setItemData(TAG, data);
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		AbilityHelper.instance.teleports.clear();
		NBTTagCompound data = nbt.getCompoundTag("warpdata");
		for (Object o : data.func_150296_c()) {
			String player = (String)o;
			NBTTagList points = data.getTagList(player, NBTTypes.COMPOUND.ID);
			HashMap<String, WarpPoint> map = new HashMap();
			AbilityHelper.instance.teleports.directPut(UUID.fromString(player), map);
			for (Object o2 : points.tagList) {
				NBTTagCompound pt = (NBTTagCompound)o2;
				String label = pt.getString("label");
				WorldLocation pos = WorldLocation.readFromNBT("pos", pt);
				if (pos != null) {
					map.put(label, new WarpPoint(label, pos));
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound data = new NBTTagCompound();
		for (UUID uid : AbilityHelper.instance.teleports.keySet()) {
			NBTTagList points = new NBTTagList();
			HashMap<String, WarpPoint> map = AbilityHelper.instance.teleports.directGet(uid);
			for (String label : map.keySet()) {
				WarpPoint wp = map.get(label);
				NBTTagCompound pt = new NBTTagCompound();
				pt.setString("label", label);
				wp.location.writeToNBT("pos", pt);
				points.appendTag(pt);
			}
			data.setTag(uid.toString(), points);
		}
		nbt.setTag("warpdata", data);
	}

}