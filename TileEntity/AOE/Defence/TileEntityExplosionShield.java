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
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityExplosionShield extends CrystalReceiverBase implements LocationCached, RangeUpgradeable {

	private static final Collection<WorldLocation> cache = new ArrayList();

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
		double f = this.getRange()*1D/MAXRANGE;
		double f2 = Math.sqrt(this.getRange()*1D/MAXRANGE);
		int r = this.getRange();
		int ry = this.getYRange();
		int n = (int)Math.ceil(4D*f*f*f);
		int n2 = (int)Math.ceil(48D*f2*f2*f2);
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double mx = xCoord+0.5-r;
		double my = Math.max(0, yCoord+0.5-ry);
		double mz = zCoord+0.5-r;
		double ax = xCoord+0.5+r;
		double ay = yCoord+0.5+ry;
		double az = zCoord+0.5+r;
		for (int i = 0; i < n; i++) {
			double mx2 = Math.max(ep.posX-60, mx);
			//double my2 = Math.max(ep.posY-40, my);
			double mz2 = Math.max(ep.posZ-60, mz);
			double ax2 = Math.min(ep.posX+60, ax);
			//double ay2 = Math.min(ep.posY+40, ay);
			double az2 = Math.min(ep.posZ+60, az);
			if (mx2 > ax2 || mz2 >= az2)
				continue;
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
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz);
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
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz);
			fx.setLife(l).setScale(s).setColor(CrystalElement.RED.getColor());
			EntityBlurFX fx2 = new EntityBlurFX(world, px, py, pz).lockTo(fx);
			fx2.setLife(l).setScale(s*0.5F).setColor(0xffffff).setIcon(ChromaIcons.FADE_GENTLE);
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
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
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
		for (WorldLocation loc : cache) {
			if (loc.dimensionID == world.provider.dimensionId) {
				TileEntityExplosionShield te = (TileEntityExplosionShield)loc.getTileEntity(world);
				//if (Math.abs(e.explosionY-loc.yCoord-0.5) <= te.rangeY) {
				//if (ReikaMathLibrary.py3d(e.explosionX-loc.xCoord-0.5, 0, e.explosionZ-loc.zCoord-0.5) <= te.range) {
				//if (te.canDampen(e)) {
				Iterator<ChunkPosition> it = e.affectedBlockPositions.iterator();
				while (it.hasNext()) {
					ChunkPosition p = it.next();
					int energy = 50*(int)(Math.pow(2, e.explosionSize));
					if (te.energy.containsAtLeast(CrystalElement.RED, energy)) {
						int r = te.getRange();
						int ry = te.getYRange();
						if (Math.abs(p.chunkPosX-te.xCoord) <= r && Math.abs(p.chunkPosZ-te.zCoord) <= r && Math.abs(p.chunkPosY-te.yCoord) <= ry) {
							it.remove();
						}
					}
				}
				//}
				//}
				//}
			}
		}
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

	public static boolean isDamageBlockable(DamageSource src) {
		return !(src instanceof PylonDamage);
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
