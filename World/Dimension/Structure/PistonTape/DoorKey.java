package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;

public class DoorKey {

	private final DoorValue[] colors;

	public final int colorCount;
	public final int index;
	public final int value;

	DoorKey(int idx, RGBColorData[] clr) {
		index = idx;
		colorCount = clr.length;
		colors = new DoorValue[colorCount];
		int v = 0;
		for (int i = 0; i < colorCount; i++) {
			colors[i] = new DoorValue(i, clr[i]);
			v |= colors[i].numberValue << (i*3);
		}
		value = v;
	}

	public DoorValue getValue(int idx) {
		return colors[idx];
	}

	void setTarget(int idx, Coordinate c) {
		colors[idx].target = c;
	}

	public class DoorValue {

		public final int index;
		private RGBColorData color;
		private Coordinate target;
		public final int numberValue;

		private DoorValue(int i, RGBColorData clr) {
			index = i;
			color = clr;
			numberValue = (clr.red ? 4 : 0) | (clr.green ? 2 : 0) | (clr.blue ? 1 : 0);
		}

		RGBColorData getColor() {
			return color.copy();
		}

		public int getRenderColor() {
			return color.getRenderColor();
		}

		DoorKey getParent() {
			return DoorKey.this;
		}

		Coordinate getTargetLocation() {
			return target;
		}

	}

	public static class KeyIO implements NBTIO<DoorKey> {

		public static final KeyIO instance = new KeyIO();

		private KeyIO() {

		}

		@Override
		public DoorKey createFromNBT(NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			int num = tag.getInteger("count");
			int idx = tag.getInteger("idx");
			RGBColorData[] arr = new RGBColorData[num];
			for (int i = 0; i < num; i++) {
				NBTTagCompound val = tag.getCompoundTag("clr_"+i);
				RGBColorData clr = RGBColorData.white();
				clr.readFromNBT(val);
				arr[i] = clr;
			}
			return new DoorKey(idx, arr);
		}

		@Override
		public NBTBase convertToNBT(DoorKey obj) {
			NBTTagCompound ret = new NBTTagCompound();
			for (int i = 0; i < obj.colorCount; i++) {
				NBTTagCompound tag = new NBTTagCompound();
				obj.colors[i].color.writeToNBT(tag);
				ret.setTag("clr_"+i, tag);
			}
			ret.setInteger("count", obj.colorCount);
			ret.setInteger("idx", obj.index);
			return ret;
		}

	}

}
