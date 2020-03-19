package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DamageBoosterAPI {

	public static interface DamageIncreaser {

		public float getDamageFactor();

	}

	public static double getTotalDamageFactorAt(World world, int x, int y, int z) {
		double f = 1;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);
			if (te instanceof DamageIncreaser) {
				f *= Math.max(1, ((DamageIncreaser)te).getDamageFactor());
			}
		}
		return f;
	}

}
