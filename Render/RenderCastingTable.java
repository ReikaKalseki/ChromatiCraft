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

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderCastingTable extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCastingTable te = (TileEntityCastingTable)tile;
		if (te.isCrafting()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2+0.5, par4+0.75, par6+0.5);
			ArrayList<CrystalTarget> li = te.getTargets();
			ReikaRenderHelper.prepareGeoDraw(true);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawing(GL11.GL_LINES);
			for (int i = 0; i < li.size(); i++) {
				CrystalTarget tg = li.get(i);
				v5.setColorOpaque(tg.color.getRed(), tg.color.getGreen(), tg.color.getBlue());/*
				v5.addVertex(0, 0, 0);
				v5.addVertex(tg.location.xCoord-te.xCoord, tg.location.yCoord-te.yCoord, tg.location.zCoord-te.zCoord);*/

				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				// glPushAttrib is done to return everything to normal after drawing

				short s = (short)((System.currentTimeMillis()/100D)%0xFFFF);

				GL11.glLineStipple(1, s);  // [1]
				GL11.glEnable(GL11.GL_LINE_STIPPLE);
				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3d(0, 0, 0);
				GL11.glVertex3d(tg.location.xCoord-te.xCoord, tg.location.yCoord-te.yCoord, tg.location.zCoord-te.zCoord);
				GL11.glEnd();

				GL11.glPopAttrib();
			}

			v5.draw();
			ReikaRenderHelper.exitGeoDraw();
			GL11.glPopMatrix();
		}
	}

}
