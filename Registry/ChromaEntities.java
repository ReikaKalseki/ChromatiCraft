package Reika.ChromatiCraft.Registry;

import net.minecraft.entity.Entity;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.DragonAPI.Interfaces.EntityEnum;

public enum ChromaEntities implements EntityEnum {

	BALLLIGHT(EntityBallLightning.class, "Ball Lightning", 0xbbbbbb, 0xffffff),
	ABILITYFIREBALL(EntityAbilityFireball.class, "Ability Fireball"),
	CHAINGUN(EntityChainGunShot.class, "ChainGun Shot");

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
