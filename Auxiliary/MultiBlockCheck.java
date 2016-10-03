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
