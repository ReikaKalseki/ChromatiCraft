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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderRift extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityRift te = (TileEntityRift)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		if (te.hasWorldObj()) {
			switch(MinecraftForgeClient.getRenderPass()) {
			case 0:
				//if (te.isLinked())
				this.drawBlackBox(te);
				break;
			case 1:
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.ADDITIVEDARK.apply();
				ForgeDirection dir = te.getSingleDirection();
				if (dir != null) {
					this.renderSideHighlight(te, dir);
				}

				this.renderAura(te);
				BlendMode.DEFAULT.apply();
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
		else {
			this.drawBlackBox(te);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			this.renderAura(te);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	private void renderSideHighlight(TileEntityRift te, ForgeDirection dir) {
		Tessellator v5 = Tessellator.instance;
		v5.setBrightness(240);
		double d = 0.501;
		double x = 0.5+dir.offsetX*d;
		double y = 0.5+dir.offsetY*d;
		double z = 0.5+dir.offsetZ*d;

		IIcon ico = CrystalElement.LIME.getGlowRune();
		ReikaTextureHelper.bindTerrainTexture();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		double h = 0.25;
		if (dir.offsetZ < 0 || dir.offsetY < 0 || dir.offsetX > 0) {
			float p = du;
			du = u;
			u = p;
		}
		v5.startDrawingQuads();
		if (dir.offsetX != 0) {
			v5.addVertexWithUV(x, y-h, z-h, u, dv);
			v5.addVertexWithUV(x, y-h, z+h, du, dv);
			v5.addVertexWithUV(x, y+h, z+h, du, v);
			v5.addVertexWithUV(x, y+h, z-h, u, v);
		}
		else if (dir.offsetY != 0) {
			v5.addVertexWithUV(x-h, y, z-h, u, dv);
			v5.addVertexWithUV(x-h, y, z+h, du, dv);
			v5.addVertexWithUV(x+h, y, z+h, du, v);
			v5.addVertexWithUV(x+h, y, z-h, u, v);
		}
		else if (dir.offsetZ != 0) {
			v5.addVertexWithUV(x-h, y-h, z, u, dv);
			v5.addVertexWithUV(x+h, y-h, z, du, dv);
			v5.addVertexWithUV(x+h, y+h, z, du, v);
			v5.addVertexWithUV(x-h, y+h, z, u, v);
		}
		v5.draw();
	}

	private void drawBlackBox(TileEntityRift te) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.RIFT.getIcon();
		ReikaTextureHelper.bindTerrainTexture();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		float f = 0.625F;
		GL11.glColor4f(f, f, f, 1);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(0, 0, 0, u, dv);

		v5.addVertexWithUV(0, 0, 1, u, v);
		v5.addVertexWithUV(1, 0, 1, du, v);
		v5.addVertexWithUV(1, 1, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, u, dv);

		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(0, 0, 1, u, dv);

		v5.addVertexWithUV(0, 1, 1, u, v);
		v5.addVertexWithUV(1, 1, 1, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(0, 1, 0, u, dv);

		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.addVertexWithUV(0, 1, 0, u, dv);

		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 1, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 0, 0, u, dv);

		v5.draw();
	}

	private void renderAura(TileEntityRift te) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.RIFTHALO.getIcon();
		ReikaTextureHelper.bindTerrainTexture();
		float max = te.hasWorldObj() ? 0.05F : 0.025F;
		float incr = 0.0125F;
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(te.getColor());
		for (float out = 0; out <= max; out += incr) {
			v5.addVertexWithUV(-out, 1+out, -out, u, v);
			v5.addVertexWithUV(1+out, 1+out, -out, du, v);
			v5.addVertexWithUV(1+out, -out, -out, du, dv);
			v5.addVertexWithUV(-out, -out, -out, u, dv);

			v5.addVertexWithUV(-out, -out, 1+out, u, v);
			v5.addVertexWithUV(1+out, -out, 1+out, du, v);
			v5.addVertexWithUV(1+out, 1+out, 1+out, du, dv);
			v5.addVertexWithUV(-out, 1+out, 1+out, u, dv);

			v5.addVertexWithUV(-out, -out, -out, u, v);
			v5.addVertexWithUV(1+out, -out, -out, du, v);
			v5.addVertexWithUV(1+out, -out, 1+out, du, dv);
			v5.addVertexWithUV(-out, -out, 1+out, u, dv);

			v5.addVertexWithUV(-out, 1+out, 1+out, u, v);
			v5.addVertexWithUV(1+out, 1+out, 1+out, du, v);
			v5.addVertexWithUV(1+out, 1+out, -out, du, dv);
			v5.addVertexWithUV(-out, 1+out, -out, u, dv);

			v5.addVertexWithUV(-out, -out, -out, u, v);
			v5.addVertexWithUV(-out, -out, 1+out, du, v);
			v5.addVertexWithUV(-out, 1+out, 1+out, du, dv);
			v5.addVertexWithUV(-out, 1+out, -out, u, dv);

			v5.addVertexWithUV(1+out, 1+out, -out, u, v);
			v5.addVertexWithUV(1+out, 1+out, 1+out, du, v);
			v5.addVertexWithUV(1+out, -out, 1+out, du, dv);
			v5.addVertexWithUV(1+out, -out, -out, u, dv);
		}

		v5.draw();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}
