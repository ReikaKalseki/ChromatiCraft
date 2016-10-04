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

import java.util.Random;

import Reika.ChromatiCraft.World.Dimension.Structure.LightPanelGenerator;
import Reika.DragonAPI.Instantiable.Data.Maps.ShuffleMap;


public class FixedLightPanelRoom extends LightPanelRoom {

	private final FixedLightPattern pattern;

	public FixedLightPanelRoom(LightPanelGenerator s, int r, int sw, int lvl, Random rand, int posX, int posY, int posZ, FixedLightPattern patt) {
		super(s, r, sw, lvl, rand, posX, posY, posZ);
		pattern = patt;
	}

	@Override
	protected void doGenerateConnections() {
		ShuffleMap rowShuffle = new ShuffleMap();
		for (int i = 0; i < rowCount; i++) {
			rowShuffle.addEntry(i);
		}
		ShuffleMap switchShuffle = new ShuffleMap();
		for (int i = 0; i < switchCount; i++) {
			switchShuffle.addEntry(i);
		}
		rowShuffle.shuffle();
		switchShuffle.shuffle();

		for (int i = 0; i < switchCount; i++) {
			LightGroup lg = pattern.getConnections(switchShuffle.getShuffledIndex(i));
			for (int k = 0; k < rowCount; k++) {
				int rk = rowShuffle.getShuffledIndex(k);
				for (int l = 0; l < LightType.list.length; l++) {
					if (lg.containsLight(rk, LightType.list[l])) {
						this.addConnection(i, rk, LightType.list[l]);
					}
				}
			}
		}
	}

	@Override
	protected boolean isSolvable() {
		return true;
	}

	public int getTier() {
		return pattern.tier;
	}

}
