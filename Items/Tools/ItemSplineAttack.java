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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.RayTracer;
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
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemSplineAttack extends ItemChromaTool {

	public static final int MAX_RANGE = 40;
	public static final int MAX_HOPS = 6;
	public static final int COOLDOWN = 10;

	private final Random rand = new Random();

	private static RayTracer losTrace = RayTracer.getVisualLOS();

	private static class SplineTargeting implements IEntitySelector {

		private Entity sourceEntity;

		@Override
		public boolean isEntityApplicable(Entity e) {
			return e != sourceEntity && this.isValidType(e) && (sourceEntity == null || this.hasLOS(sourceEntity, e));
		}

		private boolean hasLOS(Entity e1, Entity e2) {

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

	private final SplineTargeting attackableSelector = new SplineTargeting();

	private final Comparator<EntityLivingBase> entitySorter = new Comparator<EntityLivingBase>() {

		@Override
		public int compare(EntityLivingBase o1, EntityLivingBase o2) {
			return Integer.compare(o1.getEntityId(), o2.getEntityId());
		}

	};

	public ItemSplineAttack(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		if (world.getTotalWorldTime()-is.stackTagCompound.getLong("lastFire") < COOLDOWN) {
			ChromaSounds.ERROR.playSound(ep);
			return is;
		}
		rand.setSeed(ep.getUniqueID().getLeastSignificantBits() ^ ep.getUniqueID().getMostSignificantBits() + (ep.worldObj.getTotalWorldTime() << 7));
		rand.nextBoolean();
		EntityLivingBase tgt = this.getFirstEntityTarget(world, ep);
		if (tgt == null)
			return is;
		ArrayList<EntityLivingBase> li = new ArrayList();
		EntityLivingBase from = tgt;
		while (from != null) {
			li.add(from);
			List<EntityLivingBase> next = li.size() < MAX_HOPS ? this.getAttackableEntitiesFrom(world, from) : null;
			from = next == null || next.isEmpty() ? null : ReikaJavaLibrary.getRandomListEntry(rand, next);
		}
		Collections.sort(li, entitySorter);
		Spline s = new Spline(SplineType.CHORDAL);
		if (world.isRemote) {
			DecimalPosition pos = new DecimalPosition(ep.posX, ep.posY-0.375, ep.posZ);
			s.addPoint(new BasicSplinePoint(pos));
			if (li.size() == 1) {
				Vec3 vec = ep.getLookVec();
				double rx = ReikaRandomHelper.getRandomPlusMinus(vec.xCoord*ReikaRandomHelper.getRandomBetween(1D, 8D), 8D);
				double ry = ReikaRandomHelper.getRandomPlusMinus(vec.yCoord*ReikaRandomHelper.getRandomBetween(1D, 8D), 8D);
				double rz = ReikaRandomHelper.getRandomPlusMinus(vec.zCoord*ReikaRandomHelper.getRandomBetween(1D, 8D), 8D);
				s.addPoint(new BasicSplinePoint(pos.offset(rx, ry, rz)));
			}
		}
		int i = 1;
		//List<Note> n = KeySignature.D.getScale();
		//float f = 0.5F*(float)MusicKey.D4.getInterval(n.get(ReikaRandomHelper.getSafeRandomInt(n.size())).ordinal()).getRatio(MusicKey.D4);
		float f = 0.5F*(float)(CrystalMusicManager.instance.getRandomScaledDing(CrystalElement.BLACK)/CrystalMusicManager.instance.getDingPitchScale(CrystalElement.BLACK));
		int maxdur = -1;
		for (EntityLivingBase e : li) {
			if (!world.isRemote) {
				int tick = i*4;
				maxdur = Math.max(tick, maxdur);
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedAttack(e, DamageSource.causeIndirectMagicDamage(ep, ep), 6)), tick);
				ScheduledSoundEvent ev = new ScheduledSoundEvent(ChromaSounds.ORB_LO, ep, 0.5F, f);
				ev.attenuate = false;
				ev.broadcastRange = 96;
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(ev), tick);
			}
			if (world.isRemote)
				s.addPoint(new BasicSplinePoint(new DecimalPosition(e.posX, e.posY+e.height/2, e.posZ)));
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

	private EntityLivingBase getFirstEntityTarget(World world, EntityPlayer ep) {
		Vec3 vec = ep.getLookVec();
		for (double d = 0; d <= MAX_RANGE; d += 1) {
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+1.62+vec.yCoord*d;
			double dz = ep.posZ+vec.zCoord*d;
			double r = 1+d/4D;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(r, r, r);
			attackableSelector.sourceEntity = ep;
			List<EntityLivingBase> li = world.getEntitiesWithinAABBExcludingEntity(ep, box, attackableSelector);
			attackableSelector.sourceEntity = null;
			if (!li.isEmpty()) {
				return ReikaJavaLibrary.getRandomListEntry(rand, li);
			}
		}
		return null;
	}

	private List<EntityLivingBase> getAttackableEntitiesFrom(World world, Entity from) {
		for (double d = 1; d <= MAX_RANGE; d += Math.sqrt(d)) {
			d = Math.ceil(d);
			AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(from, d);
			attackableSelector.sourceEntity = from;
			List<EntityLivingBase> li = world.getEntitiesWithinAABBExcludingEntity(from, box, attackableSelector);
			attackableSelector.sourceEntity = null;
			if (!li.isEmpty())
				return li;
		}
		return null;
	}

}
