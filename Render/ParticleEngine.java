/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleEngine {

	public static final ParticleEngine instance = new ParticleEngine();

	private MultiMap<Integer, EntityFX>[] particles = new MultiMap[3];

	private Random rand = new Random();

	public static final int MAX_PARTICLES = 2000;

	private ParticleEngine() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new MultiMap();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		float frame = event.partialTicks;

		Entity entity = Minecraft.getMinecraft().thePlayer;
		TextureManager renderer = Minecraft.getMinecraft().renderEngine;
		int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/particles.png");

		GL11.glPushMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);

		for (int layer = 0; layer < particles.length; layer++) {
			Collection<EntityFX> parts = particles[layer].get(dim);
			if (!parts.isEmpty()) {
				GL11.glPushMatrix();

				switch (layer) {
				case 0:
					BlendMode.ADDITIVE2.apply();
					break;
				case 1:
					BlendMode.DEFAULT.apply();
				}

				float yaw = ActiveRenderInfo.rotationX;
				float pitch = ActiveRenderInfo.rotationZ;

				float f3 = ActiveRenderInfo.rotationYZ;
				float f4 = ActiveRenderInfo.rotationXY;
				float f5 = ActiveRenderInfo.rotationXZ;

				EntityFX.interpPosX = entity.lastTickPosX+(entity.posX-entity.lastTickPosX)*frame;
				EntityFX.interpPosY = entity.lastTickPosY+(entity.posY-entity.lastTickPosY)*frame;
				EntityFX.interpPosZ = entity.lastTickPosZ+(entity.posZ-entity.lastTickPosZ)*frame;

				Tessellator v5 = Tessellator.instance;
				v5.startDrawingQuads();

				for (EntityFX fx : parts) {
					v5.setBrightness(fx.getBrightnessForRender(frame));
					fx.renderParticle(v5, frame, yaw, f5, pitch, f3, f4);
				}

				v5.draw();

				GL11.glPopMatrix();
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
	}

	public void addEffect(World world, EntityFX fx) {
		int layer = fx.getFXLayer();
		int dim = world.provider.dimensionId;
		Collection<EntityFX> parts = particles[layer].get(dim);
		particles[layer].addValue(dim, fx);
		if (parts.size() >= MAX_PARTICLES) {
			Iterator it = parts.iterator();
			it.next();
			it.remove(); //remove oldest
		}
	}

	@SubscribeEvent
	public void updateParticles(TickEvent.ClientTickEvent event) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc.theWorld == null)
			return;
		int dim = mc.theWorld.provider.dimensionId;
		if (event.phase == TickEvent.Phase.START) {
			for (int layer = 0; layer < particles.length; layer++) {
				Collection<EntityFX> parts = particles[layer].get(dim);
				Iterator<EntityFX> it = parts.iterator();
				while (it.hasNext()) {
					EntityFX fx = it.next();
					if (fx != null)
						fx.onUpdate();
					if (fx == null || fx.isDead)
						it.remove();
				}
			}
		}
	}
}
