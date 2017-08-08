/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;

import java.util.Arrays;


public class FixedLightPattern {

	public final int rowCount;
	public final int switchCount;
	private final LightGroup[] connections;

	public final int tier;

	public FixedLightPattern(int t, int rows, int switches) {
		tier = t;
		rowCount = rows;
		switchCount = switches;
		connections = new LightGroup[switchCount];
		for (int i = 0; i < connections.length; i++) {
			connections[i] = new LightGroup(rowCount);
		}
	}

	public void connect(int sw, int row, LightType type) {
		connections[sw].addLight(row, type);
	}

	public LightGroup getConnections(int sw) {
		return connections[sw].copy();
	}

	@Override
	public String toString() {
		return Arrays.toString(connections);
	}

	public boolean isEmpty() {
		for (int i = 0; i < connections.length; i++) {
			if (!connections[i].isEmpty())
				return false;
		}
		return true;
	}

}
