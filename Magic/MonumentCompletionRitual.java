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
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
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
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.IO.Shaders.ShaderProgram.Vec4;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.SoundVariant;
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

	private final int FINAL_SOUND_COMPLETION_DELAY = 18500; //millis
	private final int COMPLETION_EXTRA = 3000; //millis

	private final long[] SOUND_TIMINGS = new long[] {0, 28000, 66500, 86000, 104800, 123500}; //in millis

	private final int BEAT_LENGTH = 2390; //2.5s / qtr

	private final int EFFECT_RANGE = 256;

	private static final ArrayList<RayNote> melody = new ArrayList();

	private long startTime;
	private long startTick;

	private long runTime = -1;
	private int tick;
	private long lastTickTime;
	private long nextSoundTime;
	private long lastSoundStart;
	private long completionTime;
	private long pauseTotal = 0;
	private int currentSound = -1;

	private boolean running;
	private boolean complete;

	private final World world;
	private final int x;
	private final int y;
	private final int z;
	private final EntityPlayer ep;

	private final PacketTarget packetTarget;
	@SideOnly(Side.CLIENT)
	private ISound playingSound;
	private float vortexSize = 0;
	private boolean vortexGrow = false;

	private final ArrayList<RingParticle> particleRing = new ArrayList();
	private final ArrayList<TimedEvent> events = new ArrayList();
	private static final float[] colorFade = new float[16];
	private static final Vec3[] shaderPositions = new Vec3[16];
	private static final Vec4[] shaderColors = new Vec4[16];
	private MusicKey activeKey = null;

	private static boolean runningRituals;

	private static boolean reShowGui;
	private static boolean reBobView;

	static {
		melody.add(new RayNote(MusicKey.A4, 2, true, false, true));
		melody.add(new RayNote(MusicKey.C5, 2));
		melody.add(new RayNote(MusicKey.B4, 2));
		melody.add(new RayNote(MusicKey.G4, 2));
		melody.add(new RayNote(MusicKey.A4, 2));
		melody.add(new RayNote(MusicKey.E4, 6, false, true));

		melody.add(new RayNote(MusicKey.A4, 2, true, false, true)); //6
		melody.add(new RayNote(MusicKey.C5, 2));
		melody.add(new RayNote(MusicKey.B4, 2));
		melody.add(new RayNote(MusicKey.G5, 4, false, true));
		melody.add(new RayNote(MusicKey.E5, 6)); //10

		melody.add(new RayNote(MusicKey.A5, 2, true, true, true));
		melody.add(new RayNote(MusicKey.G5, 2));
		melody.add(new RayNote(MusicKey.E5, 2));
		melody.add(new RayNote(MusicKey.C5, 2));
		melody.add(new RayNote(MusicKey.D5, 2));
		melody.add(new RayNote(MusicKey.A4, 6, false, true)); //16

		melody.add(new RayNote(MusicKey.A4, 2, true, false, true));
		melody.add(new RayNote(MusicKey.E5, 2));
		melody.add(new RayNote(MusicKey.D5, 2));
		melody.add(new RayNote(MusicKey.G4, 2));
		melody.add(new RayNote(MusicKey.A4, 8, true, true));
	}

	private static class RayNote {

		private final MusicKey key;
		/** In quarters */
		private final int length;
		private final int startBeat;

		private final boolean percussion;
		private final boolean bell;
		private final boolean isFirstInPhrase;

		private RayNote(MusicKey k, int dur) {
			this(k, dur, false, false);
		}

		private RayNote(MusicKey k, int dur, boolean p, boolean b) {
			this(k, dur, p, b, false);
		}

		private RayNote(MusicKey k, int dur, boolean p, boolean b, boolean first) {
			key = k;
			startBeat = melody.isEmpty() ? 0 : melody.get(melody.size()-1).startBeat+melody.get(melody.size()-1).length;
			length = dur;
			percussion = p;
			bell = b;
			isFirstInPhrase = first;
		}

		@Override
		public String toString() {
			return key.toString()+" @ "+startBeat+"-"+length+" [P="+percussion+"/B="+bell+"/F="+isFirstInPhrase+"]";
		}

	}

	private void addEvent(EventType e, int beats) {
		events.add(new TimedEvent(e, beats*BEAT_LENGTH));
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

	private static class RayEvent extends TimedEvent {

		private final RayNote ray;

		private RayEvent(RayNote r, long s) {
			super(null, s);
			ray = r;
		}

	}

	private static class TimedEvent implements Comparable<TimedEvent> {

		private final EventType type;
		private final long millis;

		private TimedEvent(EventType e, long s) {
			type = e;
			millis = s;
		}

		@Override
		public final int compareTo(TimedEvent o) {
			return Long.compare(millis, o.millis);
		}

	}

	public static boolean areRitualsRunning() {
		return runningRituals;
	}

	public static void clearRituals() {
		runningRituals = false;
		for (int i = 0; i < 16; i++) {
			colorFade[i] = 0;
			shaderColors[i] = null;
			shaderPositions[i] = null;
		}
	}

	public static float getIntensity(CrystalElement e) {
		return colorFade[e.ordinal()];
	}

	public static void addShaderData(ShaderProgram p) {
		p.setField("glowLocations", shaderPositions);
		p.setField("glowColor", shaderColors);
	}

	public MonumentCompletionRitual(World world, int x, int y, int z, EntityPlayer ep) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ep = ep;

		int t0 = 100;
		long off = 0;
		events.add(new TimedEvent(EventType.FLARES, 0));
		int nextOff = 0;
		boolean flag = false;
		boolean flag2 = false;
		boolean flag3 = false;
		for (int idx = 0; idx < melody.size(); idx++) {
			RayNote n = melody.get(idx);
			EventType e = EventType.FLARES;
			if (n.length >= 6) {
				e = EventType.PARTICLECLOUD;
			}
			else if (n.length >= 4) {
				e = EventType.TWIRL;
			}

			if (n.isFirstInPhrase && n.startBeat > 0) {
				if (off == 0)
					off -= 250; //was 1200
				else if (off == -250)
					off += 400;
				if (off == 150)
					nextOff = 200;
			}

			if (idx == 10) //long non-bell
				nextOff = 250;
			if (idx == 16)
				off = 250;
			if (idx == 18)
				off = 0;

			long t = t0+off+nextOff;
			nextOff = 0;
			if (n.isFirstInPhrase && n.startBeat > 0)
				;//t -= 1200;
			TimedEvent te = new TimedEvent(e, t);
			events.add(te);
			events.add(new RayEvent(n, t-250));
			if (n.percussion && n.startBeat > 0)
				events.add(new TimedEvent(EventType.PINWHEEL, t));
			if (n.bell && n.startBeat > 0)
				events.add(new TimedEvent(EventType.TWIRL, t));
			if (n.isFirstInPhrase) {
				for (int i = 0; i <= 250; i += 50)
					events.add(new TimedEvent(EventType.PARTICLERING, t+i));
			}
			t0 += n.length*BEAT_LENGTH;
			if (t0 >= 85000 && !flag) {
				t0 += 500;
				flag = true;
			}
			if (t0 >= 101000 && !flag2) {
				t0 += 750;
				flag2 = true;
			}
			if (t0 >= 115000 && !flag3) {
				t0 += -1200;
				flag3 = true;
			}
		}

		this.addEvent(EventType.VORTEXGROW, 12);
		this.addEvent(EventType.VORTEXGROW, 14);
		this.addEvent(EventType.VORTEXGROW, 24);
		this.addEvent(EventType.VORTEXGROW, 28);
		this.addEvent(EventType.VORTEXGROW, 44);

		Collections.sort(events);

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

		if (world.isRemote) {
			currentSound = 0;
			this.startClient();
		}

		completionTime = -1;
		startTime = System.currentTimeMillis();
		startTick = world.getTotalWorldTime();
		runTime = 0;
		tick = 0;
		lastTickTime = startTime;
		pauseTotal = 0;
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
		ISound s = ChromaDimensionTicker.instance.getCurrentMusic();
		if (s != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(s);
		}

		this.stepSound();

		reBobView = Minecraft.getMinecraft().gameSettings.viewBobbing;
		reShowGui = !Minecraft.getMinecraft().gameSettings.hideGUI;
	}

	private void stepSound() {
		//ReikaJavaLibrary.pConsole("Stepping sound to "+currentSound+" @ "+runTime+" ("+(runTime-startTime)+")");
		int idx = currentSound;
		SoundVariant s = ChromaSounds.MONUMENT.getVariant(String.valueOf(idx+1	));
		playingSound = ReikaSoundHelper.playClientSound(s, x+0.5, y+0.5, z+0.5, 1, 1F, false);
		lastSoundStart = runTime;
		currentSound++;
		nextSoundTime = currentSound >= SOUND_TIMINGS.length ? Long.MAX_VALUE : runTime+SOUND_TIMINGS[currentSound]-SOUND_TIMINGS[idx];
		//ReikaJavaLibrary.pConsole("Next sound is at "+nextSoundTime);
	}

	public void tick() {
		if (running) {
			long time = System.currentTimeMillis();
			long step = time-lastTickTime;
			if (step > 50) { //more than 50ms per tick
				pauseTotal += step-50;
			}
			runTime = time-(startTime+pauseTotal);
			//ReikaJavaLibrary.pConsole(time+" - "+startTime+" - "+pauseTotal+" = "+runTime+" @ "+FMLCommonHandler.instance().getEffectiveSide());
			tick++;

			if (world.isRemote) {
				this.manipulateCamera();
				this.doScriptedFX();
			}
			else {
				if (this.isReadyToComplete()) {
					this.completeRitual();
				}
			}

			lastTickTime = time;
		}
	}

	private boolean isReadyToComplete() {
		return runTime-SOUND_TIMINGS[SOUND_TIMINGS.length-1] >= FINAL_SOUND_COMPLETION_DELAY;//runTime >= SOUND_LENGTH_MILLIS;
	}

	/*
	@SideOnly(Side.CLIENT)
	public void setTime(long time) {
		runTime = time;
	}
	 */
	@SideOnly(Side.CLIENT)
	private void doScriptedFX() {
		this.doRingFX();
		this.doVortexFX();

		if (runTime >= nextSoundTime) {
			this.stepSound();
		}

		boolean flag = !events.isEmpty();
		while (flag) {
			flag = false;
			TimedEvent e = events.get(0);
			if (e.millis <= runTime) {
				events.remove(0);
				flag = !events.isEmpty();
				if (e instanceof RayEvent) {
					RayEvent r = (RayEvent)e;
					activeKey = r.ray.key;
					this.playRayNote(r.ray);
				}
				else {
					e.type.doEventClient(this);
					this.onEvent(e.type);
					//ReikaJavaLibrary.pConsole("Removing "+e.type+" @ "+e.millis+" at "+runTime);
				}
			}
			else {
				//ReikaJavaLibrary.pConsole(runTime+"/"+e.millis);
			}
		}

		if (complete) {
			activeKey = null;
		}

		Set<CrystalElement> set = activeKey == null ? null : CrystalMusicManager.instance.getColorsWithKeyAnyOctave(activeKey);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			colorFade[i] = set != null && set.contains(e) ? Math.min(1, colorFade[i]+0.04F) : Math.max(0, colorFade[i]-0.015F);
			TileEntityDimensionCore te = this.getCore(e);

			shaderPositions[i] = Vec3.createVectorHelper(te.xCoord+0.5, y-3.5, te.zCoord+0.5);
			shaderColors[i] = new Vec4(e.getRed()/255F, e.getGreen()/255F, e.getBlue()/255F, colorFade[i]*0.7F);

			te.shaderScale = 1+colorFade[i]*5;
		}
	}

	private TileEntityDimensionCore getCore(CrystalElement e) {
		return (TileEntityDimensionCore)TileEntityDimensionCore.getLocation(e).offset(x, y, z).getTileEntity(world);
	}

	private void playRayNote(RayNote n) {
		this.doRays(n);
	}

	private void onEvent(EventType type) {
		switch(type) {
			case FLARES:
				break;
			case PARTICLECLOUD:
				break;
			case PARTICLERING:
				break;
			case PINWHEEL:
				break;
			case TWIRL:
				break;
			case VORTEXGROW:
				vortexGrow = true;
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void doVortexFX() {
		double y0 = y+0.5-4.5;
		double h = 0;
		double vy = 0.25;
		//for (double h = 0; h <= 6; h += 0.125) {
		//vy = 0;
		double r = 2.25-h/3+0.5*vortexSize;
		for (double a = 0; a < 360; a += 30) {
			double ang = a+h*30+(System.currentTimeMillis()/20D)%360D;
			double dx = x+0.5+r*Math.cos(Math.toRadians(ang));
			double dz = z+0.5+r*Math.sin(Math.toRadians(ang));
			double vx = (x+0.5-dx)*0.01875/vortexSize;
			double vz = (z+0.5-dz)*0.01875/vortexSize;
			double dy = y0+h;
			float s = 4;//(float)(7.5-h);
			int l = a%60 == 0 ? 60 : 40;
			l *= vortexSize;
			EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz, vx, vy, vz);
			fx.setIcon(ChromaIcons.CHROMA);
			fx.setScale(s).setLife(l).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		if (vortexSize < 1) {
			vortexSize = Math.min(1, vortexSize+0.02F);
		}
		else if (vortexGrow) {
			vortexSize = Math.min(2, vortexSize+0.05F);
			vortexGrow = vortexSize < 2;
		}
		else if (vortexSize > 1) {
			vortexSize = Math.max(1, vortexSize*0.998F-0.01F);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doRingFX() {
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
		//ReikaJavaLibrary.pConsole("$$$$$$$$ COMPLETE @ "+runTime+" $$$$$$$$$$$$$$");
		if (completionTime < 0)
			completionTime = runTime;
		if (runTime-completionTime < COMPLETION_EXTRA) {
			if (!complete)
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
		complete = true;
	}

	public void endRitual() {
		running = false;
		runTime = -1;
		currentSound = -1;
		runningRituals = false;
		((TileEntityStructControl)world.getTileEntity(x, y, z)).endMonumentRitual();
		ChromaSounds.ERROR.playSoundNoAttenuation(world, x, y, z, 1, 0.75F, Integer.MAX_VALUE);
		if (world.isRemote)
			this.endSounds();
		else
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RESETMONUMENT.ordinal(), packetTarget, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void endSounds() {
		if (playingSound != null)
			Minecraft.getMinecraft().getSoundHandler().stopSound(playingSound);
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isComplete() {
		return complete;
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

	private void doRays(RayNote n) {
		float p = (float)CrystalMusicManager.instance.getPitchFactor(n.key);
		//ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 1, p, false);
		//ScheduledSoundEventClient se = new ScheduledSoundEventClient(ChromaSounds.MONUMENTRAY, world, x+0.5, y+0.5, z+0.5, 1, p);
		//se.attenuate = false;
		//TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(se), 8);
		Collection<CrystalElement> set = CrystalMusicManager.instance.getColorsWithKeyAnyOctave(n.key);
		for (CrystalElement e : set) {
			//doRay(e, world, x, y, z, p);
			int time = ReikaRandomHelper.getRandomBetween(10, BEAT_LENGTH*n.length/50-15);
			TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledRayEvent(e, world, x, y, z, p)), time);
		}
	}

	private static class ScheduledRayEvent implements ScheduledEvent {

		private final CrystalElement color;
		private final World world;
		private final int x;
		private final int y;
		private final int z;
		private final float pitch;

		public ScheduledRayEvent(CrystalElement e, World w, int x, int y, int z, float p) {
			color = e;
			world = w;
			this.x = x;
			this.y = y;
			this.z = z;
			pitch = p;
		}

		@Override
		public void fire() {
			doRay(color, world, x, y, z, pitch);
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.CLIENT;
		}

	}

	private static void doRay(CrystalElement e, World world, int x, int y, int z, float p) {
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, (float)ReikaRandomHelper.getRandomBetween(0.4, 0.8), p, false);
		Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
		DecimalPosition end = new DecimalPosition(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
		LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), end, 8);
		b.scaleVariance(1.25F);
		b.maximize();
		int l = 20+rand.nextInt(40);
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

	public static enum EventType {
		FLARES(),
		PARTICLECLOUD(),
		PARTICLERING(),
		TWIRL(),
		PINWHEEL(),
		VORTEXGROW(),
		;

		private static final EventType[] list = values();

		private EventType() {

		}

		@SideOnly(Side.CLIENT)
		private void doEventClient(MonumentCompletionRitual m) {
			this.doEventClient(m.world, m.x, m.y, m.z);
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
					int n = ReikaRandomHelper.getRandomBetween(256, 384);
					double mr = 25;
					for (int i = 0; i < n; i++) {
						double angd = rand.nextDouble()*360;
						double ang = Math.toRadians(angd);
						double fr = rand.nextDouble();
						double r = fr*mr;
						double dx = x+0.5+r*Math.cos(ang);//ReikaRandomHelper.getRandomPlusMinus(x+0.5, 20);
						double dz = z+0.5+r*Math.sin(ang);//ReikaRandomHelper.getRandomPlusMinus(z+0.5, 20);
						int h = rand.nextInt(rand.nextBoolean() ? 6 : 12);
						double dy = 1+world.getTopSolidOrLiquidBlock(MathHelper.floor_double(dx), MathHelper.floor_double(dz))+h;
						int l = ReikaRandomHelper.getRandomBetween(120, 240);//20+rand.nextInt(40);
						float s = (float)ReikaRandomHelper.getRandomBetween(4, 12D);//2+rand.nextFloat()*12;
						int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(angd));
						c = ReikaColorAPI.mixColors(c, 0xffffff, Math.min(1, (float)(fr*fr*4)));
						EntityCCFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, dx, dy, dz, 0, 90);
						fx.angleVelocity *= 7;
						fx.freedom *= 1.5;
						fx.tolerance *= 0.33;
						fx.particleVelocity *= 0.5;
						fx.setIcon(ChromaIcons.BIGFLARE).setScale(s).setColor(c).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case PARTICLERING: {
					//ReikaSoundHelper.playClientSound(ChromaSounds.USE, x+0.5, y+0.5, z+0.5, 1, 0.5F, false);
					double r = 33/2D;
					double dy = y+0.5+6;
					for (double a = 0; a < 360; a += 1) {
						double dx = x+0.5+r*Math.cos(Math.toRadians(a));
						double dz = z+0.5+r*Math.sin(Math.toRadians(a));
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
					double dy = y+0.5-4;
					for (int i = 0; i <= 8; i += 4) {
						for (double a = 0; a < 360; a += 30/(1+i/4)) {
							double r = 2+i;
							double ang = a+i*22.5;
							double dx = x+0.5+r*Math.cos(Math.toRadians(ang));
							double dz = z+0.5+r*Math.sin(Math.toRadians(ang));
							float s = (float)((1+3-r/8)*2)/2F;
							int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(ang));
							int l = 120+rand.nextInt(120);
							SpiralMotionController m = new SpiralMotionController(x+0.5, z+0.5, 4-i/4D, (0.25*(1+i/4D))/4D, r, 0.0625*2/*+((a/120)%1D)*0.3*/, ang);
							ColorController clr = new AngleColorController(x+0.5, z+0.5, 5-i/4D, (0.25*(1+i/4D))/4D, r, 0.0625, ang);
							EntityFX fx = new EntityCCBlurFX(world, dx, dy, dz).setScale(s).setColor(c).setLife(l).setRapidExpand().setMotionController(m).setPositionController(m).setColorController(clr);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					break;
				}
				case PINWHEEL: {
					double dy = y+0.5-4;
					for (double a = 0; a < 360; a += 24) {
						for (double r = 3; r <= 15; r += 0.25) {
							double ang = a-r*6;
							double dx = x+0.5+r*Math.cos(Math.toRadians(ang));
							double dz = z+0.5+r*Math.sin(Math.toRadians(ang));
							float s = (float)(7.5-r/2.5);
							int l = 60;
							SpiralMotionController m = new SpiralMotionController(x+0.5, z+0.5, r/3, 0, r, 0, ang);
							EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz);
							fx.setScale(s).setLife(l).setAlphaFading();
							fx.setMotionController(m).setPositionController(m);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					break;
				}
				case VORTEXGROW: {
					break;
				}
			}
		}
	}

}
