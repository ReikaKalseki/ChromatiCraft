/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

import net.minecraft.nbt.NBTTagCompound;

public class ElementTagCompound {

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
		if (value > 0)
			data.put(e, value);
	}

	public int getValue(CrystalElement e) {
		return data.containsKey(e) ? data.get(e) : 0;
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

	public Collection<CrystalElement> elementSet() {
		ArrayList<CrystalElement> c = new ArrayList();
		c.addAll(data.keySet());
		return c;
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

	public void maximizeWith(ElementTag tag) {
		this.setTag(tag.element, Math.max(this.getValue(tag.element), tag.value));
	}

	public void minimizeWith(ElementTag tag) {
		this.setTag(tag.element, Math.min(this.getValue(tag.element), tag.value));
		this.clearEmptyKeys();
	}

	private void clearEmptyKeys() {
		for (CrystalElement e : data.keySet()) {
			if (data.containsKey(e) && data.get(e) == 0)
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
			if (tag.hasKey(e.name())) {
				int amt = tag.getInteger(e.name());
				if (amt > 0)
					data.put(e, amt);
			}
		}
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

}
