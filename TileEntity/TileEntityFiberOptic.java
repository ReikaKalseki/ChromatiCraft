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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.WorldRift;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberIO;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.FiberNetwork;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class TileEntityFiberOptic extends TileEntityChromaticBase {

	private FiberNetwork network;
	private boolean breaking = false;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FIBER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if ((this.getTicksExisted() == 0 || network == null) && !world.isRemote) {
			this.findAndJoinNetwork(world, x, y, z);
			//ReikaJavaLibrary.pConsole(network, Side.SERVER);
		}
	}

	public final void findAndJoinNetwork(World world, int x, int y, int z) {
		network = new FiberNetwork();
		network.addBlock(this);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			this.linkTile(te, dir);
		}
		this.onJoinNetwork();
		//ReikaJavaLibrary.pConsole(network, Side.SERVER);
	}

	private void linkTile(TileEntity te, ForgeDirection dir) {
		if (te instanceof TileEntityFiberOptic) {
			//ReikaJavaLibrary.pConsole(te, Side.SERVER);
			TileEntityFiberOptic n = (TileEntityFiberOptic)te;
			if (!n.breaking) {
				FiberNetwork w = n.network;
				if (w != null) {
					//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
					w.merge(network);
				}
			}
		}
		else if (te instanceof FiberIO) {
			//ReikaJavaLibrary.pConsole(te, Side.SERVER);
			FiberIO n = (FiberIO)te;
			if (n.canNetworkOnSide(dir.getOpposite())) {
				this.connectTo(n);
			}
		}
		else if (te instanceof WorldRift) {
			WorldRift sr = (WorldRift)te;
			WorldLocation loc = sr.getLinkTarget();
			if (loc != null) {
				this.linkTile(sr.getTileEntityFrom(dir), dir);
			}
		}
	}

	public void connectTo(FiberIO n) {
		if (network != null) {
			network.addIO(n, n.getColor());
			n.setNetwork(network);
		}
	}

	protected void onJoinNetwork() {

	}

	public final FiberNetwork getNetwork() {
		return network;
	}

	public final void setNetwork(FiberNetwork n) {
		if (n == null) {
			ChromatiCraft.logger.logError(this+" was told to join a null network!");
		}
		else {
			network = n;
			network.addBlock(this);
		}
	}

	public final void removeFromNetwork() {
		breaking = true;
		if (network == null)
			ChromatiCraft.logger.logError(this+" was removed from a null network!");
		else
			network.removeBlock(this);
	}

	public final void rebuildNetwork() {
		this.removeFromNetwork();
		this.resetNetwork();
		this.findAndJoinNetwork(worldObj, xCoord, yCoord, zCoord);
	}

	public final void resetNetwork() {
		network = null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir, boolean hitbox) {
		if (dir.offsetX == 0 && !hitbox)
			dir = dir.getOpposite();
		TileEntity te = this.getAdjacentTileEntity(dir);
		return te instanceof TileEntityFiberOptic || (te instanceof FiberIO && ((FiberIO)te).canNetworkOnSide(dir.getOpposite()));
	}

	public void recomputeConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te != null)
				this.linkTile(te, dir);
		}
	}

}
