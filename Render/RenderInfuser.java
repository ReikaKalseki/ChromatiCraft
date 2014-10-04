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
