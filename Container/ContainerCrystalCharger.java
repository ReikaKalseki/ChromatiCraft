package Reika.ChromatiCraft.Container;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.DragonAPI.Base.CoreContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerCrystalCharger extends CoreContainer {

	private final TileEntityCrystalCharger tile;

	public ContainerCrystalCharger(EntityPlayer player, TileEntityCrystalCharger te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 80, 47);

		this.addPlayerInventoryWithOffset(player, 0, 25);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			for (int k = 0; k < crafters.size(); k++) {
				ICrafting icrafting = (ICrafting)crafters.get(k);
				icrafting.sendProgressBarUpdate(this, i, tile.getEnergy(e));
			}
		}
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		super.updateProgressBar(par1, par2);

		CrystalElement e = CrystalElement.elements[par1];
		tile.setEnergy(e, par2);
	}

}
