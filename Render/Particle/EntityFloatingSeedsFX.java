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

import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class EntityFloatingSeedsFX extends EntityBlurFX {

	private double angXZ;
	private double angY;

	private double angXZ_vel;
	private double angY_vel;

	private double angXZ_target;
	private double angY_target;

	private final double windAngle;
	private final double climbAngle;

	public double tolerance = 1.5;
	public double freedom = 20;
	public double angleVelocity = 0.75;
	public double particleVelocity = 0.0625;

	public EntityFloatingSeedsFX(World world, double x, double y, double z, double windAng, double climbAng) {
		this(world, x, y, z, windAng, climbAng, null);
	}

	public EntityFloatingSeedsFX(World world, double x, double y, double z, double windAng, double climbAng, IIcon icon) {
		super(world, x, y, z);

		windAngle = windAng;
		climbAngle = climbAng;

		angXZ = windAngle;
		angY = climbAngle;

		this.randomizeXZ();
		this.randomizeY();
		this.updateVelocities();

		if (icon == null) {
			IIcon ico = particleIcon;
			switch(rand.nextInt(4)) {
				case 1:
					ico = ChromaIcons.BIGFLARE.getIcon();
					break;
				case 2:
					ico = ChromaIcons.SPARKLEPARTICLE.getIcon();
					this.setBasicBlend();
					this.enableAlphaTest();
					break;
				case 3:
					ico = ChromaIcons.CENTER.getIcon();
					break;
			}
			this.setParticleIcon(ico);
		}
		else {
			this.setParticleIcon(icon);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.updateAnglesAndSpeeds();
	}

	private void updateAnglesAndSpeeds() {
		this.updateAngles();

		angXZ += angXZ_vel;
		angY += angY_vel;

		this.updateVelocities();
	}

	private void updateAngles() {
		if (Math.abs(angXZ_target-angXZ) <= tolerance) {
			this.randomizeXZ();
		}

		if (Math.abs(angY_target-angY) <= tolerance) {
			this.randomizeY();
		}
	}

	private void randomizeXZ() {
		angXZ_target = ReikaRandomHelper.getRandomPlusMinus(windAngle, freedom);

		if (angXZ_target > angXZ) {
			angXZ_vel = angleVelocity;
		}
		else if (angXZ_target < angXZ) {
			angXZ_vel = -angleVelocity;
		}
	}

	private void randomizeY() {
		angY_target = ReikaRandomHelper.getRandomPlusMinus(climbAngle, freedom);

		if (angY_target > angY) {
			angY_vel = angleVelocity;
		}
		else if (angY_target < angY) {
			angY_vel = -angleVelocity;
		}
	}

	private void updateVelocities() {
		double[] xyz = ReikaPhysicsHelper.polarToCartesianFast(particleVelocity, angY, angXZ);
		motionX = xyz[0];
		motionY = xyz[1];
		motionZ = xyz[2];
	}

}
