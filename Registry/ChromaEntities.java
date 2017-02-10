/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.entity.Entity;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Entity.EntityFlyingLight;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Entity.EntityLumaBurst;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Entity.EntityNukerBall;
import Reika.ChromatiCraft.Entity.EntityParticleCluster;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityTNTPinball;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.DragonAPI.Interfaces.Registry.EntityEnum;

public enum ChromaEntities implements EntityEnum {

	BALLLIGHT(EntityBallLightning.class, "Ball Lightning", 0xbbbbbb, 0xffffff),
	ABILITYFIREBALL(EntityAbilityFireball.class, "Ability Fireball"),
	CHAINGUN(EntityChainGunShot.class, "ChainGun Shot"),
	SPLASHGUN(EntitySplashGunShot.class, "SplashGun Shot"),
	VACUUM(EntityVacuum.class, "Vacuum"),
	LIGHT(EntityFlyingLight.class, "Light"),
	ENDERCRYS(EntityChromaEnderCrystal.class, "CC Ender Crystal"),
	METEOR(EntityMeteorShot.class, "Meteor Shot"),
	AURORA(EntityAurora.class, "Aurora"),
	THROWNGEM(EntityThrownGem.class, "Thrown Gem"),
	LASERPULSE(EntityLaserPulse.class, "Laser Pulse"),
	TNTPINBALL(EntityTNTPinball.class, "TNT Pinball"),
	DIMENSIONFLARE(EntityDimensionFlare.class, "Dimension Flare"),
	LUMABURST(EntityLumaBurst.class, "Luma Burst"),
	PARTICLECLUSTER(EntityParticleCluster.class, "Particle Swarm"),
	NUKERBALL(EntityNukerBall.class, "Cluster Ball"),
	GLOWCLOUD(EntityGlowCloud.class, "GlowCloud", 0x000040, 0x22aaff);

	public final String entityName;
	private final Class entityClass;
	private final int eggColor1;
	private final int eggColor2;
	private final boolean hasEgg;

	public static final ChromaEntities[] entityList = values();

	private ChromaEntities(Class<? extends Entity> c, String s) {
		this(c, s, -1, -1);
	}

	private ChromaEntities(Class<? extends Entity> c, String s, int c1, int c2) {
		entityClass = c;
		entityName = s;

		eggColor1 = c1;
		eggColor2 = c2;
		hasEgg = c1 >= 0 && c2 >= 0;
	}

	@Override
	public String getBasicName() {
		return entityName;
	}

	@Override
	public boolean isDummiedOut() {
		return false;
	}

	@Override
	public Class getObjectClass() {
		return entityClass;
	}

	@Override
	public String getUnlocalizedName() {
		return entityName;
	}

	@Override
	public int getTrackingDistance() {
		return 64;
	}

	@Override
	public boolean sendsVelocityUpdates() {
		return true;
	}

	@Override
	public boolean hasSpawnEgg() {
		return hasEgg;
	}

	@Override
	public int eggColor1() {
		return eggColor1;
	}

	@Override
	public int eggColor2() {
		return eggColor2;
	}

}
