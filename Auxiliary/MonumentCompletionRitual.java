/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ColorController;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.TileEntity.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionTicker;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.Structure.MonumentGenerator;
import Reika.DragonAPI.Instantiable.SpiralMotionController;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MonumentCompletionRitual {

	private static final Random rand = new Random();

	private int count;
	private int tick;
	private int totalTick = 0;

	private boolean running;
	private boolean complete;

	private final World world;
	private final int x;
	private final int y;
	private final int z;
	private final EntityPlayer ep;

	private final PacketTarget packetTarget;

	public static final int RITUAL_STEPS = 32;
	public static final int COMPLETION_EXTRA = 10; //5*4t/e = 1s

	private static final int EFFECT_RANGE = 256;

	private static int runningRituals;

	public static boolean areRitualsRunning() {
		return runningRituals > 0;
	}

	public MonumentCompletionRitual(World world, int x, int y, int z, EntityPlayer ep) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ep = ep;

		packetTarget = new PacketTarget.RadiusTarget(world, x+0.5, y+0.5, z+0.5, EFFECT_RANGE);
	}

	private void disableCores(World world, int x, int y, int z) {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			((TileEntityDimensionCore)c.getTileEntity(world)).prime(false);
		}
	}

	public void start() {
		this.disableCores(world, x, y, z);
		tick = 5;
		running = true;
		runningRituals++;
		if (world.isRemote) {
			this.startClient();
		}
	}

	@SideOnly(Side.CLIENT)
	private void startClient() {
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENT, x+0.5, y+0.5, z+0.5, 1, 1630/2112F, false);
		ISound s = ChromaDimensionTicker.instance.getCurrentMusic();
		if (s != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(s);
		}
	}

	public void tick() {
		if (running) {
			if (!world.isRemote) {
				if (tick > 0) {
					tick--;
					if (tick == 0) {
						this.doChecks(true);
					}
				}

				if (!complete) {
					double n = 1+4*(double)(count/RITUAL_STEPS);
					for (int i = 0; i < EventType.list.length; i++) {
						EventType evt = EventType.list[i];
						if (evt.tickChance > 0) {
							if (ReikaRandomHelper.doWithChance(evt.tickChance*Math.pow(n, evt.powerFactor)))
								this.triggerEvent(evt);
						}
					}
				}
			}

			if (world.isRemote) {
				this.manipulateCamera();
			}
			totalTick++;
		}
	}

	@SideOnly(Side.CLIENT)
	private void manipulateCamera() {
		Minecraft.getMinecraft().gameSettings.hideGUI = true;
		Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
		double ang = ((world.getTotalWorldTime()+ep.getUniqueID().hashCode())/2D);
		double angPrev = ((world.getTotalWorldTime()+ep.getUniqueID().hashCode()-1)/2D);
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
		this.setCameraPosition(ep, cx, cy, cz, cxPrev, cyPrev, czPrev, yaw, yawPrev, pitch, pitchPrev, true, true);
	}

	//A big block of hack
	@SideOnly(Side.CLIENT)
	private static void setCameraPosition(EntityPlayer ep, double cx, double cy, double cz, double cxPrev, double cyPrev, double czPrev, double yaw, double yawPrev, double pitch, double pitchPrev, boolean setPos, boolean setAngs) {
		ChromatiCraft.logger.debug("Moving "+ep.getCommandSenderName()+" camera to "+cx+","+cy+","+cz+" @ "+yaw+" / "+pitch);
		RenderManager rm = RenderManager.instance;
		if (setPos) {
			RenderManager.renderPosX = cx;
			RenderManager.renderPosY = cy;
			RenderManager.renderPosZ = cz;
			rm.viewerPosX = cx;
			rm.viewerPosY = cy;
			rm.viewerPosZ = cz;
		}
		if (rm.field_147941_i != null) {
			if (setPos) {
				rm.field_147941_i.posX = cx;
				rm.field_147941_i.posY = cy;
				rm.field_147941_i.posZ = cz;
				rm.field_147941_i.lastTickPosX = cxPrev;
				rm.field_147941_i.lastTickPosY = cyPrev;
				rm.field_147941_i.lastTickPosZ = czPrev;
				rm.field_147941_i.prevPosX = cxPrev;
				rm.field_147941_i.prevPosY = cyPrev;
				rm.field_147941_i.prevPosZ = czPrev;
			}
			rm.cacheActiveRenderInfo(rm.worldObj, rm.renderEngine, Minecraft.getMinecraft().fontRenderer, ep, rm.field_147941_i, rm.options, 0);
		}
		if (setPos) {
			TileEntityRendererDispatcher.staticPlayerX = cx;
			TileEntityRendererDispatcher.staticPlayerY = cy;
			TileEntityRendererDispatcher.staticPlayerZ = cz;
		}
		EntityPlayer mcp = Minecraft.getMinecraft().thePlayer;
		EntityLivingBase mcp2 = Minecraft.getMinecraft().renderViewEntity;
		if (setPos) {
			mcp.posX = cx;
			mcp.posY = cy;
			mcp.posZ = cz;
			mcp.lastTickPosX = cxPrev;
			mcp.lastTickPosY = cyPrev;
			mcp.lastTickPosZ = czPrev;
			mcp.prevPosX = cxPrev;
			mcp.prevPosY = cyPrev;
			mcp.prevPosZ = czPrev;

			mcp2.posX = cx;
			mcp2.posY = cy;
			mcp2.posZ = cz;
			mcp2.lastTickPosX = cxPrev;
			mcp2.lastTickPosY = cyPrev;
			mcp2.lastTickPosZ = czPrev;
			mcp2.prevPosX = cxPrev;
			mcp2.prevPosY = cyPrev;
			mcp2.prevPosZ = czPrev;
		}
		if (setAngs) {
			mcp.rotationYawHead = (float)yaw;
			mcp.rotationYaw = (float)yaw;
			mcp.prevRotationYaw = (float)yawPrev;
			mcp.prevRotationYawHead = (float)yawPrev;
			mcp.cameraYaw = (float)yaw;
			mcp.prevCameraYaw = (float)yawPrev;
			mcp.rotationPitch = (float)pitch;
			mcp.prevRotationPitch = (float)pitchPrev;

			mcp2.rotationYawHead = (float)yaw;
			mcp2.rotationYaw = (float)yaw;
			mcp2.prevRotationYaw = (float)yawPrev;
			mcp2.prevRotationYawHead = (float)yawPrev;
			mcp2.rotationPitch = (float)pitch;
			mcp2.prevRotationPitch = (float)pitchPrev;
		}
		Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
	}

	public boolean doChecks(boolean tick) {
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

		if (tick) {
			if (count < RITUAL_STEPS) {
				this.triggerEvent(EventType.FLARES);
			}
			this.scheduleNextCheck();
			count++;
			if (count >= RITUAL_STEPS) {
				this.completeRitual();
			}
		}
		return true;
	}

	private boolean doMineralChecks() {
		if (world.isRemote)
			return true;
		MonumentGenerator gen = ChunkProviderChroma.getMonumentGenerator();
		Map<Coordinate, Block> map = gen.getMineralBlocks();
		for (Coordinate c : map.keySet()) {
			Block b = c.getBlock(world);
			if (b != map.get(c)) {
				//ReikaJavaLibrary.pConsole("@ "+c+": "+map.get(c).getLocalizedName()+", "+b.getLocalizedName());
				return false;
			}
		}
		return true;
	}

	private void completeRitual() {
		if (count == RITUAL_STEPS) {
			complete = true;
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.MONUMENTCOMPLETE.ordinal(), packetTarget, x, y, z);

			double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
			double pitch = 90-angs[1];
			double yaw = -angs[2];
			ep.rotationYaw = ep.rotationYawHead = ep.prevRotationYawHead = ep.prevRotationYaw = (float)yaw;
			ep.rotationPitch = ep.prevRotationPitch = (float)pitch;

		}
		else if (count == RITUAL_STEPS+COMPLETION_EXTRA) {
			count = 0;
			tick = 0;
			world.setBlock(x, y, z, ChromaTiles.AURAPOINT.getBlock(), ChromaTiles.AURAPOINT.getBlockMetadata(), 3);
			TileEntityAuraPoint te = (TileEntityAuraPoint)world.getTileEntity(x, y, z);
			te.setPlacer(ep);
			ProgressStage.CTM.stepPlayerTo(ep);
			if (!world.isRemote)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RESETMONUMENT.ordinal(), packetTarget, x, y, z);
			running = false;
		}
	}

	private void scheduleNextCheck() {
		tick = Math.max(4, 128-count*4); //was 64-
	}

	private void triggerEvent(EventType evt) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.MONUMENTEVENT.ordinal(), packetTarget, x, y, z, evt.ordinal(), count);
	}

	public void endRitual() {
		count = 0;
		tick = 50;
		running = false;
		if (running)
			runningRituals--;
		((TileEntityStructControl)world.getTileEntity(x, y, z)).endMonumentRitual();
		ChromaSounds.ERROR.playSoundAtBlockNoAttenuation(world, x, y, z, 1, 0.75F, Integer.MAX_VALUE);
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
	public static void triggerMonumentEventClient(World world, int x, int y, int z, int type, int count) {
		EventType.list[type].doEventClient(world, x, y, z, count);
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
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, phi, theta).setColor(c).setScale(6).setLife(l);
			fx.particleVelocity *= 3;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
			TileEntityDimensionCore.createBeamLine(world, x, y, z, c.xCoord, c.yCoord, c.zCoord, CrystalElement.WHITE, e);
		}

		//ReikaSoundHelper.playClientSound(ChromaSounds.PYLONTURBO, x+0.5, y+0.5, z+0.5, 1, 2F, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTCOMPLETE, x+0.5, y+0.5, z+0.5, 2, 0.9439F, false);
	}

	@SideOnly(Side.CLIENT)
	public static void resetSettings(World world, int x, int y, int z) {
		Minecraft.getMinecraft().gameSettings.hideGUI = false;
		Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;

		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
		double pitch = 90-angs[1];
		double yaw = -angs[2];
		setCameraPosition(ep, ep.posX, ep.posY, ep.posZ, ep.posX, ep.posY, ep.posZ, yaw, yaw, pitch, pitch, false, true);
	}

	private static enum EventType {
		FLARES(0, 0),
		RAY(12.5, 2),
		//LEYLINE(0, 0),
		PARTICLECLOUD(1.5625, 1),
		//BALLLIGHTNING(0, 0),
		PARTICLERING(4.17, 1.5),
		TWIRL(3.125, 1);

		private static final EventType[] list = values();

		public final double tickChance;
		public final double powerFactor;

		private EventType(double c, double f) {
			tickChance = c;
			powerFactor = f;
		}

		@SideOnly(Side.CLIENT)
		public void doEventClient(World world, int x, int y, int z, int count) {
			switch(this) {
				case FLARES: {
					ReikaSoundHelper.playClientSound(ChromaSounds.USE, x+0.5, y+0.5, z+0.5, 1, 2F, false);

					int n = 32+rand.nextInt(48);
					for (int i = 0; i < n; i++) {
						double phi = rand.nextDouble()*360;
						double theta = rand.nextDouble()*360;
						double v = ReikaRandomHelper.getRandomBetween(0.125, 0.5);
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
						EntityFX fx = new EntityFlareFX(CrystalElement.elements[count%16], world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setScale(6);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case RAY: {
					CrystalElement e = CrystalElement.randomElement();
					float p = (float)CrystalMusicManager.instance.getDingPitchScale(e);
					ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 1, p, false);
					/*
					double ang = rand.nextDouble()*360;
					double dx = Math.cos(Math.toRadians(ang));
					double dz = Math.sin(Math.toRadians(ang));
					int l = 5+rand.nextInt(20);
					for (double r = 0; r < 24; r += 0.25) {
						float s = (float)(12-r/2);
						int c = ReikaColorAPI.mixColors(ReikaColorAPI.getModifiedHue(0xff0000, (int)(ang*360)), 0xffffff, (float)(r/24));
						EntityFX fx = new EntityBlurFX(world, x+0.5+dx*r, y+0.5+(12-Math.abs(r-12))*0.625, z+0.5+dz*r).setScale(s).setColor(c).setLife(l).setRapidExpand();
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					 */
					Coordinate c = TileEntityDimensionCore.getLocation(e).offset(x, y, z);
					DecimalPosition end = new DecimalPosition(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
					LightningBolt b = new LightningBolt(new DecimalPosition(x+0.5, y+0.5, z+0.5), end, 8);
					b.variance = 0.675;
					b.update();
					int l = 5+rand.nextInt(20);
					for (int i = 0; i < b.nsteps; i++) {
						DecimalPosition pos1 = b.getPosition(i);
						DecimalPosition pos2 = b.getPosition(i+1);
						for (double r = 0; r <= 1; r += 0.03125) {
							float s = 5F;
							int clr = e.getColor();
							double dx = pos1.xCoord+r*(pos2.xCoord-pos1.xCoord);
							double dy = pos1.yCoord+r*(pos2.yCoord-pos1.yCoord);
							double dz = pos1.zCoord+r*(pos2.zCoord-pos1.zCoord);
							EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(clr).setLife(l).setRapidExpand();
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
							EntityFX fx2 = new EntityBlurFX(world, dx, dy, dz).setScale(s/2.5F).setColor(0xffffff).setLife(l).setRapidExpand();
							Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
						}
					}
					break;
				}
				case PARTICLECLOUD: {
					ReikaSoundHelper.playClientSound(ChromaSounds.CAST, x+0.5, y+0.5, z+0.5, 1, 0.5F, false);
					int n = 64+rand.nextInt(256);
					for (int i = 0; i < n; i++) {
						double dx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 20);
						double dz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 20);
						double dy = 1+world.getTopSolidOrLiquidBlock(MathHelper.floor_double(dx), MathHelper.floor_double(dz))+rand.nextInt(12);
						int l = 20+rand.nextInt(40);
						float s = 2+rand.nextFloat()*12;
						double hue = ReikaPhysicsHelper.cartesianToPolar(dx-x-0.5, dy-y-0.5, dz-z-0.5)[2];
						int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(hue));
						EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(c).setLife(l).setRapidExpand().setIcon(ChromaIcons.BIGFLARE);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case PARTICLERING: {
					ReikaSoundHelper.playClientSound(ChromaSounds.USE, x+0.5, y+0.5, z+0.5, 1, 0.5F, false);
					double r = 33;
					for (double a = 0; a < 360; a += 1) {
						double dx = x+0.5*r*Math.cos(Math.toRadians(a));
						double dz = z+0.5*r*Math.sin(Math.toRadians(a));
						double dy = y+0.5+6;
						float s = (1+rand.nextFloat())*4;
						int c = CrystalElement.getBlendedColor((int)((a+180-12.25)*2), 45);
						int l = 60+rand.nextInt(40);
						float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
						EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(c).setLife(l).setRapidExpand().setGravity(g).setIcon(ChromaIcons.CENTER);
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
							EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(s).setColor(c).setLife(l).setRapidExpand().setMotionController(m).setPositionController(m).setColorController(clr);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					break;
				}
			}
		}
	}

}
