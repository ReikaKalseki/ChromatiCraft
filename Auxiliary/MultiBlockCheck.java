/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap.TimerCallback;


public class MultiBlockCheck implements TimerCallback {

	private final MultiBlockChromaTile tile;

	public MultiBlockCheck(MultiBlockChromaTile te) {
		tile = te;
	}

	@Override
	public void call() {
		tile.validateStructure();
	}

}
