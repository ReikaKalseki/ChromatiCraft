package Reika.ChromatiCraft.ModInterface.VoidRitual;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual.Effects;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;

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
		if (!active.isEmpty()) {
			Iterator<EffectVisual> it = active.iterator();
			while (it.hasNext()) {
				EffectVisual e = it.next();
				if (e.tick()) {
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

	}

	private static class SphereVisual extends EffectVisual {

		private SphereVisual() {
			super(Effects.COLLAPSING_SPHERE, false);
		}

		@Override
		protected boolean tick() {
			float r = (float)shaderData.get("size");
			r -= 0.025F;
			shaderData.put("size", r);
			if (r <= 0)
				shaderIntensity = 0;
			ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
			ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
			return r <= 0;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			float r = 2;
			shaderData.put("size", r);
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
			int has = (int)shaderData.get("wavePhase");
			has++;
			shaderData.put("wavePhase", has);
			return has >= 200;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("wavePhase", -200);
		}

		@Override
		protected void updateShaderEnabled() {

		}

		@Override
		protected void setShaderFocus(Entity e) {
			shaderData.put("wavePhase", -200);
		}

	}

}
