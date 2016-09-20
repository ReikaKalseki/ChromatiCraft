package Reika.ChromatiCraft.TileEntity.Transport;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerTimer;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.PlayerTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityTeleportGate extends CrystalReceiverBase implements LocationCached, OwnedTile, GuiController, ChunkLoadingTile {

	private static final ElementTagCompound required = new ElementTagCompound();
	private static final HashSet<GateData> cache = new HashSet();

	private boolean hasStructure = true;
	public boolean publicMode = true;
	private Directionality direction = Directionality.BOTH;

	private int activationTick;
	private TileEntityTeleportGate teleportEnd;

	private final PlayerTimer cooldowns = new PlayerTimer();

	public static final int PUBLIC_COLOR = 0x429aff;//0x22aaff;
	public static final int PRIVATE_COLOR = 0xB820FF;//0xB200FF;

	public static final int EXODUS_COLOR = 0xE02739;//0xff4040;
	public static final int EMANCIPATION_COLOR = 0xFFD84F;

	public static final int ACTIVATION_DURATION = 100;

	private static final ArrayList<Orbit> activationOrbits = new ArrayList();

	private static final HashMap<WorldLocation, BufferedImage> imageCache = new HashMap();

	private static final String LOGGER_ID = "telegate";

	static {
		required.addValueToColor(CrystalElement.LIME, 5000);
		required.addValueToColor(CrystalElement.BLACK, 2000);
		required.addValueToColor(CrystalElement.WHITE, 1000);
		required.addValueToColor(CrystalElement.LIGHTBLUE, 1000);
		required.addValueToColor(CrystalElement.PURPLE, 500);

		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_ID);

		double r = 7.5;
		for (int i = -30; i <= 30; i += 15) {
			for (double w = 0; w < 360; w += 90) {
				activationOrbits.add(new Orbit(r, 0, i+90, 0, w, w));
			}
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.hasStructure() && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (this.hasStructure() && world.isRemote) {
			this.doParticles(world, x, y, z);
		}

		if (activationTick > 0) {
			if (this.hasStructure()) {
				activationTick--;
				this.onActivatingTick(world, x, y, z);
				if (activationTick == 0) {
					this.onActivateComplete();
				}
			}
			else {
				activationTick = 0;
				ChromaSounds.POWERDOWN.playSoundAtBlock(this, 2, 0.5F);
				ChromaSounds.POWERDOWN.playSoundAtBlock(this, 2, 1);
				ChromaSounds.POWERDOWN.playSoundAtBlock(this, 2, 2);
			}
		}

		if (this.hasStructure())
			this.doGuiChecks(world, x, y, z);
		else
			cooldowns.clear();
	}

	private void tryCreateSnapshot() {
		File f = new File(new File(this.getPreviewFolder(), "screenshots"), this.getPreviewFilename(new WorldLocation(this)));
		if (f.exists())
			return;
		this.takeSnapshot();
	}

	@SideOnly(Side.CLIENT)
	public void takeSnapshot() {
		Minecraft mc = Minecraft.getMinecraft();
		boolean flag = mc.gameSettings.hideGUI;
		mc.gameSettings.hideGUI = true;
		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new TakeImage(new WorldLocation(this), flag)), 2);
	}

	@SideOnly(Side.CLIENT)
	public static String getTextureID(WorldLocation loc) {
		return "telegate "+loc.toString();
	}

	private static class TakeImage implements ScheduledEvent {

		private final WorldLocation loc;
		private final boolean unhideGUI;

		public TakeImage(WorldLocation loc, boolean flag) {
			this.loc = loc;
			unhideGUI = flag;
		}

		@Override
		public void fire() {
			Minecraft mc = Minecraft.getMinecraft();
			File dir = getPreviewFolder();
			File f = new File(new File(getPreviewFolder(), "screenshots"), getPreviewFilename(loc));
			if (f.exists())
				f.delete();
			f.getParentFile().mkdirs();
			ScreenShotHelper.saveScreenshot(dir, getPreviewFilename(loc), mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
			mc.gameSettings.hideGUI = unhideGUI;
			imageCache.remove(loc);
			ReikaTextureHelper.resetTexture(null, getTextureID(loc));
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.CLIENT;
		}

	}

	@SideOnly(Side.CLIENT)
	private static File getPreviewFolder() {
		return new File(DragonAPICore.getMinecraftDirectoryString(), "mods/Reika/ChromatiCraft/GateShots");
	}

	@SideOnly(Side.CLIENT)
	private static String getPreviewFilename(WorldLocation loc) {
		return loc.toString().replaceAll(" ", "_")+".png";
	}

	@SideOnly(Side.CLIENT)
	public static BufferedImage getPreview(WorldLocation loc) {
		BufferedImage img = imageCache.get(loc);
		if (img == null) {
			File f = new File(new File(getPreviewFolder(), "screenshots"), getPreviewFilename(loc));
			if (!f.exists())
				return null;
			try {
				img = ImageIO.read(f);
				imageCache.put(loc, img);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReikaChatHelper.write("Could not load preview image: "+e.toString());
				return null;
			}
		}
		return img;
	}

	private void checkAndRequest() {
		for (CrystalElement e : required.elementSet()) {
			int amt = this.getRemainingSpace(e);
			if (amt > 0) {
				boolean ret = this.requestEnergy(e, amt);
			}
		}
	}

	public int getActivationTick() {
		return activationTick;
	}

	private void doGuiChecks(World world, int x, int y, int z) {
		cooldowns.tick(world);
		if (this.getTicksExisted()%8 == 0) {
			AxisAlignedBB gui = ReikaAABBHelper.getBlockAABB(this).contract(0.25, 0, 0.25);
			List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, gui);
			for (EntityPlayer ep : li) {
				if (ep.onGround && (publicMode || this.isOwnedByPlayer(ep))) {
					if (!cooldowns.containsKey(ep)) {
						this.openGui(world, x, y, z, ep);
					}
					cooldowns.put(ep, 40);
				}
			}
		}
	}

	private void openGui(World world, int x, int y, int z, EntityPlayer ep) {
		if (world.isRemote) {
			this.tryCreateSnapshot();
		}
		else {
			this.updateCache();
			ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.GATECACHE.ordinal(), getCacheAsNBT(), new PlayerTarget((EntityPlayerMP)ep));
			ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		}
	}

	private static void updateCache() {
		HashSet<GateData> set = new HashSet();
		for (GateData data : cache) {
			TileEntity te = data.location.getTileEntity();
			if (te instanceof TileEntityTeleportGate) {
				GateData repl = new GateData((TileEntityTeleportGate)te);
				set.add(repl);
			}
		}
		cache.clear();
		cache.addAll(set);
	}

	public static NBTTagCompound getCacheAsNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList li = new NBTTagList();
		for (GateData dat : cache) {
			li.appendTag(dat.writeToNBT());
		}
		tag.setTag("data", li);
		return tag;
	}

	public static void loadCacheFromNBT(NBTTagCompound NBT) {
		cache.clear();
		NBTTagList li = NBT.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			GateData dat = GateData.readFromNBT((NBTTagCompound)o);
			cache.add(dat);
		}
	}

	public static Collection<GateData> getCache() {
		return Collections.unmodifiableCollection(cache);
	}

	private void onActivatingTick(World world, int x, int y, int z) {
		float f = (float)(0.5+1.5*(ACTIVATION_DURATION-activationTick)/ACTIVATION_DURATION);
		ChromaSounds.KILLAURA_CHARGE.playSoundAtBlock(this, 1, f);
		if (world.isRemote)
			this.doActivatingParticles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void doActivatingParticles(World world, int x, int y, int z) {
		int c = this.getRenderColor();
		int i = 0;
		double f = Math.min(1, (0.5+1D*(ACTIVATION_DURATION-activationTick)/ACTIVATION_DURATION));
		int l = (int)(30/4D*Math.pow(f, 2));
		double tt = (this.getTicksExisted()/8D);
		if (f < 1)
			tt = rand.nextDouble()*100000;
		for (Orbit o : activationOrbits) {
			double dt = (tt+i*10D+((i%2)/2D))*48;
			for (double t = dt; t <= dt+0.75*8; t += 0.5) {
				float s = 2.75F*(float)f;//*(float)(1-(t-dt)/4D);
				DecimalPosition p = o.getPosition(x+0.5, y+0.5+1.5, z+0.5, t);
				EntityFX fx = new EntityBlurFX(world, p.xCoord, p.yCoord, p.zCoord).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			for (double t = dt; t <= dt+0.75*8; t += 0.5) {
				float s = 2.75F;//*(float)(1-(t-dt)/4D);
				DecimalPosition p = o.getPosition(x+0.5, y+0.5+1.5, z+0.5, -t+200);
				EntityFX fx = new EntityBlurFX(world, p.xCoord, p.yCoord, p.zCoord).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			i++;
		}

		if (activationTick <= 15 && activationTick%5 == 0) {
			int s = 1+activationTick/5;
			int n = s*50;
			double r = 1+s;
			double py = y+0.5+(4-s);
			l = 10;
			float sc = 0.75F+s/3F;
			for (i = 0; i < n; i++) {
				double ang = rand.nextDouble()*360;
				double px = x+0.5+r*Math.cos(ang);
				double pz = z+0.5+r*Math.sin(ang);
				EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setScale(sc).setLife(l).setAlphaFading();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		if (!world.isRemote)
			cache.add(new GateData(this));
		this.validateStructure();
		if (hasStructure)
			ChunkManager.instance.loadChunks(this);
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		ChunkManager.instance.unloadChunks(this);
	}

	public void validateStructure() {
		hasStructure = !worldObj.isRemote && ChromaStructures.getGateStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	private static boolean canTeleport(WorldLocation loc1, WorldLocation loc2, EntityPlayer caller) {
		TileEntity te1 = loc1.getTileEntity();
		TileEntity te2 = loc2.getTileEntity();
		ModularLogger.instance.log(LOGGER_ID, "Attempting teleport from "+loc1+" to "+loc2+" of "+caller.getCommandSenderName());
		if (te1 instanceof TileEntityTeleportGate && te2 instanceof TileEntityTeleportGate) {
			TileEntityTeleportGate tg1 = (TileEntityTeleportGate)te1;
			TileEntityTeleportGate tg2 = (TileEntityTeleportGate)te2;
			ModularLogger.instance.log(LOGGER_ID, "Both tiles exist");
			if (tg1.hasStructure() && tg2.hasStructure()) {
				ModularLogger.instance.log(LOGGER_ID, "Both tiles have structures");
				if (tg1.direction.canSend && tg2.direction.canReceive) {
					ModularLogger.instance.log(LOGGER_ID, "Directions permit");
					if (tg1.canPlayerUse(caller) && tg2.canPlayerUse(caller)) {
						ModularLogger.instance.log(LOGGER_ID, "Both tiles can be used");
						ElementTagCompound cost = getCost(tg1, tg2);
						ModularLogger.instance.log(LOGGER_ID, "Cost is "+cost+", has1: "+tg1.energy.containsAtLeast(cost)+", has2: "+tg2.energy.containsAtLeast(cost));
						return tg1.energy.containsAtLeast(cost) || tg2.energy.containsAtLeast(cost);
					}
				}
			}
		}
		return false;
	}

	public boolean canPlayerUse(EntityPlayer ep) {
		return publicMode || this.isOwnedByPlayer(ep);
	}

	public static void startTriggerTeleport(WorldLocation loc1, WorldLocation loc2, EntityPlayerMP ep) {
		if (canTeleport(loc1, loc2, ep)) {
			TileEntityTeleportGate te1 = (TileEntityTeleportGate)loc1.getTileEntity();
			TileEntityTeleportGate te2 = (TileEntityTeleportGate)loc2.getTileEntity();
			te1.teleportEnd = te2;
			te2.teleportEnd = te1;
			te1.startActivate();
			ep.rotationPitch = 0;
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TELEPORTCONFIRM.ordinal(), ep, 1);
		}
		else
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TELEPORTCONFIRM.ordinal(), ep, 0);
	}

	private static void triggerTeleport(TileEntityTeleportGate te1, TileEntityTeleportGate te2) {
		if (!te1.worldObj.isRemote) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te1).expand(1.5, 1.5, 1.5).offset(0, 1, 0);
			List<Entity> li = te1.worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity e : li) {
				teleport(te1, te2, e);
			}
		}
	}

	private static void teleport(TileEntityTeleportGate te1, TileEntityTeleportGate te2, Entity e) {
		double dx = e.posX-te1.xCoord-0.5;
		double dy = e.posY-te1.yCoord-0.5;
		double dz = e.posZ-te1.zCoord-0.5;
		if (e instanceof EntityPlayer)
			te2.cooldowns.put((EntityPlayer)e, 80);
		if (te1.worldObj.provider.dimensionId != te2.worldObj.provider.dimensionId) {
			ReikaEntityHelper.transferEntityToDimension(e, te2.worldObj.provider.dimensionId, new GateTeleporter(te2, dx, dy, dz));
		}
		else {
			if (e instanceof EntityLivingBase) {
				((EntityLivingBase)e).setPositionAndUpdate(te2.xCoord+0.5+dx, te2.yCoord+0.5+dy, te2.zCoord+0.5+dz);
			}
			else {
				e.setLocationAndAngles(te2.xCoord+0.5, te2.yCoord+0.5, te2.zCoord+0.5, e.rotationYaw, e.rotationPitch);
				e.lastTickPosX = e.posX;
				e.lastTickPosY = e.posY;
				e.lastTickPosZ = e.posZ;
			}
		}
	}

	private static ElementTagCompound getCost(TileEntityTeleportGate te1, TileEntityTeleportGate te2) {
		double d = Math.sqrt(te1.getDistanceSqTo(te2.xCoord, te2.yCoord, te2.zCoord));
		if (te1.worldObj.provider.dimensionId != te2.worldObj.provider.dimensionId)
			d = Math.pow(d, 1.25)*1.125;
		double f = MathHelper.clamp_double(Math.pow(1.125, 1+d/1024D), 1, 10);
		return required.copy().scale((float)f);
	}

	private void startActivate() {
		activationTick = ACTIVATION_DURATION;
		this.syncAllData(false);
	}

	private void onActivateComplete() {
		if (!worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.ACTIVEGATE.ordinal(), this, 96);
		}

		ChromaSounds.RIFT.playSoundAtBlock(this, 1.5F, 0.5F);
		ChromaSounds.RIFT.playSoundAtBlock(this, 1.5F, 1F);
		ChromaSounds.RIFT.playSoundAtBlock(this, 1.5F, 2F);

		if (teleportEnd != null) {
			ChromaSounds.RIFT.playSoundAtBlock(teleportEnd, 1.5F, 0.5F);
			ChromaSounds.RIFT.playSoundAtBlock(teleportEnd, 1.5F, 1F);
			ChromaSounds.RIFT.playSoundAtBlock(teleportEnd, 1.5F, 2F);
		}

		ChromaSounds.MONUMENTRAY.playSoundAtBlockNoAttenuation(this, 0.75F, 1, 32);
		ChromaSounds.MONUMENTRAY.playSoundAtBlockNoAttenuation(this, 0.75F, 0.5F, 32);

		this.triggerTeleport(this, teleportEnd);

		if (teleportEnd != null) {
			teleportEnd.teleportEnd = null;
			teleportEnd = null;
		}
	}

	@SideOnly(Side.CLIENT)
	public void activateClientside(World world, int x, int y, int z) {
		for (int i = 0; i < 128; i++) {
			double a = Math.toRadians(rand.nextDouble()*360);
			double r = 5;
			double px = x+0.5+r*Math.cos(a);
			double pz = z+0.5+r*Math.sin(a);
			int l = ReikaRandomHelper.getRandomBetween(40, 180);
			float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 4D);
			double py = y+0.5;
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.25);
			double v = -ReikaRandomHelper.getRandomBetween(0.125, 0.625)/3D;
			double dx = (px-x-0.5);
			double dz = (pz-z-0.5);
			double dd = ReikaMathLibrary.py3d(dx, 0, dz);
			double vx = dx*v/dd;
			double vz = dz*v/dd;
			EntityFX fx = new EntityBlurFX(world, px, py, pz, vx, 0, vz).setColor(this.getRenderColor()).setRapidExpand().setScale(s).setLife(l).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		float pf = 1;
		double dd = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
		if (dd >= 16384) {
			pf = 0.125F;
		}
		else if (dd >= 4096) {
			pf = 0.25F;
		}
		else if (dd >= 1024) {
			pf = 0.5F;
		}
		else if (dd >= 256) {
			pf = 0.75F;
		}

		double dt = this.getTicksExisted();
		double r = 3.25;
		int l = 30;
		int c = this.getRenderColor();

		for (double t = dt; t <= dt+0.75; t += 0.25) {
			if (ReikaRandomHelper.doWithChance(pf)) {
				float s = 2.75F/MathHelper.sqrt_float(pf)*(float)(1-(t-dt)/4D);
				double ang1 = t/12D;
				double px = x+0.5+r*Math.cos(ang1);
				double pz = z+0.5+r*Math.sin(ang1);
				double py = y+0.5+1.5+0.5*Math.sin(t/32D);
				EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);

				py += 0.375;

				px = x+0.5+r*Math.cos(-ang1);
				pz = z+0.5+r*Math.sin(-ang1);
				fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);

				double ang2 = ang1+Math.toRadians(180);
				py = y+0.5+1.5+0.5*Math.sin((t+16)/32D);

				px = x+0.5+r*Math.cos(ang2);
				pz = z+0.5+r*Math.sin(ang2);
				fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);

				py += 0.375;

				px = x+0.5+r*Math.cos(-ang2);
				pz = z+0.5+r*Math.sin(-ang2);
				fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);

				for (double ang4 = ang2; ang4 <= ang2+Math.PI; ang4 += Math.PI) {
					px = x+0.5+0.5*Math.sin(t/32D);
					py = y+0.5+r*Math.cos(-ang4);
					pz = z+0.5+r*Math.sin(-ang4);
					if (py >= y-0.5) {
						fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}

					px = x+0.5+0.5*Math.sin((t+16)/32D);
					py = y+0.5+r*Math.cos(ang4);
					pz = z+0.5+r*Math.sin(ang4);
					if (py >= y-0.5) {
						fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}

				for (double ang4 = ang2; ang4 <= ang2+Math.PI; ang4 += Math.PI) {
					pz = z+0.5+0.5*Math.sin(t/32D);
					py = y+0.5+r*Math.cos(-ang4+Math.PI/2);
					px = x+0.5+r*Math.sin(-ang4+Math.PI/2);
					if (py >= y-0.5) {
						fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}

					pz = z+0.5+0.5*Math.sin((t+16)/32D);
					py = y+0.5+r*Math.cos(ang4+Math.PI/2);
					px = x+0.5+r*Math.sin(ang4+Math.PI/2);
					if (py >= y-0.5) {
						fx = new EntityBlurFX(world, px, py, pz).setColor(c).setRapidExpand().setScale(s).setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
			}
		}

		float s = 2.75F/MathHelper.sqrt_float(pf);

		int n = 1+(int)(rand.nextInt(4)*pf);
		for (int i = 0; i < n; i++) {
			double ang3 = rand.nextDouble()*360;
			r = 3.125;
			double px = x+0.5+r*Math.cos(ang3);
			double pz = z+0.5+r*Math.sin(ang3);
			double py = y;
			double vy = ReikaRandomHelper.getRandomBetween(0D, 0.0625);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125/2, 0.125);
			EntityFX fx = new EntityBlurFX(world, px, py, pz, 0, vy, 0).setColor(c).setRapidExpand().setScale(s).setLife(l).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		double ty = y+3.25;
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1.75);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1.75);
		double py = ReikaRandomHelper.getRandomPlusMinus(ty+1.5, 0.75);

		CollectingPositionController p = new CollectingPositionController(px, py, pz, x+0.5, ty, z+0.5, l);
		EntityFX fx = new EntityBlurFX(world, px, py, pz).setPositionController(p).setColor(c).setLife(l);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int maxThroughput() {
		return 240;
	}

	@Override
	public boolean canConduct() {
		return hasStructure;
	}

	public boolean hasStructure() {
		return hasStructure;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 180000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TELEPORT;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.hasStructure() ? ReikaAABBHelper.getBlockAABB(this).expand(8, 8, 8) : super.getRenderBoundingBox();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");
		publicMode = NBT.getBoolean("public");

		activationTick = NBT.getInteger("active");

		direction = Directionality.list[NBT.getInteger("senddir")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);
		NBT.setBoolean("public", publicMode);

		NBT.setInteger("active", activationTick);

		NBT.setInteger("senddir", direction.ordinal());
	}

	public boolean isPowered() {
		return energy.containsAtLeast(required);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, 1);
	}

	public int getRenderColor() {
		switch(direction) {
			case BOTH:
			default:
				return publicMode ? PUBLIC_COLOR : PRIVATE_COLOR;
			case SEND:
				return EMANCIPATION_COLOR;
			case RECEIVE:
				return EXODUS_COLOR;
		}
	}

	public void incrementDirection() {
		direction = direction.next();
	}

	private static class GateTeleporter extends Teleporter {

		private final WorldLocation location;

		private final double relativeX;
		private final double relativeY;
		private final double relativeZ;

		public GateTeleporter(TileEntity te, double dx, double dy, double dz) {
			super((WorldServer)te.worldObj);
			location = new WorldLocation(te);
			relativeX = dx;
			relativeY = dy;
			relativeZ = dz;
		}

		@Override
		public void placeInPortal(Entity e, double x, double y, double z, float facing) {
			e.setLocationAndAngles(location.xCoord+0.5+relativeX, location.yCoord+0.5+relativeY, location.zCoord+0.5+relativeZ, e.rotationYaw, e.rotationPitch);
			this.placeInExistingPortal(e, x, y, z, facing);
		}

		@Override
		public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float facing) {
			return true;
		}

		private void makeReturnPortal(World world, int x, int y, int z) {

		}

		@Override
		public boolean makePortal(Entity e) {
			return false;
		}

	}

	public static class GateData {

		public final WorldLocation location;
		public final int statusFlags;
		private final Collection<UUID> owners = new HashSet();
		public final boolean publicMode;

		private GateData(TileEntityTeleportGate te) {
			this(new WorldLocation(te), getFlags(te), te.owners, te.publicMode);
		}

		private static int getFlags(TileEntityTeleportGate te) {
			int ret = 0;
			if (te.hasStructure())
				ret |= Statuses.STRUCTURE.flag;
			if (te.isPowered())
				ret |= Statuses.POWERED.flag;
			return ret;
		}

		private GateData(WorldLocation loc, int flags, Collection<UUID> c, boolean pb) {
			location = loc;
			statusFlags = flags;
			owners.addAll(c);
			publicMode = pb;
		}

		private static GateData readFromNBT(NBTTagCompound tag) {
			WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
			HashSet<UUID> set = new HashSet();
			NBTTagList li = tag.getTagList("owners", NBTTypes.STRING.ID);
			for (Object o : li.tagList) {
				NBTTagString s = (NBTTagString)o;
				set.add(UUID.fromString(s.func_150285_a_()));
			}
			return new GateData(loc, tag.getInteger("flags"), set, tag.getBoolean("public"));
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			location.writeToNBT("loc", tag);
			tag.setInteger("flags", statusFlags);
			NBTTagList li = new NBTTagList();
			for (UUID uid : owners) {
				li.appendTag(new NBTTagString(uid.toString()));
			}
			tag.setTag("owners", li);
			tag.setBoolean("public", publicMode);
			return tag;
		}

		public boolean isOwnedBy(EntityPlayer ep) {
			return publicMode || owners.contains(ep.getUniqueID());
		}

		@Override
		public String toString() {
			return location+" ["+Integer.toBinaryString(statusFlags)+"]";
		}
	}

	public static enum Statuses {

		OWNED(0xff0000),
		STRUCTURE(0xff22ff),
		POWERED(0xffff00),
		DIMENSION(0x22aaff);

		public final int color;
		public final int flag;

		public static Statuses[] list = values();

		private Statuses(int c) {
			color = c;
			flag = 1 << this.ordinal();
		}

		public boolean check(int flags) {
			return (flags & flag) != 0 == (this != DIMENSION);
		}

	}

	private static enum Directionality {

		BOTH(true, true),
		SEND(true, false),
		RECEIVE(false, true);

		private final boolean canSend;
		private final boolean canReceive;

		private static final Directionality[] list = values();

		private Directionality(boolean s, boolean r) {
			canSend = s;
			canReceive = r;
		}

		private Directionality next() {
			return list[(this.ordinal()+1)%list.length];
		}

	}

}
