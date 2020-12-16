package Reika.ChromatiCraft.Render;

import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireSmokeFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRelayPathFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.Render.Particle.EntityShaderFX;
import Reika.DragonAPI.Extras.ThrottleableEffectRenderer;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine;

public class CCParticleEngine extends ParticleEngine {

	public static final CCParticleEngine instance = new CCParticleEngine();

	protected CCParticleEngine() {
		super();
	}

	public void registerClasses() {
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityCCBlurFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityCCFloatingSeedsFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityShaderFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFireFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFireSmokeFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityRuneFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityLaserFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityCenterBlurFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityGlobeFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityRelayPathFX.class, this);

		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityBallLightningFX.class, this);
		ThrottleableEffectRenderer.getRegisteredInstance().registerDelegateRenderer(EntityFlareFX.class, this);
	}

}
