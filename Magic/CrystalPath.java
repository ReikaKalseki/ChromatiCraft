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

import java.util.LinkedList;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class CrystalPath {

	protected final LinkedList<WorldLocation> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private int attenuation;

	public CrystalPath(CrystalElement e, LinkedList<WorldLocation> li) {
		nodes = li;
		transmitter = (CrystalSource)nodes.getLast().getTileEntity();
		origin = nodes.getFirst();
		element = e;
		this.initialize();
		//remainingAmount = amt;
	}

	protected void initialize() {
		int loss = 0;
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			if (te instanceof CrystalRepeater)
				loss = Math.max(loss, ((CrystalRepeater)te).getSignalDegradation());
		}
		attenuation = loss;
	}

	public int getSignalLoss() {
		return attenuation;
	}

	public boolean canTransmit() {
		return transmitter.getTransmissionStrength() > attenuation;
	}

	@Override
	public String toString() {
		return nodes.toString();
	}

	public boolean contains(CrystalNetworkTile te) {
		return nodes.contains(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
	}

}
