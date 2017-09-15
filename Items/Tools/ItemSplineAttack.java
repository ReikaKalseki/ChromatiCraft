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
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.DelayedAttack;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.Spline.VibratingSpline;
import Reika.DragonAPI.Instantiable.ParticleController.BlendListColorController;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemSplineAttack extends ItemChromaTool {

	private final IEntitySelector attackableSelector = new IEntitySelector() {

		@Override
		public boolean isEntityApplicable(Entity e) {
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

	private final Comparator<EntityLivingBase> entitySorter = new Comparator<EntityLivingBase>() {

		@Override
		public int compare(EntityLivingBase o1, EntityLivingBase o2) {
			return Integer.compare(o1.hashCode(), o2.hashCode());
		}

	};

	public ItemSplineAttack(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ArrayList<EntityLivingBase> li = this.getAttackableEntities(world, ep);
		if (li.isEmpty())
			return is;
		//if (world.isRemote)
		//	Collections.shuffle(li);
		Collections.sort(li, entitySorter);
		VibratingSpline s = new VibratingSpline(SplineType.CHORDAL);
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
		for (EntityLivingBase e : li) {
			if (!world.isRemote) {
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedAttack(e, DamageSource.magic, 6)), i*4);//e.attackEntityFrom(DamageSource.magic, 6);
				ScheduledSoundEvent ev = new ScheduledSoundEvent(ChromaSounds.ORB_LO, ep, 0.5F, f);
				ev.attenuate = false;
				ev.broadcastRange = 96;
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(ev), i*4);//e.attackEntityFrom(DamageSource.magic, 6);
			}
			if (world.isRemote)
				s.addPoint(new BasicSplinePoint(new DecimalPosition(e.posX, e.posY+e.height/2, e.posZ)));
			i++;
		}
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
			EntityBlurFX fx = new EntityBlurFX(world, pos.xCoord, pos.yCoord, pos.zCoord).setLife(15).setScale(2).setAlphaFading();
			fx.setColorController(clr).freezeLife(i/64);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			i++;
		}
	}

	private ArrayList<EntityLivingBase> getAttackableEntities(World world, EntityPlayer ep) {
		HashSet<EntityLivingBase> li = new HashSet();
		Vec3 vec = ep.getLookVec();
		for (double d = 0; d < 40 && li.size() < 32; d += 1) {
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+1.62+vec.yCoord*d;
			double dz = ep.posZ+vec.zCoord*d;
			double r = 1+d/4D;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(r, r, r);
			li.addAll(world.getEntitiesWithinAABBExcludingEntity(ep, box, attackableSelector));
		}
		return new ArrayList(li);
	}

}
