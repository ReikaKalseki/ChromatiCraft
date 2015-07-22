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
import Reika.ChromatiCraft.Models.ModelFarmer;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityFarmer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderFarmer extends ChromaRenderBase {

	private final ModelFarmer model = new ModelFarmer();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "farmer.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFarmer te = (TileEntityFarmer)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (te.isInWorld()) {
			int a = 0;
			switch (te.getFacing()) {
			case EAST:
				a = 90;
				GL11.glTranslated(0, 0, 1);
				break;
			case NORTH:
				a = 180;
				GL11.glTranslated(1, 0, 1);
				break;
			case SOUTH:
				break;
			case WEST:
				a = -90;
				GL11.glTranslated(1, 0, 0);
				break;
			default:
				break;
			}
			GL11.glRotated(a, 0, 1, 0);
		}
		this.renderModel(te, model);
		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		//double a = (System.currentTimeMillis()%1000/1000D);
		double u = ChromaIcons.BLUEFIRE.getIcon().getMinU();//Math.sin(a);
		double v = ChromaIcons.BLUEFIRE.getIcon().getMinV();//Math.cos(a);
		//double s = 0.125+0.0625*Math.sin(System.currentTimeMillis()/200D);
		double du = ChromaIcons.BLUEFIRE.getIcon().getMaxU();//u+s;
		double dv = ChromaIcons.BLUEFIRE.getIcon().getMaxV();//v+s;
		double d = 0.95;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.GREEN.getColor());
		v5.addVertexWithUV(0, 0, d, u, v);
		v5.addVertexWithUV(1, 0, d, du, v);
		v5.addVertexWithUV(1, 1, d, du, dv);
		v5.addVertexWithUV(0, 1, d, u, dv);

		v5.addVertexWithUV(0, 0, d, du, dv);
		v5.addVertexWithUV(1, 0, d, u, dv);
		v5.addVertexWithUV(1, 1, d, u, v);
		v5.addVertexWithUV(0, 1, d, du, v);

		v5.addVertexWithUV(0, 0, d, u, dv);
		v5.addVertexWithUV(1, 0, d, u, v);
		v5.addVertexWithUV(1, 1, d, du, v);
		v5.addVertexWithUV(0, 1, d, du, dv);

		v5.addVertexWithUV(0, 0, d, du, v);
		v5.addVertexWithUV(1, 0, d, du, dv);
		v5.addVertexWithUV(1, 1, d, u, dv);
		v5.addVertexWithUV(0, 1, d, u, v);

		u = ChromaIcons.RIFTHALO.getIcon().getMinU();//Math.sin(a);
		v = ChromaIcons.RIFTHALO.getIcon().getMinV();//Math.cos(a);
		du = ChromaIcons.RIFTHALO.getIcon().getMaxU();//u+s;
		dv = ChromaIcons.RIFTHALO.getIcon().getMaxV();//v+s;

		d = 0.995;

		v5.addVertexWithUV(1-d, 0.1875, 0.125, u, v);
		v5.addVertexWithUV(1-d, 0.1875, 0.875, du, v);
		v5.addVertexWithUV(1-d, 0.8125, 0.875, du, dv);
		v5.addVertexWithUV(1-d, 0.8125, 0.125, u, dv);

		v5.addVertexWithUV(d, 0.8125, 0.125, u, dv);
		v5.addVertexWithUV(d, 0.8125, 0.875, du, dv);
		v5.addVertexWithUV(d, 0.1875, 0.875, du, v);
		v5.addVertexWithUV(d, 0.1875, 0.125, u, v);


		u = ChromaIcons.SPARKLE.getIcon().getMinU();//Math.sin(a);
		v = ChromaIcons.SPARKLE.getIcon().getMinV();//Math.cos(a);
		du = ChromaIcons.SPARKLE.getIcon().getMaxU();//u+s;
		dv = ChromaIcons.SPARKLE.getIcon().getMaxV();//v+s;

		d = 0.9975;

		v5.addVertexWithUV(1-d, 0.1875, 0.125, u, v);
		v5.addVertexWithUV(1-d, 0.1875, 0.875, du, v);
		v5.addVertexWithUV(1-d, 0.8125, 0.875, du, dv);
		v5.addVertexWithUV(1-d, 0.8125, 0.125, u, dv);

		v5.addVertexWithUV(d, 0.8125, 0.125, u, dv);
		v5.addVertexWithUV(d, 0.8125, 0.875, du, dv);
		v5.addVertexWithUV(d, 0.1875, 0.875, du, v);
		v5.addVertexWithUV(d, 0.1875, 0.125, u, v);

		v5.addVertexWithUV(0, 0, d, u, v);
		v5.addVertexWithUV(1, 0, d, du, v);
		v5.addVertexWithUV(1, 1, d, du, dv);
		v5.addVertexWithUV(0, 1, d, u, dv);

		v5.draw();
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

}
