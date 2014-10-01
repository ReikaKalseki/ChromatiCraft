/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelInfuser;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraInfuser;
import Reika.DragonAPI.Interfaces.AnimatedSpritesheet;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class RenderInfuser extends ChromaRenderBase {

	private final ModelInfuser model = new ModelInfuser();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "infuser.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAuraInfuser te = (TileEntityAuraInfuser)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);

		if (te.isInWorld()) {
			this.renderChroma(te);
			this.renderItem(te);
		}
		GL11.glPopMatrix();
	}
	/*
	private void renderFire2(TileEntityAuraInfuser te) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		double angle = (System.currentTimeMillis()/15D)%360;

		double r = 0.5;
		double h = 0.1*r;
		double y = 0.5*(1+Math.sin(Math.toRadians(angle)));

		GL11.glTranslated(0, y, 0);

		GL11.glPushMatrix();

		GL11.glTranslated(0.5, 1.5, 0.5);
		GL11.glRotated(angle, 1, 0, 0);
		GL11.glTranslated(-0.5, -1.5, -0.5);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/runebar.png");

		for (int i = 0; i <= 1; i++) {

			GL11.glPushMatrix();

			GL11.glTranslated(0.5, 1.5, 0.5);
			GL11.glRotated(angle*i, 0, 0, 1);
			GL11.glTranslated(-0.5, -1.5, -0.5);

			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(angle, 0, 1, 0);
			GL11.glTranslated(-0.5, -0.5, -0.5);

			v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			for (int n = 0; n <= 360; n += 10) {
				double x = 0.5+r*Math.sin(Math.toRadians(n));
				double z = 0.5+r*Math.cos(Math.toRadians(n));
				double t = 2;
				double tu = t*n/360D;
				v5.addVertexWithUV(x, 1.5+h, z, tu, 0);
				v5.addVertexWithUV(x, 1.5-h, z, tu, 1);
			}
			v5.draw();
			GL11.glPopMatrix();

		}

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderFire4(TileEntityAuraInfuser te) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/temp/fire/done2.png");
		int r = 3;
		double u = 0;//(0.0001*System.currentTimeMillis())%8;
		double du = 1;//u+0.5;
		double v = ((System.currentTimeMillis()/64)%20)/20D;
		double dv = v+0.05;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		v5.addVertexWithUV(-r+0.5, 1, r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, r+0.5, du, v);

		v5.addVertexWithUV(-r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, -r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, -r+0.5, du, v);

		v5.addVertexWithUV(r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, r+0.5, du, v);

		v5.addVertexWithUV(-r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(-r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(-r+0.5, 1, r+0.5, du, v);

		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	 */
	private void renderItem(TileEntityAuraInfuser te) {
		ItemStack is = te.getStackInSlot(0);
		if (is != null) {
			float thick = 0.0625F;
			Tessellator v5 = Tessellator.instance;
			//int col = 0;
			//int row = 0;
			//ItemRenderer.renderItemIn2D(v5, 0.0625F+0.0625F*col, 0.0625F*row, 0.0625F*col, 0.0625F+0.0625F*row, 256, 256, thick);
			GL11.glEnable(GL11.GL_BLEND);
			//GL11.glDisable(GL11.GL_ALPHA_TEST);
			//GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glColor4f(1, 1, 1, 1.2F-te.getProgress()/(float)te.DURATION);

			IItemRenderer iir = MinecraftForgeClient.getItemRenderer(is, ItemRenderType.INVENTORY);
			Item item = is.getItem();
			if (item instanceof IndexedItemSprites) {
				IndexedItemSprites iis = (IndexedItemSprites)item;
				ReikaTextureHelper.bindTexture(iis.getTextureReferenceClass(), iis.getTexture(is));
				int index = iis.getItemSpriteIndex(is);
				int row = index/16;
				int col = index%16;

				if (item instanceof AnimatedSpritesheet) {
					AnimatedSpritesheet a = (AnimatedSpritesheet)item;
					if (a.useAnimatedRender(is)) {
						col = a.getColumn(is);
						int offset = (int)((System.currentTimeMillis()/32/a.getFrameSpeed())%a.getFrameCount());
						row = a.getBaseRow(is)+offset;
					}
				}

				float u = col/16F;
				float v = row/16F;

				double b = 0.25;
				double dx = 0.125;
				double dz = 0.905;
				double dy = 0.965-te.getProgress()*0.00005;
				double s = 0.8;
				GL11.glPushMatrix();
				//GL11.glRotated(ang, 1, 0, 0);
				GL11.glTranslated(dx, dy, dz);
				//GL11.glTranslated(0, b, 0);
				GL11.glRotatef(-90, 1, 0, 0);
				//GL11.glTranslated(0, -b, 0);
				GL11.glScaled(s, s, s);
				ItemRenderer.renderItemIn2D(v5, 0.0625F+0.0625F*col, 0.0625F*row, 0.0625F*col, 0.0625F+0.0625F*row, 256, 256, thick);
				GL11.glPopMatrix();
			}
			else if (iir != null) {
				;//iir.renderItem(ItemRenderType.INVENTORY, is, new RenderBlocks());
			}
			else {
				if (ReikaItemHelper.isBlock(is))
					ReikaTextureHelper.bindTerrainTexture();
				else
					ReikaTextureHelper.bindItemTexture();
				IIcon ico = item.getIcon(is, MinecraftForgeClient.getRenderPass());
				if (ico == null)
					return;
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				double b = 0.65;
				double dx = 0.1;
				double dz = 0.125;
				double dy = 0.925-te.getProgress()*0.00005;
				double s = 0.8;
				GL11.glPushMatrix();
				//GL11.glRotated(ang, 1, 0, 0);
				GL11.glTranslated(dx, dy, dz);
				//GL11.glTranslated(0, b, 0);
				GL11.glRotatef(90, 1, 0, 0);
				//GL11.glTranslated(0, -b, 0);
				GL11.glScaled(s, s, s);
				ItemRenderer.renderItemIn2D(v5, u, v, du, dv, 256, 256, thick);GL11.glPopMatrix();
			}
			GL11.glDisable(GL11.GL_BLEND);
			//GL11.glEnable(GL11.GL_CULL_FACE);
			//GL11.glEnable(GL11.GL_ALPHA_TEST);

		}
	}

	private void renderChroma(TileEntityAuraInfuser tile) {
		ReikaTextureHelper.bindTerrainTexture();
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaBlocks.CHROMA.getBlockInstance().getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		int l = tile.getLevel();
		double h = l > 0 ? 0.8125+l*0.125/tile.getCapacity() : 0.5;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(tile.getColor());
		v5.addVertexWithUV(0+0.0625, h, 1-0.0625, u, v);
		v5.addVertexWithUV(1-0.0625, h, 1-0.0625, du, v);
		v5.addVertexWithUV(1-0.0625, h, 0+0.0625, du, dv);
		v5.addVertexWithUV(0+0.0625, h, 0+0.0625, u, dv);
		v5.draw();
	}

}
