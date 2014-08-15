package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class EntityBallLightning extends EntityLiving {

	public EntityBallLightning(World world) {
		super(world);
	}

	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 1;
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_)
	{
		return 15728880;
	}

	@Override
	public final boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public final boolean isBurning()
	{
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

}
