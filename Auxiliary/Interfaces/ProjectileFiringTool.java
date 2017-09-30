package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ProjectileFiringTool {

	public void fire(ItemStack is, World world, EntityPlayer ep);

}
