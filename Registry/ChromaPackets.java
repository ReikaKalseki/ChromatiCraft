/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

public enum ChromaPackets {

	REACH(1),
	ENCHANTER(2),
	SPAWNERPROGRAM(1);

	public final int numInts;

	private ChromaPackets() {
		this(0);
	}

	private ChromaPackets(int size) {
		numInts = size;
	}

	public static final ChromaPackets getPacket(int id) {
		ChromaPackets[] list = values();
		id = Math.max(0, Math.min(id, list.length-1));
		return list[id];
	}

}
