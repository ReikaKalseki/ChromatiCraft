/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.RayTracer;

public class RenderGlowCloud extends Render {

	private static final RayTracer LOS = RayTracer.getVisualLOS();

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		if (e.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			LOS.setOrigins(e.posX, e.posY, e.posZ, ep.posX, ep.posY, ep.posZ);
			if (LOS.isClearLineOfSight(e.worldObj)) {
				GL11.glPushMatrix();
				GL11.glTranslated(par2, par4, par6);
				ChromaShaders.DIMGLOWCLOUD.setIntensity(1);
				ChromaShaders.DIMGLOWCLOUD.clearOnRender = true;
				ChromaShaders.DIMGLOWCLOUD.getShader().addFocus(e);
				HashMap<String, Object> vars = new HashMap();
				double dist = e.getDistanceToEntity(ep);
				float f = 0;
				if (e.posY < 0) {
					f = 1;
					dist *= 0.5;
				}
				else {
					if (dist <= 32) {
						f = 1;
					}
					else if (dist <= 128) {
						f = (float)((dist-32)/96);
					}
				}
				vars.put("distance", dist);
				float f2 = 1;
				if (e.posY < 10) {
					f2 = 2.5F;
				}
				else if (e.posY < 20) {
					f2 = 1+1.5F*(float)((e.posY-10)/10);
				}
				vars.put("factor", f2);
				ChromaShaders.DIMGLOWCLOUD.getShader().modifyLastCompoundFocus(f, vars);
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
