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

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.Models.ModelAspectFormer;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderAspectFormer extends ChromaRenderBase {

	private final ModelAspectFormer model = new ModelAspectFormer();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "aspect.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAspectFormer te = (TileEntityAspectFormer)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			if (MinecraftForgeClient.getRenderPass() == 1) {
				this.renderAspect(te, par2, par4, par6, par8);
				this.renderAspectTransfer(te, par2, par4, par6, par8);
			}
		}
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model, te.isInWorld());

		GL11.glPopMatrix();
	}

	private void renderAspect(TileEntityAspectFormer te, double par2, double par4, double par6, float ptick) {
		Aspect a = te.getAspect();
		if (a != null) {
			Tessellator v5 = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			GL11.glPushMatrix();

			double h = 0.2875;
			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = ChromaIcons.BIGFLARE.getIcon();
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			RenderManager rm = RenderManager.instance;
			GL11.glTranslated(0.5, h, 0.5);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			v5.startDrawingQuads();
			v5.setBrightness(240);

			double s = 0.375;
			v5.setColorOpaque_I(a.getColor());
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.addVertexWithUV(+s, s, 0, du, dv);
			v5.addVertexWithUV(+s, -s, 0, du, v);
			v5.addVertexWithUV(-s, -s, 0, u, v);

			s = 0.1875;
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.addVertexWithUV(+s, s, 0, du, dv);
			v5.addVertexWithUV(+s, -s, 0, du, v);
			v5.addVertexWithUV(-s, -s, 0, u, v);

			v5.draw();
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);

			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

	private void renderAspectTransfer(TileEntityAspectFormer te, double par2, double par4, double par6, float ptick) {
		if (te.isActive()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			GL11.glPushMatrix();

			Aspect a = te.getAspect();
			Tessellator v5 = Tessellator.instance;
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);

			//float w = GL11.glGetFloat(GL11.GL_POINT_SIZE);

			int ct = 0;
			double r = 0.5;
			for (double dy = -0.0625; dy > -0.9375; dy -= 0.0625) {
				//GL11.glPointSize(6);
				//v5.startDrawing(GL11.GL_POINTS);
				//v5.setBrightness(240);
				//v5.setColorOpaque_I(a.getColor());
				double ang = Math.toRadians(9*(te.getTicksExisted()+ct*6+ptick));
				double dx = 0.5+r*Math.sin(ang);
				double dz = 0.5+r*Math.cos(ang);
				//v5.addVertex(dx, dy, dz);
				//v5.draw();

				//GL11.glPointSize(3);
				//v5.startDrawing(GL11.GL_POINTS);
				//v5.setColorOpaque_I(0xffffff);
				//v5.addVertex(dx, dy, dz);
				//v5.draw();

				double px = dx+te.xCoord;
				double py = dy+te.yCoord;
				double pz = dz+te.zCoord;
				EntityFX fx = new EntityCenterBlurFX(CrystalElement.WHITE, te.worldObj, px, py, pz, 0, 0, 0).setLife(4).setScale(2F).setColor(a.getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				ct++;
			}

			//GL11.glPointSize(w);

			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

}
