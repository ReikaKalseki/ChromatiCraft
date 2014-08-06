package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;

public class ItemNBTSpawner extends ItemBlock {

	public ItemNBTSpawner(int par1) {
		super(par1);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List par3List, boolean par4) {
		if (is.stackTagCompound == null)
			return;
		if (is.stackTagCompound.hasKey("Spawner"))
			par3List.add("Spawns "+ReikaEntityHelper.getEntityDisplayName(is.stackTagCompound.getString("Spawner")));
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag && stack.stackTagCompound != null) {
			TileEntityMobSpawner te = (TileEntityMobSpawner)world.getBlockTileEntity(x, y, z);
			ReikaSpawnerHelper.setSpawnerFromItemNBT(stack, te);
		}
		return flag;
	}

}
