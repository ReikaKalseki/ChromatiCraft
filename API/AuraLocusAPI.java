package Reika.ChromatiCraft.API;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public interface AuraLocusAPI {

	public Collection<Coordinate> getAuraPoints(EntityPlayer ep);

	public boolean isPointWithin(World world, int x, int y, int z, int r);

}
