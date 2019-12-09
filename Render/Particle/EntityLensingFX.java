package Reika.ChromatiCraft.Render.Particle;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaShaders;

public class EntityLensingFX extends EntityBlurFX {

	private float lensingIntensity;
	private float lensingFadeRate = 0.05F;
	private float lensingClip = 1;
	private boolean pinching = false;

	public EntityLensingFX(World world, double x, double y, double z, float f) {
		super(world, x, y, z);
		lensingIntensity = f;
	}

	public EntityLensingFX setIntensity(float f) {
		lensingIntensity = f;
		return this;
	}

	public EntityLensingFX setFadeRate(float f) {
		lensingFadeRate = f;
		return this;
	}

	public EntityLensingFX setClip(float f) {
		lensingClip = f;
		return this;
	}

	public EntityLensingFX setPinching(boolean pinch) {
		pinching = pinch;
		return this;
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7) {
		ChromaShaders sh = pinching ? ChromaShaders.PINCHPARTICLE : ChromaShaders.LENSPARTICLE;
		sh.clearOnRender = true;
		sh.setIntensity(1);
		sh.getShader().addFocus(this);
		float f = Math.min(lensingIntensity, particleAge*lensingFadeRate);
		HashMap<String, Object> map = new HashMap();
		float f11 = (float)(prevPosX + (posX - prevPosX) * par2 - interpPosX);
		float f12 = (float)(prevPosY + (posY - prevPosY) * par2 - interpPosY);
		float f13 = (float)(prevPosZ + (posZ - prevPosZ) * par2 - interpPosZ);
		map.put("dx", f11);
		map.put("dy", f12);
		map.put("dz", f13);
		map.put("distance", Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(this));
		map.put("clip", lensingClip);
		map.put("scale", particleScale*0.2F);
		sh.getShader().modifyLastCompoundFocus(f, map);
	}

}
