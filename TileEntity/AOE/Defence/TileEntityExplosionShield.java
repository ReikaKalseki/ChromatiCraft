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

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.ThreadSafeTileCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityExplosionShield extends CrystalReceiverBase implements LocationCached, RangeUpgradeable {

	private static final ThreadSafeTileCache cache = new ThreadSafeTileCache().setTileClass(TileEntityExplosionShield.class);

	public static final int MAXRANGE = 32;
	//public static final int MAXRANGE_Y = 24;
	public static final double RANGE_Y_FACTOR = 0.75;

	private final RangeTracker range = new RangeTracker(MAXRANGE);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.EXPLOSIONSHIELD;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		range.update(this);
		if (world.isRemote && !energy.isEmpty())
			this.spawnParticles(world, x, y, z);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();

			energy.subtract(CrystalElement.RED, Math.min(1000, Math.max(10, energy.getValue(CrystalElement.RED)/5000)));
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		range.initialize(this);
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}
	/*
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.hasStructure() ? ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(6, 4, 6) : super.getRenderBoundingBox();
	}
	 */
	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		int r = this.getRange();
		int ry = this.getYRange();
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		boolean hold = HoldingChecks.MANIPULATOR.isHolding(ep);
		double mx = xCoord+0.5-r;
		double my = Math.max(0, yCoord+0.5-ry);
		double mz = zCoord+0.5-r;
		double ax = xCoord+0.5+r;
		double ay = yCoord+0.5+ry;
		double az = zCoord+0.5+r;
		double mx2 = Math.max(ep.posX-60, mx);
		//double my2 = Math.max(ep.posY-40, my);
		double mz2 = Math.max(ep.posZ-60, mz);
		double ax2 = Math.min(ep.posX+60, ax);
		//double ay2 = Math.min(ep.posY+40, ay);
		double az2 = Math.min(ep.posZ+60, az);
		if (mx2 > ax2 || mz2 >= az2)
			return;
		double dx = ax2-mx2;
		double dy = ay-my;
		double dz = az2-mz2;
		/*
		double r0 = (dx*dy*dz)/(r*r*r);
		double f = r*r0*1D/MAXRANGE;
		double f2 = Math.sqrt(r*r0*1D/MAXRANGE);
		int n = (int)Math.ceil(1D*f*f*f*f);
		int n2 = (int)Math.ceil(12D*f2*f2*f2*f2);
		 */
		int n = (int)Math.ceil(3*dx*dy*dz/(MAXRANGE*MAXRANGE*MAXRANGE));
		int n2 = 9*n;
		if (!hold) {
			n /= 9;
			n2 /= 18;
		}
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomBetween(mx2, ax2);
			double py = ReikaRandomHelper.getRandomBetween(my, ay);
			double pz = ReikaRandomHelper.getRandomBetween(mz2, az2);
			int pix = MathHelper.floor_double(px);
			int piy = MathHelper.floor_double(py);
			int piz = MathHelper.floor_double(pz);
			Block b = world.getBlock(pix, piy, piz);
			if (b.isOpaqueCube() && b.renderAsNormalBlock() && b.getRenderType() == 0) { //skip ones inside the ground/walls/etc
				continue;
			}
			EntityBlurFX fx = new EntityCCBlurFX(world, px, py, pz);
			fx.setLife(ReikaRandomHelper.getRandomBetween(5, 100)).setScale(1+rand.nextFloat()).setColor(CrystalElement.RED.getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < n2; i++) {
			double px = ReikaRandomHelper.getRandomBetween(mx, ax);
			double py = ReikaRandomHelper.getRandomBetween(my, ay);
			double pz = ReikaRandomHelper.getRandomBetween(mz, az);
			switch(rand.nextInt(6)) {
				case 0:
					px = x-r;
					break;
				case 1:
					px = x+r+1;
					break;
				case 2:
					pz = z-r;
					break;
				case 3:
					pz = z+r+1;
					break;
				case 4:
					py = y-ry;
					break;
				case 5:
					py = y+ry+1;
					break;
			}
			if (Math.abs(px-ep.posX) > 12 || Math.abs(py-ep.posY) > 12 || Math.abs(pz-ep.posZ) > 12) {
				continue;
			}
			int l = ReikaRandomHelper.getRandomBetween(5, 100);
			float s = 1.5F+rand.nextFloat()*1.5F;
			EntityCCBlurFX fx = new EntityCCBlurFX(world, px, py, pz);
			fx.setLife(l).setScale(s).setColor(CrystalElement.RED.getColor());
			EntityCCBlurFX fx2 = new EntityCCBlurFX(world, px, py, pz);
			fx2.setIcon(ChromaIcons.FADE_GENTLE).setLife(l).setScale(s*0.5F).setColor(0xffffff).lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	private int getYRange() {
		return (int)(this.getRange()*RANGE_Y_FACTOR);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		range.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		range.writeToNBT(NBT);
	}

	private void checkAndRequest() {
		CrystalElement e = CrystalElement.RED;
		int capacity = this.getMaxStorage(e);
		int has = this.getEnergy(e);
		int space = capacity-has;
		if (space*4 > has) { // <80%
			this.requestEnergy(e, space);
		}
	}

	public static void dampenExplosion(World world, Explosion e) {
		cache.applyToMatches(world, true, (WorldLocation loc, TileEntity tile) -> {
			TileEntityExplosionShield te = (TileEntityExplosionShield)tile;
			//if (Math.abs(e.explosionY-loc.yCoord-0.5) <= te.rangeY) {
			//if (ReikaMathLibrary.py3d(e.explosionX-loc.xCoord-0.5, 0, e.explosionZ-loc.zCoord-0.5) <= te.range) {
			//if (te.canDampen(e)) {
			Iterator<ChunkPosition> it = e.affectedBlockPositions.iterator();
			while (it.hasNext()) {
				ChunkPosition p = it.next();
				if (te.isLocationProtectedBy(world, p.chunkPosX, p.chunkPosY, p.chunkPosZ, e.explosionSize)) {
					it.remove();
				}
			}
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(world, loc, te, ChromaTiles.EXPLOSIONSHIELD);
		});
	}

	public static boolean isLocationProtected(World world, int x, int y, int z, double power) {
		return cache.lookForMatch(world, true, (WorldLocation loc, TileEntity te) -> {
			return ((TileEntityExplosionShield)te).isLocationProtectedBy(world, x, y, z, power);
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(world, loc, te, ChromaTiles.EXPLOSIONSHIELD);
		});
	}

	public boolean isLocationProtectedBy(World world, int x, int y, int z, double power) {
		int cost = 50*(int)(Math.pow(2, power));
		if (energy.containsAtLeast(CrystalElement.RED, cost)) {
			int r = this.getRange();
			int ry = this.getYRange();
			if (Math.abs(x-xCoord) <= r && Math.abs(z-zCoord) <= r && Math.abs(y-yCoord) <= ry) {
				return true;
			}
		}
		return false;
	}

	/*
	private boolean canDampen(Explosion e) {
		int amt = (int)(RATIO*Math.pow(dmg, POWER));
		if (energy.containsAtLeast(CrystalElement.RED, amt)) {
			this.drainEnergy(CrystalElement.RED, amt);
			return true;
		}
		return false;
	}
	 */

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == CrystalElement.RED;
	}

	@Override
	public int maxThroughput() {
		return 4000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 5000000;
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	@Override
	public void upgradeRange(double r) {

	}

	@Override
	public int getRange() {
		return range.getRange();
	}

	public static void clearCache() {
		cache.clear();
	}

}
