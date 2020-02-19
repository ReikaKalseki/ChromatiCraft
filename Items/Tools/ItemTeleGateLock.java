package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class ItemTeleGateLock extends ItemChromaTool {

	public ItemTeleGateLock(int index) {
		super(index);

		maxStackSize = 32;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (world.isRemote)
			return is;
		Coordinate c = new Coordinate(ep);
		WorldLocation loc = TileEntityTeleportGate.getRandomGate(world, ep);
		TileEntity te = loc != null ? loc.getTileEntity(world) : null;
		if (te instanceof TileEntityTeleportGate) {
			ChromaSounds.MONUMENTRAY.playSound(ep, 0.75F, 1);
			ChromaSounds.MONUMENTRAY.playSound(ep, 0.75F, 0.5F);
			ChromaSounds.MONUMENTRAY.playSoundAtBlockNoAttenuation(te, 0.75F, 1, 32);
			ChromaSounds.MONUMENTRAY.playSoundAtBlockNoAttenuation(te, 0.75F, 0.5F, 32);
			TileEntityTeleportGate.teleportFrom(world, c.xCoord, c.yCoord, c.zCoord, (TileEntityTeleportGate)te, ep);
			if (!ep.capabilities.isCreativeMode)
				is.stackSize--;
		}
		else {
			ChromaSounds.ERROR.playSound(ep);
		}
		return is;
	}

}
