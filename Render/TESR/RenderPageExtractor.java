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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityPageExtractor;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicVariablePoint;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class RenderPageExtractor extends ChromaRenderBase {

	private final ResourceLocation bookTex = new ResourceLocation("mystcraft:textures/entity/agebook.png");
	private final ModelBook book = new ModelBook();

	private final Collection<BasicVariablePoint> points = new ArrayList();


	public RenderPageExtractor() {
		for (int i = 0; i < 12; i++) {
			double v = ReikaRandomHelper.getRandomBetween(0.03125/4, 0.03125/2);
			BasicVariablePoint point = new BasicVariablePoint(new DecimalPosition(0, 0, 0), 1, v);
			point.tolerance = 0.03125;
			points.add(point);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityPageExtractor te = (TileEntityPageExtractor)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);

		boolean hasBook = te.getStackInSlot(0) != null;
		double t = System.currentTimeMillis()/10D;
		Tessellator v5 = Tessellator.instance;

		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 0) {
			GL11.glColor4f(1, 1, 1, 1);
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(te.isInWorld() ? te.getBlockType().getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord) : 240);
			v5.setColorOpaque_I(0xffffff);
			IIcon ico = Blocks.stone_slab.getIcon(0, 0);
			double u = ico.getMinU();
			double v = ico.getMinV();
			double du = ico.getMaxU();
			double dv = ico.getMaxV();

			ico = Blocks.stone_slab.getIcon(2, 0);
			double u2 = ico.getMinU();
			double v2 = ico.getMinV();
			double du2 = ico.getMaxU();
			double dv2 = ico.getInterpolatedV(8);

			double h = 0.1875;
			double s = 0.375;

			v5.setNormal(0, 1, 0);
			v5.addVertexWithUV(0.5-s, h, 0.5+s, u, dv);
			v5.addVertexWithUV(0.5+s, h, 0.5+s, du, dv);
			v5.addVertexWithUV(0.5+s, h, 0.5-s, du, v);
			v5.addVertexWithUV(0.5-s, h, 0.5-s, u, v);

			v5.setColorOpaque_I(0xa0a0a0);
			v5.addVertexWithUV(1, 0, 0, u2, dv2);
			v5.addVertexWithUV(0.5+s, h, 0.5-s, u2, v2);
			v5.addVertexWithUV(0.5+s, h, 0.5+s, du2, v2);
			v5.addVertexWithUV(1, 0, 1, du2, dv2);

			v5.addVertexWithUV(0, 0, 1, u2, v2);
			v5.addVertexWithUV(0.5-s, h, 0.5+s, u2, dv2);
			v5.addVertexWithUV(0.5-s, h, 0.5-s, du2, dv2);
			v5.addVertexWithUV(0, 0, 0, du2, v2);

			v5.setColorOpaque_I(0x909090);
			v5.addVertexWithUV(1, 0, 1, u2, v2);
			v5.addVertexWithUV(0.5+s, h, 0.5+s, u2, dv2);
			v5.addVertexWithUV(0.5-s, h, 0.5+s, du2, dv2);
			v5.addVertexWithUV(0, 0, 1, du2, v2);

			v5.addVertexWithUV(0, 0, 0, u2, dv2);
			v5.addVertexWithUV(0.5-s, h, 0.5-s, u2, v2);
			v5.addVertexWithUV(0.5+s, h, 0.5-s, du2, v2);
			v5.addVertexWithUV(1, 0, 0, du2, dv2);

			v5.setColorOpaque_I(0x707070);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, u, dv);
			v5.draw();

			if (hasBook) {
				GL11.glPushMatrix();
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glRotated(90, 0, 0, 1);
				double a = (t/2D)%360D;
				double da1 = 5*Math.sin(t/32D);
				double da2 = 8*Math.cos(t/48D);
				GL11.glRotated(a, 1, 0, 0);
				GL11.glRotated(da1, 0, 1, 0);
				GL11.glRotated(da2, 0, 0, 1);
				this.bindTexture(bookTex);
				book.render(null, 0, 0, 0, 1.05F, 0, 0.0625F);
				GL11.glPopMatrix();
			}

		}

		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 1) {

			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(false);

			for (BasicVariablePoint point : points) {
				double r = 0.75+0.25*Math.sin(t/50D+Math.toRadians(point.hashCode()/32D%360D));
				DecimalPosition pos = point.asPosition();
				if (hasBook) {
					ChromaFX.renderBeam(0.5, 0.5, 0.5, 0.5+r*pos.xCoord, 0.5+r*pos.yCoord, 0.5+r*pos.zCoord, par8, 255, 0.125);
				}
				else {
					r /= 2;
				}
				if (te.isInWorld() && !Minecraft.getMinecraft().isGamePaused()) {
					EntityBlurFX fx = new EntityBlurFX(te.worldObj, te.xCoord+0.5+pos.xCoord*r, te.yCoord+0.5+pos.yCoord*r, te.zCoord+0.5+pos.zCoord*r);
					fx.setRapidExpand().setScale(0.625F).setLife(8).setAlphaFading().setIcon(ChromaIcons.FADE_GENTLE);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
				point.update();
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			ReikaTextureHelper.bindTerrainTexture();

			GL11.glTranslated(0.5, 0.5, 0.5);

			RenderManager rm = RenderManager.instance;
			if (te.isInWorld()) {
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}
			else {
				GL11.glTranslated(0, 0.125, 0);
				GL11.glRotated(180+45, 0, 1, 0);
				GL11.glRotated(30, 1, 0, 0);
			}

			ChromaIcons[] icons = hasBook ?
					new ChromaIcons[]{
					ChromaIcons.HEXFLARE,
					ChromaIcons.BLURFLARE,
					ChromaIcons.RADIATE,
			}
			:
				new ChromaIcons[]{
							ChromaIcons.CONCENTRIC2REV,
							ChromaIcons.ROSES_WHITE,
					};

					for (int i = 0; i < icons.length; i++) {
						IIcon ico = icons[i].getIcon();

						double sb = hasBook ? 0.75 : 0.5;

						double s = sb-0.1875*i;

						double u = ico.getMinU();
						double v = ico.getMinV();
						double du = ico.getMaxU();
						double dv = ico.getMaxV();

						v5.startDrawingQuads();
						v5.setBrightness(240);
						v5.addVertexWithUV(-s, s, 0, u, dv);
						v5.addVertexWithUV(s, s, 0, du, dv);
						v5.addVertexWithUV(s, -s, 0, du, v);
						v5.addVertexWithUV(-s, -s, 0, u, v);
						v5.draw();
					}
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}


}
