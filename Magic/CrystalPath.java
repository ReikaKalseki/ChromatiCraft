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
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CrystalPath {

	protected final LinkedList<WorldLocation> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private int attenuation;

	protected CrystalPath(CrystalElement e, LinkedList<WorldLocation> li) {
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
		return element+": to "+origin+" from "+transmitter+": "+nodes.toString();
	}

	public boolean contains(CrystalNetworkTile te) {
		return nodes.contains(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
	}

	public final boolean checkLineOfSight() {
		for (int i = 0; i < nodes.size()-1; i++) {
			WorldLocation src = nodes.get(i);
			WorldLocation tgt = nodes.get(i+1);
			if (!PylonFinder.lineOfSight(src.getWorld(), src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord)) {
				CrystalTransmitter tr = (CrystalTransmitter)src.getTileEntity();
				CrystalTransmitter tg = (CrystalTransmitter)tgt.getTileEntity();
				if (tr.needsLineOfSight() || tg.needsLineOfSight()) {
					return false;
				}
			}
		}
		return true;
	}

	boolean stillValid() {
		for (int i = 0; i < nodes.size()-1; i++) {
			WorldLocation src = nodes.get(i);
			WorldLocation tgt = nodes.get(i+1);
			if (src.getTileEntity() instanceof CrystalTransmitter) {
				CrystalTransmitter tr = (CrystalTransmitter)src.getTileEntity();
				CrystalTransmitter tg = (CrystalTransmitter)tgt.getTileEntity();
				if (!PylonFinder.lineOfSight(src.getWorld(), src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord)) {
					if (tr.needsLineOfSight() || tg.needsLineOfSight()) {
						ReikaJavaLibrary.pConsole(tr+" >> "+tg);
						return false;
					}
				}
				if (!tr.canConduct() || !tr.isConductingElement(element)) {
					return false;
				}
			}
		}
		return true;
	}

}
