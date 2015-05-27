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

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.RenderLocusPoint;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Render.GlowKnot;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class RenderAuraPoint extends RenderLocusPoint {

	private final GlowKnot knot = new GlowKnot(0.875);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	protected void doOtherRendering(TileEntityLocusPoint tile, float par8) {
		if (tile.isInWorld()) {
			double d = 1.25+0.25*(Math.sin(System.currentTimeMillis()/1000D));
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(d, d, d);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			knot.render(tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5, ReikaColorAPI.getModifiedSat(tile.getRenderColor(), 0.875F));
			GL11.glPopMatrix();
		}
	}

}
