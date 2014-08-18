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

import Reika.ChromatiCraft.Base.TileEntity.TileEntityCrystalTile;
import Reika.ChromatiCraft.Magic.CrystalRepeater;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.world.World;

public class TileEntityCrystalRepeater extends TileEntityCrystalTile implements CrystalRepeater {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPEATER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	public int getSendRange() {
		return 16;
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public int getSignalDegradation() {
		return 0;
	}

	@Override
	public boolean canConduct() {
		return true || this.checkForStructure(worldObj, xCoord, yCoord, zCoord);
	}

	private boolean checkForStructure(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public int maxThroughput() {
		return 2;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

}
