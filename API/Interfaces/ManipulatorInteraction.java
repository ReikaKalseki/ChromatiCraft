package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.entity.player.EntityPlayer;

/** Implement this on TileEntities that can react to having the manipulator used on them. */
public interface ManipulatorInteraction {

	/** Return true to "eat" the right click */
	public boolean onManipulatorInteract(EntityPlayer ep, int side);

}
