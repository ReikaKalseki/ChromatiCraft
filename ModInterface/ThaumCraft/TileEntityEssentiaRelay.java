/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaMovement;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;


@Strippable(value={"thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityEssentiaRelay extends TileEntityChromaticBase implements IEssentiaTransport, BreakAction, SneakPop {

	//private static final int PATH_DURATION = 30;
	public static final int SEARCH_RANGE = 8;
	public static final int THROUGHPUT = 4;

	private final StepTimer scanTimer = new StepTimer(50);

	EssentiaNetwork network;

	private final Collection<EssentiaPath> activePaths = new ArrayList();
	final HashMap<Coordinate, Boolean> networkCoords = new HashMap();

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBTTagList li = new NBTTagList();
		for (Coordinate c : networkCoords.keySet()) {
			NBTTagCompound tag = c.writeToTag();
			tag.setBoolean("node", networkCoords.get(c));
			li.appendTag(tag);
		}
		NBT.setTag("points", li);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		networkCoords.clear();
		NBTTagList li = NBT.getTagList("points", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound n = (NBTTagCompound)o;
			Coordinate c = Coordinate.readTag(n);
			networkCoords.put(c, n.getBoolean("node"));
		}
	}

	public Map<Coordinate, Boolean> getNetworkTiles() {
		return Collections.unmodifiableMap(networkCoords);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ESSENTIARELAY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		HashSet<Coordinate> activeTargets = new HashSet();
		for (EssentiaPath p : activePaths) {
			p.update(world, x, y, z);
			if (p.target != null)
				activeTargets.add(p.target);
		}
		activePaths.clear();

		if (network != null) {
			EssentiaMovement mov = network.tick(world);
			if (mov != null) {
				for (EssentiaPath p : mov.paths()) {
					this.addPath(p);
				}
			}
		}

		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.scan(world, x, y, z, false);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.scan(world, x, y, z, true);
	}

	void scan(World world, int x, int y, int z, boolean rebuild) {
		if (world.isRemote)
			return;
		if (rebuild || network == null) {
			network = new EssentiaNetwork();
			network.addNode(this);
		}
		for (int i = -SEARCH_RANGE; i <= SEARCH_RANGE; i++) {
			for (int j = -SEARCH_RANGE; j <= SEARCH_RANGE; j++) {
				for (int k = -SEARCH_RANGE; k <= SEARCH_RANGE; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = world.getTileEntity(dx, dy, dz);
					if (te instanceof IEssentiaTransport) {
						if (te != this) {
							if (te instanceof TileEntityEssentiaRelay) {
								TileEntityEssentiaRelay tr = (TileEntityEssentiaRelay)te;
								if (rebuild && tr.network != null)
									network.merge(world, tr.network);
							}
							else {
								network.addEndpoint(this, (IEssentiaTransport)te);
							}
						}
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(network, !world.isRemote);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private void addPath(EssentiaPath p) {
		activePaths.add(p);
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return true;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {

	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		return null;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return 36;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		amount = Math.min(THROUGHPUT, amount);
		EssentiaMovement r = network != null ? network.removeEssentia(this, face, aspect, amount) : null;
		if (r != null) {
			for (EssentiaPath p : r.paths()) {
				this.addPath(p);
			}
			return r.totalAmount;
		}
		return 0;
	}

	private int collectEssentiaToTarget(Aspect a, int amt, Coordinate tgt) {
		amt = Math.min(THROUGHPUT, amt);
		EssentiaMovement r = network != null ? network.removeEssentia(this, ForgeDirection.DOWN, a, amt, tgt) : null;
		if (r != null) {
			for (EssentiaPath p : r.paths()) {
				this.addPath(p);
			}
			return r.totalAmount;
		}
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		amount = Math.min(THROUGHPUT, amount);
		EssentiaMovement s = network != null ? network.addEssentia(this, face, aspect, amount) : null;
		if (s != null) {
			for (EssentiaPath p : s.paths()) {
				this.addPath(p);
			}
			return s.totalAmount;
		}
		return 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		TileEntity te = this.getAdjacentTileEntity(face);
		return te instanceof IEssentiaTransport	? ((IEssentiaTransport)te).getSuctionType(face.getOpposite()) : null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		Aspect a = this.getEssentiaType(face);
		return a != null && network != null ? network.countEssentia(worldObj, a) : 0;
	}

	@Override
	public int getMinimumSuction() {
		return 24;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	public void breakBlock() {
		if (network != null)
			network.reset(worldObj);
	}

	@Override
	public final void drop() {
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		//if (!this.shouldDrop())
		//	return;
		ItemStack is = this.getTile().getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		//this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

}
