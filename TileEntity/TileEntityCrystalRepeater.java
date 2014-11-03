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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalRepeater;
import Reika.ChromatiCraft.Magic.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

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
		return 24;
	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public int getSignalDegradation() {
		return 1;
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
		return 500;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && e == this.getActiveColor();
	}

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	public boolean checkConnectivity() {
		CrystalElement c = this.getActiveColor();
		return c != null && CrystalNetworker.instance.checkConnectivity(c, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange());
	}

	public CrystalElement getActiveColor() {
		return this.canConduct() ? CrystalElement.elements[worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord)] : null;
	}

	public CrystalTransmitter getEnergySource() {
		CrystalElement e = this.getActiveColor();
		return e != null ? CrystalNetworker.instance.getConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()) : null;
	}

	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && rand.nextInt(20) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, this.getActiveColor(), 15000, this.getReceiveRange());
		}
	}

}
