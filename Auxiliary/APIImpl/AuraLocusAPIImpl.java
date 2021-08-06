package Reika.ChromatiCraft.Auxiliary.APIImpl;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.AuraLocusAPI;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class AuraLocusAPIImpl implements AuraLocusAPI {

	public Collection<Coordinate> getAuraPoints(EntityPlayer ep) {
		ArrayList<Coordinate> ret = new ArrayList();
		try {
			for (TileEntity te : TileEntityAuraPoint.getPoints(ep)) {
				ret.add(new Coordinate(te));
			}
		}
		catch (Exception e) {

		}
		return ret;
	}

	@Override
	public boolean isPointWithin(World world, int x, int y, int z, int r) {
		return TileEntityAuraPoint.isPointWithin(world, x, y, z, r);
	}

}
