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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Models.ModelRelaySource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderRelaySource extends ChromaRenderBase {

	private final ModelRelaySource model = new ModelRelaySource();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "receiver.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityRelaySource te = (TileEntityRelaySource)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		GL11.glPushMatrix();
		this.renderModel(te, model);

		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, -1.5F, -0.5F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaRenderHelper.prepareGeoDraw(false);
		int color = CrystalElement.getBlendedColor(te.getTicksExisted(), 50);
		GL11.glColor4f(ReikaColorAPI.getRed(color)/255F, ReikaColorAPI.getGreen(color)/255F, ReikaColorAPI.getBlue(color)/255F, 1);
		model.renderEdges(te);
		GL11.glPopAttrib();
		GL11.glPopMatrix();

		GL11.glPopMatrix();

		GL11.glPushMatrix();
		if (MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			this.renderCrystal(te, par2, par4, par6, par8);
			GL11.glPopAttrib();
			;//this.renderPaths(te);
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	private void renderCrystal(TileEntityRelaySource te, double par2, double par4, double par6, float par8) {
		ItemStack is = te.getStackInSlot(0);
		if (is != null) {
			ElementTagCompound tag = ItemStorageCrystal.getStoredTags(is);

			if (!tag.isEmpty()) {

				Tessellator v5 = Tessellator.instance;
				ReikaTextureHelper.bindTerrainTexture();

				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND);
				ReikaRenderHelper.disableEntityLighting();

				BlendMode.ADDITIVEDARK.apply();

				GL11.glPushMatrix();

				double d = 0.5;

				GL11.glTranslated(d, d, d);

				double t = (te.getTicksExisted()+par8);

				double w = 0.175/4;
				double th = 0.2875/2;
				double h2 = 0.1875/2;

				float max = ItemStorageCrystal.getCapacity(is);

				IIcon ico = ChromaBlocks.CRYSTAL.getBlockInstance().getIcon(0, 0);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();

				int i = 0;
				double div = 360/tag.elementSet().size();

				for (CrystalElement e : tag.elementSet()) {
					float frac = ItemStorageCrystal.getStoredEnergy(is, e)/max;

					double h = th*frac;

					GL11.glPushMatrix();

					//i = e.ordinal();
					//div = 22.5;

					int c = e.getColor();//ReikaColorAPI.mixColors(e.getColor(), 0xb0b0b0, frac);
					int a = 255;//127;

					double y = 0.1*Math.sin(t/8D+i);
					double ang = (t)%360;
					GL11.glRotated(ang+i*div, 0, 1, 0);
					GL11.glTranslated(0, y, 0);
					GL11.glTranslated(d, d, d);
					GL11.glRotated(ang*4, 0, 1, 0);
					GL11.glTranslated(-d, -d, -d);

					v5.startDrawingQuads();
					v5.setColorRGBA_I(c, a);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, u, v);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, v);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, u, dv);

					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, u, dv);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, v);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, u, v);

					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, u, v);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, v);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, u, dv);

					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, u, dv);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, v);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, u, v);
					v5.draw();

					v5.startDrawing(GL11.GL_TRIANGLE_FAN);
					v5.setColorRGBA_I(c, a);
					v5.addVertexWithUV(0.5, 0.5+h+h2, 0.5, u, dv);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
					v5.draw();

					v5.startDrawing(GL11.GL_TRIANGLE_FAN);
					v5.setColorRGBA_I(c, a);
					v5.addVertexWithUV(0.5, 0.5-h-h2, 0.5, u, dv);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, du, dv);
					v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, du, dv);
					v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, du, dv);
					v5.draw();

					GL11.glPopMatrix();
					i++;
				}

				GL11.glPopMatrix();

			}
		}
	}


}
