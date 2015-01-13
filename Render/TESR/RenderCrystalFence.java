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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;
import Reika.DragonAPI.Instantiable.Data.Perimeter;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderCrystalFence extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fence";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalFence te = (TileEntityCrystalFence)tile;
		if (te.isInWorld()) {
			Perimeter p = te.getFence();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			Tessellator v5 = Tessellator.instance;

			//v5.setColorOpaque(255, 255, 255);
			for (int i = 0; i < p.segmentCount(); i++) {
				int a = te.getSegmentAlpha(i);
				if (a > 0) {
					Coordinate c1 = p.getSegmentPreCoord(i);
					Coordinate c2 = p.getSegmentPostCoord(i);
					ReikaRenderHelper.prepareGeoDraw(true);
					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setColorRGBA(255, 255, 255, a);
					v5.addVertex(c1.xCoord+0.5-te.xCoord, 000-te.yCoord, c1.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 000-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.draw();

					v5.startDrawingQuads();
					v5.setColorRGBA(255, 255, 255, Math.min(96, 96*a/255));
					v5.addVertex(c1.xCoord+0.5-te.xCoord, 000-te.yCoord, c1.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.addVertex(c2.xCoord+0.5-te.xCoord, 000-te.yCoord, c2.zCoord+0.5-te.zCoord);
					v5.draw();
					ReikaRenderHelper.exitGeoDraw();
					//v5.addVertex(loc.xCoord-te.xCoord+0.5, loc.yCoord-te.yCoord+0.5, loc.zCoord-te.zCoord+0.5);
					//ReikaAABBHelper.renderAABB(box, par2, par4, par6, te.xCoord, te.yCoord, te.zCoord, a, 64, 192, 255, true);
				}
			}

			GL11.glPopMatrix();
			//AxisAlignedBB box = p.getAABBs().get(p.getAABBs().size()-1);
			//
		}
	}

}
