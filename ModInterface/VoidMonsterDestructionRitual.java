/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Random;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.VoidMonster.API.NonTeleportingDamage;
import Reika.VoidMonster.API.VoidMonsterHook;
import Reika.VoidMonster.Entity.EntityVoidMonster;
import Reika.VoidMonster.World.MonsterGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class VoidMonsterDestructionRitual implements VoidMonsterHook {

	private final MonsterArena arena;
	private final EntityPlayer startingPlayer;

	private static final Random rand = new Random();

	public VoidMonsterDestructionRitual(EntityPlayer ep, double r) {
		this(ep, MonsterArena.createCircular(ep.posX, ep.posZ, r));
	}

	public VoidMonsterDestructionRitual(EntityPlayer ep, MonsterArena a) {
		startingPlayer = ep;
		arena = a;
		arena.close(ep.worldObj);
	}

	@ModDependent(ModList.VOIDMONSTER)
	public void tick(EntityVoidMonster e) {
		this.keepWithinArea(e);
		for (Effects ef : Effects.list) {
			if (rand.nextInt(ef.effectChance) == 0) {
				ef.doEffectServer(startingPlayer, e);
			}
		}
	}

	private void keepWithinArea(EntityVoidMonster e) {
		if (!arena.isInside(e)) {
			e.moveTowards(arena.centerX, e.posY, arena.centerZ, 1);
		}
	}

	private void onCompletion(EntityVoidMonster e) {
		MonsterGenerator.instance.addCooldown(e, 20*60*ReikaRandomHelper.getRandomBetween(20, 45));
	}

	public static class MonsterArena {

		private final Polygon perimeter = new Polygon();
		private double centerX;
		private double centerZ;

		public MonsterArena() {

		}

		public static MonsterArena createCircular(double x, double z, double r) {
			MonsterArena m = new MonsterArena();
			for (int i = 0; i < 360; i++) {
				double a = Math.toRadians(i);
				double dx = x+r*Math.cos(a);
				double dz = z+r*Math.sin(a);
				m.addPosition(dx, dz);
			}
			return m;
		}

		public void close(World world) {
			for (int i = 0; i < perimeter.npoints; i += 8) {

			}
		}

		public void addPosition(EntityPlayer ep) {
			this.addPosition(ep.posX, ep.posZ);
		}

		private void addPosition(double dx, double dz) {
			int x = (int)Math.round(dx*100);
			int y = (int)Math.round(dz*100);
			perimeter.addPoint(x, y);
			Rectangle r = perimeter.getBounds();
			centerX = r.getCenterX();
			centerZ = r.getCenterY();
		}

		public boolean isInside(Entity e) {
			return perimeter.contains(e.posX*100, e.posZ*100);
		}

	}

	public static enum Effects {
		COLLAPSING_SPHERE(40),
		RAYS(70);

		private final int effectChance;

		private static Effects[] list = values();

		private Effects(int c) {
			effectChance = c;
		}

		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectServer(EntityPlayer ep, EntityVoidMonster e) {
			DamageSource src = new VoidMonsterRitualDamage(ep);
			switch(this) {
				case COLLAPSING_SPHERE:
					e.attackEntityFrom(src, 20);
					break;
				case RAYS:
					e.attackEntityFrom(src, 40);
					break;
			}
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.VOIDMONSTERRITUAL.ordinal(), new PacketTarget.RadiusTarget(e, 128), e.getEntityId(), this.ordinal());
		}

		@SideOnly(Side.CLIENT)
		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectClient(EntityVoidMonster e) {
			float f = 1;
			double ex = e.posX;
			double ey = e.posY+1;
			double ez = e.posZ;
			switch(this) {
				case COLLAPSING_SPHERE:
					for (int i = 0; i < 128; i++) {
						double a1 = rand.nextDouble()*360;
						double a2 = rand.nextDouble()*360;
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(4+rand.nextDouble()*0.25, a1, a2);
						double px = ex+xyz[0];
						double py = ey+xyz[1];
						double pz = ez+xyz[2];
						EntityBlurFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setAlphaFading();
						int t = ReikaRandomHelper.getRandomBetween(5, 8);
						fx.setPositionController(new CollectingPositionController(px, py, pz, ex, ey, ez, t));
						fx.setLife(t+1).setScale(1+rand.nextFloat()*0.5F);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				case RAYS:
					for (int k = 0; k < 9; k++) {
						double a1 = rand.nextDouble()*360;
						double a2 = rand.nextDouble()*360;
						for (int i = 0; i < 128; i++) {
							double[] xyz = ReikaPhysicsHelper.polarToCartesian(rand.nextDouble()*96, a1, a2);
							double px = ex+xyz[0];
							double py = ey+xyz[1];
							double pz = ez+xyz[2];
							px = ReikaRandomHelper.getRandomPlusMinus(px, 0.125);
							py = ReikaRandomHelper.getRandomPlusMinus(py, 0.125);
							pz = ReikaRandomHelper.getRandomPlusMinus(pz, 0.125);
							EntityBlurFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setAlphaFading();
							fx.setLife(40).setScale(1.5F+rand.nextFloat()*1.5F);
							double d = rand.nextDouble()*360;
							MathExpression exp = new PeriodicExpression().addWave(1, 1, d).addWave(0.5, 2, d+90).addWave(0.125, 4, d).normalize();
							fx.setColorController(new FlashColorController(exp, 0xffffff, 0x000000));
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					f = 0.5F;
					break;
			}
			ReikaSoundHelper.playClientSound(ChromaSounds.FLAREATTACK, e, 1, f, false);
		}
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.VOIDMONSTER)
	public static void handlePacket(int entity, int effect) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entity);
		if (e instanceof EntityVoidMonster) {
			Effects.list[effect].doEffectClient((EntityVoidMonster)e);
		}
	}

	private static class VoidMonsterRitualDamage extends DamageSource implements NonTeleportingDamage {

		private final EntityPlayer player;

		public VoidMonsterRitualDamage(EntityPlayer ep) {
			super("voidmonster.ritual");
			player = ep;
		}

		@Override
		public Entity getEntity() {
			return player;
		}

		@Override
		public boolean isMagicDamage() {
			return true;
		}

	}

}
