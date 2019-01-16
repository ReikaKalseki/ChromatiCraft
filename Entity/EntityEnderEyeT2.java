package Reika.ChromatiCraft.Entity;

import Reika.ChromatiCraft.Registry.ChromaItems;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class EntityEnderEyeT2 extends EntityEnderEye {

	public static final int ADDITIONAL_LIFE = 60;
	public static final double RANGE = 36;//vanilla is 12

	public EntityEnderEyeT2(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityEnderEyeT2(World world) {
		super(world);
	}

	@Override
	public void moveTowards(double x, int y, double z) {
		double d2 = x - posX;
		double d3 = z - posZ;
		float f = MathHelper.sqrt_double(d2 * d2 + d3 * d3);

		if (f > RANGE) {
			targetX = posX + d2 / f * RANGE;
			targetZ = posZ + d3 / f * RANGE;
			targetY = posY + 8.0D;
		}
		else {
			targetX = x;
			targetY = y;
			targetZ = z;
		}

		despawnTimer = 0-ADDITIONAL_LIFE;
		shatterOrDrop = false; //always die so never drops vanilla eye ([/rhyme])
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (despawnTimer > 80 && !worldObj.isRemote) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, ChromaItems.ENDEREYE.getStackOf()));
		}
	}

}
