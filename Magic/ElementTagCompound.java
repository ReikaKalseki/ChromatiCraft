/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;

import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class ElementTagCompound {

	private final EnumMap<CrystalElement, Integer> data = new EnumMap(CrystalElement.class);

	public ElementTagCompound() {

	}

	public ElementTagCompound(CrystalElement e, int value) {
		this();
		this.addTag(e, value);
	}

	public boolean addTag(CrystalElement e, int value) {
		if (value <= 0)
			return false;
		if (data.containsKey(e))
			return false;
		data.put(e, value);
		return true;
	}

	public void setTag(CrystalElement e, int value) {
		if (value >= 0)
			data.put(e, value);
	}

	public void removeTag(CrystalElement e) {
		data.remove(e);
	}

	public int getValue(CrystalElement e) {
		return this.contains(e) ? data.get(e) : 0;
	}

	public boolean contains(CrystalElement e) {
		return data.containsKey(e);
	}

	public void addValueToColor(CrystalElement e, int value) {
		if (value <= 0)
			return;
		int has = this.getValue(e);
		int sum = has+value;
		this.setTag(e, sum);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	/** Map.keySet() */
	public Collection<CrystalElement> elementSet() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public void addTag(ElementTagCompound tag) {
		if (tag == null || tag.data.isEmpty())
			return;
		for (CrystalElement e : tag.data.keySet()) {
			int amt = tag.getValue(e);
			int value = this.getValue(e);
			this.setTag(e, amt+value);
		}
	}

	public void maximizeWith(ElementTagCompound tag) {
		if (tag == null || tag.data.isEmpty())
			return;
		for (CrystalElement e : tag.data.keySet()) {
			int amt = tag.getValue(e);
			int value = this.getValue(e);
			this.setTag(e, Math.max(amt, value));
		}
	}

	public void minimizeWith(ElementTagCompound tag) {
		if (tag == null || tag.data.isEmpty())
			return;
		for (CrystalElement e : tag.data.keySet()) {
			int amt = tag.getValue(e);
			int value = this.getValue(e);
			this.setTag(e, Math.min(amt, value));
		}
		this.clearEmptyKeys();
	}

	public void addButMinimizeWith(ElementTagCompound tag) {
		if (tag == null || tag.data.isEmpty())
			return;
		for (CrystalElement e : tag.data.keySet()) {
			int amt = tag.getValue(e);
			int value = this.getValue(e);
			int val = amt > 0 && value > 0 ? Math.min(amt, value) : (amt > 0 ? amt : value);
			this.setTag(e, val);
		}
		this.clearEmptyKeys();
	}

	public void maximizeWith(ElementTag tag) {
		this.setTag(tag.element, Math.max(this.getValue(tag.element), tag.value));
	}

	public void minimizeWith(ElementTag tag) {
		this.setTag(tag.element, Math.min(this.getValue(tag.element), tag.value));
		this.clearEmptyKeys();
	}

	public void clearEmptyKeys() {
		for (CrystalElement e : data.keySet()) {
			if (data.containsKey(e) && data.get(e) <= 0)
				data.remove(e);
		}
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public ElementTagCompound copy() {
		ElementTagCompound e = new ElementTagCompound();
		e.data.putAll(data);
		return e;
	}

	public ElementTagCompound scale(float s) {
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			amt *= s;
			if (amt == 0)
				amt = 1;
			this.setTag(e, amt);
		}
		this.clearEmptyKeys();
		return this;
	}

	public ElementTagCompound square() {
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			amt *= amt;
			this.setTag(e, amt);
		}
		return this;
	}

	public ElementTagCompound power(int power) {
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			amt = ReikaMathLibrary.intpow2(amt, power);
			this.setTag(e, amt);
		}
		return this;
	}

	public ElementTagCompound clear() {
		data.clear();
		return this;
	}

	public void subtract(CrystalElement e, int amt) {
		int has = this.getValue(e);
		this.setTag(e, Math.max(0, has-amt));
		this.clearEmptyKeys();
	}

	public void subtract(ElementTagCompound energy) {
		for (CrystalElement e : energy.data.keySet()) {
			int amt = energy.getValue(e);
			int has = this.getValue(e);
			this.setTag(e, Math.max(0, has-amt));
		}
		this.clearEmptyKeys();
	}

	public void readFromNBT(String name, NBTTagCompound NBT) {
		if (!NBT.hasKey(name))
			return;
		NBTTagCompound tag = NBT.getCompoundTag(name);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int amt = tag.getInteger(e.name());
			data.put(e, amt);
		}
		this.clearEmptyKeys();
	}

	public static ElementTagCompound createFromNBT(NBTTagCompound tag) {
		ElementTagCompound c = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int amt = tag.getInteger(e.name());
			c.data.put(e, amt);
		}
		c.clearEmptyKeys();
		return c;
	}

	public void writeToNBT(String name, NBTTagCompound NBT) {
		NBTTagCompound tag = new NBTTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int amt = this.getValue(e);
			if (amt > 0)
				tag.setInteger(e.name(), amt);
		}
		NBT.setTag(name, tag);
	}

	public static ElementTagCompound getUniformTag(int level) {
		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++)
			tag.addTag(CrystalElement.elements[i], level);
		return tag;
	}

	public boolean containsAtLeast(CrystalElement e, int amt) {
		return this.getValue(e) >= amt;
	}

	public boolean containsAtLeast(ElementTagCompound tag) {
		for (CrystalElement e : tag.data.keySet()) {
			int val = tag.getValue(e);
			int has = this.getValue(e);
			if (val > has)
				return false;
		}
		return true;
	}

	public int getTotalEnergy() {
		int amt = 0;
		for (CrystalElement e : data.keySet()) {
			int val = this.getValue(e);
			amt += val;
		}
		return amt;
	}

	public void intersectWith(ElementTagCompound tag) {
		ElementTagCompound temp = new ElementTagCompound();
		for (CrystalElement e : tag.data.keySet()) {
			if (data.containsKey(e))
				temp.addTag(e, this.getValue(e));
		}
		data.clear();
		for (CrystalElement e : temp.data.keySet()) {
			this.setTag(e, temp.getValue(e));
		}
	}

	public void intersectWithMinimum(ElementTagCompound tag) {
		ElementTagCompound temp = new ElementTagCompound();
		for (CrystalElement e : tag.data.keySet()) {
			if (data.containsKey(e) && tag.getValue(e) >= data.get(e))
				temp.addTag(e, this.getValue(e));
		}
		data.clear();
		for (CrystalElement e : temp.data.keySet()) {
			this.setTag(e, temp.getValue(e));
		}
	}

	public WeightedRandom<CrystalElement> asWeightedRandom() {
		WeightedRandom<CrystalElement> rand = new WeightedRandom();
		for (CrystalElement e : data.keySet()) {
			rand.addEntry(e, data.get(e));
		}
		return rand;
	}

	public void clipToPrimaries() {
		ArrayList<CrystalElement> li = new ArrayList();
		for (CrystalElement e : data.keySet()) {
			if (!e.isPrimary())
				li.add(e);
		}
		for (CrystalElement e : li) {
			data.remove(e);
		}
	}

	public int tagCount() {
		return data.size();
	}

	public int getMaximumValue() {
		int max = 0;
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			if (amt > max)
				max = amt;
		}
		return max;
	}

	public int getMinimumValue() {
		int min = 0;
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			if (amt < min)
				min = amt;
		}
		return min;
	}

	public String toDisplay() {
		StringBuilder sb = new StringBuilder();
		for (CrystalElement e : data.keySet()) {
			int amt = data.get(e);
			if (amt > 0) {
				sb.append(e.displayName+": "+amt+" Lumens");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public float getRatio(ElementTagCompound tag, CrystalElement e) {
		int val = tag.getValue(e);
		return val > 0 ? this.getValue(e)/val : Float.POSITIVE_INFINITY;
	}

	public float getSmallestRatio(ElementTagCompound tag) {
		float min = Float.POSITIVE_INFINITY;
		for (CrystalElement e : data.keySet()) {
			min = Math.min(min, this.getRatio(tag, e));
		}
		return min;
	}

	public void setAllValuesTo(int amt) {
		for (CrystalElement e : data.keySet()) {
			data.put(e, amt);
		}
	}

}
