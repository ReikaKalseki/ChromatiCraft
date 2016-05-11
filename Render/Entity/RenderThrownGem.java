/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Items.Tools.ItemThrowableGem;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderThrownGem extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		EntityThrownGem eb = (EntityThrownGem)e;
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(par2, par4, par6);
		if (!e.isDead) {
			RenderManager rm = RenderManager.instance;
			double dx = e.posX-RenderManager.renderPosX;
			double dy = e.posY-RenderManager.renderPosY;
			double dz = e.posZ-RenderManager.renderPosZ;
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			GL11.glRotated(angs[2], 0, 1, 0);
			GL11.glRotated(90-angs[1], 1, 0, 0);
		}
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		double s1 = 0.5;
		double d = 0.001;
		float u = 0;
		float v = 0;
		float du = 0;
		float dv = 0;
		if (eb.hasImpacted()) {
			BlendMode.ADDITIVEDARK.apply();
			ReikaTextureHelper.bindTerrainTexture();
			IIcon icon = ChromaIcons.GUARDIANINNER.getIcon();
			u = icon.getMinU();
			v = icon.getMinV();
			du = icon.getMaxU();
			dv = icon.getMaxV();
		}
		else {
			BlendMode.DEFAULT.apply();
			ItemStack is = ChromaItems.THROWGEM.getStackOf(eb.getColor());
			ItemThrowableGem i = (ItemThrowableGem)is.getItem();
			ReikaTextureHelper.bindTexture(i.getTextureReferenceClass(), i.getTexture(is));
			int idx = i.getItemSpriteIndex(is);
			u = (idx%16)/16F;
			du = u+1/16F;
			v = (idx/16)/16F;
			v += (System.currentTimeMillis()/200)%16;
			dv = v+1/16F;
		}
		v5.startDrawingQuads();
		v5.setBrightness(240);
		int c1 = 0xffffff;//eb.getRenderColor();
		v5.setColorOpaque_I(c1);
		v5.addVertexWithUV(-s1, -s1, 0, u, v);
		v5.addVertexWithUV(s1, -s1, 0, du, v);
		v5.addVertexWithUV(s1, s1, 0, du, dv);
		v5.addVertexWithUV(-s1, s1, 0, u, dv);
		v5.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
