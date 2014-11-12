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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalRepeater;
import Reika.ChromatiCraft.Magic.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityCrystalRepeater extends CrystalTransmitterBase implements CrystalRepeater {

	protected ForgeDirection facing = ForgeDirection.DOWN;
	protected boolean hasMultiblock;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPEATER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.getTicksExisted() < 5)
			this.syncAllData(false);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
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
		return 5;
	}

	@Override
	public final boolean canConduct() {
		return hasMultiblock;
	}

	public final void validateStructure() {
		hasMultiblock = this.checkForStructure();
		if (!hasMultiblock) {
			CrystalNetworker.instance.breakPaths(this);
		}
	}

	protected boolean checkForStructure() {
		ForgeDirection dir = facing;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) != ChromaBlocks.RUNE.getBlockInstance())
			return false;
		for (int i = 2; i < 4; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != 0)
				return false;
		}
		return true;
	}

	public void redirect(int side) {
		facing = dirs[side].getOpposite();
		this.validateStructure();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (facing != null)
			NBT.setInteger("face", facing.ordinal());

		NBT.setBoolean("multi", hasMultiblock);
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
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	public boolean checkConnectivity() {
		CrystalElement c = this.getActiveColor();
		return c != null && CrystalNetworker.instance.checkConnectivity(c, this);
	}

	public CrystalElement getActiveColor() {
		int dx = xCoord+facing.offsetX;
		int dy = yCoord+facing.offsetY;
		int dz = zCoord+facing.offsetZ;
		return this.canConduct() ? CrystalElement.elements[worldObj.getBlockMetadata(dx, dy, dz)] : null;
	}

	public CrystalSource getEnergySource() {
		CrystalElement e = this.getActiveColor();
		return e != null ? CrystalNetworker.instance.getConnectivity(e, this) : null;
	}

	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(60) == 0)
				p.attackEntityByProxy(player, this);
			if (!CrystalNetworker.instance.isActiveFlowTo(this, p.getColor()))
				CrystalNetworker.instance.makeRequest(this, p.getColor(), 15000, this.getReceiveRange());
		}
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

}
