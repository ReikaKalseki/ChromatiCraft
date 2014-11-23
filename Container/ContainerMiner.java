package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.TileEntity.TileEntityMiner;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerMiner extends CoreContainer {

	public ContainerMiner(EntityPlayer player, TileEntityMiner te) {
		super(player, te);

		this.addSlot(0, 152, 16);
		this.addSlot(1, 152, 92);

		this.addPlayerInventoryWithOffset(player, 0, 40);
	}

}
