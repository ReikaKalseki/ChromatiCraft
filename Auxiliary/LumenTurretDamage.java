/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityLumenTurret;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;


public class LumenTurretDamage extends CustomStringDamageSource {

	private final EntityPlayer player;

	public LumenTurretDamage(TileEntityLumenTurret te, boolean fake) {
		super("got too close to "+te.getPlacerName()+"'s "+te.getName());
		player = fake ? te.getFakePlacer() : te.getPlacer();
	}

	@Override
	public Entity getSourceOfDamage()
	{
		return player;
	}

	@Override
	public Entity getEntity() {
		return player;
	}

	@Override
	public boolean isMagicDamage()
	{
		return true;
	}

}
