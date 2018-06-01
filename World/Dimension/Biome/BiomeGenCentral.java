/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Biome;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Entity.EntityTunnelNuker;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;


public class BiomeGenCentral extends ChromaDimensionBiome {

	public BiomeGenCentral(int id, String n, Biomes t) {
		super(id, n, t);
	}

	@Override
	protected void initSpawnRules() {
		spawnableCaveCreatureList.add(new SpawnListEntry(EntityTunnelNuker.class, 1, 1, 1));
	}

}
