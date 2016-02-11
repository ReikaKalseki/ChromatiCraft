package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EffectPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityAccelerationPlant;


public abstract class TileEntityMagicPlant extends TileEntityChromaticBase implements EffectPlant {

	public abstract ForgeDirection getGrowthDirection();

	protected final int getAccelerationPlants() {
		int n = 0;
		ForgeDirection dir = this.getGrowthDirection().getOpposite();
		TileEntity te = this.getAdjacentTileEntity(dir);
		while (te instanceof TileEntityAccelerationPlant) {
			TileEntityAccelerationPlant tile = (TileEntityAccelerationPlant)te;
			if (tile.isActive()) {
				n++;
				te = tile.getAdjacentTileEntity(dir);
			}
			else {
				break;
			}
		}
		return n;
	}

}
