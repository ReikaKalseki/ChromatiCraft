/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Ability.LightCast;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BreadthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.DelayedAttack;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.ParticleController.BlendListColorController;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemSplineAttack extends ItemChromaTool {

	public static final int MAX_RANGE = 40;
	public static final int MAX_HOPS = 6;
	public static final int COOLDOWN = 10;

	private final Random rand = new Random();

	private static RayTracer losTrace = RayTracer.getVisualLOS();

	private static class SplineTargeting implements IEntitySelector {

		@Override
		public boolean isEntityApplicable(Entity e) {
			return this.isValidType(e);
		}

		private boolean isValidType(Entity e) {
			if (e instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)e;
				if (ep.capabilities.isCreativeMode)
					return false;
				if (MinecraftServer.getServer() != null && !MinecraftServer.getServer().isPVPEnabled())
					return false;
			}
			return e instanceof EntityLivingBase;
		}

	};

	private static class PathFinder extends OpenPathFinder {

		private static final HashSet<BlockKey> passableBlocks = new HashSet();

		private static void initPassable() {
			if (passableBlocks.isEmpty()) {
				for (BlockKey bk : RayTracer.getTransparentBlocks())
					passableBlocks.add(bk);
				for (Block bk : LightCast.getPassthroughBlocks())
					passableBlocks.add(new BlockKey(bk));
			}
		}

		public PathFinder(Coordinate c1, Coordinate c2, int r) {
			super(c1, c2, r);
		}

		@Override
		protected boolean isValidBlock(World world, int x, int y, int z) {
			return super.isValidBlock(world, x, y, z) || this.isPassableBlock(world, x, y, z);
		}

		private boolean isPassableBlock(World world, int x, int y, int z) {
			initPassable();
			BlockKey bk = BlockKey.getAt(world, x, y, z);
			return passableBlocks.contains(bk);
		}
	}

	private static class EntityTarget {

		private final EntityLivingBase entity;
		private final LinkedList<Coordinate> path;

		private EntityTarget(EntityLivingBase e, LinkedList<Coordinate> li) {
			entity = e;
			path = li;
		}

	}

	private final SplineTargeting attackableSelector = new SplineTargeting();

	public ItemSplineAttack(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		if (world.getTotalWorldTime()-is.stackTagCompound.getLong("lastfire") < COOLDOWN) {
			ChromaSounds.ERROR.playSound(ep);
			return is;
		}
		rand.setSeed(ep.getUniqueID().getLeastSignificantBits() ^ ep.getUniqueID().getMostSignificantBits() + (is.stackTagCompound.getLong("lastfire") << 7));
		rand.nextBoolean();
		EntityTarget tgt = this.getFirstEntityTarget(world, ep);
		if (tgt == null)
			return is;
		ArrayList<EntityTarget> li = new ArrayList();
		EntityTarget from = tgt;
		while (from != null) {
			li.add(from);
			List<EntityTarget> next = li.size() < MAX_HOPS ? this.getAttackableEntitiesFrom(world, from.entity, ep, li) : null;
			from = next == null || next.isEmpty() ? null : ReikaJavaLibrary.getRandomListEntry(rand, next);
		}
		Spline s = new Spline(SplineType.CHORDAL);
		if (world.isRemote) {
			DecimalPosition pos = new DecimalPosition(ep.posX, ep.posY-0.375, ep.posZ);
			s.addPoint(new BasicSplinePoint(pos));
		}
		int i = 1;
		//List<Note> n = KeySignature.D.getScale();
		//float f = 0.5F*(float)MusicKey.D4.getInterval(n.get(ReikaRandomHelper.getSafeRandomInt(n.size())).ordinal()).getRatio(MusicKey.D4);
		float f = 0.5F*(float)(CrystalMusicManager.instance.getRandomScaledDing(CrystalElement.BLACK)/CrystalMusicManager.instance.getDingPitchScale(CrystalElement.BLACK));
		int maxdur = -1;
		for (EntityTarget e : li) {
			if (!world.isRemote) {
				int tick = i*4;
				maxdur = Math.max(tick, maxdur);
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedAttack(e.entity, DamageSource.causeIndirectMagicDamage(ep, ep), 6)), tick);
				ScheduledSoundEvent ev = new ScheduledSoundEvent(ChromaSounds.ORB_LO, ep, 0.5F, f);
				ev.attenuate = false;
				ev.broadcastRange = 96;
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(ev), tick);
			}
			if (world.isRemote) {
				/*
				for (Coordinate c : e.path) {
					double x = c.xCoord+0.5;
					double z = c.zCoord+0.5;
					double y = c.yCoord+0.5;
					if (c == e.path.getLast()) {
						x = e.entity.posX;
						y = e.entity.posY+e.entity.height/2;
						z = e.entity.posZ;
					}
					s.addPoint(new BasicSplinePoint(new DecimalPosition(x, y, z)));
				}*/
				s.addPoint(new BasicSplinePoint(new DecimalPosition(e.entity.posX, e.entity.posY+e.entity.height/2, e.entity.posZ)));
			}
			i++;
		}
		is.stackTagCompound.setLong("lastfire", world.getTotalWorldTime()+maxdur);
		if (world.isRemote)
			this.doTrail(world, s);
		return is;
	}

	@SideOnly(Side.CLIENT)
	private void doTrail(World world, Spline s) {
		List<DecimalPosition> trail = s.get(128, false);
		ColorBlendList cbl = new ColorBlendList(6, 0x000000, 0x2288ff, 0xffffff, 0xffffff, 0xffffff, 0x404040);
		ColorController clr = new BlendListColorController(cbl);
		int i = 0;
		for (DecimalPosition pos : trail) {
			EntityBlurFX fx = new EntityCCBlurFX(world, pos.xCoord, pos.yCoord, pos.zCoord).setLife(15).setScale(2).setAlphaFading();
			fx.setColorController(clr).freezeLife(i/64);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			i++;
		}
	}

	private EntityTarget getFirstEntityTarget(World world, EntityPlayer ep) {
		Vec3 vec = ep.getLookVec();
		for (double d = 0; d <= MAX_RANGE; d += 1) {
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+1.62+vec.yCoord*d;
			double dz = ep.posZ+vec.zCoord*d;
			double r = 1+d/4D;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(r, r, r);
			List<EntityLivingBase> li = world.getEntitiesWithinAABBExcludingEntity(ep, box, attackableSelector);
			ArrayList<EntityTarget> li2 = new ArrayList();
			for (EntityLivingBase e : li) {
				if (e == ep)
					continue;
				LinkedList<Coordinate> pos = this.getPath(ep, e);
				if (pos != null) {
					li2.add(new EntityTarget(e, pos));
				}
			}
			if (!li2.isEmpty()) {
				return ReikaJavaLibrary.getRandomListEntry(rand, li2);
			}
		}
		return null;
	}

	private LinkedList<Coordinate> getPath(EntityLivingBase from, EntityLivingBase to) {
		losTrace.setOrigins(from.posX, from.posY+from.height/2, from.posZ, to.posX, to.posY+to.height/2, to.posZ);
		if (losTrace.isClearLineOfSight(from.worldObj)) {
			LinkedList<Coordinate> li = new LinkedList();
			li.add(new Coordinate(from));
			li.add(new Coordinate(to));
			return li;
		}
		BlockBox box = BlockBox.between(from, to).expand(8, 6, 8);
		return BreadthFirstSearch.getOpenPathBetween(from.worldObj, new Coordinate(from), new Coordinate(to), 16, box);
	}

	private List<EntityTarget> getAttackableEntitiesFrom(World world, EntityLivingBase from, EntityPlayer owner, List<EntityTarget> path) {
		for (double d = 1; d <= MAX_RANGE; d += Math.sqrt(d)) {
			d = Math.ceil(d);
			AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(from, d);
			List<EntityLivingBase> li = world.getEntitiesWithinAABBExcludingEntity(from, box, attackableSelector);
			ArrayList<EntityTarget> li2 = new ArrayList();
			for (EntityLivingBase e : li) {
				if (e == owner)
					continue;
				boolean flag = true;
				for (EntityTarget at : path) {
					if (at.entity == e) {
						flag = false;
						break;
					}
				}
				LinkedList<Coordinate> pos = flag ? this.getPath(from, e) : null;
				if (pos != null) {
					li2.add(new EntityTarget(e, pos));
				}
			}
			if (!li2.isEmpty())
				return li2;
		}
		return null;
	}

}
