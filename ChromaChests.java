/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Worldgen.LootController;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.Location;

public class ChromaChests {

	private static final LootController data = new LootController();

	public static void addToChests() {
		for (int i = 0; i < 16; i++) {
			data.addItem(3, Location.BONUS, ChromaStacks.getShard(CrystalElement.elements[i]), 1, 5, 1);
			data.addItem(2, Location.DUNGEON, ChromaStacks.getShard(CrystalElement.elements[i]), 2, 8, 2);
			data.addItem(3, Location.VILLAGE, ChromaStacks.getShard(CrystalElement.elements[i]), 1, 3, 2);
			data.addItem(1, Location.PYRAMID, ChromaStacks.getShard(CrystalElement.elements[i]), 4, 16, 5);
			data.addItem(1, Location.JUNGLE_DISPENSER, ChromaStacks.getShard(CrystalElement.elements[i]), 2, 9, 3);
		}

		data.addItem(1, Location.DUNGEON, ChromaItems.FRAGMENT.getStackOf(), 1, 1, 10);
		data.addItem(1, Location.JUNGLE_PUZZLE, ChromaItems.FRAGMENT.getStackOf(), 1, 2, 20);
		data.addItem(1, Location.PYRAMID, ChromaItems.FRAGMENT.getStackOf(), 1, 1, 20);
		data.addItem(1, Location.STRONGHOLD_LIBRARY, ChromaItems.FRAGMENT.getStackOf(), 1, 3, 50);
		data.addItem(1, Location.STRONGHOLD_CROSSING, ChromaItems.FRAGMENT.getStackOf(), 1, 3, 10);
		data.addItem(1, Location.STRONGHOLD_HALLWAY, ChromaItems.FRAGMENT.getStackOf(), 1, 3, 10);
		data.addItem(3, Location.VILLAGE, ChromaItems.FRAGMENT.getStackOf(), 1, 1, 5);
		data.addItem(3, Location.MINESHAFT, ChromaItems.FRAGMENT.getStackOf(), 1, 1, 2);

		data.addItem(4, Location.DUNGEON, ChromaBlocks.GLOWSAPLING.getStackOf(), 1, 1, 1);
		//data.addItem(4, Location.VILLAGE, ChromaBlocks.GLOWSAPLING.getStackOf(), 1, 1, 1);
		data.addItem(4, Location.JUNGLE_DISPENSER, ChromaBlocks.GLOWSAPLING.getStackOf(), 1, 1, 2);

		data.registerToWorldGen(ChromatiCraft.instance, ChromaOptions.CHESTGEN.getValue());
	}
}
