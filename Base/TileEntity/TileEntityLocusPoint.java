/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.ThreadSafeTileCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class TileEntityLocusPoint extends TileEntityChromaticBase implements LocationCached, OwnedTile {

	//TODO this is not properly thread safe yet, but I have never seen it break, so maybe it is not a high priority...
	private static final NestedMap<Class<? extends TileEntityLocusPoint>, UUID, ThreadSafeTileCache> cache = new NestedMap();

	@Override
	public void breakBlock() {
		ThreadSafeTileCache c = cache.get(this.getClass(), placerUUID);
		if (c != null) {
			c.remove(new WorldLocation(this));
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {

		double n = this instanceof TileEntityAuraPoint ? 1+rand.nextInt(4) : 1;

		n *= 1+0.75*Math.sin((this.getTicksExisted()+this.hashCode())/8D);

		int ps = Minecraft.getMinecraft().gameSettings.particleSetting;
		switch(ps) {
			case 1:
				n *= 0.5;
				break;
			case 2:
				n *= 0.25;
				break;
		}
		double d = Math.max(1, Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5)/1024D);
		n /= d;

		int num = n <= 1 ? ReikaRandomHelper.doWithChance(n) ? 1 : 0 : (int)n;

		for (int i = 0; i < num; i++) {
			int color = this.getRenderColor();

			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			if (rand.nextInt(4) == 0)
				v *= ReikaRandomHelper.getRandomPlusMinus(2, 0.5);
			double theta = rand.nextDouble()*360;
			double phi = rand.nextDouble()*360;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);

			int l = ReikaRandomHelper.getRandomPlusMinus(20, 10);
			EntityFX fx = new EntityCCBlurFX(world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setColor(color).setScale(2).setLife(l);
			EntityFX fx2 = new EntityCCBlurFX(world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setColor(0xffffff).setScale(1.1F).setLife(l);
			fx.noClip = false;
			fx2.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	public abstract int getRenderColor();

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		cacheTile(this);
	}

	private static void cacheTile(TileEntityLocusPoint te) {
		WorldLocation loc = new WorldLocation(te);
		Class cl = te.getClass();
		ThreadSafeTileCache c = cache.get(cl, te.placerUUID);
		if (c == null) {
			c = new ThreadSafeTileCache().setTileClass(TileEntityLocusPoint.class);
			cache.put(cl, te.placerUUID, c);
		}
		c.add(loc);
	}

	public static Collection<WorldLocation> getCache(Class<? extends TileEntityLocusPoint> cl, EntityPlayer ep) {
		return getCache(cl, ep.getUniqueID());
	}

	public static boolean hasLoci(Class<? extends TileEntityLocusPoint> cl, UUID uid) {
		ThreadSafeTileCache c = cache.get(cl, uid);
		return c != null && !c.isEmpty();
	}

	public static Collection<WorldLocation> getCache(Class<? extends TileEntityLocusPoint> cl, UUID uid) {
		Collection<WorldLocation> c = cache.get(cl, uid);
		return c != null ? Collections.unmodifiableCollection(c) : null;
	}

	public static WorldLocation getMatchFromCache(Class<? extends TileEntityLocusPoint> cl, UUID uid, Function<WorldLocation, Boolean> check) {
		ThreadSafeTileCache c = cache.get(cl, uid);
		if (c == null || c.isEmpty())
			return null;
		return c.iterateAsSearch(check);
	}

	public static Collection<WorldLocation> getCaches(Class<? extends TileEntityLocusPoint> cl) {
		Map<UUID, ThreadSafeTileCache> map = cache.getMap(cl);
		Collection<WorldLocation> ret = new ArrayList();
		if (map != null) {
			for (Collection<WorldLocation> c : map.values()) {
				ret.addAll(c);
			}
		}
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final double getMaxRenderDistanceSquared() {
		return 65536D;
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

}
