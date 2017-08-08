/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAreaDistributor;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = {"cofh.api.energy.IEnergyReceiver", "cofh.api.energy.IEnergyHandler"})
public class TileEntityRFDistributor extends TileEntityAreaDistributor implements IEnergyReceiver, IEnergyHandler { //IEH only for EiO conduit connection

	private static final HashSet<Class> blacklist = new HashSet();

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (this.hasRedstoneSignal())
			return 0;
		this.addInput(new WorldLocation(this).move(from, 1));
		return this.tryDistributeEnergy(maxReceive, simulate);
	}

	private int tryDistributeEnergy(int maxReceive, boolean simulate) {
		int add = 0;
		Iterator<WorldLocation> it = this.getTargets();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			TileEntity te = loc.getTileEntity();
			if (te instanceof IEnergyReceiver || te instanceof IEnergyHandler) {
				int give = this.tryGiveEnergy(maxReceive, simulate, (IEnergyReceiver)te);
				if (give > 0) {
					this.sendEnergy(give, loc, (IEnergyReceiver)te);
					maxReceive -= give;
					add += give;
					if (maxReceive <= 0)
						return add;
				}
			}
			else {
				it.remove();
			}
		}
		return add;
	}

	private int tryGiveEnergy(int maxReceive, boolean simulate, IEnergyReceiver ie) {
		int add = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int give = ie.receiveEnergy(dir, maxReceive, simulate);
			if (give > 0) {
				maxReceive -= give;
				add += give;
				if (maxReceive <= 0)
					return add;
			}
		}
		return add;
	}

	private void sendEnergy(int rf, WorldLocation loc, IEnergyReceiver ie) {
		int x = loc.xCoord;
		int y = loc.yCoord;
		int z = loc.zCoord;
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.RFSEND.ordinal(), this, 48, x, y, z, rf);
	}

	@SideOnly(Side.CLIENT)
	public void sendRFToClient(int x, int y, int z, int rf) {
		WorldLocation loc = new WorldLocation(worldObj, x, y, z);
		if (this.trySendParticle(loc)) {
			int l = 40;
			double dx = x-xCoord;
			double dy = y-yCoord;
			double dz = z-zCoord;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = 0.25;
			double vx = v*dx/dd;
			double vy = v*dy/dd;
			double vz = v*dz/dd;

			double r = 0.3125;
			double dr = 0.0625;
			for (double d = -r; d <= r; d += dr) {
				double px = xCoord+0.5+dx/dd*d;
				double py = yCoord+0.5+dy/dd*d;
				double pz = zCoord+0.5+dz/dd*d;
				float s = (float)(1.5+ReikaMathLibrary.logbase(rf, 10)*(1D-d*d*12));
				EntityFX fx = new EntityBlurFX(worldObj, px, py, pz, vx, vy, vz).setColor(0xff0000).setLife(l).setScale(s).setRapidExpand().markDestination(x, y, z);
				EntityFX fx2 = new EntityBlurFX(worldObj, px, py, pz, vx, vy, vz).setColor(0xffffff).setLife(l).setScale(s/2).setRapidExpand().markDestination(x, y, z);
				fx.noClip = true;
				fx2.noClip = true;
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}
		}
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RFDISTRIBUTOR;
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
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			int l = 20+rand.nextInt(60);
			float s = 1.5F;
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
			EntityFX fx = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xff0000).setLife(l).setScale(s).setRapidExpand().bound(box);
			EntityFX fx2 = new EntityBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xffffff).setLife(l).setScale(s/2).setRapidExpand().bound(box);
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
		if (blacklist.contains(c))
			return false;
		if (te instanceof TileEntityRFDistributor)
			return te.yCoord < yCoord;
		if (te instanceof IEnergyReceiver || te instanceof IEnergyHandler) {
			String s = c.getName().toLowerCase(Locale.ENGLISH);
			if (s.contains("conduit") || ReikaStringParser.containsWord(s, "duct") || s.contains("cable") || s.contains("pipepower") || ReikaStringParser.containsWord(s, "wire")) {
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
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

}
