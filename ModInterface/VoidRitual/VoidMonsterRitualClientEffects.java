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

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VoidMonsterRitualClientEffects implements TickHandler {

	public static final VoidMonsterRitualClientEffects instance = new VoidMonsterRitualClientEffects();

	private final HashSet<EffectVisual> active = new HashSet();

	private VoidMonsterRitualClientEffects() {
		new SphereVisual();
		new WaveVisual();
		new CurlVisual();
		new StretchVisual();
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		if (!active.isEmpty() && !Minecraft.getMinecraft().isGamePaused()) {
			Iterator<EffectVisual> it = active.iterator();
			while (it.hasNext()) {
				EffectVisual e = it.next();
				//ReikaJavaLibrary.pConsole("Ticking active visual "+e);
				if (e.tick()) {
					//ReikaJavaLibrary.pConsole("Removing "+e);
					e.clearShader();
					it.remove();
				}
				else {
					e.getShader().setIntensity(e.shaderIntensity);
					e.getShader().getShader().setFields(e.shaderData);
					e.getShader().getShader().updateEnabled();
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
			if (e.visuals != null) {
				e.visuals.getShader().getShader().setFocus(el);
				e.visuals.getShader().getShader().setMatricesToCurrent();
			}
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
			this.getShader().setIntensity(shaderIntensity);
			this.getShader().getShader().setFields(shaderData);
			this.getShader().getShader().updateEnabled();
			instance.active.add(this);
			//ReikaJavaLibrary.pConsole("Activating "+this);
		}

		public final void clearShader() {
			shaderData.clear();
			this.getShader().getShader().clearData();
			shaderIntensity = 0;
			this.getShader().setIntensity(shaderIntensity);
			this.getShader().getShader().updateEnabled();
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

		protected abstract ChromaShaders getShader();

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
			return shaderIntensity <= 0;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("size", 0F);
			shaderData.put("thickness", 0.0125F);
		}

		@Override
		protected ChromaShaders getShader() {
			return ChromaShaders.VOIDRITUAL$SPHERE;
		}

	}

	private static class WaveVisual extends EffectVisual {

		private WaveVisual() {
			super(Effects.WAVE, true);
		}

		@Override
		protected boolean tick() {
			float r = (float)shaderData.get("waveRadius");
			float t = (float)shaderData.get("waveThickness");
			float a = (float)shaderData.get("waveAmplitude");
			r = r+1.25F;
			r *= 1.01F;
			t = MathHelper.clamp_float(r/6, 2, 15F);
			a = MathHelper.clamp_float(r/24, 1, 4F);
			shaderData.put("waveRadius", r);
			shaderData.put("waveThickness", t);
			shaderData.put("waveAmplitude", a);
			if (r > 200) {
				shaderIntensity *= 0.95F;
			}
			return shaderIntensity < 0.05;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("waveRadius", 0F);
			shaderData.put("waveThickness", 2F);
			shaderData.put("waveAmplitude", 1F);
		}

		@Override
		protected ChromaShaders getShader() {
			return ChromaShaders.VOIDRITUAL$WORLD;
		}

	}

	private static class CurlVisual extends EffectVisual {

		private CurlVisual() {
			super(Effects.CURL, true);
		}

		@Override
		protected boolean tick() {
			float d = (float)shaderData.get("curlMovementXZ");
			float h = (float)shaderData.get("curlMovementY");
			d = d*3/4+(float)Math.sqrt(d)/4;
			if (d < 0.75) {
				h += 0.02;
			}
			else {
				h *= 0.95;
			}
			d = Math.min(d, 0.95F);
			shaderData.put("curlMovementXZ", d);
			shaderData.put("curlMovementY", h);
			if (d >= 0.9) {
				shaderIntensity *= 0.9F;
			}
			return shaderIntensity < 0.01;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("curlMovementXZ", 0.0001F);
			shaderData.put("curlMovementY", 0F);
		}

		@Override
		protected ChromaShaders getShader() {
			return ChromaShaders.VOIDRITUAL$WORLD;
		}

	}

	private static class StretchVisual extends EffectVisual {

		private StretchVisual() {
			super(Effects.STRETCH, true);
		}

		@Override
		protected boolean tick() {
			float f = (float)shaderData.get("stretchFactor");
			float f2 = (float)shaderData.get("factorDelta");
			f *= f2;
			if (f > 5) {
				f2 = 0.875F;
			}
			shaderData.put("stretchFactor", f);
			if (f < 1) {
				shaderIntensity = 0;
			}
			return shaderIntensity <= 0;
		}

		@Override
		protected void initShaderData(EntityLiving e) {
			shaderData.put("stretchFactor", 1F);
			shaderData.put("factorDelta", 1.03125F);
		}

		@Override
		protected ChromaShaders getShader() {
			return ChromaShaders.VOIDRITUAL$WORLD;
		}

	}

}
