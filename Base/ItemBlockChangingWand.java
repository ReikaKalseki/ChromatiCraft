package Reika.ChromatiCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;

public abstract class ItemBlockChangingWand extends ItemWandBase implements BreakerCallback {

	public ItemBlockChangingWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BROWN, 1);
	}

	public abstract int getDepth(EntityPlayer ep);

	public abstract void getSpreadBlocks(World world, int x, int y, int z, BlockArray arr, EntityPlayer ep, ItemStack is);

	public abstract boolean canSpreadOn(World world, int x, int y, int z, Block b, int meta);

}
