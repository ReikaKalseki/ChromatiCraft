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
import Reika.ChromatiCraft.Magic.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.world.World;
//Make player able to manufacture in the very late game, otherwise rare worldgen
public class TileEntityCrystalPylon extends TileEntityCrystalTile implements CrystalTransmitter {

	public boolean hasMultiblock = false;
	private CrystalElement color = CrystalElement.LIME;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLON;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true || e == color;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	public int getSendRange() {
		return 32;
	}

	@Override
	public boolean canConduct() {
		return true || hasMultiblock;
	}

	@Override
	public int maxThroughput() {
		return 10;
	}

	@Override
	public int getTransmissionStrength() {
		return 100;
	}

}
