/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Biome;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome.ChromaDimensionSubBiome;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;

public class BiomeGenChromaOcean extends ChromaDimensionSubBiome {

	public BiomeGenChromaOcean(int id, String n, SubBiomes t) {
		super(id, n, t);
		enableRain = true;
	}

}
