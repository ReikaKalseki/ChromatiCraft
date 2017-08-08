/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;


public class RenderCrystalConsole extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalConsole te = (TileEntityCrystalConsole)tile;
		if (!te.isValid())
			return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		double o = 0.005;

		ForgeDirection dir = te.getConsoleFace();

		Tessellator v5 = Tessellator.instance;

		int n = te.getSlotCount();

		double s = 0.1875;//0.75/(n+(n-1)); //0.75 - n buttons - n-1 spaces

		double[] ds = {
				-0.25,
				0,
				0.25,
				0.5
		};

		for (int i = 0; i < n; i++) {
			boolean on = (dir.offsetX+dir.offsetZ > 0) ? te.getState(n-i-1) : te.getState(i);
			int c2 = on ? 0x600000 : 0xff0000;
			int c1 = on ? 0x00ff00 : 0x003700;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c1);
			v5.setBrightness(240);

			double d = ds[i];//0.125+s+0.03125+s*(i-2)*2;
			double h = 0;

			switch(dir) {
				case SOUTH:
					v5.addVertex(0.5-s/2+d, 0.5+h, 1+o);
					v5.addVertex(0.5+s/2+d, 0.5+h, 1+o);
					v5.addVertex(0.5+s/2+d, 0.5+s+h, 1+o);
					v5.addVertex(0.5-s/2+d, 0.5+s+h, 1+o);

					v5.setColorOpaque_I(c2);
					v5.addVertex(0.5-s/2+d, 0.5-s+h, 1+o);
					v5.addVertex(0.5+s/2+d, 0.5-s+h, 1+o);
					v5.addVertex(0.5+s/2+d, 0.5+h, 1+o);
					v5.addVertex(0.5-s/2+d, 0.5+h, 1+o);
					break;
				case NORTH:
					v5.addVertex(0.5-s/2-d, 0.5+s+h, -o);
					v5.addVertex(0.5+s/2-d, 0.5+s+h, -o);
					v5.addVertex(0.5+s/2-d, 0.5+h, -o);
					v5.addVertex(0.5-s/2-d, 0.5+h, -o);

					v5.setColorOpaque_I(c2);
					v5.addVertex(0.5-s/2-d, 0.5+h, -o);
					v5.addVertex(0.5+s/2-d, 0.5+h, -o);
					v5.addVertex(0.5+s/2-d, 0.5-s+h, -o);
					v5.addVertex(0.5-s/2-d, 0.5-s+h, -o);
					break;
				case EAST:
					v5.addVertex(1+o, 0.5+s+h, 0.5-s/2+d);
					v5.addVertex(1+o, 0.5+s+h, 0.5+s/2+d);
					v5.addVertex(1+o, 0.5+h, 0.5+s/2+d);
					v5.addVertex(1+o, 0.5+h, 0.5-s/2+d);

					v5.setColorOpaque_I(c2);
					v5.addVertex(1+o, 0.5+h, 0.5-s/2+d);
					v5.addVertex(1+o, 0.5+h, 0.5+s/2+d);
					v5.addVertex(1+o, 0.5-s+h, 0.5+s/2+d);
					v5.addVertex(1+o, 0.5-s+h, 0.5-s/2+d);
					break;
				case WEST:
					v5.addVertex(-o, 0.5+h, 0.5-s/2+d);
					v5.addVertex(-o, 0.5+h, 0.5+s/2+d);
					v5.addVertex(-o, 0.5+s+h, 0.5+s/2+d);
					v5.addVertex(-o, 0.5+s+h, 0.5-s/2+d);

					v5.setColorOpaque_I(c2);
					v5.addVertex(-o, 0.5-s+h, 0.5-s/2+d);
					v5.addVertex(-o, 0.5-s+h, 0.5+s/2+d);
					v5.addVertex(-o, 0.5+h, 0.5+s/2+d);
					v5.addVertex(-o, 0.5+h, 0.5-s/2+d);
					break;
				default:
					break;
			}

			v5.draw();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
