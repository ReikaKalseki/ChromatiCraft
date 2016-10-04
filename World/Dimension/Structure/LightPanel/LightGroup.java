/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;

import java.util.Arrays;


public class LightGroup {

	private final boolean[][] lights;
	private boolean isEmpty = true;

	public LightGroup(int rows) {
		lights = new boolean[rows][LightType.list.length];
	}

	public void addLight(int row, LightType light) {
		lights[row][light.ordinal()] = true;
		isEmpty = false;
	}

	public boolean containsLight(int row, LightType light) {
		return lights[row][light.ordinal()];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lights.length; i++) {
			sb.append(Arrays.toString(lights[i]));
			sb.append("\n");
		}
		return sb.toString();
	}

	public LightGroup copy() {
		LightGroup lg = new LightGroup(lights.length);
		for (int i = 0; i < lg.lights.length; i++) {
			lg.lights[i] = Arrays.copyOf(lights[i], lights[i].length);
		}
		lg.isEmpty = isEmpty;
		return lg;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

}
