package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;

public final class PylonDamage extends CustomStringDamageSource {

	public PylonDamage(String desc) {
		super(desc);
	}

	@Override
	public boolean isUnblockable()
	{
		return true;
	}

	@Override
	public boolean isDamageAbsolute()
	{
		return true;
	}

	@Override
	public boolean isExplosion()
	{
		return false;
	}

	@Override
	public boolean isProjectile()
	{
		return false;
	}

	@Override
	public boolean canHarmInCreative()
	{
		return false;
	}

	@Override
	public Entity getSourceOfDamage()
	{
		return this.getEntity();
	}

	@Override
	public Entity getEntity()
	{
		return null;
	}

	@Override
	public boolean isFireDamage()
	{
		return false;
	}

	@Override
	public boolean isDifficultyScaled()
	{
		return false;
	}

	@Override
	public boolean isMagicDamage()
	{
		return true;
	}

}
