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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.World.Dimension.Rendering.Aurora;


public class RenderAurora extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		EntityAurora ea = (EntityAurora)e;
		Aurora a = ea.getAurora();
		if (a != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2-e.posX, par4-e.posY, par6-e.posZ);
			a.render();
			GL11.glPopMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
