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

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;
import Reika.DragonAPI.Instantiable.Data.Perimeter;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

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
			//	GL11.glTranslated(par2, par4, par6);
			Tessellator v5 = Tessellator.instance;

			//v5.startDrawing(GL11.GL_LINE_LOOP);
			//v5.setColorOpaque(255, 255, 255);
			ArrayList<AxisAlignedBB> li = p.getAABBs();
			for (int i = 0; i < li.size(); i++) {
				AxisAlignedBB box = li.get(i);
				int a = te.getSegmentAlpha(i);
				if (a > 0) {
					//v5.addVertex(loc.xCoord-te.xCoord+0.5, loc.yCoord-te.yCoord+0.5, loc.zCoord-te.zCoord+0.5);
					ReikaAABBHelper.renderAABB(box, par2, par4, par6, te.xCoord, te.yCoord, te.zCoord, a, 64, 192, 255, true);
				}
			}

			//v5.draw();
			GL11.glPopMatrix();
			//AxisAlignedBB box = p.getAABBs().get(p.getAABBs().size()-1);
			//
		}
	}

}
