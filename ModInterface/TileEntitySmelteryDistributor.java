/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAreaDistributor;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.ParticlePath;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityFluidFX;
import Reika.DragonAPI.Instantiable.ParticleController.SplineMotionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySmelteryDistributor extends TileEntityAreaDistributor {

	private static Class drainClass;
	private static Method getDirection;
	private static Class basinClass;
	private static Class tableClass;

	static {
		try {
			drainClass = Class.forName("tconstruct.smeltery.logic.SmelteryDrainLogic");
			getDirection = drainClass.getDeclaredMethod("getForgeDirection");
			getDirection.setAccessible(true);

			tableClass = Class.forName("tconstruct.smeltery.logic.CastingTableLogic");
			basinClass = Class.forName("tconstruct.smeltery.logic.CastingBasinLogic");
		}
		catch (Exception e) {
			e.printStackTrace();
			ChromatiCraft.logger.logError("Could not load TiC smeltery drain or casting handlers!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}
	}

	private Coordinate controller;
	private final ArrayList<SmelteryDrain> drains = new ArrayList();
	private final ArrayList<CastingBlock> targets = new ArrayList();

	private void sendFluid(FluidStack fs, Coordinate drain, Coordinate target) {
		int x = drain.xCoord;
		int y = drain.yCoord;
		int z = drain.zCoord;
		int x2 = target.xCoord;
		int y2 = target.yCoord;
		int z2 = target.zCoord;
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FLUIDSEND.ordinal(), this, 48, x, y, z, x2, y2, z2, fs.getFluidID());
	}

	@SideOnly(Side.CLIENT)
	public void sendFluidToClient(int x, int y, int z, int x2, int y2, int z2, ForgeDirection drainFace, Fluid f) {
		ParticlePath path = ParticlePath.getPath(worldObj, new BlockVector(x, y, z, drainFace), new BlockVector(x2, y2, z2, ForgeDirection.UP), 0.5, 0.5);

		//if (this.trySendParticle(loc)) {
		int l = 90;

		double r = 0.3125;
		double dr = 0.0625;
		double px = xCoord+0.5;
		double py = yCoord+0.5;
		double pz = zCoord+0.5;
		float s = 0.75F;
		PositionController p = new SplineMotionController(l, path.spline);
		//MotionController p = TargetMotionController(x+0.5, y+0.5, z+0.5, 0.0625/16);
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(x-xCoord, y-yCoord, z-zCoord);
		double theta = -angs[1]+60*Math.sin(this.getTicksExisted()/64D);
		double phi = -angs[2]+90+60*Math.cos(this.getTicksExisted()/32D);
		double[] vel = ReikaPhysicsHelper.polarToCartesian(0.125, theta, phi);
		EntityFX fx = new EntityFluidFX(worldObj, px, py, pz, vel[0], vel[1], vel[2], f).setLife(l).setScale(s).setPositionController(p);//.markDestination(x, y, z);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		//}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.SMELTERYDISTRIBUTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}

		if (controller == null) {
			this.findController(world, x, y, z);
		}
		else {
			FluidStack fs = this.getTransferrableFluid(world);
			for (CastingBlock cb : targets) {
				cb.update(world);
			}
			Collections.sort(targets);
			CastingBlock cb = this.getFirstValidTarget(fs);
			if (cb != null) {
				SmelteryDrain sd = ReikaJavaLibrary.getRandomListEntry(rand, drains);
				this.doTransfer(sd, cb);
			}
		}
	}

	private void findController(World world, int x, int y, int z) {

	}

	private FluidStack getTransferrableFluid(World world) {
		TileEntity te = controller.getTileEntity(world);
	}

	private CastingBlock getFirstValidTarget(FluidStack fs) {
		for (CastingBlock cb : targets) {

		}
	}

	private void doTransfer(SmelteryDrain sd, CastingBlock cb) {

	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (rand.nextInt(3) == 0) {
			double px = x+rand.nextDouble();
			double py = y+rand.nextDouble();
			double pz = z+rand.nextDouble();
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625)/2;
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			int l = 20+rand.nextInt(60);
			float s = 1.5F;
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
			EntityFX fx = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(0x2255ff).setLife(l).setScale(s).setRapidExpand().bound(box).setIcon(ChromaIcons.BLURFLARE);
			EntityFX fx2 = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xffffff).setLife(l).setScale(s/2.5F).setRapidExpand().bound(box);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	protected boolean isValidTarget(TileEntity te) {
		if (te == this)
			return false;
		if (te == null)
			return false;
		if (te instanceof TileEntityRift)
			return false;
		Class c = te.getClass();
		if (te.yCoord >= yCoord)
			return false;
		return false;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static final class SmelteryDrain {

		private final Coordinate location;
		private final ForgeDirection facing;

		private SmelteryDrain(TileEntity te) throws Exception {
			location = new Coordinate(te);
			facing = this.getDirection(te);
		}

		private ForgeDirection getDirection(TileEntity te) throws Exception {
			return (ForgeDirection)getDirection.invoke(te);
		}

	}

	private static class CastingBlock implements Comparable<CastingBlock> {

		private final Coordinate location;
		private final boolean isBasin;

		private boolean isOutputFull;
		private boolean hasCast;
		private boolean hasNonIngotCast;

		private CastingBlock(TileEntity te) {
			location = new Coordinate(te);
			isBasin = te.getClass() == basinClass;
		}

		private void update(World world) {
			IInventory ii = (IInventory)location.getTileEntity(world);
			isOutputFull = ii.getStackInSlot(1) != null;
			hasCast = ii.getStackInSlot(0) != null;
			hasNonIngotCast = hasCast && !ReikaItemHelper.matchStacks(ii.getStackInSlot(0), TinkerToolHandler.getInstance().getIngotCast());
		}

		@Override
		public int compareTo(CastingBlock o) {
			return Integer.compare(o.getPriority(), this.getPriority());
		}

		private int getPriority() {
			if (isOutputFull)
				return -1000;
			if (isBasin) {
				return 100;
			}
			else {
				if (hasNonIngotCast) {
					return 500;
				}
				else if (hasCast) {
					return 50;
				}
				else { //empty table
					return 0;
				}
			}
		}

	}

}
