/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Render.AngleColorController;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionTicker;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.Structure.MonumentGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.SpiralMotionController;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MonumentCompletionRitual {

	private static final Random rand = new Random();

	private static final int SOUND_LENGTH = 141; //2:21
	private static final int COMPLETION_EXTRA = 30;

	private static final int EFFECT_RANGE = 256;

	private static final ArrayList<RayNote>[] melody = new ArrayList[4];

	private long startTime;
	private long startTick;

	private long runTime = -1;
	private int tick;

	private boolean running;
	private boolean complete;

	private final World world;
	private final int x;
	private final int y;
	private final int z;
	private final EntityPlayer ep;

	private final PacketTarget packetTarget;

	private final ArrayList<RingParticle> particleRing = new ArrayList();

	private static boolean runningRituals;

	private static boolean reShowGui;
	private static boolean reBobView;

	static {
		melody[0] = new ArrayList();
		melody[1] = new ArrayList();
		melody[2] = new ArrayList();
		melody[3] = new ArrayList();

		melody[0].add(new RayNote(MusicKey.A4, 8));
		melody[0].add(new RayNote(MusicKey.C5, 8));
		melody[0].add(new RayNote(MusicKey.B4, 8));
		melody[0].add(new RayNote(MusicKey.G4, 8));
		melody[0].add(new RayNote(MusicKey.A4, 8));
		melody[0].add(new RayNote(MusicKey.E4, 24));

		melody[1].add(new RayNote(MusicKey.A4, 8));
		melody[1].add(new RayNote(MusicKey.C5, 8));
		melody[1].add(new RayNote(MusicKey.B4, 8));
		melody[1].add(new RayNote(MusicKey.G5, 12));
		melody[1].add(new RayNote(MusicKey.E5, 2));
		melody[1].add(new RayNote(MusicKey.D5, 2));
		melody[1].add(new RayNote(MusicKey.E5, 24));

		melody[2].add(new RayNote(MusicKey.A5, 8));
		melody[2].add(new RayNote(MusicKey.G5, 6));
		melody[2].add(new RayNote(MusicKey.A5, 1));
		melody[2].add(new RayNote(MusicKey.G5, 1));
		melody[2].add(new RayNote(MusicKey.E5, 8));
		melody[2].add(new RayNote(MusicKey.C5, 6));
		melody[2].add(new RayNote(MusicKey.D5, 1));
		melody[2].add(new RayNote(MusicKey.E5, 1));
		melody[2].add(new RayNote(MusicKey.D5, 8));
		melody[2].add(new RayNote(MusicKey.A4, 24));

		melody[3].add(new RayNote(MusicKey.A4, 8));
		melody[3].add(new RayNote(MusicKey.E5, 8));
		melody[3].add(new RayNote(MusicKey.D5, 8));
		melody[3].add(new RayNote(MusicKey.G4, 8));
		melody[3].add(new RayNote(MusicKey.B4, 2));
		melody[3].add(new RayNote(MusicKey.C5, 1));
		melody[3].add(new RayNote(MusicKey.B4, 1));
		melody[3].add(new RayNote(MusicKey.A4, 2));
		melody[3].add(new RayNote(MusicKey.G4, 2));
		melody[3].add(new RayNote(MusicKey.A4, 24));
	}

	private static class RayNote {

		private final MusicKey key;
		/** In 16ths */
		private final int length;

		private RayNote(MusicKey k, int dur) {
			key = k;
			length = dur;
		}

	}

	private static class RingParticle {

		private final double offsetX;
		private final double offsetZ;
		private final int color;

		private float particleSizeFactor = 0.5F;
		private float particleSizeFactorVelocity = 0;

		private RingParticle(double x, double z, int c) {
			offsetX = x;
			offsetZ = z;
			color = c;
		}

	}

	public static boolean areRitualsRunning() {
		return runningRituals;
	}

	public MonumentCompletionRitual(World world, int x, int y, int z, EntityPlayer ep) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ep = ep;

		packetTarget = new PacketTarget.RadiusTarget(world, x+0.5, y+0.5, z+0.5, EFFECT_RANGE);

		/*
		Spline s = new Spline(SplineType.CHORDAL);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate rel = TileEntityDimensionCore.getLocation(e);
			Coordinate c2 = rel.offset(x, y, z);
			s.addPoint(new BasicSplinePoint(new DecimalPosition(c2)));
		}
		List<DecimalPosition> li = s.get(24, true);
		for (int i = 0; i < li.size(); i++) {
			DecimalPosition d = li.get(i);
			//float f = i/(float)li.size();
			int idx = (i+12)%li.size();
			int c = CrystalElement.getBlendedColor(idx, 24);
			particleRing.add(new RingParticle(d.xCoord, d.yCoord, d.zCoord, c));
		}
		 */

		//double r0 = 29;//32;//33;
		for (double a = 0; a < 360; a += 1) {
			//double r = r0;//*(1-);
			//double dx = x+0.5*r*Math.cos(Math.toRadians(a));
			//double dz = z+0.5*r*Math.sin(Math.toRadians(a));
			double dx = Math.cos(Math.toRadians(a));
			double dz = Math.sin(Math.toRadians(a));
			//double dy = y+0.5+6;
			int c = CrystalElement.getBlendedColor((int)((a+180-12.25)*2), 45);
			particleRing.add(new RingParticle(dx, dz, c));
		}
	}

	public void start() {
		this.disableCores();
		running = true;
		runningRituals = true;
		tick = 0;
		startTime = System.currentTimeMillis();
		startTick = world.getTotalWorldTime();
		runTime = 0;
		if (world.isRemote) {
			this.startClient();
		}
	}

	private void disableCores() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			((TileEntityDimensionCore)c.getTileEntity(world)).prime(false);
		}
	}

	@SideOnly(Side.CLIENT)
	private void startClient() {
		//	ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENT, x+0.5, y+0.5, z+0.5, 1, 1F, false);
		ISound s = ChromaDimensionTicker.instance.getCurrentMusic();
		if (s != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(s);
		}

		reBobView = Minecraft.getMinecraft().gameSettings.viewBobbing;
		reShowGui = !Minecraft.getMinecraft().gameSettings.hideGUI;
	}

	public void tick() {
		if (running) {
			long time = System.currentTimeMillis();
			runTime = time-startTime;
			tick++;

			if (world.isRemote) {
				//this.manipulateCamera();
				this.doScriptedFX();
			}
			else {
				if (runTime >= SOUND_LENGTH*1000) {
					//this.completeRitual();
				}
			}
		}
	}
	/*
	@SideOnly(Side.CLIENT)
	public void setTime(long time) {
		runTime = time;
	}
	 */
	@SideOnly(Side.CLIENT)
	private void doScriptedFX() {
		int n = 3;
		int di = particleRing.size()/n;

		for (int i = 0; i < n; i++) {
			int idx = (tick+di*i)%particleRing.size();
			RingParticle p = particleRing.get(idx);
			p.particleSizeFactorVelocity = 0.5F;
			p.particleSizeFactor = 1;
		}

		for (int i = 0; i < particleRing.size(); i++) {
			RingParticle p = particleRing.get(i);
			p.particleSizeFactor = Math.min(1, p.particleSizeFactor+p.particleSizeFactorVelocity);
			p.particleSizeFactorVelocity = Math.max(-0.1F, p.particleSizeFactorVelocity-0.05F);

			if (p.particleSizeFactor <= 0)
				continue;

			double r = 32.5-2.5*Math.abs(Math.sin(Math.toRadians(i*2)));//29+2*Math.sin(Math.toRadians(tick-i));//32;//+p.particleSizeFactor*0-2.5*Math.abs(Math.sin(Math.toRadians(i*2)));
			double dx = x+0.5+0.5*r*p.offsetX;
			double dz = z+0.5+0.5*r*p.offsetZ;

			float s = p.particleSizeFactor*(1+rand.nextFloat())*4;
			int l = (int)(p.particleSizeFactor*(60+rand.nextInt(40)));
			float g = p.particleSizeFactor*p.particleSizeFactor*p.particleSizeFactor*(float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, y+6.5, dz);
			fx.setIcon(ChromaIcons.CENTER).forceIgnoreLimits().setScale(s).setColor(p.color).setLife(l).setRapidExpand().setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void manipulateCamera() {
		Minecraft.getMinecraft().gameSettings.hideGUI = true;
		Minecraft.getMinecraft().gameSettings.viewBobbing = false;
		Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
		double ang = ((world.getTotalWorldTime()+ep.getUniqueID().hashCode()%8000)/2D);
		double angPrev = ((world.getTotalWorldTime()+ep.getUniqueID().hashCode()%8000-1)/2D);
		//double r = 15+7.5*Math.sin(ang/32D);
		//double cx = x+0.5+r*Math.cos(Math.toRadians(ang));
		//double cz = z+0.5+r*Math.sin(Math.toRadians(ang));
		//double cxPrev = x+0.5+r*Math.cos(Math.toRadians(ang));
		//double czPrev = z+0.5+r*Math.sin(Math.toRadians(ang));
		double r = 26*1.25;
		double R = 32*1.25;
		double d = 13*1.25*0.75;
		double cx = x+0.5+(R-r)*Math.cos(Math.toRadians(ang))+d*Math.cos(Math.toRadians((R-r)/r*ang));
		double cz = z+0.5+(R-r)*Math.sin(Math.toRadians(ang))-d*Math.sin(Math.toRadians((R-r)/r*ang));
		double cxPrev = x+0.5+(R-r)*Math.cos(Math.toRadians(angPrev))+d*Math.cos(Math.toRadians((R-r)/r*angPrev));
		double czPrev = z+0.5+(R-r)*Math.sin(Math.toRadians(angPrev))-d*Math.sin(Math.toRadians((R-r)/r*angPrev));
		double dd = ReikaMathLibrary.py3d(cx-x-0.5, 0, cz-z-0.5);
		double ddPrev = ReikaMathLibrary.py3d(cxPrev-x-0.5, 0, czPrev-z-0.5);
		double cy = y+15+4+4-0.25*dd+4*Math.sin(Math.toRadians(ang/4))-2;
		double cyPrev = y+15+4+4-0.25*ddPrev+4*Math.sin(Math.toRadians(angPrev/4))-2;
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(cx-x-0.5, cy-y-0.5, cz-z-0.5);
		double[] angsPrev = ReikaPhysicsHelper.cartesianToPolar(cxPrev-x-0.5, cyPrev-y-0.5, czPrev-z-0.5);
		double pitch = 90-angs[1];
		double pitchPrev = 90-angsPrev[1];
		double yaw = -angs[2];
		double yawPrev = -angsPrev[2];
		ReikaRenderHelper.setCameraPosition(ep, cx, cy, cz, cxPrev, cyPrev, czPrev, yaw, yawPrev, pitch, pitchPrev, true, true);
	}

	public boolean doChecks() {
		EntityPlayer ep = null;
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			ChromaTiles t = ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world));
			if (t != ChromaTiles.DIMENSIONCORE) {
				this.endRitual();
				return false;
			}
			TileEntityDimensionCore te = (TileEntityDimensionCore)c.getTileEntity(world);
			if (te.getColor() != e) {
				this.endRitual();
				return false;
			}
			EntityPlayer own = te.getPlacer();
			if (own != null && !ReikaPlayerAPI.isFake(own)) {
				if (ep == null || own == ep) {
					ep = own;
				}
				else {
					this.endRitual();
					return false;
				}
			}
		}

		if (ep == null) {
			this.endRitual();
			return false;
		}

		if (!this.doMineralChecks()) {
			this.endRitual();
			return false;
		}

		return true;
	}

	private boolean doMineralChecks() {
		if (world.isRemote)
			return true;
		MonumentGenerator gen = ChunkProviderChroma.getMonumentGenerator();
		if (world.provider.dimensionId == 0) {
			gen = new MonumentGenerator();
			gen.startCalculate(x, z, rand);
		}
		Map<Coordinate, Block> map = gen.getMineralBlocks();
		for (Coordinate c : map.keySet()) {
			Block b = c.getBlock(world);
			if (b != map.get(c)) {
				//ReikaJavaLibrary.pConsole("@ "+c+": "+map.get(c).getLocalizedName()+", "+b.getLocalizedName());
				//c.setBlock(world, map.get(c));
				return false;
			}
		}
		return true;
	}

	private void completeRitual() {
		complete = true;
		if (runTime-SOUND_LENGTH < COMPLETION_EXTRA) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.MONUMENTCOMPLETE.ordinal(), packetTarget, x, y, z);

			double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
			double pitch = 90-angs[1];
			double yaw = -angs[2];
			ep.rotationYaw = ep.rotationYawHead = ep.prevRotationYawHead = ep.prevRotationYaw = (float)yaw;
			ep.rotationPitch = ep.prevRotationPitch = (float)pitch;
		}
		else {
			world.setBlock(x, y, z, ChromaTiles.AURAPOINT.getBlock(), ChromaTiles.AURAPOINT.getBlockMetadata(), 3);
			TileEntityAuraPoint te = (TileEntityAuraPoint)world.getTileEntity(x, y, z);
			te.setPlacer(ep);
			ep.setLocationAndAngles(x+0.5, y-4, z-4.5, 0, -25);
			ep.onGround = true;
			ep.motionX = ep.motionY = ep.motionZ = 0;
			ProgressStage.CTM.stepPlayerTo(ep);
			if (!world.isRemote)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RESETMONUMENT.ordinal(), packetTarget, x, y, z);
			running = false;
		}
	}

	private void triggerEvent(EventType evt) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.MONUMENTEVENT.ordinal(), packetTarget, x, y, z, evt.ordinal());
	}

	public void endRitual() {
		running = false;
		runTime = -1;
		runningRituals = false;
		((TileEntityStructControl)world.getTileEntity(x, y, z)).endMonumentRitual();
		ChromaSounds.ERROR.playSoundNoAttenuation(world, x, y, z, 1, 0.75F, Integer.MAX_VALUE);
		if (!world.isRemote)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RESETMONUMENT.ordinal(), packetTarget, x, y, z);
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isComplete() {
		return complete;
	}

	@SideOnly(Side.CLIENT)
	public static void triggerMonumentEventClient(World world, int x, int y, int z, int type) {
		EventType.list[type].doEventClient(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public static void completeMonumentClient(World world, int x, int y, int z) {
		int n = 32+rand.nextInt(64);
		for (int i = 0; i < n; i++) {
			double phi = rand.nextDouble()*360;
			double theta = ReikaRandomHelper.getRandomPlusMinus(0D, 90D);
			double v = ReikaRandomHelper.getRandomBetween(0.125, 0.5);
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
			int c = CrystalElement.randomElement().getColor();
			int l = 30+rand.nextInt(40);
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityCCFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, phi, theta).setColor(c).setScale(6).setLife(l);
			fx.particleVelocity *= 3;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			TileEntityDimensionCore.createBeamLine(world, x, y, z, c.xCoord, c.yCoord, c.zCoord, CrystalElement.WHITE, e);
		}

		//ReikaSoundHelper.playClientSound(ChromaSounds.PYLONTURBO, x+0.5, y+0.5, z+0.5, 1, 2F, false);

		//ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTCOMPLETE, x+0.5, y+0.5, z+0.5, 2, 0.9439F, false);

		Minecraft.getMinecraft().gameSettings.hideGUI = !reShowGui;
		Minecraft.getMinecraft().gameSettings.viewBobbing = reBobView;
	}

	@SideOnly(Side.CLIENT)
	public static void resetSettings(World world, int x, int y, int z) {
		Minecraft.getMinecraft().gameSettings.hideGUI = false;
		Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;

		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
		double pitch = 90-angs[1];
		double yaw = -angs[2];
		ReikaRenderHelper.setCameraPosition(ep, ep.posX, ep.posY, ep.posZ, ep.posX, ep.posY, ep.posZ, yaw, yaw, pitch, pitch, false, true);
	}

	private void doRay(MusicKey key) {
		float p = (float)CrystalMusicManager.instance.getPitchFactor(key);
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 1, p, false);
		Collection<CrystalElement> set = CrystalMusicManager.instance.getColorsWithKeyAnyOctave(key);
		for (CrystalElement e : set) {
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			DecimalPosition end = new DecimalPosition(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
			LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), end, 8);
			b.setVariance(0.9);
			b.maximize();
			int l = 5+rand.nextInt(20);
			for (int i = 0; i < b.nsteps; i++) {
				DecimalPosition pos1 = b.getPosition(i);
				DecimalPosition pos2 = b.getPosition(i+1);
				for (double r = 0; r <= 1; r += 0.03125) {
					float s = 5F;
					int clr = e.getColor();
					DecimalPosition dd = DecimalPosition.interpolate(pos1, pos2, r);
					EntityFX fx = new EntityCCBlurFX(world, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s).setColor(clr).setLife(l).setRapidExpand();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					EntityFX fx2 = new EntityCCBlurFX(world, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s/2.5F).setColor(0xffffff).setLife(l).setRapidExpand();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				}
			}
		}
	}

	private static enum EventType {
		FLARES(),
		PARTICLECLOUD(),
		PARTICLERING(),
		TWIRL();

		private static final EventType[] list = values();

		private EventType() {

		}

		@SideOnly(Side.CLIENT)
		public void doEventClient(World world, int x, int y, int z) {
			switch(this) {
				case FLARES: {
					//ReikaSoundHelper.playClientSound(ChromaSounds.USE, x+0.5, y+0.5, z+0.5, 1, 2F, false);

					int n = 32+rand.nextInt(48);
					for (int i = 0; i < n; i++) {
						double phi = rand.nextDouble()*360;
						double theta = rand.nextDouble()*360;
						double v = ReikaRandomHelper.getRandomBetween(0.125, 0.5);
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
						EntityFX fx = new EntityFlareFX(CrystalElement.WHITE, world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setScale(6);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case PARTICLECLOUD: {
					//ReikaSoundHelper.playClientSound(ChromaSounds.CAST, x+0.5, y+0.5, z+0.5, 1, 0.5F, false);
					int n = 64+rand.nextInt(256);
					for (int i = 0; i < n; i++) {
						double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 20);
						double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 20);
						double dy = 1+world.getTopSolidOrLiquidBlock(MathHelper.floor_double(dx), MathHelper.floor_double(dz))+rand.nextInt(12);
						int l = 20+rand.nextInt(40);
						float s = 2+rand.nextFloat()*12;
						double hue = ReikaPhysicsHelper.cartesianToPolar(dx-x-0.5, dy-y-0.5, dz-z-0.5)[2];
						int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(hue));
						EntityFX fx = new EntityCCBlurFX(world, dx, dy, dz).setIcon(ChromaIcons.BIGFLARE).setScale(s).setColor(c).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case PARTICLERING: {
					//ReikaSoundHelper.playClientSound(ChromaSounds.USE, x+0.5, y+0.5, z+0.5, 1, 0.5F, false);
					double r = 33;
					for (double a = 0; a < 360; a += 1) {
						double dx = x+0.5*r*Math.cos(Math.toRadians(a));
						double dz = z+0.5*r*Math.sin(Math.toRadians(a));
						double dy = y+0.5+6;
						float s = (1+rand.nextFloat())*4;
						int c = CrystalElement.getBlendedColor((int)((a+180-12.25)*2), 45);
						int l = 60+rand.nextInt(40);
						float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
						EntityFX fx = new EntityCCBlurFX(world, dx, dy, dz).setIcon(ChromaIcons.CENTER).setScale(s).setColor(c).setLife(l).setRapidExpand().setGravity(g);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case TWIRL: {
					for (int i = 0; i <= 8; i += 4) {
						for (double a = 0; a < 360; a += 60/(1+i/4)) {
							double r = 2+i;
							double ang = a+i*22.5;
							double dx = x+0.5*r*Math.cos(Math.toRadians(ang));
							double dz = z+0.5*r*Math.sin(Math.toRadians(ang));
							double dy = y+0.5-4;
							float s = (float)((1+3-r/8)*2)/2F;
							int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(ang));
							int l = 120+rand.nextInt(120);
							SpiralMotionController m = new SpiralMotionController(x+0.5, z+0.5, 5-i/4D, (0.25*(1+i/4D))/4D, r, 0.0625, ang);
							ColorController clr = new AngleColorController(x+0.5, z+0.5, 5-i/4D, (0.25*(1+i/4D))/4D, r, 0.0625, ang);
							EntityFX fx = new EntityCCBlurFX(world, dx, dy, dz).setScale(s).setColor(c).setLife(l).setRapidExpand().setMotionController(m).setPositionController(m).setColorController(clr);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					break;
				}
			}
		}
	}

}
