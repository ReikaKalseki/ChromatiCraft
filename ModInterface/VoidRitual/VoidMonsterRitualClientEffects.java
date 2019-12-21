package Reika.ChromatiCraft.ModInterface.VoidRitual;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual.Effects;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VoidMonsterRitualClientEffects implements TickHandler {

	public static final VoidMonsterRitualClientEffects instance = new VoidMonsterRitualClientEffects();

	private final HashSet<EffectVisual> active = new HashSet();

	private VoidMonsterRitualClientEffects() {
		new SphereVisual();
		new DistortionVisual();
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		if (!active.isEmpty() && !Minecraft.getMinecraft().isGamePaused()) {
			Iterator<EffectVisual> it = active.iterator();
			while (it.hasNext()) {
				EffectVisual e = it.next();
				//ReikaJavaLibrary.pConsole("Ticking active visual "+e);
				if (e.tick()) {
					ReikaJavaLibrary.pConsole("Removing "+e);
					e.clearShader();
					it.remove();
				}
				else {
					e.updateShaderEnabled();
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "voidritual";
	}

	public void setShaderFoci(Entity el) {
		for (Effects e : Effects.list) {
			if (e.visuals != null)
				e.visuals.setShaderFocus(el);
		}
	}

	public static abstract class EffectVisual {

		public final Effects cause;
		public final boolean hasTerrainShader;
		private final float shaderDecayFactor;
		private final float shaderDecayLinear;

		protected final HashMap<String, Object> shaderData = new HashMap();
		protected float shaderIntensity = 0;

		private static final Collection<EffectVisual> terrainShaderEffects = new HashSet();

		private EffectVisual(Effects e, boolean shader) {
			this(e, shader, 0, 0);
		}

		private EffectVisual(Effects e, boolean shader, float f, float l) {
			cause = e;
			cause.visuals = this;
			hasTerrainShader = shader;
			shaderDecayFactor = f;
			shaderDecayLinear = l;
			if (hasTerrainShader) {
				terrainShaderEffects.add(this);
			}
		}

		public static Collection<EffectVisual> getTerrainShaders() {
			return Collections.unmodifiableCollection(terrainShaderEffects);
		}

		final void activate(EntityLiving e) {
			shaderIntensity = 1;
			this.initShaderData(e);
			instance.active.add(this);
			ReikaJavaLibrary.pConsole("Activating "+this);
		}

		public final void clearShader() {
			shaderData.clear();
			shaderIntensity = 0;
			this.updateShaderEnabled();
		}

		protected final void fadeShader() {
			shaderIntensity = Math.max(0, shaderIntensity*shaderDecayFactor-shaderDecayLinear);
		}

		public final Map<String, Object> getShaderData() {
			return Collections.unmodifiableMap(shaderData);
		}

		public final float getShaderIntensity() {
			return shaderIntensity;
		}

		protected abstract boolean tick();

		protected abstract void initShaderData(EntityLiving e);

		protected abstract void updateShaderEnabled();

		protected abstract void setShaderFocus(Entity e);

		@Override
		public final String toString() {
			return "Visual effect for "+cause+" ["+hasTerrainShader+"] F="+shaderIntensity+" with data "+shaderData;
		}

	}

	private static class SphereVisual extends EffectVisual {

		private SphereVisual() {
			super(Effects.COLLAPSING_SPHERE, false);
		}

		@Override
		protected boolean tick() {
			float r = (float)shaderData.get("size");
			r = r*1.005F+0.005F;
			shaderData.put("size", r);
			float t = (float)shaderData.get("thickness");
			shaderData.put("thickness", t*1.05F);
			if (r >= 0.5)
				shaderIntensity = 0;
			else if (r >= 0.35)
				shaderIntensity *= 0.75;
			ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
			return shaderIntensity <= 0;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("size", 0F);
			shaderData.put("thickness", 0.0125F);
			ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
		}

		@Override
		protected void updateShaderEnabled() {
			ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
		}

		@Override
		protected void setShaderFocus(Entity e) {
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFocus(e);
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().setMatricesToCurrent();
		}

	}

	private static class DistortionVisual extends EffectVisual {

		private DistortionVisual() {
			super(Effects.DISTORTION, true);
		}

		@Override
		protected boolean tick() {
			float r = (float)shaderData.get("radius");
			float t = (float)shaderData.get("thickness");
			float a = (float)shaderData.get("amplitude");
			r = r+1.25F;
			r *= 1.01F;
			t = MathHelper.clamp_float(r/6, 2, 15F);
			a = MathHelper.clamp_float(r/24, 1, 4F);
			shaderData.put("radius", r);
			shaderData.put("thickness", t);
			shaderData.put("amplitude", a);
			if (r > 200) {
				shaderIntensity *= 0.95F;
			}
			return shaderIntensity < 0.05;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("radius", 0F);
			shaderData.put("thickness", 2F);
			shaderData.put("amplitude", 1F);
		}

		@Override
		protected void updateShaderEnabled() {

		}

		@Override
		protected void setShaderFocus(Entity e) {
			ChromaShaders.VOIDRITUAL$WORLD.getShader().setFocus(e);
			ChromaShaders.VOIDRITUAL$WORLD.getShader().setMatricesToCurrent();
		}

	}

}
