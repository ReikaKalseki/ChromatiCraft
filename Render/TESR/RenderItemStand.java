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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelItemStand;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderItemStand extends ChromaRenderBase {

	private final ModelItemStand model = new ModelItemStand();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "itemstand.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityItemStand te = (TileEntityItemStand)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			this.renderItem(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		if (MinecraftForgeClient.getRenderPass() == 0 || !te.isInWorld() || StructureRenderer.isRenderingTiles()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			this.renderModel(te, model, te.getItem());
			GL11.glPopAttrib();
		}

		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityItemStand te, double par2, double par4, double par6, float ptick) {
		EntityItem ei = te.getItem();
		ItemStack is = te.getStackInSlot(0);
		if (ei != null && is != null) {

			if (MinecraftForgeClient.getRenderPass() == 0) {
				GL11.glPushMatrix();

				Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
				int num = 1;
				if (is.stackSize >= 32) {
					num = 6;
				}
				else if (is.stackSize >= 18) {
					num = 5;
				}
				else if (is.stackSize >= 8) {
					num = 4;
				}
				else if (is.stackSize >= 4) {
					num = 3;
				}
				else if (is.stackSize >= 2) {
					num = 2;
				}
				float tick = te.getTicksExisted()+ptick;
				double s = num > 1 ? 1D/Math.pow(num, 0.25) : 1;
				double d = 0.3125;//-0.25/s;
				double d2 = 0;
				for (int i = 0; i < num; i++) {
					GL11.glPushMatrix();
					double ang = (tick*3D)%360+i*360D/num;
					double dy = 0.0625*Math.sin(Math.toRadians(ang*2));
					GL11.glTranslated(par2, par4, par6);
					GL11.glTranslated(0.5, (0.625+dy), 0.5);
					GL11.glRotated(ang, 0, 1, 0);
					if (num > 1) {
						GL11.glScaled(s, s, s);
						GL11.glTranslated(d, 0, d2);
						GL11.glRotated(tick*4+i*90D/num, 0, 1, 0);
						GL11.glTranslated(-d, 0, -d2);
					}
					GL11.glTranslated(-par2, -par4, -par6);
					if (num > 1) {
						GL11.glTranslated(0.3125, 0, 0);
					}
					r.doRender(ei, par2, par4, par6, 0, 0);
					GL11.glPopMatrix();
				}

				GL11.glPopMatrix();
			}
			else if (HoldingChecks.MANIPULATOR.isClientHolding()) {
				GL11.glPushMatrix();
				double s = 0.03125;
				GL11.glTranslated(par2, par4, par6);
				double a = 0.5;
				double b = 1.25;
				double c = 0.5;
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glTranslated(a, b, c);
				GL11.glRotated(180-RenderManager.instance.playerViewY, 0, 1, 0);
				GL11.glRotated(-RenderManager.instance.playerViewX/2D, 1, 0, 0);
				GL11.glDepthMask(false);
				GL11.glScaled(s, -s, 1);
				String sg = String.valueOf(is.stackSize);
				this.getFontRenderer().drawStringWithShadow(sg, -this.getFontRenderer().getStringWidth(sg)/2, 0, 0xffffff);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_LIGHTING);
				ReikaRenderHelper.enableEntityLighting();
				GL11.glPopMatrix();
			}
		}
	}

}
