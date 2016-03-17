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
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCloakingTower;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderCloakingTower extends ChromaRenderBase {

	private static final ColorBlendList blends = new ColorBlendList(10).addColor(0xffffff).addColor(0xff0000).addColor(0xffffff).addColor(0x00ff00).addColor(0xffffff).addColor(0x0000ff);

	private static final ChromaIcons[] icons = {
		ChromaIcons.CAUSTICS,
		ChromaIcons.CAUSTICS_GENTLE
	};

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCloakingTower te = (TileEntityCloakingTower)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		if (te.isInWorld()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			if (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles())
				this.renderGlowTower(te, par2, par4, par6);
		}
		else {
			GL11.glTranslated(-1.0625, -1.25, 0);
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			double s = 1.5;
			GL11.glScaled(s, s, s);
			GL11.glTranslated(0, 0, 2);
			IIcon ico = ChromaIcons.CAUSTICS.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			Tessellator v5 = Tessellator.instance;
			ReikaTextureHelper.bindTerrainTexture();

			v5.startDrawingQuads();

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.draw();

			v5.startDrawing(GL11.GL_LINE_LOOP);

			GL11.glDisable(GL11.GL_TEXTURE_2D);

			v5.setColorOpaque_I(0x909090);

			v5.addVertex(0, 0, 0);
			v5.addVertex(1, 0, 0);
			v5.addVertex(1, 1, 0);
			v5.addVertex(0, 1, 0);

			v5.draw();
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderGlowTower(TileEntityCloakingTower tile, double par2, double par4, double par6) {
		ReikaTextureHelper.bindTerrainTexture();

		Tessellator v5 = Tessellator.instance;

		double h = 4.5;
		double d = 1;

		boolean flag = tile.isActive() || StructureRenderer.isRenderingTiles();

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		//GL11.glDisable(GL11.GL_BLEND);
		//ReikaTextureHelper.bindEnchantmentTexture();
		//ReikaRenderHelper.renderTube(0, -h, 0, 0, h, 0, 0xffffffff, 0xffffffff, 0.375, 0.375);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");
		double t = (((System.currentTimeMillis()/600D)%360+360)%360)/30D+Math.abs(tile.hashCode());
		int c = flag ? ReikaColorAPI.GStoHex((int)(222+32*Math.sin(t*8))) : ReikaColorAPI.GStoHex((int)(128+64*Math.sin(t*32)));
		DecimalPosition p1 = new DecimalPosition(tile);
		DecimalPosition p2 = new DecimalPosition(tile).offset(0, h, 0);
		ChromaFX.drawEnergyTransferBeam(p1, p2, c, 0.35, 0.35, (byte)6, t);
		ChromaFX.drawEnergyTransferBeam(p1, p2.offset(0, -2*h, 0), c, 0.35, 0.35, (byte)6, t);
		GL11.glPopAttrib();

		int f1 = flag ? 0 : 1;

		t = Math.abs((System.currentTimeMillis()/600D)/30D+tile.hashCode());

		for (double i = -h; i < h; i += d) {
			for (int f = f1; f < 2; f++) {

				c = flag ? blends.getColor(t*8) : 0xffffff;

				if (f > 0) {
					c = ReikaColorAPI.mixColors(c, 0xffffff, 0.25F);
				}

				IIcon ico = icons[f].getIcon();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				double w = tile.getWidth(i)*(1+f/2D);
				double wp = tile.getWidth(i+d);
				double wn = tile.getWidth(i-d);

				double wap = (w+wp)/2;
				double wan = (w+wn)/2;

				v5.startDrawingQuads();
				v5.setBrightness(240);
				v5.setColorOpaque_I(c);

				v5.addVertexWithUV(-wan, i, wan, u, v);
				v5.addVertexWithUV(wan, i, wan, du, v);
				v5.addVertexWithUV(wap, i+d, wap, du, dv);
				v5.addVertexWithUV(-wap, i+d, wap, u, dv);

				v5.addVertexWithUV(-wap, i+d, -wap, du, dv);
				v5.addVertexWithUV(wap, i+d, -wap, u, dv);
				v5.addVertexWithUV(wan, i, -wan, u, v);
				v5.addVertexWithUV(-wan, i, -wan, du, v);

				v5.addVertexWithUV(wap, i+d, -wap, du, dv);
				v5.addVertexWithUV(wap, i+d, wap, u, dv);
				v5.addVertexWithUV(wan, i, wan, u, v);
				v5.addVertexWithUV(wan, i, -wan, du, v);

				v5.addVertexWithUV(-wan, i, -wan, u, v);
				v5.addVertexWithUV(-wan, i, wan, du, v);
				v5.addVertexWithUV(-wap, i+d, wap, du, dv);
				v5.addVertexWithUV(-wap, i+d, -wap, u, dv);
				v5.draw();

				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				//GL11.glDisable(GL11.GL_BLEND);
				//BlendMode.DEFAULT.apply();
				ReikaTextureHelper.bindEnchantmentTexture();
				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				//ReikaRenderHelper.disableEntityLighting();
				int a = f == 0 ? 0xa0 : 0x50;
				int c1 = ReikaColorAPI.GStoHex(a) | (a << 24);
				int c2 = ReikaColorAPI.GStoHex(a) | (a << 24);
				double r = 0.03125;
				ReikaRenderHelper.renderTube(-wan, i, wan, -wap, i+d, wap, c1, c2, r, r);
				ReikaRenderHelper.renderTube(wan, i, wan, wap, i+d, wap, c1, c2, r, r);
				ReikaRenderHelper.renderTube(-wan, i, -wan, -wap, i+d, -wap, c1, c2, r, r);
				ReikaRenderHelper.renderTube(wan, i, -wan, wap, i+d, -wap, c1, c2, r, r);
				GL11.glPopAttrib();

			}
		}

	}

}
