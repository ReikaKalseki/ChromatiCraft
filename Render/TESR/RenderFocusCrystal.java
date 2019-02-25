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

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FocusCrystalRecipes;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFocusCrystal;
import Reika.ChromatiCraft.Models.ModelFocusCrystalMessy;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.FocusConnection;
import Reika.DragonAPI.Instantiable.Effects.TruncatedCube;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;


public class RenderFocusCrystal extends ChromaRenderBase {

	private static final TruncatedCube cube = new TruncatedCube(0.005, 0.15);

	private final ModelFocusCrystal ordered = new ModelFocusCrystal();
	private final ModelFocusCrystalMessy unordered = new ModelFocusCrystalMessy();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(par2, par4, par6);
		GL11.glDepthMask(true);
		TileEntityFocusCrystal te = (TileEntityFocusCrystal)tile;
		float t = te.getTicksExisted()+par8+System.identityHashCode(te)%512000;
		if (MinecraftForgeClient.getRenderPass() == 0 || StructureRenderer.isRenderingTiles() || !te.isInWorld()) {
			this.renderBase(te, par8);

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaRenderHelper.disableEntityLighting();
			String tex = "Textures/TileEntity/focuscrystal"+te.getTier().getTextureSuffix()+".png";
			this.bindTextureByName(tex);
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslated(0.5, -1.625, -0.5);
			if (te.getTier().useOrganizedModel()) {
				ordered.renderAll(te, ReikaJavaLibrary.makeListFrom(t, te.getTier().getRenderColor(t)));
			}
			else {
				unordered.renderAll(te, ReikaJavaLibrary.makeListFrom(t, te.getTier().getRenderColor(t)));
			}
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}

		if (StructureRenderer.isRenderingTiles() || (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1)) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			BlendMode.ADDITIVEDARK.apply();
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDepthMask(false);
			double n = 12+2*Math.sin(System.identityHashCode(te)%(Math.PI*2));
			double dy = 0.125*Math.sin((t/n)%(Math.PI*2));
			this.renderBobber(te, t, n, dy);
			this.renderFlare(te, t, n, dy);
			this.renderLines(te, t);
			GL11.glPopAttrib();
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderBase(TileEntityFocusCrystal te, float tick) {
		IIcon ico = FocusCrystalRecipes.getBaseRenderIcon(te.getTier());
		if (ico != null) {
			double h = 0.125;
			ReikaTextureHelper.bindTerrainTexture();
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(te.isInWorld() ? te.getBlockType().getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord) : 240);
			v5.setNormal(0, 1, 0);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			float u2 = ico.getInterpolatedU(1);
			float du2 = ico.getInterpolatedU(3);

			v5.addVertexWithUV(0, h, 1, u, dv);
			v5.addVertexWithUV(1, h, 1, du, dv);
			v5.addVertexWithUV(1, h, 0, du, v);
			v5.addVertexWithUV(0, h, 0, u, v);

			v5.setNormal(0, 0.5F, 0);
			v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, u, dv);

			v5.setColorOpaque_F(0.625F, 0.625F, 0.625F);
			v5.setNormal(0, 0.625F, 0);
			v5.addVertexWithUV(0, 0, 1, u2, dv);
			v5.addVertexWithUV(0, h, 1, du2, dv);
			v5.addVertexWithUV(0, h, 0, du2, v);
			v5.addVertexWithUV(0, 0, 0, u2, v);

			v5.addVertexWithUV(1, 0, 0, u2, v);
			v5.addVertexWithUV(1, h, 0, du2, v);
			v5.addVertexWithUV(1, h, 1, du2, dv);
			v5.addVertexWithUV(1, 0, 1, u2, dv);

			v5.setColorOpaque_F(0.75F, 0.75F, 0.75F);
			v5.setNormal(0, 0.75F, 0);
			v5.addVertexWithUV(0, 0, 0, u2, v);
			v5.addVertexWithUV(0, h, 0, du2, v);
			v5.addVertexWithUV(1, h, 0, du2, dv);
			v5.addVertexWithUV(1, 0, 0, u2, dv);

			v5.addVertexWithUV(1, 0, 1, u2, dv);
			v5.addVertexWithUV(1, h, 1, du2, dv);
			v5.addVertexWithUV(0, h, 1, du2, v);
			v5.addVertexWithUV(0, 0, 1, u2, v);

			v5.draw();
		}
	}

	private void renderFlare(TileEntityFocusCrystal te, float tick, double n, double dy) {
		GL11.glPushMatrix();
		double s = 0.75*(0.25+(1+te.getTier().ordinal())/4D);
		int c = ReikaColorAPI.getColorWithBrightnessMultiplier(te.getTier().getRenderColor(tick), 0.75F);
		GL11.glTranslated(0.5, 0.5+dy, 0.5);
		GL11.glScaled(s, s, s);
		RenderManager rm = RenderManager.instance;
		GL11.glRotatef(180-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.STARFLARE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(c);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glPopMatrix();
	}

	private void renderLines(TileEntityFocusCrystal te, float tick) {
		/*
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator v5 = Tessellator.instance;
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque_I(0xffffff);
		Collection<FocusConnection> li = te.getConnections();
		for (FocusConnection c : li) {
			v5.addVertex(0.5, 0.5, 0.5);
			v5.addVertex(, , );
		}
		v5.draw();
		GL11.glPopAttrib();
		 */
		FocusConnection c = te.getConnection();
		if (c != null) {
			double x2 = -c.relativeLocation.relativeLocation().xCoord+0.5;
			double y2 = -c.relativeLocation.relativeLocation().yCoord;//+0.125;
			double z2 = -c.relativeLocation.relativeLocation().zCoord+0.5;
			ChromaFX.renderBeam(0.5, 0.25, 0.5, x2, y2, z2, tick, 255, 0.375F+(float)(0.25*Math.sin(System.identityHashCode(te)+System.currentTimeMillis()/800D)));
		}
	}

	private void renderBobber(TileEntityFocusCrystal te, float tick, double n, double dy) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5+dy, 0.5);
		GL11.glRotated((100*tick/(n*n))%360D, 1, 0, 0);
		GL11.glRotated((100*tick/(n*n))%360D, 0, 1, 0);
		GL11.glRotated((100*tick/(n*n))%360D, 0, 0, 1);
		double s = (1+te.getTier().getEffectiveOrdinal())/4D;
		GL11.glScaled(s, s, s);
		int c0 = te.getTier().getRenderColor(tick);
		float pdist = (float)Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
		int c = (c0 & 0xffffff) | (192 << 24);
		int c2 = ReikaColorAPI.mixColors(c, 0xffffff | (192 << 24), 0.75F);
		if ((c0 & 0xffffff) == 0xffffff)
			c = (c&0xff000000) | 0xbfbfbf;
		cube.render(0, 0, 0, c, c2, true, pdist);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

}
