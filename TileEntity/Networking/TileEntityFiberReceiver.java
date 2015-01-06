/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FiberSource;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.FiberNetwork;
import Reika.ChromatiCraft.Magic.FiberPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class TileEntityFiberReceiver extends CrystalReceiverBase implements FiberSource {

	private FiberNetwork network;

	private CrystalElement color = CrystalElement.WHITE;

	private HashMap<WorldLocation, FiberPath> paths = new HashMap();

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
	protected int getCooldownLength() {
		return 200;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		//ReikaJavaLibrary.pConsole(network, Side.SERVER);

		if (!paths.isEmpty() && this.getTicksExisted()%(1+2*paths.size()) == 0) {
			CrystalElement e = this.getColor();
			if (network != null && e != null) {
				int amt = this.getEnergy(e);
				if (amt > 0) {
					int add = network.distribute(this, e, amt);
					if (add > 0) {
						this.drainEnergy(e, add);
					}
				}
			}
		}

		for (WorldLocation loc : paths.keySet()) {
			paths.get(loc).tick();
		}
	}

	private void checkAndRequest() {
		CrystalElement e = this.getColor();
		int space = this.getRemainingSpace(e);
		if (space > 0) {
			this.requestEnergy(e, space);
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
	public int getMaxStorage(CrystalElement e) {
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

		if (NBT.hasKey("paths")) {
			NBTTagList tag = NBT.getTagList("paths", NBTTypes.COMPOUND.ID);
			for (Object o : tag.tagList) {
				NBTTagCompound b = (NBTTagCompound)o;
				FiberPath p = FiberPath.readFromNBT(b);
				if (p != null && p.isValid()) {
					paths.put(p.getSink(), p);
				}
			}
		}
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());

		NBTTagList li = new NBTTagList();
		for (FiberPath p : paths.values()) {
			NBTTagCompound tag = new NBTTagCompound();
			p.writeToNBT(tag);
			li.appendTag(tag);
		}
		NBT.setTag("paths", li);
	}

	@Override
	public void removeTerminus(TileEntityFiberTransmitter te) {
		paths.remove(new WorldLocation(te));
		this.syncAllData(true);
	}

	@Override
	public void addTerminus(TileEntityFiberTransmitter te) {
		FiberPath p = network.getPathBetween(this, te);
		if (p != null)
			paths.put(new WorldLocation(te), p);
		this.syncAllData(true);
	}

	@Override
	public void onTransmitTo(TileEntityFiberTransmitter te, CrystalElement e, int energy) {
		FiberPath path = paths.get(new WorldLocation(te));
		if (path != null) {
			path.pulse();
		}
	}

	public Collection<FiberPath> getActivePaths() {
		return Collections.unmodifiableCollection(paths.values());
	}

}
