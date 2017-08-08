/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EntityAbilityFireball extends EntityLargeFireball {

	private final EntityPlayer source;

	public EntityAbilityFireball(World world) {
		super(world);

		source = null;
	}

	public EntityAbilityFireball(World world, EntityPlayer ep, double x, double y, double z) {
		super(world, ep, x, y, z);

		source = ep;
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (!isDead) {
			super.onImpact(mov);

			if (field_92057_e > 2) {
				int x = MathHelper.floor_double(posX);
				int y = MathHelper.floor_double(posY);
				int z = MathHelper.floor_double(posZ);
				int r = field_92057_e-1;
				for (int i = -r; i <= r; i++) {
					for (int j = -r; j <= r; j++) {
						for (int k = -r; k <= r; k++) {
							int dx = x+i;
							int dy = y+j;
							int dz = z+k;
							ReikaWorldHelper.temperatureEnvironment(worldObj, dx, dy, dz, 950);
						}
					}
				}
			}

			if (!worldObj.isRemote)
				source.addExperience(5);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (source == null && !worldObj.isRemote)
			this.setDead();
	}

}
