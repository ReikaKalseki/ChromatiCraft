/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Layers;

import Reika.ChromatiCraft.Base.GenLayerChroma;

public class BiomeControlLayer extends GenLayerChroma {

	public BiomeControlLayer(long seed) {
		super(seed);
	}

	@Override
	protected int[] getProperties(int x, int z, int width, int depth) {
		return null;
	}

}
