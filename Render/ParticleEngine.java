/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

import org.lwjgl.opengl.GL11;

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
import Reika.DragonAPI.IO.ThrottleableEffectRenderer;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.ListFactory;
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
public class ParticleEngine extends EffectRenderer {

	public static final ParticleEngine instance = new ParticleEngine();

	private final MultiMap<RenderKey, EntityFX> particles = new MultiMap(new ListFactory()).setNullEmpty();
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
		return particles.totalSize()+" Particles, "+keyMap.size()+" keys";
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

		for (RenderKey rm : particles.keySet()) {
			Collection<EntityFX> parts = particles.get(rm);
			if (!parts.isEmpty()) {
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

				rm.apply();

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
					if (rm.mode.flags[RenderModeFlags.LIGHT.ordinal()]) {
						v5.setBrightness(fx.getBrightnessForRender(frame));
					}
					else {
						v5.setBrightness(240);
					}
					fx.renderParticle(v5, frame, yaw, f5, pitch, f3, f4);
				}

				v5.draw();

				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
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
		particles.addValue(rm, fx, true);
		Collection<EntityFX> parts = particles.get(rm);
		if (parts.size() >= MAX_PARTICLES) {
			((List)parts).remove(0);
		}
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
		for (RenderKey rm : particles.keySet()) {
			Collection<EntityFX> parts = particles.get(rm);
			Iterator<EntityFX> it = parts.iterator();
			while (it.hasNext()) {
				try {
					EntityFX fx = it.next();
					if (fx != null)
						fx.onUpdate();
					if (fx == null || fx.isDead)
						it.remove();
				}
				catch (ConcurrentModificationException e) {

				}
			}
		}
		isTicking = false;
	}

	@Override
	public void clearEffects(World world) {
		particles.clear();
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

		/*
		DEFAULT(true, false, true, true),
		ADDITIVEDARK(true, true, false, true),
		LIT(true, false, false, true);

		private final boolean useAlpha;
		private final boolean additiveBlend;
		private final boolean useLighting;
		private final boolean depthTest;

		private RenderMode(boolean alpha, boolean add, boolean light, boolean depth) {
			useAlpha = alpha;
			additiveBlend = add;
			useLighting = light;
			depthTest = depth;
		}
		 */

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
