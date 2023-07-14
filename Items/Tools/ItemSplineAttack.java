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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.ProjectileFiringTool;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Ability.LightCast;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BreadthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder.PassRules;
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
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemSplineAttack extends ItemChromaTool implements ProjectileFiringTool {

	public static final int MAX_RANGE = 40;
	public static final int MAX_HOPS = 6;
	public static final int COOLDOWN = 10;

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

		@Override
		public String toString() {
			return entity.toString();
		}

	}

	private final SplineTargeting attackableSelector = new SplineTargeting();

	public ItemSplineAttack(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (world.isRemote)
			return is;
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		long time = world.getTotalWorldTime();
		if (time-is.stackTagCompound.getLong("lastfire") < COOLDOWN) {
			ChromaSounds.ERROR.playSound(ep);
			return is;
		}
		this.fire(is, world, ep, false);
		return is;
	}

	public void fire(ItemStack is, World world, EntityPlayer ep, boolean randomVec) {
		long time = world.getTotalWorldTime();
		EntityTarget tgt = this.getFirstEntityTarget(world, ep, randomVec);
		if (tgt == null)
			return;
		ArrayList<EntityTarget> li = new ArrayList();
		EntityTarget from = tgt;
		int max = MAX_HOPS;
		if (ProgressStage.DIMENSION.isPlayerAtStage(ep))
			max *= 2;
		if (ProgressStage.CTM.isPlayerAtStage(ep))
			max *= 2;
		while (from != null) {
			li.add(from);
			List<EntityTarget> next = li.size() < max ? this.getAttackableEntitiesFrom(world, from.entity, ep, li) : null;
			from = next == null || next.isEmpty() ? null : ReikaJavaLibrary.getRandomListEntry(DragonAPICore.rand, next);
		}
		//List<Note> n = KeySignature.D.getScale();
		//float f = 0.5F*(float)MusicKey.D4.getInterval(n.get(ReikaRandomHelper.getSafeRandomInt(n.size())).ordinal()).getRatio(MusicKey.D4);
		float f = 0.5F*(float)(CrystalMusicManager.instance.getRandomScaledDing(CrystalElement.BLACK)/CrystalMusicManager.instance.getDingPitchScale(CrystalElement.BLACK));
		int maxdur = -1;
		int[] data = new int[li.size()+3];
		data[0] = li.size()+2;
		int[] split = ReikaJavaLibrary.splitLong(time);
		data[1] = split[0];
		data[2] = split[1];
		for (int i = 0; i < li.size(); i++)
			data[i+3] = li.get(i).entity.getEntityId();
		int i = 1;
		//ReikaJavaLibrary.pConsole(li.size()+":"+li);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.SPLINEATTACKTARGETS.ordinal(), ep, 64, data);
		for (EntityTarget e : li) {
			int tick = Math.max(1, (int)(i*5.67)-10);
			maxdur = Math.max(tick, maxdur);
			TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedAttack(e.entity, DamageSource.causeIndirectMagicDamage(ep, ep), 6)), tick+4);
			ScheduledSoundEvent ev = new ScheduledSoundEvent(ChromaSounds.ORB.getDownshiftedPitch(), ep, 0.5F, f);
			ev.attenuate = false;
			ev.broadcastRange = 96;
			TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(ev), tick);
			i++;
		}
		is.stackTagCompound.setLong("lastfire", time+maxdur);
	}

	@SideOnly(Side.CLIENT)
	public static void receiveTargetListForTrail(World world, EntityPlayer ep, int[] data) {
		Spline s = new Spline(SplineType.CHORDAL);
		long time = ReikaJavaLibrary.buildLong(data[0], data[1]);
		int offset = (int)(world.getTotalWorldTime()-time);
		if (world.isRemote) {
			DecimalPosition pos = new DecimalPosition(ep.posX, ep.posY+1.62-0.375, ep.posZ);
			s.addPoint(new BasicSplinePoint(pos));
		}
		//ReikaJavaLibrary.pConsole(data.length-2+":"+Arrays.toString(data));
		for (int i = 2; i < data.length; i++) {
			Entity e = world.getEntityByID(data[i]);
			if (e == null) //just to keep the list entries in sync
				e = ep;
			s.addPoint(new BasicSplinePoint(new DecimalPosition(e.posX, e.posY+e.height/2, e.posZ)));
		}
		doTrail(world, s, Math.max(0, offset));
	}

	@SideOnly(Side.CLIENT)
	private static void doTrail(World world, Spline s, int offset) {
		List<DecimalPosition> trail = s.get(1024, false);
		ColorBlendList cbl = new ColorBlendList(12, 0x000000, 0xffffff, 0x2288ff, 0x2288ff, 0x2288ff, 0x2288ff, 0x2288ff, 0x2288ff, 0x2288ff, 0x404040);
		ColorController clr = new BlendListColorController(cbl);
		ColorBlendList cbl2 = new ColorBlendList(12, 0x000000, 0x555555, 0x004187, 0x004187, 0x004187, 0x004187, 0x004187, 0x004187, 0x004187, 0x000000);
		ColorController clr2 = new BlendListColorController(cbl2);
		int i = 0;
		int l = 120;
		float sc = 3;
		for (DecimalPosition pos : trail) {
			EntityBlurFX fx = new EntityCCBlurFX(world, pos.xCoord, pos.yCoord, pos.zCoord).setIcon(ChromaIcons.CENTER).setLife(l).setScale(sc*0.15F);
			fx.setColorController(clr).freezeLife((int)(i/205F)-offset).forceIgnoreLimits().setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			if (i%4 == 0) {
				fx = new EntityCCBlurFX(world, pos.xCoord, pos.yCoord, pos.zCoord).setIcon(ChromaIcons.CENTER).setLife(l).setScale(sc);
				fx.setColorController(clr2).freezeLife((int)(i/205F)-offset).forceIgnoreLimits().setAlphaFading();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			i++;
		}
	}

	private EntityTarget getFirstEntityTarget(World world, EntityPlayer ep, boolean randomVec) {
		Vec3 vec = ep.getLookVec();
		if (randomVec) {
			vec.xCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.yCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.zCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec = vec.normalize();
		}
		for (double d = 0; d <= MAX_RANGE; d += 1) {
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+vec.yCoord*d;
			if (!world.isRemote)
				dy += 1.62;
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
				return ReikaJavaLibrary.getRandomListEntry(DragonAPICore.rand, li2);
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
		return BreadthFirstSearch.getOpenPathBetween(from.worldObj, new Coordinate(from), new Coordinate(to), 16, box, EnumSet.allOf(PassRules.class)).getPath();
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

	@Override
	public int getAutofireRate() {
		return 120;
	}

}
