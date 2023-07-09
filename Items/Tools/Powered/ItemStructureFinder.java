/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Powered;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Magic.ToolChargingSystem;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator.StructureSeekData;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemStructureFinder extends ItemPoweredChromaTool {

	private static final String NBT_TAG = "pingActivation";
	private static final int RANGE = 1024;
	private static final int FUZZ = 96;
	private static final int CAPACITY = 48000;
	private static final int PING_COST = CAPACITY/24;
	private static final EnumMap<ChromaStructures, Integer> COLORS = new EnumMap(ChromaStructures.class);
	private static final Interpolation ANGLE_FUZZ = new Interpolation(false);

	static {
		COLORS.put(ChromaStructures.BURROW, 0x79B783);
		COLORS.put(ChromaStructures.CAVERN, 0x8FA3B5);
		COLORS.put(ChromaStructures.OCEAN, 0x60C4C4);
		COLORS.put(ChromaStructures.DESERT, 0xBF655D);
		COLORS.put(ChromaStructures.SNOWSTRUCT, 0xf050c0);
		COLORS.put(ChromaStructures.BIOMEFRAG, 0x22aaff);

		ANGLE_FUZZ.addPoint(0, 180);
		ANGLE_FUZZ.addPoint(FUZZ, 90);
		ANGLE_FUZZ.addPoint(FUZZ*2, 60);
		ANGLE_FUZZ.addPoint(1800, 0);
	}

	private static final Random rand = new Random();

	public ItemStructureFinder(int index) {
		super(index);
	}

	private double getSightRangeModifier(ChromaStructures s) {
		switch(s) {
			case OCEAN:
			case BIOMEFRAG:
				return 2;
			default:
				return 1;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doTickFX(EntityPlayer ep, ChromaStructures s) {
		ReikaSoundHelper.playClientSound(ChromaSounds.BOUNCE, ep, 1, 2);
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.0625, -ep.rotationPitch, ep.rotationYawHead+90+60);
		double px = ep.posX+xyz[0];
		double py = ep.posY+xyz[1];
		double pz = ep.posZ+xyz[2];
		int c = getColor(s);
		EntityCCBlurFX fx = new EntityCCBlurFX(ep.worldObj, px, py, pz);
		float sc = 0.125F+rand.nextFloat()*0.25F;
		fx.setIcon(ChromaIcons.FADE_RAY);
		double d = rand.nextDouble()*360;
		MathExpression e = new PeriodicExpression().addWave(0.5, 16, d).normalize();
		fx.setColorController(new FlashColorController(e, c, 0xffffff));
		fx.motionX *= 0.375;
		fx.motionY *= 0.375;
		fx.motionZ *= 0.375;
		fx.setLife(30).setScale(sc).setColor(c);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	public static void doTriggerFX(EntityPlayer ep, ChromaStructures s, int sx, int sz, boolean close, boolean genned) {
		double dist = ReikaMathLibrary.py3d(ep.posX-sx-0.5, 0, ep.posZ-sz-0.5);
		double da = ANGLE_FUZZ.getValue(dist);
		double ang = Math.toDegrees(Math.atan2(sz+0.5-ep.posZ, sx+0.5-ep.posX));
		ang = ReikaRandomHelper.getRandomPlusMinus(ang, da/2D);
		double r1 = 1;
		double r2 = 6;
		double rt = ReikaMathLibrary.linterpolate(dist, FUZZ, 900, r2, r1, true);
		int c = getColor(s);
		float sc = 1.5F+rand.nextFloat()*1.5F;
		int l = 120;
		if (!genned)
			c = ReikaColorAPI.mixColors(c, 0xffffff, 0.5F);
		//ReikaJavaLibrary.pConsole(s+">"+sx+","+sz+"/"+close+"+"+genned+">"+ang+"$"+da+"*"+rt);
		for (double a = -da; a <= da; a += 1) {
			float f = 1F-(float)Math.pow(Math.abs(a/da), 4)*0.999F;
			//ReikaJavaLibrary.pConsole(a);
			double rad = Math.toRadians(a+ang);
			double dx = Math.cos(rad);
			double dz = Math.sin(rad);
			for (double r = r1; r <= r2; r += 0.25) {
				double px = r*dx+ep.posX;
				double pz = r*dz+ep.posZ;
				EntityCCBlurFX fx = new EntityCCBlurFX(ep.worldObj, px, ep.posY-1.62, pz).setIcon(ChromaIcons.FADE_BASICBLEND);
				int c2 = c;
				if (r > rt)
					c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F);
				fx.setLife(l).setScale(sc).setColor(c2).setAlphaFading(f*0.2F).setRapidExpand().forceIgnoreLimits();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	public static int getColor(ChromaStructures s) {
		return COLORS.get(s);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		super.addInformation(is, ep, li, vb);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote && world.provider.dimensionId != ExtraChromaIDs.DIMID.getValue() && ToolChargingSystem.instance.getCharge(is) >= PING_COST) {
			EnumMap<ChromaStructures, StructureSeekData> findData = new EnumMap(ChromaStructures.class);
			boolean debug = ReikaObfuscationHelper.isDeObfEnvironment() && ep.isSneaking() && ReikaPlayerAPI.isReika(ep);
			for (Entry<ChromaStructures, Integer> e : COLORS.entrySet()) {
				ChromaStructures s = e.getKey();
				double r = RANGE*this.getSightRangeModifier(s);
				if (debug)
					r = 5000;
				StructureSeekData loc = DungeonGenerator.instance.getNearestRealStructure(s, (WorldServer)world, ep.posX, ep.posZ, r, false);
				if (loc != null)
					findData.put(s, loc);
			} //collect then send in case of lag, so that all packets get sent at once
			for (Entry<ChromaStructures, StructureSeekData> e : findData.entrySet()) {
				ChromaStructures s = e.getKey();
				StructureSeekData loc = e.getValue();
				double dist = loc.location.getDistanceTo(ep);
				int fz = debug ? 0 : FUZZ;
				boolean flag = dist <= fz;
				int x = loc.location.xCoord;
				int z = loc.location.zCoord;
				if (flag) {
					x = ReikaRandomHelper.getRandomPlusMinus(MathHelper.floor_double(ep.posX), fz);
					z = ReikaRandomHelper.getRandomPlusMinus(MathHelper.floor_double(ep.posZ), fz);
				}
				if (debug) {
					ReikaJavaLibrary.pConsole(s+">"+loc.location+"/"+loc.isKnownSuccess+"&"+flag);
				}
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTFIND.ordinal(), new PacketTarget.RadiusTarget(ep, 32), s.ordinal(), x, z, flag ? 1 : 0, loc.isKnownSuccess ? 1 : 0);
			}
			if (!ep.capabilities.isCreativeMode)
				ToolChargingSystem.instance.removeCharge(is, PING_COST, ep);
			ChromaSounds.NETWORKOPT.playSound(ep, 1, 0.75F);
		}
		return is;
	}

	@Override
	protected boolean doTick(ItemStack is, World world, EntityPlayer ep, boolean held) { //only called if isActivated is true anyway - do not need to recheck
		if (!world.isRemote && world.provider.dimensionId != ExtraChromaIDs.DIMID.getValue()) {
			NBTTagCompound li = is.stackTagCompound.getCompoundTag("points");
			for (Object key : li.func_150296_c()) {
				NBTTagCompound tag = li.getCompoundTag((String)key);
				ChromaStructures s = ChromaStructures.valueOf(tag.getString("struct"));
				WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
				if (loc != null) {
					int dist = (int)loc.getDistanceTo(ep);
					int tick = Math.max(0, dist-20/2)+5;
					//ReikaJavaLibrary.pConsole(loc+">"+dist+">"+tick);
					if (world.getTotalWorldTime()%tick == 0)
						ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTFINDTICK.ordinal(), new PacketTarget.RadiusTarget(ep, 16), s.ordinal());
				}
			}
		}
		return held;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		if (is.stackTagCompound == null || !(is.stackTagCompound.getTag("points") instanceof NBTTagCompound))
			return false;
		NBTTagCompound li = is.stackTagCompound.getCompoundTag("points");
		Iterator<String> it = li.func_150296_c().iterator();
		while (it.hasNext()) {
			NBTTagCompound tag = li.getCompoundTag(it.next());
			WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
			if (loc == null || loc.dimensionID != e.worldObj.provider.dimensionId || e.worldObj.getTotalWorldTime()-tag.getLong(NBT_TAG) >= 20)
				it.remove();
		}
		return !li.hasNoTags();
	}

	public static void activate(ItemStack is, EntityPlayer ep, TileEntityStructControl te) {
		//ReikaJavaLibrary.pConsole(te);
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound li = is.stackTagCompound.getCompoundTag("points");
		WorldLocation loc = new WorldLocation(te);
		NBTTagCompound tag = li.getCompoundTag(loc.toSerialString());
		tag.setLong(NBT_TAG, ep.worldObj.getTotalWorldTime());
		loc.writeToNBT("loc", tag);
		tag.setString("struct", te.getStructureType().name());
		li.setTag(loc.toSerialString(), tag);
		is.stackTagCompound.setTag("points", li);
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.BLACK;
	}

	@Override
	public int getMaxCharge() {
		return CAPACITY;
	}

	@Override
	public int getChargeStates() {
		return 2;
	}

	@Override
	public int getChargeState(float frac) {
		return frac > 0.1 ? 1 : 0;
	}

	@Override
	public int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 1;
	}

}
