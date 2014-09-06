/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderItemRift extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "itemrift.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {

	}

}
