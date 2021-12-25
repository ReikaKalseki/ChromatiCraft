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

import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.ThreadSafeTileCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalBeacon extends CrystalReceiverBase implements LocationCached, RangeUpgradeable, MultiBlockChromaTile, OwnedTile {

	private static final ThreadSafeTileCache cache = new ThreadSafeTileCache().setTileClass(TileEntityCrystalBeacon.class);

	public static final int RATIO = 100;
	public static final int POWER = 2;

	public static final int MAXRANGE = 64;

	public static final int COOLDOWN = 30;
	//private static final TimerMap<UUID> playerCooldowns = new TimerMap();
	private static final String NBT_TAG = "LAST_PROTECTION_BEACON_DATA";

	private boolean hasStructure;

	private final RangeTracker range = new RangeTracker(MAXRANGE);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEACON;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		range.update(this);
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (!world.isRemote && hasStructure && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		//if (!world.isRemote) {
		//	playerCooldowns.tick();
		//}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		range.initialize(this);
		this.validateStructure();
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}

	public void validateStructure() {
		ChromaStructures.PROTECT.getStructure().resetToDefaults();
		hasStructure = ChromaStructures.PROTECT.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
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
		EntityBlurFX fx = new EntityCCBlurFX(c, world, px, py, pz, 0, 0, 0).setIcon(ChromaIcons.CENTER).setScale(2F).setLife(10);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		if (energy.contains(CrystalElement.RED) && hasStructure) {
			double a = rand.nextDouble()*360;
			double v = rand.nextBoolean() ? 0.03125 : -0.03125;
			double r = 0.25;
			px = x+0.5+r*Math.cos(Math.toRadians(a));
			pz = z+0.5+r*Math.sin(Math.toRadians(a));
			py = y+ReikaRandomHelper.getRandomBetween(0.25, 1.5);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			fx = new EntityCCBlurFX(c, world, px, py, pz, 0, v, 0).setIcon(ChromaIcons.FADE_GENTLE).setScale(1F).setLife(30).setColor(CrystalElement.RED.getColor()).setRapidExpand().setGravity(g);
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
				fx = new EntityCCBlurFX(c, world, px, py, pz, vx, vy, vz).setIcon(ChromaIcons.NODE2).setScale(1.5F).setLife(10).setColor(CrystalElement.RED.getColor());
				EntityFX fx2 = new EntityCCBlurFX(c, world, px, py, pz, vx, vy, vz).setIcon(ChromaIcons.NODE2).setScale(0.75F).setLife(10).setColor(0xffffff);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			}
			//}

			int rg = this.getRange();
			double pt = ReikaRenderHelper.getPartialTickTime();
			for (EntityPlayer ep : this.getOwners(false)) {
				long dur = ep.worldObj.getTotalWorldTime()-ep.getEntityData().getCompoundTag(NBT_TAG).getLong("time");
				//ReikaJavaLibrary.pConsole(dur);
				if (dur < 60) {
					float ap = dur < 30 ? 1 : 1-((dur-30)/30F);
					double dx = ep.posX+(ep.posX-ep.lastTickPosX)*pt-x-0.5;
					double dy = ep.posY+(ep.posY-ep.lastTickPosY)*pt-y-0.5;
					double dz = ep.posZ+(ep.posZ-ep.lastTickPosZ)*pt-z-0.5;
					if (dx <= rg && dz <= rg && dy <= rg/2) {
						Random rand = new Random(ep.hashCode());
						double a0 = ep.hashCode()%360D;
						double dt = 0;
						for (int n = 0; n < 8; n++) {
							v = 0.5+1.5*rand.nextDouble();
							for (double dt2 = 0; dt2 <= 3; dt2 += 1.5) {
								double t = (ep.ticksExisted+pt+dt+dt2)*v;
								double ang = a0+t*6;
								r = 1+0.25*Math.sin(t*0.06D);
								ang = Math.toRadians(ang);
								px = ep.posX+r*Math.cos(ang);
								pz = ep.posZ+r*Math.sin(ang);
								py = ep.posY-0.6+0.6*Math.sin(t*0.017);

								fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.CENTER).setScale(0.7F).setLife(8);
								int clr = ReikaColorAPI.getColorWithBrightnessMultiplier(CrystalElement.RED.getColor(), ap);
								fx.setColor(clr);
								Minecraft.getMinecraft().effectRenderer.addEffect(fx);
							}
							dt += rand.nextDouble()*90;
						}
					}
				}
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
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
		if (updateDamageNBT(ep, dmg))
			return true;
		if (cache.lookForMatch(ep.worldObj, true, (WorldLocation loc, TileEntity tile) -> {
			TileEntityCrystalBeacon te = (TileEntityCrystalBeacon)tile;
			int r = te.getRange();
			if (Math.abs(ep.posY-te.yCoord) <= r/2) {
				if (loc.getDistanceTo(ep) <= r) {
					if (te.isPlacer(ep) && te.prevent(dmg)) {
						ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
						return true;
					}
				}
			}
			return false;
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(ep.worldObj, loc, te, ChromaTiles.BEACON);
		})) {
			return true;
		}
		ep.getEntityData().removeTag(NBT_TAG);
		return false;
	}

	private static boolean updateDamageNBT(EntityLivingBase ep, float dmg) {
		NBTTagCompound tag = ep.getEntityData().getCompoundTag(NBT_TAG);
		ep.getEntityData().setTag(NBT_TAG, tag);
		if (ep.worldObj.provider.dimensionId != tag.getInteger("dimension")) {
			ep.getEntityData().removeTag(NBT_TAG);
		}
		if (ep.worldObj.getTotalWorldTime()-tag.getLong("time") <= COOLDOWN) {
			if (dmg <= tag.getFloat("damage")) {
				return true;
			}
		}
		else {
			tag.setLong("time", ep.worldObj.getTotalWorldTime());
		}
		tag.setFloat("damage", dmg);
		tag.setInteger("dimension", ep.worldObj.provider.dimensionId);
		return false;
	}

	public static boolean isEntityInvincible(EntityLiving ep, String owner, String uuid, float dmg) {
		if (updateDamageNBT(ep, dmg))
			return true;
		if (cache.lookForMatch(ep.worldObj, true, (WorldLocation loc, TileEntity tile) -> {
			TileEntityCrystalBeacon te = (TileEntityCrystalBeacon)tile;
			int r = te.getRange();
			if (Math.abs(ep.posY-te.yCoord) <= r/2) {
				if (loc.getDistanceTo(ep) <= r) {
					String n = te.getPlacerName();
					boolean flag = false;
					if (n != null && n.equals(owner)) {
						flag = true;
					}
					else {
						UUID u = te.getPlacerUUID();
						if (u != null && u.toString().equals(uuid)) {
							flag = true;
						}
					}
					if (flag && te.prevent(dmg)) {
						return true;
					}
				}
			}
			return false;
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(ep.worldObj, loc, te, ChromaTiles.BEACON);
		})) {
			return true;
		}
		ep.getEntityData().removeTag(NBT_TAG);
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
		return 250;
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

	}

	@Override
	public int getRange() {
		return range.getRange();
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public ChromaStructures getPrimaryStructure() {
		return ChromaStructures.PROTECT;
	}

	@Override
	public Coordinate getStructureOffset() {
		return null;
	}

	public boolean canStructureBeInspected() {
		return true;
	}

	/*
	private static class DamageEntry {

		private final UUID player;
		private final float amount;

		private DamageEntry(EntityPlayer ep, float amt) {
			player = ep.getPersistentID();
			amount = amt;
		}

		@Override
		public int hashCode() {
			return player.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof DamageEntry && ((DamageEntry)o).player.equals(player);
		}

	}*/

}
