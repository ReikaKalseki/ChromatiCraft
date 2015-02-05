/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import Reika.DragonAPI.Auxiliary.PacketTypes;

public enum ChromaPackets {

	ENCHANTER(2),
	SPAWNERPROGRAM(1),
	CRYSTALEFFECT(),
	PLANTUPDATE(),
	ABILITY(2),
	PYLONATTACK(6),
	ABILITYCHOOSE(1),
	BUFFERINC(1),
	TELEPUMP(1),
	//TRANSMIT(3),
	ASPECT(),
	LAMPCHANNEL(1),
	LAMPCONTROL(2),
	TNT(4),
	BOOKINVSCROLL(1),
	TICKER(1),
	PYLONCLEAR(1),
	SHARDBOOST(1),
	GIVERESEARCH(1),
	LEAFBREAK(1),
	GIVEPROGRESS(2),
	HEALTHSYNC(1),
	INVCYCLE(1),
	RELAYCONNECT(),
	RERESEARCH(1);

	public final int numInts;
	public final PacketTypes type;

	private ChromaPackets() {
		this(0);
	}

	private ChromaPackets(int size) {
		this(size, PacketTypes.DATA);
	}

	private ChromaPackets(int size, PacketTypes t) {
		numInts = size;
		type = t;
	}

	public static final ChromaPackets getPacket(int id) {
		ChromaPackets[] list = values();
		id = Math.max(0, Math.min(id, list.length-1));
		return list[id];
	}

	public boolean hasData() {
		return numInts > 0;
	}

}
