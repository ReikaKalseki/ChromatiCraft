/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public interface ChargingPoint extends CrystalNetworkTile, LumenTile {

	public CrystalElement getDeliveredColor(EntityPlayer ep, World world, int clickX, int clickY, int clickZ);

	public int getEnergy(CrystalElement e);

	public boolean allowCharging(EntityPlayer ep, CrystalElement e);

	public float getChargeRateMultiplier(EntityPlayer ep, CrystalElement e);

	public void onUsedBy(EntityPlayer ep, CrystalElement e);

	public boolean drain(CrystalElement e, int amt);

	public Coordinate getChargeParticleOrigin(EntityPlayer ep, CrystalElement e);

	public float getHeldToolChargingPower(EntityPlayer ep, CrystalElement e, ItemStack is);

}
