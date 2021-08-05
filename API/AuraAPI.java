package Reika.ChromatiCraft.API;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class AuraAPI {

	private static Class teClass;
	private static Method getPoints;
	private static Method isWithin;

	public static Collection<Coordinate> getAuraPoints(EntityPlayer ep) {
		ArrayList<Coordinate> ret = new ArrayList();
		try {
			Collection<TileEntity> c = (Collection<TileEntity>)getPoints.invoke(null, ep);
			for (TileEntity te : c) {
				ret.add(new Coordinate(te));
			}
		}
		catch (Exception e) {

		}
		return ret;
	}

	public static boolean isPointWithin(World world, int x, int y, int z, int r) {
		try {
			return (boolean)isWithin.invoke(null, world, x, y, z, r);
		}
		catch (Exception e) {
			return false;
		}
	}

	static {
		try {
			teClass = Class.forName("Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint");
			getPoints = teClass.getDeclaredMethod("teClass", EntityPlayer.class);
			isWithin = teClass.getDeclaredMethod("isPointWithin", World.class, int.class, int.class, int.class, int.class);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
