package Reika.ChromatiCraft.Magic.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.CrystalElement;


public interface ChargingPoint extends CrystalNetworkTile, LumenTile {

	public CrystalElement getDeliveredColor(EntityPlayer ep, World world, int clickX, int clickY, int clickZ);

	public int getEnergy(CrystalElement e);

	public boolean allowCharging(EntityPlayer ep, CrystalElement e);

	public float getChargeRateMultiplier(EntityPlayer ep, CrystalElement e);

	public void onUsedBy(EntityPlayer ep, CrystalElement e);

	public boolean drain(CrystalElement e, int amt);

}
