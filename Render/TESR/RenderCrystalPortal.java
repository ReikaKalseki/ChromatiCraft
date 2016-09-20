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

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockChromaPortal.TileEntityCrystalPortal;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalPortal extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalPortal te = (TileEntityCrystalPortal)tile;
		int p = te.getPortalPosition(StructureRenderer.isRenderingTiles() ? StructureRenderer.getRenderAccess() : te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator v5 = Tessellator.instance;
		if (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles()) {
			if (StructureRenderer.isRenderingTiles() || te.isFull9x9()) {
				if (p == 5) {
					ReikaTextureHelper.bindTerrainTexture();
					IIcon ico = ChromaIcons.RIFT.getIcon();
					double u = ico.getMinU();
					double v = ico.getMinV();
					double du = ico.getMaxU();
					double dv = ico.getMaxV();
					v5.startDrawingQuads();
					int rgb = ReikaColorAPI.getModifiedHue(0x0000ff, (int)(240+40*Math.sin((te.getTicks()+par8)/8F)));
					v5.setColorOpaque_I(rgb);
					v5.addVertexWithUV(-1, 1, 2, u, dv);
					v5.addVertexWithUV(2, 1, 2, du, dv);
					v5.addVertexWithUV(2, 1, -1, du, v);
					v5.addVertexWithUV(-1, 1, -1, u, v);
					v5.draw();

					if (StructureRenderer.isRenderingTiles() || te.isComplete()) {
						BlendMode.ADDITIVEDARK.apply();


						ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/beam2.png");
						u = 2*Math.sin(System.currentTimeMillis()/3200D);
						v = Math.cos(90+System.currentTimeMillis()/1600D);
						du = u+1;
						dv = v+1;
						v5.startDrawingQuads();
						v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(CrystalElement.getBlendedColor(te.getTicks(), 20), 0.0625F), 255);
						v5.addVertexWithUV(-1, 1, 2, u, dv);
						v5.addVertexWithUV(2, 1, 2, du, dv);
						v5.addVertexWithUV(2, 1, -1, du, v);
						v5.addVertexWithUV(-1, 1, -1, u, v);
						v5.draw();

						boolean half = StructureRenderer.isRenderingTiles() || te.getCharge() >= te.MINCHARGE/2;

						v5.startDrawingQuads();
						ReikaTextureHelper.bindTerrainTexture();
						ico = ChromaIcons.RINGS.getIcon();
						u = ico.getMinU();
						v = ico.getMinV();
						du = ico.getMaxU();
						dv = ico.getMaxV();
						double dh = 0.25;
						double n = half ? 2D : 2D*te.getCharge()*2/te.MINCHARGE;
						for (double i = 0; i <= n; i += dh) {
							double s = 1.5-i/2;
							double a = 255-64*i;
							v5.setColorRGBA_I(ReikaColorAPI.GStoHex((int)(a*(0.75+0.25*Math.sin((te.getTicks()+par8+i*96)/4F)))), 255);
							v5.addVertexWithUV(0.5-s, i+1, 0.5+s, u, dv);
							v5.addVertexWithUV(0.5+s, i+1, 0.5+s, du, dv);
							v5.addVertexWithUV(0.5+s, i+1, 0.5-s, du, v);
							v5.addVertexWithUV(0.5-s, i+1, 0.5-s, u, v);
						}
						v5.draw();

						if (half) {
							GL11.glPushMatrix();
							GL11.glTranslated(0.5, 0, 0.5);
							for (double a = 0; a < 180; a += 60) {
								GL11.glRotated(a-RenderManager.instance.playerViewY, 0, 1, 0);
								v5.startDrawingQuads();
								ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/arches2.png");
								for (double ux = 0; ux <= 0.5; ux += 0.5) {
									//v5.setColorRGBA_I(CrystalElement.getBlendedColor(te.getTicks()+(int)(8*ux), 20), 255);
									u = ux+((System.currentTimeMillis()/50)%32)/32D;
									v = 0;
									du = u+1/32D;
									dv = v+1;
									double h = 9;
									//v5.addVertexWithUV(0, 2, -0.5, u, dv);
									//v5.addVertexWithUV(1, 2, -0.5, du, dv);
									//v5.addVertexWithUV(1, h, -0.5, du, v);
									//v5.addVertexWithUV(0, h, -0.5, u, v);

									v5.addVertexWithUV(-0.5, 2, 0, u, dv);
									v5.addVertexWithUV(0.5, 2, 0, du, dv);
									v5.addVertexWithUV(0.5, h, 0, du, v);
									v5.addVertexWithUV(-0.5, h, 0, u, v);
								}
								v5.draw();
							}
							GL11.glPopMatrix();
						}
					}
				}
			}
		}
		else {
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glFrontFace(GL11.GL_CW);
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/entity/end_portal.png"));
			double u = Math.sin(Math.toRadians(te.getTicks()%360));
			double v = te.getTicks()%256/256D+Math.cos(Math.toRadians((te.getTicks()/2D)%360));
			double s = 0.25;
			double du = u+s;
			double dv = v+s;
			double o = 0.001;
			double h = 1-o;

			v5.startDrawingQuads();

			if (te.worldObj.getBlock(te.xCoord, te.yCoord, te.zCoord+1) != ChromaBlocks.PORTAL.getBlockInstance()) {
				v5.addVertexWithUV(0, o, 1, u, v);
				v5.addVertexWithUV(1, o, 1, du, v);
				v5.addVertexWithUV(1, h, 1, du, dv);
				v5.addVertexWithUV(0, h, 1, u, dv);
			}

			if (te.worldObj.getBlock(te.xCoord, te.yCoord, te.zCoord-1) != ChromaBlocks.PORTAL.getBlockInstance()) {
				v5.addVertexWithUV(0, h, 0, u, dv);
				v5.addVertexWithUV(1, h, 0, du, dv);
				v5.addVertexWithUV(1, o, 0, du, v);
				v5.addVertexWithUV(0, o, 0, u, v);
			}

			if (te.worldObj.getBlock(te.xCoord+1, te.yCoord, te.zCoord) != ChromaBlocks.PORTAL.getBlockInstance()) {
				v5.addVertexWithUV(1, h, 0, u, dv);
				v5.addVertexWithUV(1, h, 1, du, dv);
				v5.addVertexWithUV(1, o, 1, du, v);
				v5.addVertexWithUV(1, o, 0, u, v);
			}

			if (te.worldObj.getBlock(te.xCoord-1, te.yCoord, te.zCoord) != ChromaBlocks.PORTAL.getBlockInstance()) {
				v5.addVertexWithUV(0, o, 0, u, v);
				v5.addVertexWithUV(0, o, 1, du, v);
				v5.addVertexWithUV(0, h, 1, du, dv);
				v5.addVertexWithUV(0, h, 0, u, dv);
			}

			if (!te.isFull9x9()) {
				v5.addVertexWithUV(0, h, 0, u, v);
				v5.addVertexWithUV(0, h, 1, du, v);
				v5.addVertexWithUV(1, h, 1, du, dv);
				v5.addVertexWithUV(1, h, 0, u, dv);
			}

			v5.addVertexWithUV(1, o, 0, u, dv);
			v5.addVertexWithUV(1, o, 1, du, dv);
			v5.addVertexWithUV(0, o, 1, du, v);
			v5.addVertexWithUV(0, o, 0, u, v);
			v5.draw();

			//int a = (int)(255*Math.sin(te.getTicks()/40D));
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		//this.renderStaticTexture(par2, par4, par6, par8);
	}

	private void renderStaticTextureLayer(double par2, double par4, double par6, float ptick, int rgb) {
		float f1 = (float)field_147501_a.field_147560_j;
		float f2 = (float)field_147501_a.field_147561_k;
		float f3 = (float)field_147501_a.field_147558_l;
		GL11.glDisable(GL11.GL_LIGHTING);
		staticRand.setSeed(31100L);

		float f5 = 16;

		float f4 = 1.001F;//0.75F;//+i*0.125F;
		GL11.glPushMatrix();
		float f6 = 0.0625F;
		float f7 = 1.0F/(f5+1.0F);

		float f8 = (float)(-(par4+f4));
		float f9 = f8+ActiveRenderInfo.objectY;
		float f10 = f8+f5+ActiveRenderInfo.objectY;
		float f11 = f9/f10;
		f11 += (float)(par4+f4);
		GL11.glTranslatef(f1, f11, f3);
		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
		GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.genBuffer(1.0F, 0.0F, 0.0F, 0.0F));
		GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 1.0F, 0.0F));
		GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.genBuffer(0.0F, 1.0F, 0.0F, 0.0F));
		GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, Minecraft.getSystemTime() % 700000L/700000.0F, 0.0F);
		GL11.glScalef(f6, f6, f6);
		GL11.glTranslatef(0.5F, 0.5F, 0.0F);
		//GL11.glRotatef((i*i*4321+i*9)*2.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
		GL11.glTranslatef(-f1, -f3, -f2);
		f9 = f8+ActiveRenderInfo.objectY;
		GL11.glTranslatef(ActiveRenderInfo.objectX*f5/f9, ActiveRenderInfo.objectZ*f5/f9, -f2);
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		f11 = staticRand.nextFloat()*0.5F+0.1F;
		float f12 = staticRand.nextFloat()*0.5F+0.4F;
		float f13 = staticRand.nextFloat()*0.5F+0.5F;

		f11 = ReikaColorAPI.getRed(rgb)/255F;
		f12 = ReikaColorAPI.getGreen(rgb)/255F;
		f13 = ReikaColorAPI.getBlue(rgb)/255F;

		v5.setColorRGBA_F(f11*f7, f12*f7, f13*f7, 1.0F);
		v5.addVertex(par2, par4+f4, par6);
		v5.addVertex(par2, par4+f4, par6+1.0D);
		v5.addVertex(par2+1.0D, par4+f4, par6+1.0D);
		v5.addVertex(par2+1.0D, par4+f4, par6);
		v5.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private static final ResourceLocation starFieldTex = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation portalTex = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random staticRand = new Random(31100L);
	FloatBuffer fBuffer = GLAllocation.createDirectFloatBuffer(16);

	private void renderStaticTexture(double par2, double par4, double par6, float ptick) {
		float f1 = (float)field_147501_a.field_147560_j;
		float f2 = (float)field_147501_a.field_147561_k;
		float f3 = (float)field_147501_a.field_147558_l;
		GL11.glDisable(GL11.GL_LIGHTING);
		staticRand.setSeed(31100L);

		for (int i = 0; i < 16; ++i) {
			float f4 = 1.001F;//0.75F;//+i*0.125F;
			GL11.glPushMatrix();
			float f5 = 16-i;
			float f6 = 0.0625F;
			float f7 = 1.0F/(f5+1.0F);

			if (i == 0) {
				//this.bindTexture(starFieldTex);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Dimension Bump Maps/cyan.png");
				f7 = 0.1F;
				f5 = 65.0F;
				f6 = 0.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			else if (i == 1) {
				//this.bindTexture(portalTex);
				GL11.glEnable(GL11.GL_BLEND);
				//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				BlendMode.ADDITIVEDARK.apply();
				f6 = 0.5F;
			}
			else if (i > 1) {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Dimension Bump Maps/gray.png");
			}

			float f8 = (float)(-(par4+f4));
			float f9 = f8+ActiveRenderInfo.objectY;
			float f10 = f8+f5+ActiveRenderInfo.objectY;
			float f11 = f9/f10;
			f11 += (float)(par4+f4);
			GL11.glTranslatef(f1, f11, f3);
			GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.genBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.genBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, Minecraft.getSystemTime() % 700000L/700000.0F, 0.0F);
			GL11.glScalef(f6, f6, f6);
			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
			GL11.glRotatef((i*i*4321+i*9)*2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-f1, -f3, -f2);
			f9 = f8+ActiveRenderInfo.objectY;
			GL11.glTranslatef(ActiveRenderInfo.objectX*f5/f9, ActiveRenderInfo.objectZ*f5/f9, -f2);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			f11 = staticRand.nextFloat()*0.5F+0.1F;
			float f12 = staticRand.nextFloat()*0.5F+0.4F;
			float f13 = staticRand.nextFloat()*0.5F+0.5F;

			if (i == 0) {
				f13 = 1.0F;
				f12 = 1.0F;
				f11 = 1.0F;
			}

			CrystalElement e = CrystalElement.elements[i];
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			f7 = 0.5F+0.5F*(float)Math.sin(System.currentTimeMillis()/200D+i/2D);
			//ReikaJavaLibrary.pConsole(e+":"+a);
			f7 *= 0.125;

			v5.setColorRGBA_F(e.getRed()*f7/255F, e.getGreen()*f7/255F, e.getBlue()*f7/255F, 1);
			//v5.setColorRGBA_F(f11*f7, f12*f7, f13*f7, 1.0F);
			v5.addVertex(par2, par4+f4, par6);
			v5.addVertex(par2, par4+f4, par6+1.0D);
			v5.addVertex(par2+1.0D, par4+f4, par6+1.0D);
			v5.addVertex(par2+1.0D, par4+f4, par6);
			v5.draw();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private FloatBuffer genBuffer(float v1, float v2, float v3, float v4) {
		fBuffer.clear();
		fBuffer.put(v1).put(v2).put(v3).put(v4);
		fBuffer.flip();
		return fBuffer;
	}

}
