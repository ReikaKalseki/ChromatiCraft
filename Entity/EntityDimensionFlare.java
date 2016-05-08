package Reika.ChromatiCraft.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.OuterRegionsEvents;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

//Like ships in supercruise for sound, physics, render
public class EntityDimensionFlare extends Entity/*Living*/ {

	private EntityPlayer target;

	private int stateTick;
	private RelationState state = RelationState.FIND;

	private final ArrayList<EntityPlayer> aggroPlayers = new ArrayList();

	public EntityDimensionFlare(World world) {
		super(world);
	}

	public EntityDimensionFlare(World world, EntityPlayer ep) {
		super(world);
		target = ep;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (target != null) {
			EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, 100);
			if (ep != null && !worldObj.isRemote && ticksExisted%20 == 0) {
				OuterRegionsEvents.instance.doRejectAttack(this, ep);
			}
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
					}
				}
			}
		}
		else if (!worldObj.isRemote) {
			this.setDead();
		}
	}

	private void doAggro() {
		Iterator<EntityPlayer> it = aggroPlayers.iterator();
		while (it.hasNext()) {
			EntityPlayer ep = it.next();
			boolean flag = OuterRegionsEvents.instance.doRejectAttack(this, ep);

			if (flag || ep.isDead || ep.getHealth() <= 0 || ep.dimension != ExtraChromaIDs.DIMID.getValue()) {
				it.remove();
			}
		}
	}

	private void moveToTarget() { //looping flight
		double dx = target.posX-posX;
		double dy = target.posY-posY;
		double dz = target.posZ-posZ;
		double v = 0.125;
		motionX += v*Math.signum(dx);
		motionY += v*Math.signum(dy);
		motionZ += v*Math.signum(dz);
	}

	private void progressRelationship() {
		stateTick++;
		if (stateTick >= state.duration) {
			state = state.next(!ProgressStage.CTM.isPlayerAtStage(target));
			stateTick = 0;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float amt) {
		if (src == DamageSource.fall)
			return false;
		boolean flag = super.attackEntityFrom(src, amt);
		if (flag) {
			if (src.getEntity() == target) {
				state = RelationState.ATTACKED;
			}
		}
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
		if (!aggroPlayers.contains(ep))
			aggroPlayers.add(ep);
		state = RelationState.ATTACKED;
	}

}
