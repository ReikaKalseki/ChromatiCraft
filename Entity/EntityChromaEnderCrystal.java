package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class EntityChromaEnderCrystal extends EntityEnderCrystal {

	public EntityChromaEnderCrystal(World world) {
		super(world);
	}

	@Override //Identical except no block damage
	public boolean attackEntityFrom(DamageSource src, float amt) {
		if (this.isEntityInvulnerable())  {
			return false;
		}
		else {
			if (!isDead && !worldObj.isRemote) {
				health = 0;

				if (health <= 0) {
					this.setDead();

					if (!worldObj.isRemote)  {
						worldObj.createExplosion((Entity)null, posX, posY, posZ, 6.0F, false);
					}
				}
			}

			return true;
		}
	}

	@Override
	public String getCommandSenderName() {
		return StatCollector.translateToLocal("chroma.endercrystal");
	}

}
