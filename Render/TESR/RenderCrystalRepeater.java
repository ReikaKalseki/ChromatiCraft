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

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.Magic.CastingTuning.CastingTuningManager;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Objects.LineType;

public class RenderCrystalRepeater extends CrystalTransmitterRender {

	private final RemoteSourcedAsset rangeTexture = ChromaClient.dynamicAssets.createAsset("Textures/repeaterrange3.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		TileEntityCrystalRepeater te = (TileEntityCrystalRepeater)tile;

		ChromaTiles c = te.getTile();
		if (tile.hasWorldObj() && ((MinecraftForgeClient.getRenderPass() == 1 && te.canConduct()) || StructureRenderer.isRenderingTiles())) {
			//TileEntityCrystalRepeater te = (TileEntityCrystalRepeater)tile;
			IIcon ico = ChromaIcons.SPARKLE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;
			GL11.glTranslated(0.5, 0.5, 0.5);

			this.renderPlayerConnectivityLine(te, par8);
			this.renderRangeSphere(te, par8);

			double s = 0.75;
			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}

			v5.startDrawingQuads();
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			this.doAuxRendering(te, par8);

			if (te.canConduct()) {
				if (te.isRainAffected())
					this.renderIconHalo(te, ChromaIcons.RAINFLARE.getIcon(), 1, par8);
				if (te.isTableGrouped())
					this.renderIconHalo(te, ChromaIcons.SUNFLARE.getIcon(), 1, par8);
				else if (te.isClustered())
					this.renderIconHalo(te, ChromaIcons.CELLFLARE.getIcon(), 1.25, par8);
				float f = HoldingChecks.MANIPULATOR.getFade();
				if (f > 0) {
					UUID uid = te.getCaster();
					if (uid != null) {
						this.renderCasterHalo(te, f, uid, par8);
					}
				}
				if (te.isTurbocharged()) {
					this.renderHalo(te, par8);
				}
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		else if (!tile.hasWorldObj()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			if (par8 < 0)
				ReikaRenderHelper.disableEntityLighting();
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushMatrix();
			RenderBlocks.getInstance().renderBlockAsItem(c.getBlock(), c.getBlockMetadata(), 1);
			GL11.glPopMatrix();

			IIcon ico = ChromaIcons.SPARKLE.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-45, 1, 0, 0);
			double s = 0.8;
			GL11.glScaled(s, s, s);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			if (te.isTurbocharged()) {
				GL11.glPushMatrix();
				this.renderHalo(te, par8);
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	private void renderCasterHalo(TileEntityCrystalRepeater te, float fade, UUID uid, float par8) {
		int clr = this.getHaloRenderColor(te);
		clr = ReikaColorAPI.mixColors(clr, 0xffffff, 0.875F+0.125F*(float)Math.sin(te.getTicksExisted()/90D));
		clr = ReikaColorAPI.getColorWithBrightnessMultiplier(clr, fade);
		double s = 1.75+0.0625*Math.sin(te.getTicksExisted()/6D);
		if (te.isTurbocharged())
			s *= 1.25;
		CastingTuningManager.instance.getTuningKey(te.worldObj, uid).drawIcon(Tessellator.instance, s, clr);
	}

	private void renderIconHalo(TileEntityCrystalRepeater te, IIcon ico, double sizeScale, float par8) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		Tessellator v5 = Tessellator.instance;
		GL11.glDepthMask(false);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		double s = (2.75+0.125*Math.sin(te.getTileEntityAge()/40D))*sizeScale;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(this.getHaloRenderColor(te));
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.addVertexWithUV(s, -s, 0, du, v);
		v5.addVertexWithUV(s, s, 0, du, dv);
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.draw();
	}

	protected void doAuxRendering(TileEntityCrystalRepeater te, float par8) {

	}

	/*
	private void renderConnectivityLines(TileEntityCrystalRepeater te, float par8) {
		int a = te.updateAndGetConnectionRenderAlpha();
		if (a != 0) {
			HashSet<WorldLocation> c = te.getRenderedConnectableTiles();
			//ReikaJavaLibrary.pConsole(c);
			if (c != null) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LINE_STIPPLE);
				GL11.glLineWidth(6);
				GL11.glLineStipple(24, (short)0xAAAA);
				BlendMode.DEFAULT.apply();

				Tessellator v5 = Tessellator.instance;
				v5.startDrawing(GL11.GL_LINES);

				v5.setColorRGBA_I(0xffffff, a);

				for (WorldLocation loc : c) {
					if (loc.xCoord+loc.yCoord+loc.zCoord > te.xCoord+te.yCoord+te.zCoord) { //hack to ensure 1-way rendering
						v5.addVertex(0, 0, 0);
						v5.addVertex(loc.xCoord-te.xCoord, loc.yCoord-te.yCoord, loc.zCoord-te.zCoord);
					}
				}

				v5.draw();

				GL11.glPopAttrib();
			}
		}
	}
	 */
	private void renderRangeSphere(TileEntityCrystalRepeater te, float par8) {
		int a = te.getRangeAlpha();
		if (a > 0) {
			double r = Math.min(te.getReceiveRange(), te.getSendRange());
			//r += 0.75*Math.sin(te.getTicksExisted()/5D);
			this.renderSphere(te, par8, ReikaColorAPI.getColorWithBrightnessMultiplier(this.getHaloRenderColor(te), a/512F), r);
		}
	}

	protected void renderSphere(TileEntityCrystalRepeater te, float par8, int color, double r) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		ReikaRenderHelper.disableEntityLighting();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);

		ReikaTextureHelper.bindTexture(rangeTexture);
		Tessellator var5 = Tessellator.instance;
		var5.startDrawingQuads();
		var5.setColorOpaque_I(color);
		double dx = 0.5;
		double dy = 0.5;
		double dz = 0.5;
		double dk = 0.5*r/16;
		double di = 10;
		double f = 0.75*Math.sin(te.getTicksExisted()/128D);
		for (double k = -r; k <= r; k += dk) {
			double r2 = f+Math.sqrt(r*r-k*k);
			double r3 = f+Math.sqrt(r*r-(k+dk)*(k+dk));
			if (Double.isNaN(r2) || Double.isNaN(r3))
				continue;
			for (int i = 0; i < 360; i += di) {
				double a = Math.toRadians(i);
				double a2 = Math.toRadians(i+di);
				double ti = i+(System.currentTimeMillis()/50D%360);
				double tk = k+(System.currentTimeMillis()/220D%360);
				double u = ti/360D*3;
				double du = (ti+di)/360D*3;
				double v = tk*r/1024D;
				double dv = (tk+dk)*r/1024D;
				double s1 = Math.sin(a);
				double s2 = Math.sin(a2);
				double c1 = Math.cos(a);
				double c2 = Math.cos(a2);
				var5.addVertexWithUV(dx+r2*c1, dy+k, dz+r2*s1, u, v);
				var5.addVertexWithUV(dx+r2*c2, dy+k, dz+r2*s2, du, v);
				var5.addVertexWithUV(dx+r3*c2, dy+k+dk, dz+r3*s2, du, dv);
				var5.addVertexWithUV(dx+r3*c1, dy+k+dk, dz+r3*s1, u, dv);
			}
		}
		var5.draw();
		/*
		var5.startDrawing(GL11.GL_TRIANGLE_FAN);
		var5.setColorRGBA_I(color, color >> 24 & 255);
		var5.addVertexWithUV(x, y+0.5, z, 0.5, 0.5);
		double dr = 2;
		for (int i = 0; i < 360; i += 10) {
			double a = Math.toRadians(i);
			double a2 = a+Math.toRadians(System.currentTimeMillis()/20D%360);
			double dx = Math.cos(a);
			double dz = Math.sin(a);
			double ux = (System.currentTimeMillis()/3100D)%10;
			double uy = (System.currentTimeMillis()/4700D)%10;
			double u = Math.cos(a2)+ux;
			double v = Math.sin(a2)+uy;
			u = u*0.25;
			v = v*0.25;
			var5.addVertexWithUV(x+dx*dr, y+r-0.25, z+dz*dr, u, v);
		}
		var5.draw();*/

		GL11.glPopAttrib();
	}

	private void renderPlayerConnectivityLine(TileEntityCrystalRepeater te, float par8) {
		int a = te.updateAndGetConnectionRenderAlpha();
		if (a > 0) {
			//ReikaJavaLibrary.pConsole(c);
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlockClient(4.5, false);
			if (mov != null && mov.typeOfHit == MovingObjectType.BLOCK) {
				ForgeDirection dir = mov.sideHit >= 0 ? ForgeDirection.VALID_DIRECTIONS[mov.sideHit] : ForgeDirection.UNKNOWN;
				int x = mov.blockX+dir.offsetX;
				int y = mov.blockY+dir.offsetY;
				int z = mov.blockZ+dir.offsetZ;
				if (ReikaMathLibrary.py3d(x-te.xCoord, y-te.yCoord, z-te.zCoord) <= te.getSendRange()) {
					if (PylonFinder.lineOfSight(Minecraft.getMinecraft().theWorld, te.xCoord, te.yCoord, te.zCoord, x, y, z).hasLineOfSight) {
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glEnable(GL11.GL_LINE_STIPPLE);
						GL11.glLineWidth(6);
						LineType.DASHED.setMode(128);
						BlendMode.DEFAULT.apply();

						Tessellator v5 = Tessellator.instance;
						v5.startDrawing(GL11.GL_LINES);

						v5.setColorRGBA_I(0xffffff, a);

						v5.addVertex(0, 0, 0);
						v5.addVertex(x-te.xCoord, y-te.yCoord, z-te.zCoord);

						v5.draw();

						GL11.glPopAttrib();
					}
				}
			}
		}
	}

	private void renderHalo(TileEntityCrystalRepeater te, float par8) {
		float f = 1;
		if (te.worldObj != null) {
			if (ChromaOptions.EPILEPSY.getState()) {
				f = HoldingChecks.MANIPULATOR.getFade();
				if (f <= 0)
					return;
			}
		}

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		int c = te.worldObj != null ? this.getHaloRenderColor(te) : 0xffffff;
		if (f < 1)
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f);
		if (te.worldObj == null)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		Tessellator v5 = Tessellator.instance;

		int step = 15;
		double d = (System.currentTimeMillis()/50D)%360;
		int a = te.worldObj != null ? 90 : 30;
		int n = 0;
		for (int i = 0; i < a; i += step) {
			float u = 0;//ico.getMinU();
			float v = 0;//ico.getMinV();
			float du = 1;//ico.getMaxU();
			float dv = 1;//ico.getMaxV();

			double z = -i/(double)step*0.01;
			double z2 = z-0.005;

			GL11.glRotated(i+d, 0, 0, 1);

			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Turbo/sections.png");
			double s = te.worldObj != null ? 1.5+0.5*Math.sin(Math.toRadians(4*d+i*2)) : 1.75;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-s, -s, z, u, v);
			v5.addVertexWithUV(s, -s, z, du, v);
			v5.addVertexWithUV(s, s, z, du, dv);
			v5.addVertexWithUV(-s, s, z, u, dv);
			v5.draw();

			if (te.worldObj != null) {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Turbo/radiate.png");
				s = 3;//2+1*Math.sin(Math.toRadians(4*d+i*2));
				u = (te.getTicksExisted()+n*2)%18/18F;
				du = u+1/18F;
				v5.startDrawingQuads();
				v5.setColorOpaque_I(c);
				v5.addVertexWithUV(-s, -s, z2, u, v);
				v5.addVertexWithUV(s, -s, z2, du, v);
				v5.addVertexWithUV(s, s, z2, du, dv);
				v5.addVertexWithUV(-s, s, z2, u, dv);
				v5.draw();
			}
			n++;
		}
		GL11.glPopAttrib();
	}

	protected int getHaloRenderColor(TileEntityCrystalRepeater te) {
		CrystalElement e = te.getActiveColor();
		return e != null ? e.getColor() : 0xffffff;
	}

}
