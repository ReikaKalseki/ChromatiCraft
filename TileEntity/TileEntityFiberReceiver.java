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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberIO;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.FiberNetwork;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityFiberReceiver extends CrystalReceiverBase implements FiberIO {

	private FiberNetwork network;

	private CrystalElement color = CrystalElement.WHITE;

	@Override
	public void setNetwork(FiberNetwork net) {
		network = net;
	}

	@Override
	public void onBroken() {
		if (network != null)
			network.removeTerminus(this);
	}
	/*
	@Override
	public FiberNetwork getNetwork() {
		return network;
	}
	 */
	public void setColor(CrystalElement e) {
		if (network != null)
			network.onTileChangeColor(this, e);
		color = e;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		//ReikaJavaLibrary.pConsole(network, Side.SERVER);

		CrystalElement e = this.getColor();
		if (network != null && e != null) {
			int amt = this.getEnergy(e);
			if (amt > 0) {
				int add = network.distribute(e, amt);
				this.drainEnergy(e, add);
			}
		}
	}

	@Override
	public void onPathBroken(CrystalElement e) {
		if (network != null) {
			network.killChannel(e);
		}
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct() && e != null && e == this.getColor();
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	public int maxThroughput() {
		return 2500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage() {
		return 6000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FIBERSOURCE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
	}

}
