package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class OuterRegionsEvents {

	public static OuterRegionsEvents instance = new OuterRegionsEvents();

	private OuterRegionsEvents() {

	}

	public void tickPlayerInOuterRegion(EntityPlayer ep) {

	}

	private void spawnFlare(World world, EntityPlayer ep) {
		if (!world.isRemote) {

		}
	}

}
