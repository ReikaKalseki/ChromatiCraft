/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalBeacon extends CrystalReceiverBase implements LocationCached, RangeUpgradeable, MultiBlockChromaTile {

	private static final Collection<WorldLocation> cache = new ArrayList();

	public static final int RATIO = 100;
	public static final int POWER = 2;
	public static final int MAXRANGE = 64;

	private boolean hasStructure;

	private int range;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEACON;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (!world.isRemote && hasStructure && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		range = MAXRANGE;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.validateStructure();
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}

	public void validateStructure() {
		hasStructure = ChromaStructures.getProtectionBeaconStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		//ReikaJavaLibrary.pConsole(hasStructure, Side.SERVER);
		this.syncAllData(false);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.hasStructure() ? ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(6, 4, 6) : super.getRenderBoundingBox();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double angle = (System.currentTimeMillis()/15D)%360;
		double d = 0.05;
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(y+1.5+0.5*(1+Math.sin(Math.toRadians(angle))), d);
		CrystalElement c = CrystalElement.randomElement();//CrystalElement.elements[(this.getTicksExisted()/16)%16];
		EntityBlurFX fx = new EntityBlurFX(c, world, px, py, pz, 0, 0, 0).setScale(2F).setLife(10).setIcon(ChromaIcons.CENTER);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		if (energy.contains(CrystalElement.RED) && hasStructure) {
			double a = rand.nextDouble()*360;
			double v = rand.nextBoolean() ? 0.03125 : -0.03125;
			double r = 0.25;
			px = x+0.5+r*Math.cos(Math.toRadians(a));
			pz = z+0.5+r*Math.sin(Math.toRadians(a));
			py = y+ReikaRandomHelper.getRandomBetween(0.25, 1.5);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			fx = new EntityBlurFX(c, world, px, py, pz, 0, v, 0).setScale(1F).setLife(30).setIcon(ChromaIcons.FADE_GENTLE).setColor(CrystalElement.RED.getColor()).setRapidExpand().setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			int[][] dir = {
					{-2, -2},
					{-2, 2},
					{2, 2},
					{2, -2},
			};
			for (int t = 0; t < 80; t += 30) {
				int i = this.getTicksExisted()%4;
				//for (int i = 0; i < 4; i++) {
				int dx = dir[i][0];//rand.nextBoolean() ? 2 : -2;
				int dz = dir[i][1];//rand.nextBoolean() ? 2 : -2;
				int dy = 1;
				d = 1-(((this.getTicksExisted()+t+i*20)%80)/80D);//rand.nextDouble();
				px = x+0.5+dx*d;
				pz = z+0.5+dz*d;
				py = y+0.5+dy*d;
				/*
				v = ReikaRandomHelper.getRandomBetween(0.03125, 0.0625);
				double vx = v*Math.signum(dx);
				double vy = v*Math.signum(dy);
				double vz = v*Math.signum(dz);
				if (rand.nextBoolean()) {
					vx = -vx;
					vy = -vy;
					vz = -vz;
				}
				 */
				double vx = 0;
				double vy = 0;
				double vz = 0;
				fx = new EntityBlurFX(c, world, px, py, pz, vx, vy, vz).setScale(1.5F).setLife(10).setIcon(ChromaIcons.NODE2).setColor(CrystalElement.RED.getColor());
				EntityFX fx2 = new EntityBlurFX(c, world, px, py, pz, vx, vy, vz).setScale(0.75F).setLife(10).setIcon(ChromaIcons.NODE2).setColor(0xffffff);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}
			//}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		range = NBT.getInteger("range");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setInteger("range", range);
	}

	private void checkAndRequest() {
		CrystalElement e = CrystalElement.RED;
		int capacity = this.getMaxStorage(e);
		int space = capacity-this.getEnergy(e);
		if (space > 0) {
			this.requestEnergy(e, space);
		}
	}

	public static boolean isPlayerInvincible(EntityPlayer ep, float dmg) {
		for (WorldLocation loc : cache) {
			if (loc.dimensionID == ep.worldObj.provider.dimensionId) {
				TileEntityCrystalBeacon te = (TileEntityCrystalBeacon)loc.getTileEntity(ep.worldObj);
				if (Math.abs(ep.posY-loc.yCoord) <= te.range/2) {
					if (loc.getDistanceTo(ep) <= te.range) {
						if (te.isPlacer(ep) && te.prevent(dmg))
							return true;
					}
				}
			}
		}
		return false;
	}

	private boolean prevent(float dmg) {
		if (this.hasStructure()) {
			int amt = (int)(RATIO*Math.pow(dmg, POWER));
			if (energy.containsAtLeast(CrystalElement.RED, amt)) {
				this.drainEnergy(CrystalElement.RED, amt);
				return true;
			}
		}
		return false;
	}

	public boolean hasStructure() {
		return hasStructure;
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == CrystalElement.RED;
	}

	@Override
	public int maxThroughput() {
		return 500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 250000;
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	public static boolean isDamageBlockable(DamageSource src) {
		return !(src instanceof PylonDamage);
	}

	@Override
	public void upgradeRange(double r) {
		range = (int)(MAXRANGE*r);
	}

	@Override
	public int getRange() {
		return range;
	}

	public static void clearCache() {
		cache.clear();
	}

}
