package Reika.ChromatiCraft.Render.Particle;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaShaders;

public class EntityShaderFX extends EntityBlurFX {

	private float lensingIntensity;
	private float lensingFadeRate = 0.05F;
	private float lensingClip = 1;
	private ChromaShaders shaderType;
	private boolean stillRender;

	public EntityShaderFX(World world, double x, double y, double z, float f, ChromaShaders s) {
		super(world, x, y, z);
		lensingIntensity = f;
		shaderType = s;
	}

	public EntityShaderFX(World world, double x, double y, double z, double vx, double vy, double vz, float f, ChromaShaders s) {
		super(world, x, y, z, vx, vy, vz);
		lensingIntensity = f;
		shaderType = s;
	}

	public EntityShaderFX setIntensity(float f) {
		lensingIntensity = f;
		return this;
	}

	public EntityShaderFX setFadeRate(float f) {
		lensingFadeRate = f;
		return this;
	}

	public EntityShaderFX setClip(float f) {
		lensingClip = f;
		return this;
	}

	public EntityShaderFX setShader(ChromaShaders s) {
		shaderType = s;
		return this;
	}

	public EntityShaderFX setRendering(boolean render) {
		stillRender = render;
		return this;
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7) {
		if (stillRender)
			super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		shaderType.clearOnRender = true;
		shaderType.setIntensity(1);
		shaderType.getShader().addFocus(this);
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
		shaderType.getShader().modifyLastCompoundFocus(f, map);
	}

}
