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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaMovement;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


@Strippable(value={"thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityEssentiaRelay extends TileEntityChromaticBase implements IEssentiaTransport, BreakAction, SneakPop {

	//private static final int PATH_DURATION = 30;
	public static final int SEARCH_RANGE = 8;
	public static final int THROUGHPUT = 4;

	private static Class jarClass;
	private static Field filterField;

	private final StepTimer scanTimer = new StepTimer(50);

	EssentiaNetwork network;

	private final Collection<EssentiaPath> activePaths = new ArrayList();
	private final HashMap<Coordinate, Aspect> labelledJars = new HashMap();

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
				activeTargets.add(new Coordinate(p.target));
		}
		activePaths.clear();

		for (Coordinate c : labelledJars.keySet()) {
			if (!activeTargets.contains(c)) {
				Aspect a = labelledJars.get(c);
				TileEntity te = c.getTileEntity(world);
				if (te instanceof IAspectContainer) {
					IAspectContainer ia = (IAspectContainer)te;
					AspectList al = ia.getAspects();
					if (al != null) {
						int amt = Math.min(THROUGHPUT, 64-al.getAmount(a));
						if (amt > 0) {
							int found = this.collectEssentiaToTarget(a, amt, new WorldLocation(world, c));
							//ReikaJavaLibrary.pConsole(a.getName()+">"+amt+">"+found);
							if (found > 0) {
								//ReikaJavaLibrary.pConsole(a.getName()+">"+amt+">"+found);
								int ret = ia.addToContainer(a, found);
							}
						}
					}
				}
			}
		}

		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.scan(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.scan(world, x, y, z);
	}

	void scan(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		labelledJars.clear();
		network = new EssentiaNetwork();
		//network.addTile(this);
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
								if (tr.network != null)
									network.merge(tr.network);
							}
							network.addTile(this, te);
						}
						if (jarClass != null && jarClass.isAssignableFrom(te.getClass())) {
							Aspect a;
							try {
								a = (Aspect)filterField.get(te);
								if (a != null) {
									labelledJars.put(new Coordinate(te), a);
								}
							}
							catch (Exception e) {
								e.printStackTrace();
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

	private int collectEssentiaToTarget(Aspect a, int amt, WorldLocation tgt) {
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
		return a != null && network != null ? network.countEssentia(a) : 0;
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
			network.reset();
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

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				jarClass = Class.forName("thaumcraft.common.tiles.TileJarFillable");
				filterField = jarClass.getField("aspectFilter");
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch Warded Jar class");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}
		}
	}

}
