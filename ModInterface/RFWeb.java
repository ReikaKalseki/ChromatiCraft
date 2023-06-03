package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Base.BlockAttachableMini;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.ParticlePath;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.ParticleController.SplineMotionController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RFWeb {

	private static final HashMap<Integer, RFWeb> instances = new HashMap();

	static {
		FMLCommonHandler.instance().bus().register(new RFWebHandler());
	}

	public static final int RANGE = 12;
	public static final int THROUGHPUT = 3600/2;

	private final HashMap<Coordinate, RFConnection> data = new HashMap();

	private final int dimensionID;

	private RFWeb(World world) {
		dimensionID = world.provider.dimensionId;
	}

	public static class RFWebHandler {

		@SubscribeEvent
		public void tickEvent(WorldTickEvent evt) {
			if (evt.world.isRemote)
				return;
			RFWeb web = instances.get(evt.world.provider.dimensionId);
			if (web != null) {
				web.tick(evt.world);
			}
		}
	}

	public static RFWeb getWeb(World world) {
		RFWeb ret = instances.get(world.provider.dimensionId);
		if (ret == null) {
			ret = new RFWeb(world);
			instances.put(world.provider.dimensionId, ret);
		}
		return ret;
	}

	private void tick(World world) {
		HashSet<Coordinate> toRemove = new HashSet();
		//float avg = 0;
		int amt = 0;
		//long removable = 0;
		//long addable = 0;
		Iterator<Entry<Coordinate, RFConnection>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Coordinate, RFConnection> e = it.next();
			boolean valid = false;
			Coordinate c = e.getKey();
			RFConnection r = e.getValue();
			BlockKey at = c.getBlockKey(world);
			if (c.getBlockKey(world).equals(r.blockType)) {
				IEnergyConnection con = r.getConnection(world);
				if (con != null) {
					valid = true;
					amt++;
					r.recalculateFraction(con);
					//avg += r.currentFraction;
				}
			}
			if (!valid) {
				toRemove.add(c);
			}
		}
		for (Coordinate c : toRemove) {
			this.removeNode(world, c.xCoord, c.yCoord, c.zCoord);
		}
		if (amt == 0)
			return;
		//avg /= amt;
		/*
		HashSet<RFConnection> emitters = new HashSet();
		HashSet<RFConnection> receivers = new HashSet();
		for (RFConnection r : data.values()) {
			if (r.currentFraction < avg && r.canReceive) {
				//this.tryTransferTo(world, r, avg);
				receivers.add(r);
				addable += r.addEnergy(world, Integer.MAX_VALUE, false);
			}
			else if (r.currentFraction > avg && r.canEmit) {
				//this.tryTransferFrom(world, r, avg);
				emitters.add(r);
				removable += r.takeEnergy(world, Integer.MAX_VALUE, false);
			}
		}
		if (!emitters.isEmpty() && !receivers.isEmpty() && addable > 0 && removable > 0) {
			int mov = (int)Math.min(THROUGHPUT, Math.min(addable, removable)/emitters.size());
			long pool = 0;
			for (RFConnection r : emitters) {
				pool += r.takeEnergy(world, mov, true);
			}
			int mov2 = (int)(pool/receivers.size());
			for (RFConnection r : receivers) {
				r.addEnergy(world, mov2, true);
			}
		}*/
		for (RFConnection r : data.values()) {
			r.balanceWithBranches(this, world);
		}
	}/*

	private void tryTransferTo(World world, RFConnection r, float avg) {
		int amt = r.addEnergy(world, THROUGHPUT, false);
		if (amt == 0)
			return;
		amt = this.tryCollectEnergy(world, amt, avg);
		if (amt == 0)
			return;
		r.addEnergy(world, amt, true);
	}

	private void tryTransferFrom(World world, RFConnection r, float avg) {
		int amt = r.takeEnergy(world, THROUGHPUT, false);
		if (amt == 0)
			return;
		amt = this.tryDistributeEnergy(world, amt, avg);
		if (amt == 0)
			return;
		r.takeEnergy(world, amt, true);
	}

	private int tryDistributeEnergy(World world, int amt, float avg) {
		int ret = 0;
		for (RFConnection r : data.values()) {
			if (r.currentFraction < avg && r.canReceive) {
				int mov = r.addEnergy(world, amt, true);
				ret += mov;
				amt -= mov;
				if (amt <= 0)
					break;
			}
		}
		return ret;
	}

	private int tryCollectEnergy(World world, int amt, float avg) {
		int ret = 0;
		for (RFConnection r : data.values()) {
			if (r.currentFraction > avg && r.canEmit) {
				int mov = r.takeEnergy(world, amt, true);
				ret += mov;
				amt -= mov;
				if (amt <= 0)
					break;
			}
		}
		return ret;
	}*/

	public void addNode(World world, int x, int y, int z, ForgeDirection side) {
		if (world.isRemote)
			return;
		Coordinate c = new Coordinate(x, y, z);
		RFConnection get = data.get(c);
		if (get != null)
			return;
		RFConnection con = new RFConnection(world, c, side);
		data.put(c, con);
		for (RFConnection r : this.findNear(c)) {
			r.branches.add(c);
			con.branches.add(r.location);
		}
	}

	private Collection<RFConnection> findNear(Coordinate c) {
		Collection<RFConnection> li = new ArrayList();
		for (RFConnection r : data.values()) {
			if (r.location.isWithinDistOnAllCoords(c, RANGE)) {
				li.add(r);
			}
		}
		return li;
	}

	public void removeNode(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		Coordinate c = new Coordinate(x, y, z);
		RFConnection prev = data.remove(c);
		for (RFConnection r : data.values()) {
			r.branches.remove(c);
		}
	}

	public int tryDistributeEnergy(World world, int x, int y, int z, int amt) {
		amt = Math.min(amt, RFWeb.THROUGHPUT);
		Coordinate c = new Coordinate(x, y, z);
		RFConnection rf = data.get(c);
		if (rf == null)
			return 0;
		return rf.tryDistribute(this, world, amt);
	}

	@SideOnly(Side.CLIENT)
	private static boolean shouldAcceptParticle(World world) {
		return HoldingChecks.MANIPULATOR.isClientHolding() ? true : world.rand.nextInt(4) == 0;
	}

	@SideOnly(Side.CLIENT)
	public static void doSendParticle(World world, int x1, int y1, int z1, int x2, int y2, int z2, int amt) {
		if (!shouldAcceptParticle(world))
			return;
		ForgeDirection d1 = ((BlockAttachableMini)world.getBlock(x1, y1, z1)).getSide(world, x1, y1, z1);
		ForgeDirection d2 = ((BlockAttachableMini)world.getBlock(x2, y2, z2)).getSide(world, x2, y2, z2);
		ParticlePath p = ParticlePath.getPath(world, new BlockVector(x1, y1, z1, d1), new BlockVector(x2, y2, z2, d2), 0.375, 0.7);
		EntityCCBlurFX fx = new EntityCCBlurFX(world, x1+0.5, y1+0.5, z1+0.5);
		List<DecimalPosition> path = p != null ? p.getPath() : null;
		int l = p != null ? Math.max(10, 3*path.size()/2) : 90;
		float s = Math.max(0.8F, Math.min(2.4F, amt*2.4F/THROUGHPUT));
		fx.setColor(0xff0000).setScale(s).setLife(l).setAlphaFading().forceIgnoreLimits();
		int hash = p != null ? Math.abs(p.hashCode()) : 0;
		int dt = p != null ? Math.max(1, 5-path.size()/20) : 3;
		if (world.getTotalWorldTime()%dt != hash%dt)
			return;
		if (p != null) {
			fx.setPositionController(new SplineMotionController(l, p.spline));
		}
		else {
			double v = 0.0625;
			double dx = x2-x1;
			double dy = y2-y1;
			double dz = z2-z1;
			double d = ReikaMathLibrary.py3d(dx, dy, dz);
			fx.motionX = v*dx/d;
			fx.motionY = v*dy/d;
			fx.motionZ = v*dz/d;
			fx.setColor(0xff9090).setNoSlowdown();
		}
		if (p != null) {
			EntityCCBlurFX fx2 = new EntityCCBlurFX(world, x1+0.5, y1+0.5, z1+0.5);
			fx2.setColor(0xffffff).setScale(s/2F).setLife(l).setAlphaFading().forceIgnoreLimits();
			fx2.setPositionController(new SplineMotionController(l, p.spline).setTick(-6));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private static class RFConnection {

		private final ForgeDirection connection;
		private final Coordinate location;
		private final HashSet<Coordinate> branches = new HashSet();
		private final BlockKey blockType;

		private final boolean canEmit;
		private final boolean canReceive;
		private float currentFraction;

		private RFConnection(World world, Coordinate c, ForgeDirection dir) {
			blockType = c.getBlockKey(world);
			connection = dir;
			location = c;

			IEnergyConnection con = this.getConnection(world);
			canEmit = con instanceof IEnergyProvider || con instanceof IEnergyHandler;
			canReceive = con instanceof IEnergyReceiver || con instanceof IEnergyHandler;
		}

		private void balanceWithBranches(RFWeb web, World world) {
			for (Coordinate c : branches) {
				RFConnection r = web.data.get(c);
				if (r != null) {
					if (currentFraction > r.currentFraction) {
						this.moveEnergyTo(world, r, THROUGHPUT, false);
					}
				}
			}
		}

		private int tryDistribute(RFWeb web, World world, int amt) {
			int total = 0;
			for (Coordinate c : branches) {
				RFConnection r = web.data.get(c);
				if (r != null && r.canAcceptFrom(world, this)) {
					int moved = this.moveEnergyTo(world, r, amt, true);
					if (moved > 0) {
						amt -= moved;
						total += moved;
						if (amt <= 0)
							break;
					}
				}
			}
			return total;
		}

		private boolean canAcceptFrom(World world, RFConnection con) {
			return con != this && this.getConnection(world) != this.getConnection(world);
		}

		private int moveEnergyTo(World world, RFConnection r, int amt, boolean forceAllow) {
			int mov = r.addEnergy(world, amt, false);
			if (!forceAllow)
				mov = this.takeEnergy(world, mov, true);
			mov *= ChromaAux.getRFTransferEfficiency(world, location.xCoord, location.yCoord, location.zCoord);
			if (mov > 0)
				mov = r.addEnergy(world, mov, true);
			if (mov > 0 && this.shouldCreateParticle(world)) {
				int range = world.rand.nextInt(3) == 0 ? 60 : 30;
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.RFWEBSEND.ordinal(), world, location.xCoord, location.yCoord, location.zCoord, range, r.location.xCoord, r.location.yCoord, r.location.zCoord, mov);
			}
			return mov;
		}

		private boolean shouldCreateParticle(World world) {
			return world.rand.nextInt(2) == 0;
		}

		private int addEnergy(World world, int amt, boolean doAdd) {
			IEnergyConnection con = this.getConnection(world);
			if (con instanceof IEnergyHandler) {
				return ((IEnergyHandler)con).receiveEnergy(connection.getOpposite(), amt, !doAdd);
			}
			else if (con instanceof IEnergyReceiver) {
				return ((IEnergyReceiver)con).receiveEnergy(connection.getOpposite(), amt, !doAdd);
			}
			return 0;
		}

		private int takeEnergy(World world, int amt, boolean doTake) {
			IEnergyConnection con = this.getConnection(world);
			if (con instanceof IEnergyHandler) {
				return ((IEnergyHandler)con).extractEnergy(connection.getOpposite(), amt, !doTake);
			}
			else if (con instanceof IEnergyProvider) {
				return ((IEnergyProvider)con).extractEnergy(connection.getOpposite(), amt, !doTake);
			}
			return 0;
		}

		private void recalculateFraction(IEnergyConnection con) {
			currentFraction = this.getCurrentEnergy(con)/(float)this.getCapacity(con);
		}

		private int getCurrentEnergy(IEnergyConnection con) {
			if (con instanceof IEnergyHandler) {
				return ((IEnergyHandler)con).getEnergyStored(connection.getOpposite());
			}
			else if (con instanceof IEnergyReceiver) {
				return ((IEnergyReceiver)con).getEnergyStored(connection.getOpposite());
			}
			else if (con instanceof IEnergyProvider) {
				return ((IEnergyProvider)con).getEnergyStored(connection.getOpposite());
			}
			else {
				return 0;
			}
		}

		private int getCapacity(IEnergyConnection con) {
			if (con instanceof IEnergyHandler) {
				return ((IEnergyHandler)con).getMaxEnergyStored(connection.getOpposite());
			}
			else if (con instanceof IEnergyReceiver) {
				return ((IEnergyReceiver)con).getMaxEnergyStored(connection.getOpposite());
			}
			else if (con instanceof IEnergyProvider) {
				return ((IEnergyProvider)con).getMaxEnergyStored(connection.getOpposite());
			}
			else {
				return 0;
			}
		}

		private IEnergyConnection getConnection(World world) {
			TileEntity te = location.offset(connection, 1).getTileEntity(world);
			return te instanceof IEnergyConnection ? (IEnergyConnection)te : null;
		}

	}

}
