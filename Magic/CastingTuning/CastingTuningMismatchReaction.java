package Reika.ChromatiCraft.Magic.CastingTuning;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Instantiable.ParticleController.SpiralMotionController;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CastingTuningMismatchReaction {

	private static final int SOUND_DURATION = 176; //sound duration is 176 ticks
	private static final int DURATION = SOUND_DURATION*24;

	private final TileEntityCastingTable tile;
	private final EntityPlayer trigger;

	private final Random fixedRand = new Random();
	private final Random particleRand = new Random();

	private int tick;

	private float shockwaveCharge;
	private int shockwaveCooldown;

	public CastingTuningMismatchReaction(TileEntityCastingTable te, EntityPlayer ep) {
		tile = te;
		trigger = ep;
		fixedRand.setSeed(ChunkCoordIntPair.chunkXZ2Int(te.xCoord, te.zCoord) ^ te.yCoord);
		fixedRand.nextBoolean();
		fixedRand.nextBoolean();
	}

	public boolean tick() {
		if (tile.worldObj.isRemote) {
			this.doParticles();
		}

		if (tick%(SOUND_DURATION/2) == 0) {
			ChromaSounds.CASTTUNEREJECT.playSoundAtBlockNoAttenuation(tile, 0.5F, 1, 128);
		}

		if (fixedRand.nextInt(50) == 0) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(tile).expand(60, 24, 60);
			List<EntityLivingBase> li = tile.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
			EntityLivingBase e = ReikaJavaLibrary.getRandomListEntry(fixedRand, li);
			CrystalElement c = ReikaJavaLibrary.getRandomListEntry(fixedRand, Arrays.asList(CrystalElement.BLACK, CrystalElement.WHITE, CrystalElement.YELLOW, CrystalElement.BLUE, CrystalElement.LIGHTGRAY, CrystalElement.PURPLE));
			double dx = tile.xCoord+0.5;
			double dz = tile.zCoord+0.5;
			double dy = ReikaRandomHelper.getRandomBetween(tile.yCoord+2.5, tile.yCoord+9);
			ChromaAux.dischargeIntoPlayer(dx, dy, dz, e.getRNG(), e, c, 1, 3.5F);
		}

		if (shockwaveCooldown > 0) {
			shockwaveCooldown--;
		}
		else {
			shockwaveCharge = shockwaveCharge*1.05F+0.015F;
			ChromaSounds.KILLAURA_CHARGE.playSoundAtBlockNoAttenuation(tile, 1, 0.5F+1.5F*shockwaveCharge*2, 128);
			if (shockwaveCharge >= 1) {
				this.shockwave();
			}
		}

		tick++;
		return tick >= DURATION;
	}

	private void shockwave() {
		shockwaveCharge = 0;
		shockwaveCooldown = 60+fixedRand.nextInt(200);
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(tile).expand(24, 12, 24);
		List<Entity> li = tile.worldObj.getEntitiesWithinAABB(Entity.class, box);
		for (Entity e : li) {
			double dx = e.posX-tile.xCoord-0.5;
			double dy = e.posY-tile.yCoord-0.5;
			double dz = e.posZ-tile.zCoord-0.5;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = 5;
			double vy = 2.5;
			e.addVelocity(v*dx/dd, vy+0*Math.max(v*dy/dd, vy), v*dz/dd);
			e.velocityChanged = true;
			e.fallDistance = 20;
			if (e instanceof EntityLivingBase)
				ChromaAux.doPylonAttack(null, (EntityLivingBase)e, 3, false);
			else
				e.attackEntityFrom(DamageSource.magic, 3);
			if (e instanceof EntityPlayer) {
				((EntityPlayer)e).capabilities.allowFlying = false;
				((EntityPlayer)e).capabilities.isFlying = false;
			}
		}
		if (tile.worldObj.isRemote)
			this.doShockwaveParticles();
	}

	@SideOnly(Side.CLIENT)
	private void doParticles() {
		int n = ReikaRandomHelper.getRandomBetween(1, 4);
		for (int i = 0; i < n; i++) {
			float s = (float)ReikaRandomHelper.getRandomBetween(7.5, 15);
			int l = ReikaRandomHelper.getRandomBetween(20, 80);
			double vy = ReikaRandomHelper.getRandomBetween(0.125, 1);
			EntityBlurFX fx = new EntityBlurFX(tile.worldObj, tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5, 0, vy, 0);
			fx.setScale(s).setColor(0xffffff).setLife(l).setIcon(ChromaIcons.FADE_GENTLE);
			fx.forceIgnoreLimits().setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (double br = 2.5; br <= 7.5; br += 2.5) {
			for (double a = 0; a < 360; a += 30) {
				double r = ReikaRandomHelper.getRandomPlusMinus(br, 1);
				double ang = ReikaRandomHelper.getRandomPlusMinus(a, 2.5);
				double dx = tile.xCoord+0.5*r*Math.cos(Math.toRadians(ang));
				double dz = tile.zCoord+0.5*r*Math.sin(Math.toRadians(ang));
				double dy = tile.yCoord+0.5-4;
				float s = (float)ReikaRandomHelper.getRandomBetween(7.5, 15)*1.5F*(float)Math.sqrt(br/7.5);
				int c = ReikaColorAPI.getModifiedHue(0xff0000, ReikaRandomHelper.getRandomBetween(22, 45));
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, (float)ReikaRandomHelper.getRandomBetween(0.4, 0.9));
				int l = ReikaRandomHelper.getRandomBetween(40, 180);
				float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
				double vv = ReikaRandomHelper.getRandomBetween(0, 0.125);
				double ra = ReikaRandomHelper.getRandomBetween(1D, 4);
				SpiralMotionController m = new SpiralMotionController(tile.xCoord+0.5, tile.zCoord+0.5, ra, vv, r, 0, ang);
				EntityBlurFX fx = new EntityBlurFX(tile.worldObj, dx, dy, dz);
				fx.setScale(s).setColor(c).setLife(l).setGravity(g).setIcon(ChromaIcons.FADE_GENTLE);
				fx.forceIgnoreLimits().setRapidExpand().setAlphaFading().setMotionController(m).setPositionController(m);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doShockwaveParticles() {
		int n = ReikaRandomHelper.getRandomBetween(120, 240);
		for (int i = 0; i < n; i++) {
			double a1 = particleRand.nextDouble()*360;
			double a2 = particleRand.nextDouble()*360;
			double v = ReikaRandomHelper.getRandomBetween(0.5, 2);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, v*1.5);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, v*1.5);
			float s = (float)ReikaRandomHelper.getRandomBetween(10D, 20);
			int l = ReikaRandomHelper.getRandomBetween(10, 40)*2;
			float g = (float)ReikaRandomHelper.getRandomBetween(v*0.5, v);//(float)ReikaRandomHelper.getRandomBetween(0.0625, 0.5);
			EntityBlurFX fx = new EntityBlurFX(tile.worldObj, tile.xCoord+0.5, tile.yCoord+1, tile.zCoord+0.5, vx, v, vz);
			double d = particleRand.nextDouble()*360;
			MathExpression e = new PeriodicExpression().addWave(1, 1, d).addWave(0.5, 2, d+90).addWave(0.125, 4, d).normalize();
			ColorController blink = new FlashColorController(e, 0xa0a0a0, 0x707070);
			fx.setScale(s).setColor(0xffffff).setLife(l).setGravity(g).setIcon(ChromaIcons.FADE).setColorController(blink);
			fx.forceIgnoreLimits().setRapidExpand();
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}
