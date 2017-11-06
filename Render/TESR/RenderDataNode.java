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

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.ScriptLocations;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.InWorldScriptRenderer;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderDataNode extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityDataNode te = (TileEntityDataNode)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2+0.5, par4+0.5, par6+0.5);

		Tessellator v5 = Tessellator.instance;

		if (MinecraftForgeClient.getRenderPass() == 0 || StructureRenderer.isRenderingTiles() || !te.isInWorld()) {
			this.renderTower(te, v5);
		}
		if (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles() || !te.isInWorld()) {

			if (!te.hasBeenScanned(Minecraft.getMinecraft().thePlayer)) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.ADDITIVEDARK.apply();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glDepthMask(false);
				//GL11.glDisable(GL11.GL_TEXTURE_2D);

				GL11.glPushMatrix();
				this.renderPrism(te, v5);
				if (te.isInWorld()) {
					this.renderSymbol(te, v5);
					this.renderFlare(te, v5);
				}
				GL11.glPopMatrix();

				if (te.isInWorld() && ScriptLocations.TOWER.isEnabled() && MinecraftForgeClient.getRenderPass() == 1 && Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) < 4096) {
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					InWorldScriptRenderer.renderTowerScript(te, par8, v5, 0.03125/2, 4096);
					GL11.glPopAttrib();
				}

				GL11.glPopAttrib();
			}

		}

		GL11.glPopMatrix();
	}

	private void renderTower(TileEntityDataNode te, Tessellator v5) {
		int l = te.isInWorld() ? te.getBlockType().getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord) : 240;

		double t = 0.125;

		if (te.isInWorld()) {
			this.renderBase(te, v5, l, t);
		}

		GL11.glPushMatrix();
		GL11.glRotated(te.getRotation(), 0, 1, 0);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/datanode.png");

		double h1 = te.isInWorld() ? 2.5 : 1.25;
		double h2 = te.isInWorld() ? 1.5 : 1.25;
		double R = 0.125;
		double r = 0.03125/2;
		double dy = te.isInWorld() ? 0.5+te.getExtension1()-1 : -0.625;
		double dy2 = te.isInWorld() ? 0.5+te.getExtension1()+te.getExtension2() : -0.5;

		double af = 1D/Math.sin(Math.toRadians(60));

		double oz = -0.5;

		IIcon ico = ChromaBlocks.PYLONSTRUCT.getBlockInstance().getIcon(0, 0);
		float iw = 64;
		float ih = 96;

		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		double w = R*af*1.5+r*0;
		double oc = R*af*0.5;

		for (int i = 0; i < 360; i += 120) {

			float u1a = 2.5F/iw;
			float v1a = 31/ih;
			float u1b = 9.5F/iw;
			float v1b = 17/ih;
			float u2a = 46/iw;
			float v2a = 31/ih;
			float u2b = 38/iw;
			float v2b = 17/ih;

			GL11.glPushMatrix();
			GL11.glRotated(i, 0, 1, 0);

			v5.startDrawingQuads();
			v5.setBrightness(l);
			v5.setColorOpaque_F(1, 1, 1);
			v5.setNormal(0, 1, 0);

			v5.addVertexWithUV(-R*af, dy+h1, oz+R, u1b, v1b);
			v5.addVertexWithUV(R*af, dy+h1, oz+R, u2b, v2b);
			v5.addVertexWithUV(w, dy+h1, oz, u2a, v2a);
			v5.addVertexWithUV(-w, dy+h1, oz, u1a, v1a);

			v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			v5.setNormal(0, 0.5F, 0);

			v5.addVertexWithUV(-w, dy, oz, u1a, v1a);
			v5.addVertexWithUV(w, dy, oz, u2a, v2a);
			v5.addVertexWithUV(R*af, dy, oz+R, u2b, v2b);
			v5.addVertexWithUV(-R*af, dy, oz+R, u1b, v1b);

			v5.setColorOpaque_F(0.675F, 0.675F, 0.675F);
			v5.setNormal(0, 0.675F, 0);

			u1a = 3/iw;
			v1a = 34/ih;
			u1b = 3/iw;
			v1b = 94/ih;
			u2a = 45/iw;
			v2a = 34/ih;
			u2b = 45/iw;
			v2b = 94/ih;
			float u16a = 31/iw;
			float v16a = 34/ih;
			float u16b = 31/iw;
			float v16b = 94/ih;
			float u13a = 17/iw;
			float v13a = 34/ih;
			float u13b = 17/iw;
			float v13b = 94/ih;

			v5.addVertexWithUV(w, dy+h1, oz, u2a, v2a);
			v5.addVertexWithUV(w, dy, oz, u2b, v2b);
			v5.addVertexWithUV(-w, dy, oz, u1b, v1b);
			v5.addVertexWithUV(-w, dy+h1, oz, u1a, v1a);

			v5.addVertexWithUV(-R*af, dy+h1, oz+R, u1a, v1a);
			v5.addVertexWithUV(-R*af, dy, oz+R, u1b, v1b);
			v5.addVertexWithUV(R*af, dy, oz+R, u16b, v16b);
			v5.addVertexWithUV(R*af, dy+h1, oz+R, u16a, v16a);

			v5.addVertexWithUV(-w, dy+h1, oz, u13a, v13a);
			v5.addVertexWithUV(-w, dy, oz, u13b, v13b);
			v5.addVertexWithUV(-R*af, dy, oz+R, u1b, v1b);
			v5.addVertexWithUV(-R*af, dy+h1, oz+R, u1a, v1a);

			v5.addVertexWithUV(R*af, dy+h1, oz+R, u1a, v1a);
			v5.addVertexWithUV(R*af, dy, oz+R, u1b, v1b);
			v5.addVertexWithUV(w, dy, oz, u13b, v13b);
			v5.addVertexWithUV(w, dy+h1, oz, u13a, v13a);


			//Row 2
			u1a = 9.5F/iw;
			v1a = 15/ih;
			u1b = 16.5F/iw;
			v1b = 1/ih;
			u2a = 39/iw;
			v2a = 15/ih;
			u2b = 30/iw;
			v2b = 1/ih;

			v5.setColorOpaque_F(1, 1, 1);
			v5.setNormal(0, 1, 0);

			v5.addVertexWithUV(-oc, dy2+h2, oz+2*R+r/2, u1b, v1b);
			v5.addVertexWithUV(oc, dy2+h2, oz+2*R+r/2, u2b, v2b);
			v5.addVertexWithUV(R*af, dy2+h2, oz+R+r/2, u2a, v2a);
			v5.addVertexWithUV(-R*af, dy2+h2, oz+R+r/2, u1a, v1a);

			v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			v5.setNormal(0, 0.5F, 0);

			v5.addVertexWithUV(-R*af, dy2, oz+R+r/2, u1a, v1a);
			v5.addVertexWithUV(R*af, dy2, oz+R+r/2, u2a, v2a);
			v5.addVertexWithUV(oc, dy2, oz+2*R+r/2, u2b, v2b);
			v5.addVertexWithUV(-oc, dy2, oz+2*R+r/2, u1b, v1b);

			v5.setColorOpaque_F(0.675F, 0.675F, 0.675F);
			v5.setNormal(0, 0.675F, 0);

			u1a = 3/iw;
			v1a = 34/ih;
			u1b = 3/iw;
			v1b = 94/ih;
			u2a = 45/iw;
			v2a = 34/ih;
			u2b = 45/iw;
			v2b = 94/ih;
			u16a = 31/iw;
			v16a = 34/ih;
			u16b = 31/iw;
			v16b = 94/ih;
			u13a = 17/iw;
			v13a = 34/ih;
			u13b = 17/iw;
			v13b = 94/ih;

			v5.addVertexWithUV(R*af, dy2+h2, oz+R+r/2, u16a, v16a);
			v5.addVertexWithUV(R*af, dy2, oz+R+r/2, u16b, v16b);
			v5.addVertexWithUV(-R*af, dy2, oz+R+r/2, u1b, v1b);
			v5.addVertexWithUV(-R*af, dy2+h2, oz+R+r/2, u1a, v1a);

			v5.addVertexWithUV(-oc, dy2+h2, oz+2*R+r/2, u1a, v1a);
			v5.addVertexWithUV(-oc, dy2, oz+2*R+r/2, u1b, v1b);
			v5.addVertexWithUV(oc, dy2, oz+2*R+r/2, u13b, v13b);
			v5.addVertexWithUV(oc, dy2+h2, oz+2*R+r/2, u13a, v13a);

			v5.addVertexWithUV(-R*af, dy2+h2, oz+R+r/2, u13a, v13a);
			v5.addVertexWithUV(-R*af, dy2, oz+R+r/2, u13b, v13b);
			v5.addVertexWithUV(-oc, dy2, oz+2*R+r/2, u1b, v1b);
			v5.addVertexWithUV(-oc, dy2+h2, oz+2*R+r/2, u1a, v1a);

			v5.addVertexWithUV(oc, dy2+h2, oz+2*R+r/2, u1a, v1a);
			v5.addVertexWithUV(oc, dy2, oz+2*R+r/2, u1b, v1b);
			v5.addVertexWithUV(R*af, dy2, oz+R+r/2, u13b, v13b);
			v5.addVertexWithUV(R*af, dy2+h2, oz+R+r/2, u13a, v13a);

			v5.draw();

			GL11.glPopMatrix();
		}

		ReikaTextureHelper.bindTerrainTexture();

		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);

		double t2 = t*0.75;
		double t3 = t*1.5;

		double ut3 = u+uu*t3;
		double vt3 = v+vv*t3;
		double ut3b = du-uu*t3;
		double vt3b = dv-vv*t3;

		double dy3 = dy+1.5;

		v5.startDrawingQuads();
		v5.setBrightness(l);

		v5.setColorOpaque_F(1, 1, 1);
		v5.setNormal(0, 1, 0);

		v5.addVertexWithUV(0+t2,	dy3, 1-t2, u, dv);
		v5.addVertexWithUV(0+t2+t3, dy3, 1-t2-t3, ut3, vt3b);
		v5.addVertexWithUV(0+t2+t3, dy3, 0+t2+t3, ut3, vt3);
		v5.addVertexWithUV(0+t2,	dy3, 0+t2, u, v);

		v5.addVertexWithUV(1-t2,	dy3, 0+t2, du, v);
		v5.addVertexWithUV(1-t2-t3, dy3, 0+t2+t3, ut3b, vt3);
		v5.addVertexWithUV(1-t2-t3, dy3, 1-t2-t3, ut3b, vt3b);
		v5.addVertexWithUV(1-t2,	dy3, 1-t2, du, dv);

		v5.addVertexWithUV(0+t2,	dy3, 0+t2, u, v);
		v5.addVertexWithUV(0+t2+t3, dy3, 0+t2+t3, ut3, vt3);
		v5.addVertexWithUV(1-t2-t3, dy3, 0+t2+t3, ut3b, vt3);
		v5.addVertexWithUV(1-t2,	dy3, 0+t2, du, v);

		v5.addVertexWithUV(1-t2,	dy3, 1-t2, du, v);
		v5.addVertexWithUV(1-t2-t3, dy3, 1-t2-t3, ut3b, vt3);
		v5.addVertexWithUV(0+t2+t3, dy3, 1-t2-t3, ut3, vt3);
		v5.addVertexWithUV(0+t2,	dy3, 1-t2, u, v);

		v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		v5.setNormal(0, 0.5F, 0);

		v5.addVertexWithUV(0+t2,	dy3-t3, 0+t2, u, v);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 0+t2+t3, ut3, vt3);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 1-t2-t3, ut3, vt3b);
		v5.addVertexWithUV(0+t2,	dy3-t3, 1-t2, u, dv);

		v5.addVertexWithUV(1-t2,	dy3-t3, 1-t2, du, dv);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 1-t2-t3, ut3b, vt3b);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 0+t2+t3, ut3b, vt3);
		v5.addVertexWithUV(1-t2,	dy3-t3, 0+t2, du, v);

		v5.addVertexWithUV(1-t2,	dy3-t3, 0+t2, du, v);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 0+t2+t3, ut3b, vt3);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 0+t2+t3, ut3, vt3);
		v5.addVertexWithUV(0+t2,	dy3-t3, 0+t2, u, v);

		v5.addVertexWithUV(0+t2,	dy3-t3, 1-t2, u, v);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 1-t2-t3, ut3, vt3);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 1-t2-t3, ut3b, vt3);
		v5.addVertexWithUV(1-t2,	dy3-t3, 1-t2, du, v);

		v5.setColorOpaque_F(0.675F, 0.675F, 0.675F);
		v5.setNormal(0, 0.675F, 0);

		v5.addVertexWithUV(0+t2,	dy3,	0+t2, u, v);
		v5.addVertexWithUV(0+t2, 	dy3-t3, 0+t2, du, v);
		v5.addVertexWithUV(0+t2, 	dy3-t3, 1-t2, du, dv);
		v5.addVertexWithUV(0+t2,	dy3,	1-t2, u, dv);

		v5.addVertexWithUV(0+t2+t3,	dy3,	1-t2-t3, u, dv);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 1-t2-t3, du, dv);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 0+t2+t3, du, v);
		v5.addVertexWithUV(0+t2+t3,	dy3,	0+t2+t3, u, v);

		v5.addVertexWithUV(1-t2,	dy3,	1-t2, u, dv);
		v5.addVertexWithUV(1-t2, 	dy3-t3, 1-t2, du, dv);
		v5.addVertexWithUV(1-t2, 	dy3-t3, 0+t2, du, v);
		v5.addVertexWithUV(1-t2,	dy3,	0+t2, u, v);

		v5.addVertexWithUV(1-t2-t3,	dy3,	0+t2+t3, u, v);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 0+t2+t3, du, v);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 1-t2-t3, du, dv);
		v5.addVertexWithUV(1-t2-t3,	dy3,	1-t2-t3, u, dv);

		v5.addVertexWithUV(1-t2,	dy3,	0+t2, u, dv);
		v5.addVertexWithUV(1-t2, 	dy3-t3, 0+t2, du, dv);
		v5.addVertexWithUV(0+t2, 	dy3-t3, 0+t2, du, v);
		v5.addVertexWithUV(0+t2,	dy3,	0+t2, u, v);

		v5.addVertexWithUV(1-t2-t3,	dy3,	1-t2-t3, u, dv);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 1-t2-t3, du, dv);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 1-t2-t3, du, v);
		v5.addVertexWithUV(0+t2+t3,	dy3,	1-t2-t3, u, v);

		v5.addVertexWithUV(0+t2,	dy3,	1-t2, u, v);
		v5.addVertexWithUV(0+t2, 	dy3-t3, 1-t2, du, v);
		v5.addVertexWithUV(1-t2, 	dy3-t3, 1-t2, du, dv);
		v5.addVertexWithUV(1-t2,	dy3,	1-t2, u, dv);

		v5.addVertexWithUV(0+t2+t3,	dy3,	0+t2+t3, u, v);
		v5.addVertexWithUV(0+t2+t3, dy3-t3, 0+t2+t3, du, v);
		v5.addVertexWithUV(1-t2-t3, dy3-t3, 0+t2+t3, du, dv);
		v5.addVertexWithUV(1-t2-t3,	dy3,	0+t2+t3, u, dv);

		v5.draw();

		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	private void renderBase(TileEntityDataNode te, Tessellator v5, int l, double t) {
		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);

		GL11.glTranslated(0, 1.05, 0);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(1, BlockType.MOSS.metadata);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		v5.startDrawingQuads();
		v5.setBrightness(l);

		double s = 0.0625;//0.125;//(1-i)/16D;

		double ut = u+uu*t;
		double vt = v+vv*t*2;
		double ut2 = du-uu*t;
		double vt2 = dv-vv*t*2;

		double dh = -0.125;

		v5.setColorOpaque_F(1, 1, 1);
		v5.setNormal(0, 1, 0);
		/*
		v5.addVertexWithUV(0-s, te.getExtension0(), 0-s, u, v);
		v5.addVertexWithUV(0-s, te.getExtension0(), 1+s, u, dv);
		v5.addVertexWithUV(1+s, te.getExtension0(), 1+s, du, dv);
		v5.addVertexWithUV(1+s, te.getExtension0(), 0-s, du, v);
		 */

		v5.addVertexWithUV(0-s, te.getExtension0(), 0-s, u, v);
		v5.addVertexWithUV(t, te.getExtension0()+dh, t, ut, vt);
		v5.addVertexWithUV(1+s-t, te.getExtension0()+dh, t, ut2, vt);
		v5.addVertexWithUV(1+s, te.getExtension0(), 0-s, du, v);

		v5.addVertexWithUV(1+s, te.getExtension0(), 1+s, du, v);
		v5.addVertexWithUV(1+s-t, te.getExtension0()+dh, 1+s-t, ut2, vt);
		v5.addVertexWithUV(t, te.getExtension0()+dh, 1+s-t, ut, vt);
		v5.addVertexWithUV(0-s, te.getExtension0(), 1+s, u, v);

		v5.addVertexWithUV(0-s, te.getExtension0(), 1+s, du, v);
		v5.addVertexWithUV(t, te.getExtension0()+dh, 1+s-t, ut2, vt);
		v5.addVertexWithUV(t, te.getExtension0()+dh, t, ut, vt);
		v5.addVertexWithUV(0-s, te.getExtension0(), 0-s, u, v);

		v5.addVertexWithUV(1+s, te.getExtension0(), 0-s, u, v);
		v5.addVertexWithUV(1+s-t, te.getExtension0()+dh, t, ut, vt);
		v5.addVertexWithUV(1+s-t, te.getExtension0()+dh, 1+s-t, ut2, vt);
		v5.addVertexWithUV(1+s, te.getExtension0(), 1+s, du, v);

		v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		v5.setNormal(0, 0.5F, 0);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(0, 0, 1, u, dv);
		v5.addVertexWithUV(0, 0, 0, u, v);

		for (int i = 0; i < te.getExtension0()+1; i++) {
			//s = (1-i)/16D;
			double y = -i+te.getExtension0()-1;

			v5.setColorOpaque_F(0.675F, 0.675F, 0.675F);
			v5.setNormal(0, 0.675F, 0);

			v5.addVertexWithUV(0-s, y+0, 0-s, u, v);
			v5.addVertexWithUV(0-s, y+0, 1+s, u, dv);
			v5.addVertexWithUV(0-s, y+1, 1+s, du, dv);
			v5.addVertexWithUV(0-s, y+1, 0-s, du, v);

			v5.addVertexWithUV(0-s+t, y+1+dh, 0-s+t, du, v);
			v5.addVertexWithUV(0-s+t, y+1, 1+s-t, du, dv);
			v5.addVertexWithUV(0-s+t, y+0+dh, 1+s-t, u, dv);
			v5.addVertexWithUV(0-s+t, y+0+dh, 0-s+t, u, v);

			v5.addVertexWithUV(1+s, y+1, 0-s, du, v);
			v5.addVertexWithUV(1+s, y+1, 1+s, du, dv);
			v5.addVertexWithUV(1+s, y+0, 1+s, u, dv);
			v5.addVertexWithUV(1+s, y+0, 0-s, u, v);

			v5.addVertexWithUV(1+s-t, y+0+dh, 0-s+t, u, v);
			v5.addVertexWithUV(1+s-t, y+0+dh, 1+s-t, u, dv);
			v5.addVertexWithUV(1+s-t, y+1+dh, 1+s-t, du, dv);
			v5.addVertexWithUV(1+s-t, y+1+dh, 0-s+t, du, v);

			v5.setColorOpaque_F(0.8F, 0.8F, 0.8F);
			v5.setNormal(0, 0.675F, 0);

			v5.addVertexWithUV(0-s, y+1, 0-s, du, v);
			v5.addVertexWithUV(1+s, y+1, 0-s, du, dv);
			v5.addVertexWithUV(1+s, y+0, 0-s, u, dv);
			v5.addVertexWithUV(0-s, y+0, 0-s, u, v);

			v5.addVertexWithUV(0-s+t, y+0+dh, 0-s+t, u, v);
			v5.addVertexWithUV(1+s-t, y+0+dh, 0-s+t, u, dv);
			v5.addVertexWithUV(1+s-t, y+1+dh, 0-s+t, du, dv);
			v5.addVertexWithUV(0-s+t, y+1+dh, 0-s+t, du, v);

			v5.addVertexWithUV(0-s, y+0, 1+s, u, v);
			v5.addVertexWithUV(1+s, y+0, 1+s, u, dv);
			v5.addVertexWithUV(1+s, y+1, 1+s, du, dv);
			v5.addVertexWithUV(0-s, y+1, 1+s, du, v);

			v5.addVertexWithUV(0-s+t, y+1+dh, 1+s-t, du, v);
			v5.addVertexWithUV(1+s-t, y+1+dh, 1+s-t, du, dv);
			v5.addVertexWithUV(1+s-t, y+0+dh, 1+s-t, u, dv);
			v5.addVertexWithUV(0-s+t, y+0+dh, 1+s-t, u, v);
		}

		v5.draw();

		GL11.glPopMatrix();
	}

	private void renderSymbol(TileEntityDataNode te, Tessellator v5) {
		Towers t = StructureRenderer.isRenderingTiles() ? Towers.towerList[(int)((System.currentTimeMillis()/1000)%Towers.towerList.length)] : te.getTower();
		if (t != null) {
			double d = te.getExtension0()+te.getExtension1()+te.getExtension2();
			d /= te.EXTENSION_LIMIT_0+te.EXTENSION_LIMIT_1+te.EXTENSION_LIMIT_2;
			d = Math.pow(d, 6);
			if (d > 0) {
				int idx = t.textureIndex;
				double u = (idx%8)/8D;
				double v = (idx/8)/8D;
				double du = u+1/8D;
				double dv = v+1/8D;
				double tt = StructureRenderer.isRenderingTiles() ? System.currentTimeMillis()/25D : te.getTicksExisted();
				double tk = (-tt/1.5D)%360D;
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/towersymbols.png");
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glTranslated(0, -0.625, 0);
				double s = 3;
				GL11.glScaled(s, s, s);
				for (int i = 0; i < 360; i += 60) {
					GL11.glPushMatrix();
					GL11.glRotated(i+tk, 0, 1, 0);
					GL11.glTranslated(0, 0, 1.75/s);
					v5.startDrawingQuads();
					v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, (float)d));
					//GL11.glRotated(90, 0, 1, 0);
					v5.addVertexWithUV(-0.5, 0, 0, u, dv);
					v5.addVertexWithUV(0.5, 0, 0, du, dv);
					v5.addVertexWithUV(0.5, 1, 0, du, v);
					v5.addVertexWithUV(-0.5, 1, 0, u, v);
					v5.draw();
					GL11.glPopMatrix();
				}
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
			}
		}
	}

	private void renderFlare(TileEntityDataNode te, Tessellator v5) {
		double d = te.getExtension1()+te.getExtension2();
		d /= te.EXTENSION_LIMIT_1+te.EXTENSION_LIMIT_2;
		float f = 0.125F+0.875F*(float)d;

		renderFlare(v5, f);
	}

	public static void renderFlare(Tessellator v5, float colorFactor) {
		ReikaTextureHelper.bindTerrainTexture();
		double s = 3;
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glTranslated(0, 0.75, 0);

		IIcon ico = ChromaIcons.FLARE7.getIcon();
		RenderManager rm = RenderManager.instance;
		if (StructureRenderer.isRenderingTiles()) {
			GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
			GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
		}
		else {
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		//GL11.glRotated(-(System.currentTimeMillis()/20D)%360D, 0, 0, 1);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		//f *= 0.5;

		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xb0e0ff, colorFactor));

		double z = 0;//-0.5;

		v5.addVertexWithUV(-s, s, z, u, dv);
		v5.addVertexWithUV(+s, s, z, du, dv);
		v5.addVertexWithUV(+s, -s, z, du, v);
		v5.addVertexWithUV(-s, -s, z, u, v);
		v5.draw();

		GL11.glPopMatrix();
	}

	private void renderPrism(TileEntityDataNode te, Tessellator v5) {
		double d = te.getExtension0()+te.getExtension1()+te.getExtension2();
		d /= te.EXTENSION_LIMIT_0+te.EXTENSION_LIMIT_1+te.EXTENSION_LIMIT_2;
		float f = 0.5F+0.5F*(float)d;
		double h = te.isInWorld() ? 1.5 : 1;
		double t = StructureRenderer.isRenderingTiles() ? System.currentTimeMillis()/50D : te.getTicksExisted();
		double dy = te.isInWorld() ? 0.5+te.getExtension1()+te.getExtension2()+0.0625*Math.sin((t/8D)%(2*Math.PI)) : -0.5;
		this.renderPrism(te.isInWorld() ? t*2 : System.currentTimeMillis()/20D, v5, f, h, dy);
	}

	public static void renderPrism(double tick, Tessellator v5, float colorFactor, double h, double dy) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/datanode.png");

		double s = 0.125*0.875;

		int c1 = 0xa0e0ff;
		int c2 = 0xffffff;

		c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(c1, colorFactor);
		c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c2, colorFactor);

		GL11.glTranslated(0, dy, 0);

		double r1 = 2*s*0.875;
		double r2 = 2*s;

		double[] da = {90, 15, 15};
		double[] ra = {r1, r2, r1};
		int i = 0;
		double a0 = 45+(tick)%360;

		double u = 50D/64;
		double v = 17D/96;
		double du = 63D/64;
		double dv = 30D/96;
		double uu = du-u;
		double vv = dv-v;

		double u1 = u+uu*0.5;
		double v1 = v+vv*0.5;

		ArrayList<DecimalPosition> li = new ArrayList();

		for (double a = a0; a <= a0+360; a += da[i]) {
			double r = ra[i];
			double dx = r*Math.cos(Math.toRadians(a));
			double dz = r*Math.sin(Math.toRadians(a));
			li.add(new DecimalPosition(dx, 0, dz));
			i = (i+1)%da.length;
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setColorOpaque_I(c1);
		v5.addVertex(0, 0, 0);
		v5.setColorOpaque_I(c2);
		for (DecimalPosition p : li) {
			v5.addVertex(p.xCoord, p.yCoord, p.zCoord);
		}
		v5.draw();

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setColorOpaque_I(0xa0e0ff);
		v5.addVertex(0, h, 0);
		v5.setColorOpaque_I(0xffffff);
		for (int idx = li.size()-1; idx >= 0; idx--) {
			DecimalPosition p = li.get(idx);
			v5.addVertex(p.xCoord, p.yCoord+h, p.zCoord);
		}
		v5.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);

		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(c2);
		for (int idx = 0; idx < li.size(); idx++) {
			DecimalPosition p1 = li.get(idx);
			DecimalPosition p2 = li.get((idx+1)%li.size());
			u = idx%3 != 2 ? 63D/64 : 49D/64;
			v = 34D/96;
			du = 64D/64;
			dv = 94D/96;

			v5.addVertexWithUV(p1.xCoord, p1.yCoord, p1.zCoord, u, v);
			v5.addVertexWithUV(p1.xCoord, p1.yCoord+h, p1.zCoord, u, dv);
			v5.addVertexWithUV(p2.xCoord, p2.yCoord+h, p2.zCoord, du, dv);
			v5.addVertexWithUV(p2.xCoord, p2.yCoord, p2.zCoord, du, v);
		}
		v5.draw();
	}

}
