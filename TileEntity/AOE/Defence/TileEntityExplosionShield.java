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

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
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
	public static final int MAXRANGE_Y = 24;

	private int range;
	private int rangeY;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.EXPLOSIONSHIELD;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote && !energy.isEmpty())
			this.spawnParticles(world, x, y, z);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();

			energy.subtract(CrystalElement.RED, Math.min(1000, Math.max(10, energy.getValue(CrystalElement.RED)/5000)));
		}

		range = MAXRANGE;
		rangeY = MAXRANGE_Y;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
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
		float f = (float)range/MAXRANGE;
		int n = (int)Math.ceil(4D*f*f*f);
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomBetween(xCoord+0.5-range, xCoord+0.5+range);
			double py = ReikaRandomHelper.getRandomBetween(yCoord+0.5-rangeY, yCoord+0.5+rangeY);
			double pz = ReikaRandomHelper.getRandomBetween(zCoord+0.5-range, zCoord+0.5+range);
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz);
			fx.setLife(ReikaRandomHelper.getRandomBetween(5, 100)).setScale(1+rand.nextFloat()).setColor(CrystalElement.RED.getColor());
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
		range = NBT.getInteger("range");
		rangeY = NBT.getInteger("rangeY");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
		NBT.setInteger("range", range);
		NBT.setInteger("rangeY", rangeY);
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
						if (Math.abs(p.chunkPosX-te.xCoord) <= te.range && Math.abs(p.chunkPosZ-te.zCoord) <= te.range && Math.abs(p.chunkPosY-te.yCoord) <= te.rangeY) {
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
		range = (int)(MAXRANGE*r);
		rangeY = (int)(MAXRANGE_Y*r);
	}

	@Override
	public int getRange() {
		return range;
	}

	public static void clearCache() {
		cache.clear();
	}

}
