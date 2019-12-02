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
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityVoidMonsterTrap;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderVoidMonsterTrap extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityVoidMonsterTrap te = (TileEntityVoidMonsterTrap)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (te.isInWorld()) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			double dist = ep.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
			float f = 0;
			if (dist <= 16) {
				f = 1;
			}
			else if (dist <= 32) {
				f = 1-(float)((dist-16D)/16D);
			}
			ChromaShaders.VOIDTRAP.clearOnRender = true;
			ChromaShaders.VOIDTRAP.setIntensity(f);
			ChromaShaders.VOIDTRAP.getShader().setFocus(te);
			ChromaShaders.VOIDTRAP.getShader().setMatricesToCurrent();
			ChromaShaders.VOIDTRAP.getShader().setField("distance", dist*dist);
			ChromaShaders.VOIDTRAP.getShader().setField("rotation", te.getShaderRotation());
		}

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld())
			this.drawInner(te, par8);

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawInner(TileEntityVoidMonsterTrap te, float par8) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon[] icons = {ChromaIcons.SUNFLARE.getIcon(), ChromaIcons.CELLFLARE.getIcon(), ChromaIcons.ECLIPSEFLARE.getIcon()};
		double[] sz = {te.isInWorld() ? 4 : 1.25, te.isInWorld() ? 2.75 : 1.25, te.isInWorld() ? 3 : 0.9375};
		float[] br = {1, 1, 0.75F};

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			s = 0.5;
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}

		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);

		double t = te.isInWorld() ? (te.getTicksExisted()+par8) : System.currentTimeMillis()/50D;

		double s2 = 2+0.5*Math.sin(t/90D);
		//float f = te.isInWorld() ? ((float)(Math.sin(t/5.4D)+Math.cos(t/3.9D))*15-14) : 0;
		//if (f > 1)
		//	f = 1;
		float f = te.getFlashBrightness();

		for (int i = 0; i < icons.length; i++) {
			int c = ReikaColorAPI.getModifiedHue(0xff0000, 265+(int)(15*Math.sin(t/30D+i)));
			c = ReikaColorAPI.getModifiedSat(c, 0.9F+0.1F*(float)Math.sin(t/20D+i/4D));
			c = ReikaColorAPI.getModifiedSat(c, 1-0.15F*i);
			if (f > 0)
				c = ReikaColorAPI.getModifiedSat(c, 1-f);
			IIcon ico = icons[i];
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			float uu = du-u;
			float vv = dv-v;

			double s3 = s2*sz[i];

			GL11.glPushMatrix();
			GL11.glScaled(s3, s3, s3);
			if (i == 1)
				GL11.glRotated((-System.currentTimeMillis()/50D)%360D, 0, 0, 1);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, br[i]));
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

}
