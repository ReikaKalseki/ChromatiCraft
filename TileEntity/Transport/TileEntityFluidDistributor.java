/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAreaDistributor;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Spline;
import Reika.DragonAPI.Instantiable.Spline.BasicVariablePoint;
import Reika.DragonAPI.Instantiable.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Effects.EntityFluidFX;
import Reika.DragonAPI.Instantiable.ParticleController.SplineMotionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFluidDistributor extends TileEntityAreaDistributor implements IFluidHandler {

	private static final HashSet<Class> blacklist = new HashSet();
	private PluralMap<Spline> splines = new PluralMap(2);

	@Override
	public int fill(ForgeDirection from, FluidStack fs, boolean simulate) {
		if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			return 0;
		this.addInput(new WorldLocation(this).move(from, 1));
		return this.tryDistributeFluid(fs, simulate);
	}

	private int tryDistributeFluid(FluidStack fs, boolean simulate) {
		int add = 0;
		Iterator<WorldLocation> it = this.getTargets();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			TileEntity te = loc.getTileEntity();
			if (te instanceof IFluidHandler) {
				int give = this.tryGiveFluid(fs, simulate, (IFluidHandler)te);
				if (give > 0) {
					if (!worldObj.isRemote)
						this.sendFluid(new FluidStack(fs.getFluid(), give), loc, (IFluidHandler)te);
					fs.amount -= give;
					add += give;
					if (fs.amount <= 0)
						return add;
				}
			}
			else {
				it.remove();
			}
		}
		return add;
	}

	private int tryGiveFluid(FluidStack fs, boolean simulate, IFluidHandler ie) {
		int add = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int give = ie.fill(dir, fs, simulate);
			if (give > 0) {
				fs.amount -= give;
				add += give;
				if (fs.amount <= 0)
					return add;
			}
		}
		return add;
	}

	private void sendFluid(FluidStack fs, WorldLocation loc, IFluidHandler ie) {
		int x = loc.xCoord;
		int y = loc.yCoord;
		int z = loc.zCoord;
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FLUIDSEND.ordinal(), this, 48, x, y, z, fs.getFluidID(), fs.amount);
	}

	@SideOnly(Side.CLIENT)
	public void sendFluidToClient(int x, int y, int z, Fluid f, int amt) {
		WorldLocation loc = new WorldLocation(worldObj, x, y, z);
		Spline spl = this.getOrCreateSpline(f, loc);
		spl.update();
		//if (this.trySendParticle(loc)) {
		int l = 90;

		double r = 0.3125;
		double dr = 0.0625;
		FluidStack fs = new FluidStack(f, amt);
		double px = xCoord+0.5;
		double py = yCoord+0.5;
		double pz = zCoord+0.5;
		float s = (float)(0.5+ReikaMathLibrary.logbase(amt, 10)/4D);
		PositionController p = new SplineMotionController(px, py, pz, x+0.5, y+0.5, z+0.5, l, spl);
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

	private Spline getOrCreateSpline(Fluid f, WorldLocation loc) {
		Spline s = splines.get(loc, f);
		if (s == null) {
			s = new Spline(SplineType.CHORDAL);
			splines.put(s, loc, f);
			DecimalPosition pos1 = new DecimalPosition(this);
			DecimalPosition pos2 = new DecimalPosition(loc).offset(0, 1, 0);
			for (double d = 0; d <= 1; d += 0.125) {
				BasicVariablePoint p = new BasicVariablePoint(DecimalPosition.interpolate(pos1, pos2, d), 0.5, 0.03125/4);
				p.tolerance *= 0.03125;
				s.addPoint(p);
			}
			BasicVariablePoint p = new BasicVariablePoint(new DecimalPosition(loc), 0.5, 0.03125/4);
			p.tolerance *= 0.03125;
			s.addPoint(p);
		}
		return s;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FLUIDDISTRIBUTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
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
		Class c = te.getClass();
		if (blacklist.contains(c))
			return false;
		if (te.yCoord >= yCoord)
			return false;
		if (te instanceof IFluidHandler) {
			String s = c.getName().toLowerCase(Locale.ENGLISH);
			if (s.contains("conduit") || ReikaStringParser.containsWord(s, "duct") || s.contains("cable") || s.contains("pipe")) {
				blacklist.add(c);
				return false;
			}
			if (s.contains("tesseract") || s.contains("hypercube")) { //SOE
				blacklist.add(c);
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[0];
	}

}
