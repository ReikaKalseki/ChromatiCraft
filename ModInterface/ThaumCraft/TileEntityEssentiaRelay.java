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
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaMovement;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.EssentiaNetwork.EssentiaSubnet;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;


@Strippable(value={"thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityEssentiaRelay extends TileEntityChromaticBase implements IEssentiaTransport, BreakAction, SneakPop {

	//private static final int PATH_DURATION = 30;
	public static final int SEARCH_RANGE = 8;
	public static final int THROUGHPUT = 4;

	private final StepTimer scanTimer = new StepTimer(50);

	private final Collection<EssentiaPath> activePaths = new ArrayList();
	private EssentiaSubnet network;
	private boolean isController;
	private boolean alreadyDropped;

	private static Class infusionMatrix;
	private static Class essentiaHandler;
	private static HashMap<WorldCoordinates, ArrayList<WorldCoordinates>> essentiaHandlerData;

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				infusionMatrix = Class.forName("thaumcraft.common.tiles.TileInfusionMatrix");
				essentiaHandler = Class.forName("thaumcraft.common.lib.events.EssentiaHandler");
			}
			catch (Exception e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
				ChromatiCraft.logger.logError("Could not access TC infusion matrix classes!");
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("controller", isController);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		isController = NBT.getBoolean("controller");
	}

	public void tryBuildNetwork() {
		if (network != null) {
			network.destroy(worldObj, false);
			//ChromaSounds.ERROR.playSoundAtBlock(this);
			ChromaSounds.USE.playSoundAtBlock(this, 1, 0.5F);
			//network.reloadEndpoints(worldObj);
		}
		else {
			network = EssentiaNetwork.NetworkBuilder.buildFrom(this);
			if (network != null) {
				isController = true;
				ChromaSounds.CAST.playSoundAtBlock(this);
			}
			else {
				ChromaSounds.ERROR.playSoundAtBlock(this);
			}
		}
	}

	public Map<Coordinate, Boolean> getNetworkTiles() {
		return network != null ? network.getGeneralizedNetworkRenderer() : null;
	}

	public Collection<Coordinate> getVisibleOtherNodes() {
		return network != null ? network.getNode(this).getNeighbors() : null;
	}

	public Collection<Coordinate> getVisibleEndpoints() {
		return network != null ? network.getNode(this).getVisibleEndpoints() : null;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ESSENTIARELAY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		HashSet<Coordinate> activeTargets = new HashSet();
		Iterator<EssentiaPath> it = activePaths.iterator();
		while (it.hasNext()) {
			EssentiaPath p = it.next();
			p.update(world, x, y, z);
			if (p.target != null)
				activeTargets.add(p.target);
			it.remove();
		}

		if (network != null) {
			if (!world.isRemote) {
				EssentiaMovement mov = network.tick(world);
				if (mov != null) {
					for (EssentiaPath p : mov.paths()) {
						this.addPath(p);
					}
				}
				TileEntity te = this.getAdjacentTileEntity(ForgeDirection.DOWN);
				if (te != null && te.getClass() == EssentiaNetwork.getCentrifugeClass()) {
					IEssentiaTransport ie = (IEssentiaTransport)te;
					Aspect pull = ie.getEssentiaType(ForgeDirection.UP);
					if (pull != null && ie.getEssentiaAmount(ForgeDirection.UP) > 0) {
						EssentiaMovement em = network.addEssentia(this, ForgeDirection.DOWN, pull, 1);
						if (em != null && em.totalAmount > 0) {
							int rem = ie.takeEssentia(pull, 1, ForgeDirection.UP);
							for (EssentiaPath p : em.paths()) {
								this.addPath(p);
							}
						}
					}
				}
			}
			else {
				this.doNetworkConnectivityParticles(world, x, y, z);
			}
		}

		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.scan(world, x, y, z, false);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doNetworkConnectivityParticles(World world, int x, int y, int z) {
		if (HoldingChecks.MANIPULATOR.isClientHolding()) {
			int n = 12;
			int n2 = 48;
			Coordinate h = new Coordinate(this);
			for (Coordinate c : this.getVisibleOtherNodes()) {
				if (c.hashCode() >= h.hashCode()) {
					if (this.getTicksExisted()%n == (c.hashCode()^h.hashCode())%n)
						this.doConnectionParticles(world, x, y, z, c.xCoord, c.yCoord, c.zCoord, true);
				}
			}
			for (Coordinate c : this.getVisibleEndpoints()) {
				if (this.getTicksExisted()%(n2+n) == (c.hashCode()^h.hashCode())%(n2+n))
					this.doConnectionParticles(world, x, y, z, c.xCoord, c.yCoord, c.zCoord, false);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doConnectionParticles(World world, int x, int y, int z, int x2, int y2, int z2, boolean isNode) {
		for (double d = 0; d <= 1; d += 0.03125) {
			double dx = x+(x2-x)*d;
			double dy = y+(y2-y)*d;
			double dz = z+(z2-z)*d;
			int c = isNode ? 0x6f6fff : 0x6fff6f;
			float s = 0.5F+(float)(2*Math.min(d, 1-d));
			EntityBlurFX fx = new EntityBlurFX(world, dx+0.5, dy+0.5, dz+0.5).setScale(s).setColor(c).setAlphaFading().setLife(10).setIcon(ChromaIcons.CENTER);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		//this.scan(world, x, y, z, true);
		if (isController && network == null) {
			this.tryBuildNetwork();
		}
	}

	private void scan(World world, int x, int y, int z, boolean rebuild) {
		if (world.isRemote)
			return;
		if (network == null)
			return;
		HashSet<Coordinate> matrices = new HashSet();
		for (int i = -SEARCH_RANGE; i <= SEARCH_RANGE; i++) {
			for (int j = -SEARCH_RANGE; j <= SEARCH_RANGE; j++) {
				for (int k = -SEARCH_RANGE; k <= SEARCH_RANGE; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = world.getTileEntity(dx, dy, dz);
					if (te != null && te.getClass() == infusionMatrix) {
						matrices.add(new Coordinate(te));
					}
				}
			}
		}
		if (!matrices.isEmpty()) {
			for (Coordinate loc : matrices) {
				Collection<Coordinate> li = network.getAllEndpoints();
				for (Coordinate c : li) {
					if (network.isFilteredJar(c)) {
						this.injectFilteredJar(loc, c);
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(network, !world.isRemote);
	}

	private void injectFilteredJar(Coordinate loc, Coordinate c) {
		if (essentiaHandlerData == null) {
			try {
				Field f = essentiaHandler.getDeclaredField("sources");
				f.setAccessible(true);
				essentiaHandlerData = (HashMap<WorldCoordinates, ArrayList<WorldCoordinates>>)f.get(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (essentiaHandlerData != null) {
			WorldCoordinates key = new WorldCoordinates(loc.xCoord, loc.yCoord, loc.zCoord, worldObj.provider.dimensionId);
			ArrayList<WorldCoordinates> li = essentiaHandlerData.get(key);
			if (li == null) {
				li = new ArrayList();
				essentiaHandlerData.put(key, li);
			}
			WorldCoordinates add = new WorldCoordinates(c.xCoord, c.yCoord, c.zCoord, worldObj.provider.dimensionId);
			if (!li.contains(add)) {
				li.add(add);
			}
		}
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
		if (network == null)
			return 0;
		amount = Math.min(THROUGHPUT, amount);
		EssentiaMovement r = network.removeEssentia(this, face, aspect, amount);
		if (r != null) {
			for (EssentiaPath p : r.paths()) {
				this.addPath(p);
			}
			return r.totalAmount;
		}
		return 0;
	}

	private int collectEssentiaToTarget(Aspect a, int amt, Coordinate tgt) {
		if (network == null)
			return 0;
		amt = Math.min(THROUGHPUT, amt);
		EssentiaMovement r = network.removeEssentia(this, ForgeDirection.DOWN, a, amt, tgt);
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
		if (network == null)
			return 0;
		amount = Math.min(THROUGHPUT, amount);
		EssentiaMovement s = network.addEssentia(this, face, aspect, amount);
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
		if (network == null)
			return null;
		TileEntity te = this.getAdjacentTileEntity(face);
		return te instanceof IEssentiaTransport	? ((IEssentiaTransport)te).getSuctionType(face.getOpposite()) : null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		if (network == null)
			return 0;
		Aspect a = this.getEssentiaType(face);
		return a != null ? network.countEssentia(worldObj, a) : 0;
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
			network.destroy(worldObj, true);
		network = null;
		isController = false;
	}

	public void reset() {
		//network.destroy(worldObj, false);
		network = null;
		isController = false;
	}

	void setNetwork(EssentiaSubnet net) {
		network = net;
	}

	EssentiaSubnet getNetwork() {
		return network;
	}

	@Override
	public final void drop() {
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		//if (!this.shouldDrop())
		//	return;
		if (alreadyDropped)
			return;
		alreadyDropped = true;
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
