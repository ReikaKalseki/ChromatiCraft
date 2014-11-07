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

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberIO;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityFiberPowered;
import Reika.ChromatiCraft.Magic.FiberNetwork;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFiberTransmitter extends TileEntityChromaticBase implements FiberIO {

	private FiberNetwork network;

	private CrystalElement color = CrystalElement.WHITE;

	private ForgeDirection facing;
	private int particleTick = 20;

	public final ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	public void setFacing(ForgeDirection dir) {
		if (facing != dir && network != null) {
			network.removeTerminus(this);
			network = null;
		}
		facing = dir;
		TileEntity te = this.getAdjacentTileEntity(facing.getOpposite());
		if (te instanceof TileEntityFiberOptic) {
			((TileEntityFiberOptic)te).connectTo(this);
		}
	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];

		color = CrystalElement.elements[NBT.getInteger("color")];
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("color", color.ordinal());
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FIBERSINK;
	}

	@Override
	public void onBroken() {
		if (network != null)
			network.removeTerminus(this);
	}

	public void setColor(CrystalElement e) {
		if (network != null)
			network.onTileChangeColor(this, e);
		color = e;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		particleTick++;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void setNetwork(FiberNetwork net) {
		network = net;
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}
	/*
	@Override
	public FiberNetwork getNetwork() {
		return network;
	}
	 */

	public CrystalElement getColor() {
		return color;
	}

	public int dumpEnergy(CrystalElement e, int amt) {
		return amt > 0 && e == this.getColor() ? this.emitEnergy(e, amt) : 0;
	}

	private int emitEnergy(CrystalElement e, int amt) {
		ForgeDirection dir = this.getFacing();
		for (int i = 1; i < 4; i++) {
			TileEntity tile = worldObj.getTileEntity(xCoord+dir.offsetX*i, yCoord+dir.offsetY*i, zCoord+dir.offsetZ*i);
			if (tile instanceof TileEntityFiberPowered) {
				TileEntityFiberPowered te = (TileEntityFiberPowered)tile;
				int added = te.addEnergy(e, amt);
				if (added > 0) {
					if (particleTick > 1 && rand.nextInt(Math.max(1, 20-particleTick)) == 0) {
						int[] data = {dir.ordinal(), i, this.getColor().ordinal()};
						ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TRANSMIT.ordinal(), this, data);
						particleTick = 0;
					}
				}
				return added;
			}
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void transmitParticle(ForgeDirection dir, int dist, CrystalElement e) {
		double v = 0.05+0.0125*(dist-1);
		double vx = dir.offsetX*v;
		double vy = dir.offsetY*v;
		double vz = dir.offsetZ*v;
		EntityFX fx = new EntityBlurFX(e, worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, vx, vy, vz).setScale(4).setLife(15*dist).setNoSlowdown();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
}
