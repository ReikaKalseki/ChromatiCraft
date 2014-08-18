/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.world.World;

public class TileEntityCrystalLaser extends TileEntityChromaticBase implements CrystalReceiver {

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LASER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
