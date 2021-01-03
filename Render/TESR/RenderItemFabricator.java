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

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFabricator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderItemFabricator extends ChromaRenderBase {

	private final ModelFabricator model = new ModelFabricator();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fabricator.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityItemFabricator te = (TileEntityItemFabricator)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1)
			this.renderItem(te, par8);
		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityItemFabricator te, float par8) {
		EntityItem ei = te.getEntityItem();
		if (ei != null) {
			double s = 2;
			double s2 = s;//0.25;
			float tick = par8+te.getTicksExisted();
			ei.age = 0;
			ei.hoverStart = MathHelper.sin(tick/4F * 0.2F + 0.2F);
			ei.rotationYaw = 0;
			//for (double dt = -0.0625; dt <= 0.0625; dt += 0.0625) {
			boolean blend = true;//Math.abs(dt) < 0.125;
			GL11.glPushMatrix();
			//double s = 2;
			GL11.glTranslated(0.5, 0.75, 0.5);
			GL11.glRotated(tick*2, 0, 1, 0);
			GL11.glScaled(s, s, s2);
			//GL11.glTranslated(0, 0, dt);
			GL11.glEnable(GL11.GL_BLEND);
			double c = 0.2;
			double a = blend ? 1 : 0.125;
			GL11.glColor4d(c, c, c, a);
			if (blend) {
				//GL11.glDisable(GL11.GL_CULL_FACE);
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				BlendMode.ADDITIVE.apply();
			}
			ReikaRenderHelper.disableEntityLighting();
			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			RenderItem.renderInFrame = false;
			ReikaRenderHelper.enableEntityLighting();
			if (blend) {
				BlendMode.DEFAULT.apply();
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				//GL11.glEnable(GL11.GL_CULL_FACE);
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			//}
		}
	}

}
