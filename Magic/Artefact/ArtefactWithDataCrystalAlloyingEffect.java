package Reika.ChromatiCraft.Magic.Artefact;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.AlloyingEffect;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.DelayedAttack;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.DelayedKnockback;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArtefactWithDataCrystalAlloyingEffect implements AlloyingEffect {

	public static final ArtefactWithDataCrystalAlloyingEffect instance = new ArtefactWithDataCrystalAlloyingEffect();

	private int tick = 0;
	private int fireTick = 0;

	private final Random rand = new Random();

	private ArtefactWithDataCrystalAlloyingEffect() {

	}

	public void initialize(EntityItem ei) {
		rand.setSeed(ei.getEntityId());
		rand.nextBoolean();
		rand.nextBoolean();
		ei.getEntityData().setBoolean("artealloy", true);
		tick = 0;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(new Coordinate(ei));
		Collection<EntityItem> li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ei2 : li) {
			ei2.getEntityData().setBoolean("artealloy", true);
		}
	}

	@Override
	public void doEffect(EntityItem ei) {
		//ReikaJavaLibrary.pConsole(ei.worldObj.isRemote+" @ "+tick+" for "+ei.getEntityItem());
		if (ei.worldObj.isRemote) {
			this.doClientFX(ei);
			if (fireTick > 0)
				fireTick--;
		}
		else {
			if (tick%45 == 0)
				ChromaSounds.ARTEALLOY.playSoundNoAttenuation(ei.worldObj, ei.posX, ei.posY, ei.posZ, 2, 1, 96);
			if (tick > 10 && rand.nextInt(90) == 0) {
				List<EntityLivingBase> li = ei.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, ReikaAABBHelper.getEntityCenteredAABB(ei, 12));
				for (EntityLivingBase e : li) {
					TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedKnockback(e, new DecimalPosition(ei), 5, 0.5)), 12);
					TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new DelayedAttack(e, DamageSource.magic, 2)), 12);
				}
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.ARTEALLOYBURST.ordinal(), ei, 128, ei.getEntityId());//ChromaSounds.ARTEALLOYHIT.playSound(ei, 2, 1);
			}
		}
		tick++;
	}

	@SideOnly(Side.CLIENT)
	private void doClientFX(EntityItem ei) {

	}

	public void doBurst(EntityItem ei) {
		ReikaSoundHelper.playClientSound(ChromaSounds.ARTEALLOYHIT, ei, 2, 1, false);
		fireTick = 50;
		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ParticleBurst(new DecimalPosition(ei))), 10);
	}

	public int getFireTick() {
		return fireTick;
	}

	@Override
	public void onFinish(EntityItem ei, EntityItem result) {
		ReikaEntityHelper.setInvulnerable(ei, true);
		ReikaEntityHelper.setInvulnerable(result, true);
		ei.worldObj.newExplosion(ei, ei.posX, ei.posY, ei.posZ, 6, true, true);
	}

	private static class ParticleBurst implements ScheduledEvent {

		private final DecimalPosition position;

		public ParticleBurst(DecimalPosition pos) {
			position = pos;
		}

		@Override
		public void fire() {
			this.fireClient();
		}

		@SideOnly(Side.CLIENT)
		private void fireClient() {
			for (int i = 0; i < 32; i++) {
				double px = ReikaRandomHelper.getRandomPlusMinus(position.xCoord, 0.25);
				double py = ReikaRandomHelper.getRandomPlusMinus(position.yCoord, 0.25);
				double pz = ReikaRandomHelper.getRandomPlusMinus(position.zCoord, 0.25);
				double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
				double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
				double vy = ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(5, 2.5);
				EntityFX fx1 = new EntityBlurFX(Minecraft.getMinecraft().theWorld, px, py, pz, vx, vy, vz).setColor(0xffffff).setScale(s).setLife(24).setRapidExpand();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			}
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.CLIENT;
		}

	}

}
