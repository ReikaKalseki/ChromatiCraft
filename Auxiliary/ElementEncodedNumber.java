package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collections;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class ElementEncodedNumber {

	private final int originalValue;
	private final boolean isNegative;
	private final CrystalElement[] encodedValue;

	public ElementEncodedNumber(int value) {
		this(value, 0);
	}

	public ElementEncodedNumber(int value, int bitOffset) {
		originalValue = value;
		isNegative = value < 0;
		encodedValue = encodeValue(value, bitOffset);
	}

	private static CrystalElement[] encodeValue(int value, int offset) {
		byte[] vals = ReikaJavaLibrary.splitIntToHexChars(value);
		ArrayList<Byte> v = ReikaJavaLibrary.makeIntListFromArray(vals);
		Collections.reverse(v);
		while (v.get(0) == 0 && v.size() >= 1) //strip leading values
			v.remove(0);
		CrystalElement[] ret = new CrystalElement[v.size()];
		for (int i = 0; i < v.size(); i++) {
			int idx = (v.get(i)+offset)%16;
			ret[i] = CrystalElement.elements[idx];
		}
		return ret;
	}

	public int getLength() {
		return encodedValue.length;
	}

	public CrystalElement getSlot(int idx) {
		return encodedValue[idx];
	}

}
