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

import java.util.HashMap;

import Reika.ChromatiCraft.Registry.CrystalElement;

public class ElementTagCompound {

	private final HashMap<CrystalElement, Integer> data = new HashMap();

	public ElementTagCompound() {

	}

	public ElementTagCompound(CrystalElement e, int value) {
		this();
		this.addTag(e, value);
	}

	public boolean addTag(CrystalElement e, int value) {
		if (data.containsKey(e))
			return false;
		data.put(e, value);
		return true;
	}

	public void setTag(CrystalElement e, int value) {
		data.put(e, value);
	}

	public int getValue(CrystalElement e) {
		return data.containsKey(e) ? data.get(e) : 0;
	}

	@Override
	public String toString() {
		return data.toString();
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
	}

}
