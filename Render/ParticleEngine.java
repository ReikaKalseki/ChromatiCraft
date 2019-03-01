/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomRenderFX;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireSmokeFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Extras.ThrottleableEffectRenderer;
import Reika.DragonAPI.Extras.ThrottleableEffectRenderer.CustomEffectRenderer;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleEngine extends EffectRenderer implements CustomEffectRenderer {

	public static final ParticleEngine instance = new ParticleEngine();

	private final HashMap<RenderKey, ParticleList> particles = new HashMap();
	private final PluralMap<RenderKey> keyMap = new PluralMap(2);

	private Random rand = new Random();

	public static final int MAX_PARTICLES = ThrottleableEffectRenderer.getRegisteredInstance().limit;

	public final TextureMode blockTex = new VanillaTextureMode(TextureMap.locationBlocksTexture);
	public final TextureMode itemTex = new VanillaTextureMode(TextureMap.locationItemsTexture);
	public final TextureMode particleTex = new VanillaTextureMode(new ResourceLocation("textures/particle/particles.png"));

	private final RenderKey DEFAULT_RENDER = new RenderKey(particleTex, new RenderMode());

	private boolean isRendering;
	private boolean isTicking;

	private ParticleEngine() {
		super(null, null);
	}

	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityBlurFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFloatingSeedsFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFireFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFireSmokeFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityRuneFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityLaserFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityCenterBlurFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityGlobeFX.class, this);
	}

	@Override
	public String getStatistics() {
		return this.getParticleCount()+" Particles, "+keyMap.size()+" keys";
	}

	@Override
	public void renderParticles(Entity entity, float frame) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);

		isRendering = true;

		for (ParticleList parts : particles.values()) {
			parts.render(entity, frame);
		}

		isRendering = false;

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void addEffect(EntityFX fx) {
		//int layer = fx.getFXLayer();
		//int dim = world.provider.dimensionId;
		if (isRendering) {
			ChromatiCraft.logger.logError("Tried adding a particle mid-render!");
			Thread.dumpStack();
			return;
		}
		if (isTicking) {
			ChromatiCraft.logger.logError("Tried adding a particle mid-update!");
			Thread.dumpStack();
			return;
		}
		RenderKey rm = DEFAULT_RENDER;
		if (fx instanceof CustomRenderFX) {
			rm = this.getOrCreateKey(((CustomRenderFX)fx).getTexture(), ((CustomRenderFX)fx).getRenderMode());
		}
		this.addParticle(rm, fx);
	}

	private void addParticle(RenderKey rm, EntityFX fx) {
		ParticleList li = particles.get(rm);
		if (li == null) {
			li = new ParticleList(rm);
			particles.put(rm, li);
		}
		li.addParticle(fx);
	}

	private RenderKey getOrCreateKey(TextureMode tex, RenderMode rm) {
		RenderKey rk = keyMap.get(tex, rm);
		if (rk == null) {
			rk = new RenderKey(tex, rm);
			keyMap.put(rk, tex, rm);
		}
		return rk;
	}

	@Override
	public void updateEffects() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc.theWorld == null)
			return;
		isTicking = true;
		int dim = mc.theWorld.provider.dimensionId;
		for (ParticleList li : particles.values()) {
			li.tick();
		}
		isTicking = false;
	}

	@Override
	public void clearEffects(World world) {
		particles.clear();
	}

	@Override
	public int getParticleCount() {
		int ret = 0;
		for (ParticleList li : particles.values()) {
			ret += li.particles.size(); //not count since this is for debug
		}
		return ret;
	}

	private static class ParticleList {

		private final RenderKey key;
		private final ArrayList<ParticleEntry> particles = new ArrayList();
		private int effectiveCount = 0;

		private ParticleList(RenderKey rk) {
			key = rk;
		}

		private void render(Entity entity, float frame) {
			if (!particles.isEmpty()) {
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

				key.apply();

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

				for (ParticleEntry p : particles) {
					EntityFX fx = p.effect;
					if (ThrottleableEffectRenderer.isParticleVisible(fx)) {
						if (ThrottleableEffectRenderer.isEntityCloseEnough(fx, EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ)) {
							if (key.mode.flags[RenderModeFlags.LIGHT.ordinal()]) {
								v5.setBrightness(fx.getBrightnessForRender(frame));
							}
							else {
								v5.setBrightness(240);
							}
							fx.renderParticle(v5, frame, yaw, f5, pitch, f3, f4);
						}
					}
				}

				v5.draw();

				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
		}

		private boolean isEmpty() {
			return particles.isEmpty();
		}

		private void addParticle(EntityFX fx) {
			ParticleEntry e = new ParticleEntry(fx);
			particles.add(e);
			if (e.countsToLimit)
				effectiveCount++;

			if (effectiveCount >= MAX_PARTICLES) {
				int i = 0;
				ParticleEntry rfx = particles.get(i);
				while (!rfx.countsToLimit && i < particles.size()-1) {
					i++;
					rfx = particles.get(i);
				}
				particles.remove(i);
				effectiveCount--;
			}
		}

		private void tick() {
			Iterator<ParticleEntry> it = particles.iterator();
			while (it.hasNext()) {
				try {
					ParticleEntry fx = it.next();
					if (fx != null) {
						fx.effect.onUpdate();
					}
					if (fx.effect == null || fx.effect.isDead) {
						it.remove();
						effectiveCount--;
					}
				}
				catch (ConcurrentModificationException e) {
					ChromatiCraft.logger.log("CME thrown updating particle type "+key+"!");
					//e.printStackTrace();
				}
			}
		}

	}

	private static class ParticleEntry {

		private final EntityFX effect;
		private boolean countsToLimit;

		private ParticleEntry(EntityFX fx) {
			effect = fx;
			boolean flag = true;
			if (fx instanceof CustomRenderFX) {
				flag = !((CustomRenderFX)fx).rendersOverLimit();
			}
			countsToLimit = flag;
		}

	}

	private static class RenderKey {

		private final RenderMode mode;
		private final TextureMode texture;

		private RenderKey(TextureMode s, RenderMode rm) {
			texture = s;
			mode = rm;
		}

		private void apply() {
			mode.apply();
			texture.bind();
		}

		@Override
		public int hashCode() {
			return texture.hashCode() ^ mode.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RenderKey) {
				RenderKey r = (RenderKey)o;
				return texture.equals(r.texture) && mode.equals(r.mode);
			}
			return false;
		}

	}

	public static abstract class TextureMode {

		protected abstract void bind();

		@Override
		public final boolean equals(Object o) {
			return o.getClass() == this.getClass() && this.isTextureSame((TextureMode)o);
		}

		protected abstract boolean isTextureSame(TextureMode o);

		@Override
		public abstract int hashCode();

	}

	public static final class CustomTextureMode extends TextureMode {

		private final Class reference;
		private final String texture;

		public CustomTextureMode(Class c, String t) {
			reference = c;
			texture = t;
		}

		@Override
		protected void bind() {
			ReikaTextureHelper.bindFinalTexture(reference, texture);
		}

		@Override
		protected boolean isTextureSame(TextureMode o) {
			CustomTextureMode cm = (CustomTextureMode)o;
			return cm.reference == reference && texture.equals(cm.texture);
		}

		@Override
		public int hashCode() {
			return reference.hashCode() ^ texture.hashCode();
		}

	}

	private static final class VanillaTextureMode extends TextureMode {

		private final ResourceLocation resource;

		private VanillaTextureMode(ResourceLocation loc) {
			resource = loc;
		}

		@Override
		protected void bind() {
			Minecraft.getMinecraft().renderEngine.bindTexture(resource);
		}

		@Override
		protected boolean isTextureSame(TextureMode o) {
			return ((VanillaTextureMode)o).resource.equals(resource);
		}

		@Override
		public int hashCode() {
			return resource.hashCode();
		}

	}

	public static class RenderMode {

		private final boolean[] flags = new boolean[RenderModeFlags.list.length];

		public RenderMode() {
			for (int i = 0; i < flags.length; i++) {
				flags[i] = RenderModeFlags.list[i].defaultValue;
			}
		}

		public RenderMode setFlag(RenderModeFlags f, boolean flag) {
			flags[f.ordinal()] = flag;
			return this;
		}

		@Override
		public int hashCode() {
			return ReikaArrayHelper.booleanToBitflags(flags);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RenderMode) {
				RenderMode r = (RenderMode)o;
				return Arrays.equals(flags, r.flags);
			}
			return false;
		}

		@Override
		public String toString() {
			return Arrays.toString(flags);
		}

		private void apply() {
			for (int i = 0; i < flags.length; i++) {
				RenderModeFlags.list[i].apply(flags[i]);
			}
		}

		/*
		private void apply() {
			if (useLighting) {
				GL11.glEnable(GL11.GL_LIGHTING);
				ReikaRenderHelper.enableEntityLighting();
			}
			else {
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
			}

			if (useAlpha) {
				GL11.glEnable(GL11.GL_BLEND);
			}
			else {
				GL11.glDisable(GL11.GL_BLEND);
			}

			if (additiveBlend) {
				BlendMode.ADDITIVEDARK.apply();
			}
			else {
				BlendMode.DEFAULT.apply();
			}

			if (depthTest) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			else {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}
		}
		 */

	}

	public static enum RenderModeFlags {
		ALPHA(true),
		LIGHT(true),
		ADDITIVE(false),
		DEPTH(true),
		ALPHACLIP(true);

		private final boolean defaultValue;

		private static final RenderModeFlags[] list = values();

		private RenderModeFlags(boolean f) {
			defaultValue = f;
		}

		private int getFlag() {
			return 1 << this.ordinal();
		}

		private void apply(boolean set) {
			switch(this) {
				case LIGHT:
					if (set) {
						GL11.glEnable(GL11.GL_LIGHTING);
						ReikaRenderHelper.enableEntityLighting();
					}
					else {
						GL11.glDisable(GL11.GL_LIGHTING);
						ReikaRenderHelper.disableEntityLighting();
					}
					break;

				case ALPHA:
					if (set) {
						GL11.glEnable(GL11.GL_BLEND);
					}
					else {
						GL11.glDisable(GL11.GL_BLEND);
					}
					break;

				case ADDITIVE:
					if (set) {
						BlendMode.ADDITIVEDARK.apply();
					}
					else {
						BlendMode.DEFAULT.apply();
					}
					break;

				case DEPTH:
					if (set && !ThrottleableEffectRenderer.renderThroughWalls()) {
						GL11.glEnable(GL11.GL_DEPTH_TEST);
					}
					else {
						GL11.glDisable(GL11.GL_DEPTH_TEST);
					}
					break;

				case ALPHACLIP:
					if (set) {
						GL11.glEnable(GL11.GL_ALPHA_TEST);
					}
					else {
						GL11.glDisable(GL11.GL_ALPHA_TEST);
					}
					break;
			}
		}
	}
}
