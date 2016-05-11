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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityMeteorTower;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderMeteorTower extends ChromaRenderBase {

	private final ChromaIcons[] ACTIVE_ICONS = {
			ChromaIcons.BIGFLARE,
			ChromaIcons.FADE_STAR,
	};

	private final ChromaIcons[] INACTIVE_ICONS = {
			ChromaIcons.FADE_STAR,
	};

	private final ChromaIcons[] ITEM_ICONS = {
			ChromaIcons.FADE_STAR,
			//ChromaIcons.RIFTHALO,
	};

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMeteorTower te = (TileEntityMeteorTower)tile;

		if (tile.hasWorldObj() && (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles())) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDepthMask(false);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glDisable(GL11.GL_LIGHTING);
			//GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;
			GL11.glTranslated(0.5, 0.5, 0.5);

			double s = 0.875;

			ChromaIcons[] icons = ACTIVE_ICONS;

			if (!te.canConduct() && !StructureRenderer.isRenderingTiles()) {
				s = 0.5;
				icons = INACTIVE_ICONS;
			}

			double hmin = te.canConduct() ? -1.5 : 0;
			double hmax = te.canConduct() ? 2 : 0;
			for (double h = hmin; h <= hmax; h += 0.5) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, h, 0);

				for (int i = 0; i < icons.length; i++) {
					double z = -0.005*i;
					IIcon ico = icons[i].getIcon();
					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();
					GL11.glPushMatrix();
					boolean last = i == icons.length-1;
					double s1 = s*(1-0.75F*i/icons.length);
					GL11.glScaled(s1, s1, s1);
					if (StructureRenderer.isRenderingTiles()) {
						GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
						GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
					}
					else {
						RenderManager rm = RenderManager.instance;
						GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
						GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
					}

					int alpha = 255;//te.getEnergy()*255/te.MAX_ENERGY;
					//ReikaJavaLibrary.pConsole(te.getEnergy());

					int color = last && icons.length > 1 ? 0xffffff : TileEntityMeteorTower.COLORS[te.getTier()];

					v5.startDrawingQuads();
					v5.setColorRGBA_I(color, alpha);
					v5.addVertexWithUV(-1, -1, z, u, v);
					v5.addVertexWithUV(1, -1, z, du, v);
					v5.addVertexWithUV(1, 1, z, du, dv);
					v5.addVertexWithUV(-1, 1, z, u, dv);
					v5.draw();

					GL11.glPopMatrix();
				}

				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		else if (!tile.hasWorldObj()) {
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glDisable(GL11.GL_LIGHTING);
			//ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-45, 1, 0, 0);
			Tessellator v5 = Tessellator.instance;

			for (int i = 0; i < ITEM_ICONS.length; i++) {
				IIcon ico = ITEM_ICONS[i].getIcon();
				boolean last = i == ITEM_ICONS.length-1;
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				double s = last ? 0.875 : 1;

				v5.startDrawingQuads();
				v5.setColorOpaque_I(TileEntityMeteorTower.COLORS[te.getTier()]);
				v5.addVertexWithUV(-s, -s, 0, u, v);
				v5.addVertexWithUV(s, -s, 0, du, v);
				v5.addVertexWithUV(s, s, 0, du, dv);
				v5.addVertexWithUV(-s, s, 0, u, dv);
				v5.draw();

			}

			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			//RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}
