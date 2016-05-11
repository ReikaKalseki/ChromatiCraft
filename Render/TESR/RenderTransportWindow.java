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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelTransportWindow;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderTransportWindow extends ChromaRenderBase {

	private final ModelTransportWindow model = new ModelTransportWindow();
	private final ColorBlendList colorList = new ColorBlendList(2000).addColor(0x00ff00).addColor(0xa000ff).addColor(0xffffff).multiplySaturation(0.5F);

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();

		TileEntityTransportWindow te = (TileEntityTransportWindow)tile;

		GL11.glTranslated(par2, par4, par6);
		GL11.glPushMatrix();
		if (te.isInWorld() && te.getBlockMetadata() < 2) {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-1, 0, 0);
		}
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glColor4f(0.35F, 0.35F, 0.35F, 1);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		this.renderModel(te, model, te.isInWorld());
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		if (te.isInWorld() && te.doRender()) {
			double dx = par2+0.5;
			double dy = par4+0.74; //0.5 + (0.62-0.5)*2
			double dz = par6+0.5;
			double[] dat = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			double theta = dat[1];
			double phi = dat[2];
			while (phi < 0)
				phi += 360;
			while (phi >= 360)
				phi -= 360;
			double phit = 0;
			switch(te.getBlockMetadata()) {
				case 0:
					phit = 270;
					break;
				case 1:
					phit = 90;
					break;
				case 2:
					phit = 180;
					break;
				case 3:
					phit = 0;
					break;
			}
			if ((ReikaMathLibrary.approxr(phi, phit, 60) || ReikaMathLibrary.approxr(phi, phit+360, 60)) && ReikaMathLibrary.approxr(theta, 80, 80))
				this.renderTexture(te, par2, par4, par6, par8);
		}
		//if (te.isInWorld()) {
		//	this.doConnectionRender(te, par2, par4, par6, par8);
		//}

		GL11.glPopMatrix();

	}

	private void doConnectionRender(TileEntityTransportWindow te, double par2, double par4, double par6, float par8) {
		WorldLocation src = te.getSourceLocation();
		WorldLocation tgt = te.getTargetLocation();
		WorldLocation loc = new WorldLocation(te);
		if (src != null) {
			this.renderConnection(src, loc, -loc.xCoord, -loc.yCoord, -loc.zCoord, 0x0000ff, 0xffffff);
		}
		if (tgt != null) {
			this.renderConnection(loc, tgt, -loc.xCoord, -loc.yCoord+1, -loc.zCoord, 0xff00000, 0x00ff000);
		}
	}

	private void renderConnection(WorldLocation loc1, WorldLocation loc2, double dx, double dy, double dz, int c1, int c2) {
		Tessellator v5 = Tessellator.instance;
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINES);
		v5.setBrightness(240);

		v5.setColorOpaque_I(c1);
		v5.addVertex(loc1.xCoord+0.5+dx, loc1.yCoord+0.5+dy, loc1.zCoord+0.5+dz);
		v5.setColorOpaque_I(c2);
		v5.addVertex(loc2.xCoord+0.5+dx, loc2.yCoord+0.5+dy, loc2.zCoord+0.5+dz);
		/*

		TileEntity te1 = loc1.getTileEntity();
		TileEntity te2 = loc2.getTileEntity();

		if (te1 != null && te2 != null) {

			double ox1 = te1.getBlockMetadata() >= 2 ? 0 : 0.5;
			double oz1 = te1.getBlockMetadata() < 2 ? 0 : 0.5;

			double ox2 = te2.getBlockMetadata() >= 2 ? 0 : 0.5;
			double oz2 = te2.getBlockMetadata() < 2 ? 0 : 0.5;

			double tx1 = oz1*2;
			double tz1 = ox1*2;
			double tx2 = oz2*2;
			double tz2 = ox2*2;


		v5.addVertex(loc1.xCoord+dx+ox1, loc1.yCoord+dy, loc1.zCoord+dz+oz1);
		v5.addVertex(loc2.xCoord+dx+ox2, loc2.yCoord+dy, loc2.zCoord+dz+oz2);

		v5.addVertex(loc1.xCoord+dx+ox1+tx1, loc1.yCoord+dy, loc1.zCoord+dz+oz1+tz1);
		v5.addVertex(loc2.xCoord+dx+ox2+tx2, loc2.yCoord+dy, loc2.zCoord+dz+oz2+tz2);

		v5.addVertex(loc1.xCoord+dx+ox1, loc1.yCoord+dy+1, loc1.zCoord+dz+oz1);
		v5.addVertex(loc2.xCoord+dx+ox2, loc2.yCoord+dy+1, loc2.zCoord+dz+oz2);

		v5.addVertex(loc1.xCoord+dx+ox1+tx1, loc1.yCoord+dy+1, loc1.zCoord+dz+oz1+tz1);
		v5.addVertex(loc2.xCoord+dx+ox2+tx2, loc2.yCoord+dy+1, loc2.zCoord+dz+oz2+tz2);
	}
		 */

		v5.draw();
		GL11.glPopAttrib();
		GL11.glShadeModel(GL11.GL_FLAT);
	}

	private void renderTexture(TileEntityTransportWindow te, double par2, double par4, double par6, float par8) {
		//render end-portal-like texture through the holes  or maybe a tunnel-like render; leave "backface" clear
		GL11.glPushMatrix();
		int ang = 0;
		switch(te.getBlockMetadata()) {
			case 0:
				ang = 270;
				GL11.glTranslated(1, 0, 0);
				break;
			case 1:
				ang = 90;
				GL11.glTranslated(0, 0, 1);
				break;
			case 2:
				ang = 180;
				GL11.glTranslated(1, 0, 1);
				break;
			case 3:
				ang = 0;
				break;
		}
		GL11.glRotated(ang, 0, 1, 0);
		Tessellator v5 = Tessellator.instance;
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double out1 = 0.375;
		double out2 = 0.425;
		double d1 = -0.5;
		double d2 = -1;

		//float f = (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/2000D));
		//int c = ReikaColorAPI.mixColors(0x00ff00, 0xa000ff, f);//ReikaColorAPI.getModifiedHue(0xff0000, (int)(System.currentTimeMillis()/32D%360));
		//c = ReikaColorAPI.getModifiedSat(c, 0.5F);
		int c = colorList.getColor(System.currentTimeMillis());
		int c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.15F);//0x121212;
		int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.95F);//0xe0e0e0;

		//Center square
		v5.setColorOpaque_I(c1);
		v5.addVertex(0.5-out1, 0.5-out1, 0.5+d2);
		v5.addVertex(0.5+out1, 0.5-out1, 0.5+d2);
		v5.addVertex(0.5+out1, 0.5+out1, 0.5+d2);
		v5.addVertex(0.5-out1, 0.5+out1, 0.5+d2);

		//Walls
		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5+out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5-out2, 0.5+out2, 0.5+d1);
		v5.setColorOpaque_I(c1);
		v5.addVertex(0.5-out1, 0.5+out1, 0.5+d2);
		v5.addVertex(0.5+out1, 0.5+out1, 0.5+d2);

		v5.setColorOpaque_I(c1);
		v5.addVertex(0.5+out1, 0.5-out1, 0.5+d2);
		v5.addVertex(0.5-out1, 0.5-out1, 0.5+d2);
		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5-out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0.5+out2, 0.5-out2, 0.5+d1);

		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5+out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0.5+out2, 0.5+out2, 0.5+d1);
		v5.setColorOpaque_I(c1);
		v5.addVertex(0.5+out1, 0.5+out1, 0.5+d2);
		v5.addVertex(0.5+out1, 0.5-out1, 0.5+d2);

		v5.setColorOpaque_I(c1);
		v5.addVertex(0.5-out1, 0.5-out1, 0.5+d2);
		v5.addVertex(0.5-out1, 0.5+out1, 0.5+d2);
		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5-out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5-out2, 0.5-out2, 0.5+d1);

		//Border
		v5.setColorOpaque_I(c2);
		v5.addVertex(0, 0.5-out2, 0.5);
		v5.addVertex(0.5-out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0.5-out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0, 0.5+out2, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(1, 0.5+out2, 0.5);
		v5.addVertex(0.5+out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5+out2, 0.5-out2, 0.5+d1);
		v5.addVertex(1, 0.5-out2, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5-out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5+out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5+out2, 1, 0.5);
		v5.addVertex(0.5-out2, 1, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(0.5-out2, 0, 0.5);
		v5.addVertex(0.5+out2, 0, 0.5);
		v5.addVertex(0.5+out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0.5-out2, 0.5-out2, 0.5+d1);

		//Corners
		v5.setColorOpaque_I(c2);
		v5.addVertex(0, 0.5+out2, 0.5);
		v5.addVertex(0.5-out2, 0.5+out2, 0.5+d1);
		v5.addVertex(0.5-out2, 1, 0.5);
		v5.addVertex(0, 1, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(1, 1, 0.5);
		v5.addVertex(0.5+out2, 1, 0.5);
		v5.addVertex(0.5+out2, 0.5+out2, 0.5+d1);
		v5.addVertex(1, 0.5+out2, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(0, 0, 0.5);
		v5.addVertex(0.5-out2, 0, 0.5);
		v5.addVertex(0.5-out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0, 0.5-out2, 0.5);

		v5.setColorOpaque_I(c2);
		v5.addVertex(1, 0.5-out2, 0.5);
		v5.addVertex(0.5+out2, 0.5-out2, 0.5+d1);
		v5.addVertex(0.5+out2, 0, 0.5);
		v5.addVertex(1, 0, 0.5);

		v5.draw();
		GL11.glPopAttrib();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glPopMatrix();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return ((TileEntityTransportWindow)te).renderTexture ? "window.png" : "window_notex.png";
	}

}
