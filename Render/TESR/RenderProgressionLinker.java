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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelProgressionLinker;
import Reika.ChromatiCraft.TileEntity.TileEntityProgressionLinker;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderProgressionLinker extends ChromaRenderBase {

	private static final RayTracer trace = RayTracer.getVisualLOS();

	private final ModelProgressionLinker model = new ModelProgressionLinker();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "progresslink.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityProgressionLinker te = (TileEntityProgressionLinker)tile;
		if (MinecraftForgeClient.getRenderPass() != 1)
			;//return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		ReikaRenderHelper.disableEntityLighting();
		this.bindTextureByName(this.getTextureFolder()+this.getImageFileName(null));
		this.renderModel(te, model);

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);

		if (te.hasStructure()) {
			if (te.hasPlayer()) {
				GL11.glTranslated(0.5, 0.75, 0.5);
				double h = 1.5;

				double dy = 0.1875*Math.sin(System.currentTimeMillis()/400D);
				//RenderDataNode.renderPrism(System.currentTimeMillis()/20D, Tessellator.instance, 1, h, 0.5+dy);
				if (this.checkRayTrace(te)) {
					GL11.glTranslated(0, dy, 0);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					RenderDataNode.renderFlare(Tessellator.instance, 1, true);
				}
			}
			else {
				GL11.glTranslated(0.5, 0, 0.5);
				GL11.glDisable(GL11.GL_CULL_FACE);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/lightcone-foggy.png");
				long tick = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
				int idx = (int)(tick%48);
				double u = (idx%12)/12D;
				double v = (idx/12)/4D;
				double du = u+1/12D;
				double dv = v+1/4D;
				Tessellator v5 = Tessellator.instance;

				double h = 2.5;
				double f = 0.5+0.5*Math.sin(tick/8D)+0.125*Math.sin((tick+300)/3D)+0.25*Math.cos((tick+20)/7D);
				int c = ReikaColorAPI.mixColors(0x22aaff, 0x5588ff, (float)MathHelper.clamp_double(f, 0, 1));
				float fd = te.getFailFade();
				if (fd > 0) {
					c = ReikaColorAPI.mixColors(c, 0xff0000, 1-fd);
				}

				//for (double a = 0; a < 360; a += 120) {
				GL11.glPushMatrix();
				//GL11.glRotated(a, 0, 1, 0);
				GL11.glRotated(-RenderManager.instance.playerViewY, 0, 1, 0);
				v5.startDrawingQuads();
				v5.setColorOpaque_I(c);
				v5.addVertexWithUV(-0.5, h, 0, u, v);
				v5.addVertexWithUV(0.5, h, 0, du, v);
				v5.addVertexWithUV(0.5, 0, 0, du, dv);
				v5.addVertexWithUV(-0.5, 0, 0, u, dv);
				v5.draw();
				GL11.glPopMatrix();
				//}
			}
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private boolean checkRayTrace(TileEntityProgressionLinker te) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double r = 0.5;
		for (double i = -r; i <= r; i += r) {
			for (double k = -r; k <= r; k += r) {
				trace.setOrigins(te.xCoord+i, te.yCoord, te.zCoord, ep.posX, ep.posY, ep.posZ);
				if (trace.isClearLineOfSight(te.worldObj))
					return true;
			}
		}
		return false;
	}
}
