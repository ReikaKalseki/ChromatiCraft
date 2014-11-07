/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelItemStand;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

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
		if (MinecraftForgeClient.getRenderPass() == 0 || !te.isInWorld())
			this.renderModel(te, model);

		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityItemStand te, double par2, double par4, double par6, float ptick) {
		EntityItem ei = te.getItem();
		ItemStack is = te.getStackInSlot(0);
		if (ei != null && is != null) {

			if (MinecraftForgeClient.getRenderPass() == 0) {
				GL11.glPushMatrix();
				double ang = ((te.getTicksExisted()+ptick)*3D)%360;
				double dy = 0.0625*Math.sin(Math.toRadians(ang*2));
				GL11.glTranslated(0.5, 0.625+dy, 0.5);
				GL11.glTranslated(par2, par4, par6);
				GL11.glRotated(ang, 0, 1, 0);
				GL11.glTranslated(-par2, -par4, -par6);

				Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
				r.doRender(ei, par2, par4, par6, 0, 0);

				GL11.glPopMatrix();
			}
			else if (ChromaItems.TOOL.matchWith(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem())) {
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
