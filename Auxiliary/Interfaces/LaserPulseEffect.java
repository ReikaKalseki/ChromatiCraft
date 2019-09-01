package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Entity.EntityLaserPulse;

public interface LaserPulseEffect {

	public boolean onImpact(World world, int x, int y, int z, EntityLaserPulse e);

}
