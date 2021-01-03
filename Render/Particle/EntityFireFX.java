/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;


public class EntityFireFX extends EntityCCBlurFX {

	private int color1;
	private int color2;

	private float particleStretch = (float)ReikaRandomHelper.getRandomBetween(0.03125, 0.0625);
	private float tailOff = (float)ReikaRandomHelper.getRandomPlusMinus(5D, 2.5);

	private boolean exploding = false;

	private boolean hasImpacted;

	public EntityFireFX(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setColliding();
		color1 = 0xff0000 | (rand.nextInt(127) << 8);
		color2 = ReikaColorAPI.mixColors(0xffffff, 0xffff00, rand.nextFloat());
	}

	public EntityFireFX setStretch(float s) {
		particleStretch = s;
		return this;
	}

	public EntityFireFX setTailoff(float t) {
		tailOff = t;
		return this;
	}

	public EntityFireFX setExploding() {
		exploding = true;
		return this;
	}

	@Override
	protected void onCollision() {
		hasImpacted = true;
		particleStretch *= 0.25F;
		if (exploding) {
			this.explode();
		}
		else { //Fade like an ember
			float frac = particleAge/(float)particleMaxAge;
			particleMaxAge = 20;
			particleAge = (int)(frac*particleMaxAge);
		}
	}

	private void explode() {
		ReikaParticleHelper.EXPLODE.spawnAt(this);
		this.setDead();
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7) {
		float s = particleScale;
		double y = posY;
		int n = hasImpacted ? 1+(rand.nextInt(2) == 0 ? 1 : 0) : 5;
		for (int i = 0; i < n; i++) {
			float ds = 1-i/tailOff;
			posY = y+i*s*particleStretch;
			particleScale = s*ds;
			this.setColor(color1);
			super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
			this.setColor(color2);
			particleScale = s*ds*0.6F;
			super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		}
		particleScale = s;
		posY = y;
	}

}
