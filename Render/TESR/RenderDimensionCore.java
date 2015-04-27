/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import Reika.ChromatiCraft.Base.RenderLocusPoint;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.TileEntity.TileEntityDimensionCore;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderDimensionCore extends RenderLocusPoint {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	protected void doOtherRendering(TileEntityLocusPoint tile, float par8) {

	}

	@Override
	protected int getColor(TileEntityLocusPoint tile) { //do an iridescent effect
		return ((TileEntityDimensionCore)tile).getColor().getColor();
	}

}
