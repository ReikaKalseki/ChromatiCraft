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

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalRepeater;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class TileEntityCrystalRepeater extends CrystalTransmitterBase implements CrystalRepeater {

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
		return this.checkForStructure(worldObj, xCoord, yCoord, zCoord);
	}

	private boolean checkForStructure(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) != ChromaBlocks.RUNE.getBlockInstance())
			return false;
		for (int i = 2; i < 4; i++) {
			Block id = world.getBlock(x, y-i, z);
			int meta = world.getBlockMetadata(x, y-i, z);
			if (id != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != 0)
				return false;
		}
		return true;
	}

	@Override
	public int maxThroughput() {
		return 2;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e.ordinal() == worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
	}

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken() {

	}

}
