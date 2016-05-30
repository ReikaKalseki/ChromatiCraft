/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.RenderLocusPoint;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class RenderAuraPoint extends RenderLocusPoint {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	protected void doOtherRendering(TileEntityLocusPoint tile, float par8) {
		if (!tile.isInWorld() || MinecraftForgeClient.getRenderPass() == 1) {
			double d = 1.25+0.25*(Math.sin(System.currentTimeMillis()/1000D));
			GL11.glPushMatrix();
			//GL11.glTranslated(0.5, 0.5, 0.5);
			//GL11.glScaled(d, d, d);
			//GL11.glTranslated(-0.5, -0.5, -0.5);
			double dx = tile.xCoord+0.5;
			double dy = tile.yCoord+0.5;
			double dz = tile.zCoord+0.5;
			int color = 0xff000000 | ReikaColorAPI.getModifiedSat(tile.getRenderColor(), 0.875F);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDepthMask(false);
			float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			if (!tile.isInWorld()) {
				dx = dy = dz = 0;
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				float f = 0.5F;
				GL11.glColor4f(f, f, f, f);
				int hue = (int)((System.currentTimeMillis()/100)%360);
				//color = ((int)(f*255) << 24) | ReikaColorAPI.getModifiedHue(0xff9090, hue);
				GL11.glLineWidth(1.5F);
			}
			((TileEntityAuraPoint)tile).knot.render(dx, dy, dz, color, tile.isInWorld());
			GL11.glLineWidth(w);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

}
