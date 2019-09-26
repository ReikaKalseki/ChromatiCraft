/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.OuterRegionsEvents;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.EntityLockMotionController;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Like ships in supercruise for sound, physics, render
public class EntityDimensionFlare extends Entity/*Living*/ {

	private EntityPlayer target;

	private int stateTick;
	private RelationState state = RelationState.FIND;

	private FlareIdentity identity;

	private final HashMap<UUID, Integer> aggroPlayers = new HashMap();

	private final ArrayList<ColorDirection> trailColors = new ArrayList();

	public EntityDimensionFlare(World world) {
		super(world);
		if (world.isRemote && identity == null) {
			identity = FlareIdentity.getRandomFlare(this);
			for (int i = 0; i < 16; i++) {
				double a1 = ReikaRandomHelper.getRandomPlusMinus(0D, 10D);
				double a2 = ReikaRandomHelper.getRandomPlusMinus(0D, 10D);
				trailColors.add(new ColorDirection(identity.flareColor, a1, a2));
			}
		}
		this.setSize(0.5F, 0.5F);
	}

	public EntityDimensionFlare(World world, EntityPlayer ep) {
		super(world);
		target = ep;
	}

	public FlareIdentity getIdentity() {
		return identity;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public void playSound(String snd, float vol, float p) {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (target != null) {
			this.doAggro();
			double d = this.getDistanceSqToEntity(target);
			if (d < 65536) { //256
				this.moveToTarget();
				if (d < 9216) { //96
					this.progressRelationship();
					if (d < 576) { //24
						if (rand.nextInt(320) == 0) {
							if (target instanceof EntityPlayerMP)
								this.sendMessageToPlayer();
						}
						if (rand.nextInt(80) == 0 && state == RelationState.ATTACK) {
							OuterRegionsEvents.instance.doRejectAttack(this, target);
						}
					}
				}
			}
		}
		else if (!worldObj.isRemote) {
			this.setDead();
		}

		this.moveEntity(motionX, motionY, motionZ);

		this.func_145771_j(posX, posY-2, posZ);

		if (worldObj.isRemote) {
			this.spawnParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles() {
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(motionX, motionY, motionZ);
		int n = 1+rand.nextInt(trailColors.size()/2);
		for (int i = 0; i < n; i++) {
			int idx = rand.nextInt(trailColors.size());
			ColorDirection dir = trailColors.get(idx);
			//for (ColorDirection dir : trailColors) {
			double vel = ReikaRandomHelper.getRandomBetween(0.0625, 0.125);
			double a1 = angs[1]+dir.angle1-90;
			double a2 = -angs[2]+90+dir.angle2;
			double[] v = ReikaPhysicsHelper.polarToCartesian(vel, a1, a2);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX, posY, posZ, v[0], v[1], v[2]).setColor(dir.color).setScale(1.5F+rand.nextFloat()*1.5F);
			double d = rand.nextDouble()*360;
			MathExpression e = new PeriodicExpression().addWave(1, 1, d).addWave(0.5, 2, d+90).addWave(0.125, 4, d).normalize();
			fx.setColorController(new FlashColorController(e, dir.color, 0x000000));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}

		for (int i = 0; i < 1; i++) {
			int idx = rand.nextInt(trailColors.size());
			ColorDirection dir = trailColors.get(idx);
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(1.25, rand.nextDouble()*360, rand.nextDouble()*360);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX+xyz[0], posY+xyz[1], posZ+xyz[2]).setColor(dir.color).setScale(0.75F+rand.nextFloat()*0.5F);
			double d = rand.nextDouble()*360;
			fx.setMotionController(new EntityLockMotionController(this, 0.03125/4, 0.125*8, 0.875));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < 1; i++) {
			int idx = rand.nextInt(trailColors.size());
			ColorDirection dir = trailColors.get(idx);
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.625, rand.nextDouble()*360, rand.nextDouble()*360);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX+xyz[0]-motionX, posY+xyz[1]-motionY, posZ+xyz[2]-motionZ, motionX, motionY, motionZ).setColor(dir.color);
			fx.setScale(2.5F+rand.nextFloat()*1F).setLife(20).setRapidExpand();
			double d = rand.nextDouble()*360;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void doAggro() {
		Iterator<Entry<UUID, Integer>> it = aggroPlayers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<UUID, Integer> e = it.next();
			EntityPlayer ep = worldObj.func_152378_a(e.getKey());
			int get = e.getValue() != null ? e.getValue() : 0;
			e.setValue(get+1);
			if (rand.nextInt(120) == 0 && this.getDistanceSqToEntity(ep) < 1024) { //32
				boolean flag = OuterRegionsEvents.instance.doRejectAttack(this, ep);
				if (flag || ep.isDead || ep.getHealth() <= 0) {
					it.remove();
				}
			}
		}
	}

	public int getAttackTicks(EntityPlayer ep) {
		Integer get = aggroPlayers.get(ep.getUniqueID());
		return get != null ? get.intValue() : 0;
	}

	private void moveToTarget() { //looping flight
		double dx = target.posX-posX;
		double dy = target.posY-posY;
		double dz = target.posZ-posZ;
		if (ReikaMathLibrary.py3d(dx, dy, dz) < 16)
			return;
		double v = 0.03125;
		motionX += v*Math.signum(dx);
		motionY += v*Math.signum(dy);
		motionZ += v*Math.signum(dz);
		//motionX = motionY = motionZ = 0;
		//motionX = 0.25*Math.sin(ticksExisted/64D);
		velocityChanged = true;
	}

	private void progressRelationship() {
		stateTick++;
		if (stateTick >= state.duration) {
			state = state.next(!ProgressStage.CTM.isPlayerAtStage(target));
			stateTick = 0;
			if (state == RelationState.ATTACK) {
				aggroPlayers.put(target.getUniqueID(), 0);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float amt) {
		if (src == DamageSource.fall)
			return false;
		boolean flag = super.attackEntityFrom(src, amt);
		//if (flag) {
		if (src.getEntity() instanceof EntityPlayer) {
			state = RelationState.ATTACKED;
			OuterRegionsEvents.instance.doFlareAggro((EntityPlayer)src.getEntity());
		}
		//}
		return flag;
	}

	private void sendMessageToPlayer() {
		String msg = state.getRandomMessage(rand);
		String s = "<"+msg+">";
		ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.FLAREMSG.ordinal(), s, new PacketTarget.PlayerTarget((EntityPlayerMP)target));
	}

	public static enum RelationState {
		FIND(30, "Curiosity", "Inquisitiveness", "Confusion", "Interest", "Confidence", "Fascination"),
		INSPECT(5, "Suspicion", "Caution", "Investigative", "Hope", "Anxiety", "Expectation", "Anticipation"),
		REJECT(15, "Dismissiveness", "Superiority", "Contempt", "Insult", "Concern", "Territoriality"),
		WARN(30, "Aggravation", "Provocation", "Irritation", "Agitation", "Exclusion"),
		THREATEN(45, "Hostile", "Alarm", "Intimidation", "Exile"),
		ATTACK(Integer.MAX_VALUE, "Aggression", "Defence", "Banishment"),
		ATTACKED(Integer.MAX_VALUE, "Shock", "Rage", "Anger", "Retaliation"),
		ACCEPT(15, "Acceptance", "Pleasure", "Satisfaction"),
		WELCOME(Integer.MAX_VALUE, "Friendliness", "Welcoming", "Admiration", "Gratitude");

		private final int duration;
		private final ArrayList<String> messages;

		private static final RelationState[] list = values();

		private RelationState(int d, String... s) {
			duration = d == Integer.MAX_VALUE ? d : d*20;
			messages = ReikaJavaLibrary.makeListFrom(s);
		}

		public String getRandomMessage(Random rand) {
			return messages.get(rand.nextInt(messages.size()));
		}

		private RelationState next(boolean hostile) {
			if (duration == Integer.MAX_VALUE)
				return this;
			if (this == INSPECT)
				return hostile ? REJECT : ACCEPT;
			return list[this.ordinal()+1];
		}
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {

	}

	public void aggroTo(EntityPlayer ep) {
		if (!aggroPlayers.containsKey(ep.getUniqueID()))
			aggroPlayers.put(ep.getUniqueID(), 0);
		state = RelationState.ATTACKED;
	}

	@Override
	public float getShadowSize() {
		return 0;
	}

	@Override
	public boolean isInRangeToRenderDist(double distsq) {
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private static class ColorDirection {

		private final double angle1;
		private final double angle2;
		private final int color;

		private ColorDirection(int c, double a1, double a2) {
			color = c;
			angle1 = a1;
			angle2 = a2;
		}

	}

	public static class FlareIdentity {

		public final float soundPitch;
		public final int flareColor;

		private FlareIdentity(int c, float sound) {
			soundPitch = sound;
			flareColor = c;
		}

		public static FlareIdentity getRandomFlare(EntityDimensionFlare e) {
			int c = 0xffffff;
			switch(e.rand.nextInt(4)) {
				case 0:
					c = 0xffffff;
					break;
				case 1:
					c = 0x22aaff;
					break;
				case 2:
					c = 0xDA8CFF;
					break;
				case 3:
					c = 0xFFF1AD;
					break;
			}
			return new FlareIdentity(c, e.rand.nextFloat()+0.5F);
		}

	}

}
