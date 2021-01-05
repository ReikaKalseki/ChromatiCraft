/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.Render.MouseoverOverlayRenderer;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.LumenRequestingTile;
import Reika.ChromatiCraft.Magic.Interfaces.NotifiedNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.WrapperTile;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper.EffectType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public final class NodeReceiverWrapper implements CrystalReceiver, NotifiedNetworkTile, WrapperTile, LumenRequestingTile {

	private static final int DELAY = 600;
	private static final int MIN_DELAY = 200;

	private static final int NEW_ASPECT_COST = 240000;
	private static final float ASPECT_EXPANSION_COST_PER_VIS = 200;

	private static final ElementTagCompound brightnessCost = new ElementTagCompound();
	private static final ElementTagCompound typeCost = new ElementTagCompound();

	private static final float EFFICIENCY_FACTOR = 0.8F;
	private static final float EFFICIENCY_FACTOR_JAR = 0.6F;

	private static final String LOGGER_ID = "chromanodes";

	private static final Random rand = new Random();

	private final INode node;
	public final WorldLocation location;
	private final UUID uid = UUID.randomUUID();

	private long age = 0;
	private int fulltick = MIN_DELAY;
	private int tick = DELAY;
	private int ticksSinceEnergyInput = 0;

	private Aspect activeAspect;
	private ElementTagCompound currentRequestSet;

	private final WeightedRandom<Aspect> candidateRefillAspects = new WeightedRandom();
	private final WeightedRandom<Aspect> candidateNextAspects = new WeightedRandom();

	private final ElementTagCompound storedEnergy = new ElementTagCompound();

	private NodeImprovementStatus status = NodeImprovementStatus.IDLE;

	private final boolean isJarred;
	private final boolean isClient;

	private boolean needsSync = true;

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_ID);

		brightnessCost.addTag(CrystalElement.BLACK, 40000);
		brightnessCost.addTag(CrystalElement.YELLOW, 10000);
		brightnessCost.addTag(CrystalElement.BLUE, 2000);
		brightnessCost.addTag(CrystalElement.PURPLE, 5000);

		typeCost.addTag(CrystalElement.BLACK, 90000);
		typeCost.addTag(CrystalElement.MAGENTA, 60000);
		typeCost.addTag(CrystalElement.WHITE, 40000);
	}

	NodeReceiverWrapper(INode n) {
		node = n;
		location = new WorldLocation((TileEntity)n);
		isClient = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
		isJarred = n.getClass().getSimpleName().contains("Jar");
		ModularLogger.instance.log(LOGGER_ID, "Node wrapper created for node "+location);
	}

	private int getCurrentValue(CrystalElement e) {
		int val = 0;
		AspectList al = node.getAspects();
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			val += this.getTagValue(a).getValue(e)*amt;
		}
		return val;
	}

	private ElementTagCompound getTagValue(Aspect a) {
		return ChromaAspectManager.instance.getElementCost(a, 1).scale(ASPECT_EXPANSION_COST_PER_VIS);
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return currentRequestSet != null && currentRequestSet.contains(e);
	}

	@Override
	public void cachePosition() {}

	@Override
	public void removeFromCache() {}

	@Override
	public double getDistanceSqTo(double x, double y, double z) {
		return location.getSquaredDistance(x, y, z);
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}

	@Override
	public int getX() {
		return location.xCoord;
	}

	@Override
	public int getY() {
		return location.yCoord;
	}

	@Override
	public int getZ() {
		return location.zCoord;
	}

	@Override
	public int maxThroughput() {
		int base = isJarred ? 40 : 60;
		return this.getAgeModifiedThroughput(base);
	}

	private int getAgeModifiedThroughput(int base) {
		if (age < 24000) //1h
			return base;
		float f = 1+2F*(age-24000)/408000F; //reaches max of 3x at 18h
		return (int)(base*f);
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public UUID getUniqueID() {
		return uid;
	}

	@Override
	public UUID getPlacerUUID() {
		return null;
	}

	@Override
	public int receiveElement(CrystalElement e, int amt) {
		amt = Math.max(1, MathHelper.floor_float(amt*this.getEfficiencyFactor()));
		int add = Math.min(this.getRemainingSpace(e), amt);
		if (add > 0) {
			storedEnergy.addValueToColor(e, add);
			ticksSinceEnergyInput = 0;
			needsSync = true;
		}
		return add;
	}

	public void tick() {
		if (isClient)
			return;
		age++;
		ticksSinceEnergyInput++;

		/*
		if (true) {
			node.getAspectsBase().aspects.clear();
			node.getAspects().aspects.clear();
			node.getAspectsBase().add(Aspect.FIRE, 20);
			node.getAspects().add(Aspect.FIRE, 20);
			node.setNodeModifier(NodeModifier.PALE);
			node.setNodeType(NodeType.UNSTABLE);
			location.triggerBlockUpdate(false);
			return;
		}
		 */

		if (rand.nextInt(240) == 0) {
			this.playSound("thaumcraft:zap");
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CHARGINGNODE.ordinal(), (TileEntity)node, 32);
		}

		if (age < 10)
			return;

		if (status == NodeImprovementStatus.IDLE && rand.nextInt(this.modifyChanceByAge(750)) == 0) {
			this.tryImproveNode();
		}

		if (status == NodeImprovementStatus.BRIGHTENING) {
			if (this.tryImproveNodeBrightness())
				location.triggerBlockUpdate(false);
		}

		if (status == NodeImprovementStatus.HEALING) {
			if (this.tryImproveNodeType())
				location.triggerBlockUpdate(false);
		}

		if (status == NodeImprovementStatus.IDLE && rand.nextInt(this.modifyChanceByAge(200)) == 0) {
			this.startAspectRefill();
		}

		if (status == NodeImprovementStatus.REFILLING && activeAspect != null) {
			this.tryAspectRefill(activeAspect);
		}

		if (status == NodeImprovementStatus.IDLE && rand.nextInt(this.modifyChanceByAge(6000)) == 0) {
			this.startNewAspect();
		}

		if (status == NodeImprovementStatus.NEWASPECT && activeAspect != null) {
			if (this.tryToAddAspect(activeAspect)) {
				ModularLogger.instance.log(LOGGER_ID, "Node "+location+" gained aspect '"+activeAspect.getName()+"'");
				activeAspect = null;
			}
			if (age%48 == 0)
				ChromaSounds.CASTHARMONIC.playSound(this.getWorld(), this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, 0.6F, 0.5F);
		}

		if (candidateNextAspects.isEmpty() && candidateRefillAspects.isEmpty()) {
			if (node.getNodeType() == NodeType.PURE && node.getNodeModifier() == NodeModifier.BRIGHT) {
				boolean flag = true;
				for (Aspect a : this.getCurrentNodeAspects()) {
					if (node.getAspects().getAmount(a) < 720) {
						flag = false;
						break;
					}
				}
				if (flag)
					status = NodeImprovementStatus.COMPLETE;
			}
		}

		needsSync |= age%64 == 0;

		if (needsSync)
			this.sync();
	}

	private void sync() {
		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" sending sync");
		needsSync = false;
		NBTTagCompound tag = new NBTTagCompound();
		location.writeToNBT("location", tag);
		this.write(tag);
		this.writeSync(tag);

		ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.NODERECEIVERSYNC.ordinal(), tag, new PacketTarget.RadiusTarget(location, 64));
	}

	private void writeSync(NBTTagCompound tag) {
		if (currentRequestSet != null) {
			tag.setInteger("requestSet", ReikaArrayHelper.booleanToBitflags(currentRequestSet.flagSet()));
		}
	}

	private void startAspectRefill() {
		this.recalculateRefillAspects();
		if (!candidateRefillAspects.isEmpty()) {
			this.setStatus(NodeImprovementStatus.REFILLING);
			activeAspect = candidateRefillAspects.getRandomEntry();
			this.playSound("thaumcraft:runicShieldCharge", 1, 0.5F);
			this.tryAspectRefill(activeAspect);
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" recharge begin");
		}
	}

	private boolean tryAspectRefill(Aspect a) {
		ElementTagCompound tag = this.getTagValue(a);
		if (storedEnergy.containsAtLeast(tag)) {
			this.doAspectRefill(a, tag);
			return true;
		}
		else {
			int amt = node.getAspectsBase().getAmount(a)-node.getAspects().getAmount(a);
			this.requestEnergy(tag.copy().scale(amt));
			currentRequestSet = tag.copy().scale(1F/Float.MAX_VALUE);
			return false;
		}
	}

	private void doAspectRefill(Aspect a, ElementTagCompound tag) {
		ticksSinceEnergyInput = Math.max(ticksSinceEnergyInput, 200);
		if (node.getAspectsBase().getAmount(a) > node.getAspects().getAmount(a)) {
			storedEnergy.subtract(tag);
			node.addToContainer(a, 1);
			location.triggerBlockUpdate(false);
		}
		if (node.getAspects().getAmount(a) >= node.getAspectsBase().getAmount(a)) {
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" recharge finish "+activeAspect.getName());
			activeAspect = null;
			this.setStatus(NodeImprovementStatus.IDLE);
		}
	}

	private void startNewAspect() {
		this.recalculateCandidateAspects();
		if (!candidateNextAspects.isEmpty()) {
			this.setStatus(NodeImprovementStatus.NEWASPECT);
			activeAspect = candidateNextAspects.getRandomEntry();
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" attempting to gain aspect '"+activeAspect.getName()+"'");
		}
	}

	private float getEfficiencyFactor() {
		return isJarred ? EFFICIENCY_FACTOR_JAR : EFFICIENCY_FACTOR;
	}

	private int modifyChanceByAge(int base) {
		long t = age;
		if (isJarred)
			t *= 0.75;
		return (int)Math.max(5, Math.max(base/10, base/(1+t/1.2F*ChromaOptions.getNodeGrowthSpeed()/(double)base)));
	}

	private boolean tryToAddAspect(Aspect a) {
		if (currentRequestSet == null) {
			currentRequestSet = this.getTagValue(a);
		}
		boolean flag = true;
		for (CrystalElement e : currentRequestSet.elementSet()) {
			flag &= storedEnergy.getValue(e) >= NEW_ASPECT_COST;
		}
		if (flag) {
			this.doAddAspect(a);
			return true;
		}
		else {
			this.requestForNewAspect(a, currentRequestSet);
			return false;
		}
	}

	private int getRemainingSpace(CrystalElement e) {
		return this.getMaxStorage(e)-storedEnergy.getValue(e);
	}

	private void doAddAspect(Aspect a) {
		for (CrystalElement e : currentRequestSet.elementSet()) {
			storedEnergy.subtract(e, NEW_ASPECT_COST);
		}
		currentRequestSet = null;
		node.getAspectsBase().add(a, 1);
		node.addToContainer(a, 1);
		this.playSound("thaumcraft:hhon");
		this.playSound("thaumcraft:hhoff");
		ReikaPacketHelper.sendStringPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.NEWASPECTNODE.ordinal(), (TileEntity)node, 32, a.getName().toLowerCase(Locale.ENGLISH));
		location.triggerBlockUpdate(false);
		this.setStatus(NodeImprovementStatus.IDLE);
	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {
		if (status == NodeImprovementStatus.IDLE)
			return;
		this.playSound("thaumcraft:craftfail");
		if (rand.nextInt(8) == 0)
			this.damageNode();
	}

	private void damageNode() {
		ChromaSounds.ERROR.playSound(((TileEntity)node).worldObj, this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, 0.75F, 2);
		ChromaSounds.ERROR.playSound(((TileEntity)node).worldObj, this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, 1F, 1);
		ChromaSounds.ERROR.playSound(((TileEntity)node).worldObj, this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, 0.75F, 0.5F);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.HURTNODE.ordinal(), (TileEntity)node, 32);

		if (rand.nextInt(60) == 0) {
			if (node.getNodeModifier() != null) {
				switch(node.getNodeModifier()) {
					case BRIGHT:
						node.setNodeModifier(null);
						break;
					case PALE:
						node.setNodeModifier(NodeModifier.FADING);
						break;
					case FADING:
						this.emptyNode();
						break;
				}
			}
			else {
				node.setNodeModifier(NodeModifier.PALE);
			}
		}

		if (rand.nextInt(480) == 0) {
			switch(node.getNodeType()) {
				case PURE:
					node.setNodeType(NodeType.NORMAL);
					break;
				case NORMAL:
					node.setNodeType(NodeType.UNSTABLE);
					break;
				case UNSTABLE:
					node.setNodeType(NodeType.DARK);
					break;
				case DARK:
					node.setNodeType(NodeType.TAINTED);
					break;
				case TAINTED:
					node.setNodeType(NodeType.HUNGRY);
					break;
				case HUNGRY:
					this.destroyNode();
					break;
			}
		}

		AspectList al = node.getAspectsBase();
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			if (amt > 1) {
				int rem = Math.min(amt-1, (int)(rand.nextFloat()*(amt/2F)));
				al.remove(a, rem);
			}
		}

		location.triggerBlockUpdate(false);
	}

	private void tryImproveNode() {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.HEALNODE.ordinal(), (TileEntity)node, 32);

		boolean flag = this.tryExpandNodeCapacity();

		if (rand.nextInt(this.modifyChanceByAge(90)) == 0) {
			flag |= this.tryImproveNodeBrightness();
		}
		if (status == NodeImprovementStatus.IDLE && rand.nextInt(this.modifyChanceByAge(360)) == 0) {
			flag |= this.tryImproveNodeType();
		}

		//ReikaJavaLibrary.pConsole(a.getName()+" from "+amt+" to "+newamt);

		if (flag)
			location.triggerBlockUpdate(false);
	}

	private boolean tryExpandNodeCapacity() {
		boolean flag = false;
		AspectList al = node.getAspectsBase();
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			double f = rand.nextDouble()*MathHelper.clamp_double(age/15000D*ChromaOptions.getNodeGrowthSpeed(), 1, 4);
			int newamt = Math.min(720, Math.max(amt+1, (int)(amt+f*Math.sqrt(amt))));//(int)(amt*(1+rand.nextFloat()/5F));
			if (newamt > amt) {
				al.merge(a, newamt);
				ModularLogger.instance.log(LOGGER_ID, "Node "+location+" aspect '"+a.getName()+"' cap increased to "+newamt);
				flag = true;
			}
		}
		if (flag)
			ChromaSounds.CAST.playSoundAtBlock(location);
		return flag;
	}

	private boolean tryImproveNodeType() {
		if (node.getNodeType() == NodeType.PURE)
			return false;
		if (storedEnergy.containsAtLeast(typeCost)) {
			storedEnergy.subtract(typeCost);
			switch(node.getNodeType()) {
				case PURE:
					break;
				case NORMAL:
					node.setNodeType(NodeType.PURE);
					break;
				case UNSTABLE:
					node.setNodeType(NodeType.NORMAL);
					break;
				case DARK:
					node.setNodeType(NodeType.UNSTABLE);
					break;
				case TAINTED:
					node.setNodeType(NodeType.DARK);
					break;
				case HUNGRY:
					node.setNodeType(NodeType.TAINTED);
					break;
			}

			ChromaSounds.DASH.playSoundAtBlock(location.getWorld(), location.xCoord, location.yCoord, location.zCoord, 1, 0.5F);
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" type change");
			this.setStatus(NodeImprovementStatus.IDLE);
			return true;
		}
		else {
			this.setStatus(NodeImprovementStatus.HEALING);
			if (this.requestEnergy(typeCost)) {
				this.playSound("thaumcraft:craftstart");
			}
			return false;
		}
	}

	private boolean tryImproveNodeBrightness() {
		if (node.getNodeModifier() == NodeModifier.BRIGHT)
			return false;
		if (storedEnergy.containsAtLeast(brightnessCost)) {
			storedEnergy.subtract(brightnessCost);
			if (node.getNodeModifier() != null) {
				switch(node.getNodeModifier()) {
					case BRIGHT:
						break;
					case PALE:
						node.setNodeModifier(null); //Tier is bright-null-pale-fading
						break;
					case FADING:
						node.setNodeModifier(NodeModifier.PALE);
						break;
				}
			}
			else {
				node.setNodeModifier(NodeModifier.BRIGHT);
			}

			ChromaSounds.DASH.playSoundAtBlock(location.getWorld(), location.xCoord, location.yCoord, location.zCoord, 1, 0.5F);
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" brightness change");
			this.setStatus(NodeImprovementStatus.IDLE);
			return true;
		}
		else {
			this.setStatus(NodeImprovementStatus.BRIGHTENING);
			if (this.requestEnergy(brightnessCost)) {
				this.playSound("thaumcraft:craftstart");
			}
			return false;
		}
	}

	private void setStatus(NodeImprovementStatus s) {
		status = s;
		if (s == NodeImprovementStatus.IDLE) {
			currentRequestSet = null;
			CrystalNetworker.instance.breakPaths(this);
		}
		//ReikaJavaLibrary.pConsole("Setting status to "+s);
	}

	private boolean requestEnergy(ElementTagCompound tag) {
		if (ticksSinceEnergyInput < 300)
			return false;
		boolean flag = false;
		for (CrystalElement e : tag.elementSet()) {
			int req = MathHelper.ceiling_float_int(tag.getValue(e)/this.getEfficiencyFactor());
			int amt = Math.min(req, this.getRemainingSpace(e));
			flag |= CrystalNetworker.instance.makeRequest(this, e, amt, this.getReceiveRange());
		}
		return flag;
	}

	private void requestForNewAspect(Aspect a, ElementTagCompound tag) {
		if (ticksSinceEnergyInput < 300)
			return;
		for (CrystalElement e : tag.elementSet()) {
			int req = MathHelper.ceiling_float_int(NEW_ASPECT_COST/this.getEfficiencyFactor());
			int amt = Math.min(req, this.getRemainingSpace(e));
			boolean flag = CrystalNetworker.instance.makeRequest(this, e, amt, this.getReceiveRange());
		}
	}

	private void emptyNode() {
		for (Aspect a : node.getAspects().aspects.keySet()) {
			int amt = node.getAspects().getAmount(a);
			if (amt > 1)
				node.takeFromContainer(a, amt-1);
		}

		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" emptied");

		location.triggerBlockUpdate(false);
	}

	private void fillNode() {
		node.setAspects(node.getAspectsBase());

		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" filled");

		location.triggerBlockUpdate(false);
	}

	private void destroyNode() {
		this.playSound("thaumcraft:craftfail", 2, 1);
		this.playSound("thaumcraft:craftfail", 2, 1);
		this.playSound("thaumcraft:craftfail", 1, 0.5F);
		World world = ((TileEntity)node).worldObj;
		double x = location.xCoord+0.5;
		double y = location.yCoord+0.5;
		double z = location.zCoord+0.5;
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.DESTROYNODE.ordinal(), (TileEntity)node, 32);
		ChromaSounds.POWERDOWN.playSound(world, x, y, z, 2, 1);
		ChromaSounds.POWERDOWN.playSound(world, x, y, z, 2, 1);
		EntityLightningBolt elb = new EntityLightningBolt(world, x-0.5, y, z-0.5); //already has +0.5
		world.addWeatherEffect(elb);

		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" destroyed");

		location.setBlock(Blocks.air);
	}

	@SideOnly(Side.CLIENT)
	public static void triggerNewAspectFX(World world, int x, int y, int z, Aspect a) {
		double dx = x+0.5;
		double dy = y+0.5;
		double dz = z+0.5;
		for (int i = 0; i < 16; i++) {
			float fx = (float)ReikaRandomHelper.getRandomPlusMinus(dx, 4);
			float fy = (float)ReikaRandomHelper.getRandomPlusMinus(dy, 4);
			float fz = (float)ReikaRandomHelper.getRandomPlusMinus(dz, 4);
			ReikaThaumHelper.triggerEffect(EffectType.NODEBOLT, world, (float)dx, (float)dy, (float)dz, fx, fy, fz);
		}

		int color = a.getColor();

		for (int i = 0; i < 64; i++) {
			float gv = (float)ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125);
			if (rand.nextInt(3) > 0)
				gv = -gv;
			double v = 0.125+rand.nextDouble()*0.0625;

			double va = rand.nextDouble()*360;
			double vx = v*Math.cos(Math.toDegrees(va));
			double vz = v*Math.sin(Math.toDegrees(va));
			EntityBlurFX fx = new EntityCCBlurFX(world, x+0.5, y+0.5, z+0.5, vx, 0, vz).setColor(color).setGravity(gv).setScale(2).setRapidExpand();
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		EntityCenterBlurFX fx = new EntityCenterBlurFX(world, x+0.5, y+0.5, z+0.5).setColor(a.getColor()).setScale(20);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	public static void triggerDestroyFX(World world, int x, int y, int z) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper((INode)world.getTileEntity(x, y, z));
		for (int i = 0; i < 512; i++) {
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(rand, wrap.node.getAspects().aspects.keySet());
			double v = 0.125+rand.nextDouble()*0.25;
			double rx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double ry = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double rz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			int color = a.getColor();
			EntityBlurFX fx = new EntityCCBlurFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), rx, ry, rz).setColor(color);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (int k = 0; k < 16; k++) {
			CrystalElement e = CrystalElement.elements[k];
			if (wrap.getCurrentValue(e) > 0) {
				for (int i = 0; i < 8; i++) {
					double v = 0.125+rand.nextDouble()*0.25;
					double rx = ReikaRandomHelper.getRandomPlusMinus(0, v);
					double ry = ReikaRandomHelper.getRandomPlusMinus(0, v);
					double rz = ReikaRandomHelper.getRandomPlusMinus(0, v);
					EntityRuneFX fx = new EntityRuneFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), rx, ry, rz, e);
					fx.noClip = true;
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void triggerDamageFX(World world, int x, int y, int z) {
		ReikaThaumHelper.triggerEffect(EffectType.NODEBURST, world, x+0.5, y+0.5, z+0.5, 1F);
	}

	@SideOnly(Side.CLIENT)
	public static void triggerChargingFX(World world, int x, int y, int z) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper((INode)world.getTileEntity(x, y, z));
		double dx = x+0.5;
		double dy = y+0.5;
		double dz = z+0.5;
		for (int i = 0; i < 8; i++) {
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(rand, wrap.node.getAspects().aspects.keySet());
			double px = ReikaRandomHelper.getRandomPlusMinus(dx, 0.125);
			double py = ReikaRandomHelper.getRandomPlusMinus(dy, 0.125);
			double pz = ReikaRandomHelper.getRandomPlusMinus(dz, 0.125);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125); //0.015625
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125); //0
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125); //0.015625
			EntityLaserFX fx = new EntityLaserFX(CrystalElement.WHITE, world, px, py, pz, vx, vy, vz).setColor(a.getColor());
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (int i = 0; i < 6; i++) {
			double r = rand.nextInt(32) == 0 ? 2.5 : rand.nextInt(8) == 0 ? 1.25 : 0.75;
			float px = (float)ReikaRandomHelper.getRandomPlusMinus(dx, r);
			float py = (float)ReikaRandomHelper.getRandomPlusMinus(dy, r);
			float pz = (float)ReikaRandomHelper.getRandomPlusMinus(dz, r);
			ReikaThaumHelper.triggerEffect(EffectType.NODEBOLT, world, (float)dx, (float)dy, (float)dz, px, py, pz);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void triggerHealFX(World world, int x, int y, int z) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper((INode)world.getTileEntity(x, y, z));
		double dx = x+0.5;
		double dy = y+0.5;
		double dz = z+0.5;
		for (double d = -1; d <= 1; d += 0.125) {
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(rand, wrap.node.getAspects().aspects.keySet());
			int color = a.getColor();
			EntityBlurFX fx = new EntityCCBlurFX(world, dx, dy+d, dz).setColor(color).setScale(2.5F-2*(float)Math.abs(d)).setRapidExpand().setLife(20);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void playSound(String s) {
		this.playSound(s, 1, 1);
	}

	private void playSound(String s, float vol, float p) {
		ReikaSoundHelper.playSoundFromServer(((TileEntity)node).worldObj, this.getX()+0.5, this.getY()+0.5, this.getZ()+0.5, s, vol, p, false);
	}

	@Override
	public void onPathCompleted(CrystalFlow p) {

	}

	@Override
	public void onPathConnected(CrystalPath p) {
		ChromaSounds.CAST.playSoundAtBlock(this.getWorld(), this.getX(), this.getY(), this.getZ(), 1, 0.65F);
	}

	@Override
	public void onTileNetworkTopologyChange(CrystalNetworkTile te, boolean remove) {

	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.0875;
	}

	@Override
	public boolean existsInWorld() {
		return false;
	}

	@Override
	public Class getTileClass() {
		return node.getClass();
	}

	@Override
	public String toString() {
		return ReikaThaumHelper.aspectsToString(node.getAspectsBase())+" @ "+location;
	}

	/*
	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENDGAME;
	}
	 */

	public boolean canConductInterdimensionally() {
		return false;
	}

	@Override
	public boolean canReceiveFrom(CrystalTransmitter r) {
		return true;
	}

	@Override
	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r) {
		return true;
	}

	@Override
	public void triggerBottleneckDisplay(int duration) {

	}

	public void load(NBTTagCompound tag, boolean sync) {
		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" loading NBT");

		age = tag.getLong("age");
		tick = tag.getInteger("tick");
		fulltick = tag.getInteger("ftick");

		storedEnergy.readFromNBT("energy", tag);
		status = NodeImprovementStatus.list[tag.getInteger("status")];

		if (tag.hasKey("request"))
			activeAspect = Aspect.getAspect(tag.getString("request"));
		else if (sync)
			activeAspect = null;

		if (tag.hasKey("requestSet"))
			currentRequestSet = ElementTagCompound.createFromFlags(ReikaArrayHelper.booleanFromBitflags(tag.getInteger("requestSet"), 16), 1);
		else if (sync)
			currentRequestSet = null;

		ticksSinceEnergyInput = tag.getInteger("receiveTime");

		this.recalculateCandidateAspects();
	}

	public void write(NBTTagCompound tag) {
		tag.setLong("age", age);
		tag.setInteger("tick", tick);
		tag.setInteger("ftick", fulltick);
		tag.setInteger("status", status.ordinal());

		storedEnergy.writeToNBT("energy", tag);
		tag.setInteger("receiveTime", ticksSinceEnergyInput);

		if (activeAspect != null) {
			tag.setString("request", activeAspect.getTag());
		}
	}

	public void recalculateCandidateAspects() {
		candidateNextAspects.clear();
		Set<Aspect> set = this.getCurrentNodeAspects();
		for (Aspect a : ReikaThaumHelper.getAllAspects()) {
			if (!set.contains(a)) {
				boolean prereqs = true;
				if (!a.isPrimal()) {
					for (Aspect par : a.getComponents()) {
						if (!set.contains(par)) {
							prereqs = false;
							break;
						}
					}
				}
				if (prereqs)
					candidateNextAspects.addEntry(a, 1+ReikaThaumHelper.getMaxAspectTier()-ReikaThaumHelper.getAspectTier(a));
			}
		}
	}

	public void recalculateRefillAspects() {
		candidateRefillAspects.clear();
		AspectList base = node.getAspectsBase();
		AspectList in = node.getAspects();
		HashMap<Aspect, Integer> significant = new HashMap();
		for (Aspect a : base.aspects.keySet()) {
			int cap = base.getAmount(a);
			int has = in.getAmount(a);
			int diff = cap-has;
			if (diff > 0) {
				candidateRefillAspects.addEntry(a, diff);
				if (has == 0 || (cap-has >= 10 && cap/has > 3)) {
					significant.put(a, diff);
				}
			}
		}
		if (!significant.isEmpty()) {
			candidateRefillAspects.clear();
			for (Entry<Aspect, Integer> e : significant.entrySet()) {
				candidateRefillAspects.addEntry(e.getKey(), e.getValue());
			}
		}
	}

	private Set<Aspect> getCurrentNodeAspects() {
		return node.getAspectsBase().aspects.keySet();
	}

	@SideOnly(Side.CLIENT)
	public void renderOverlay(EntityPlayer ep, int gsc) {
		MouseoverOverlayRenderer.instance.renderStorageOverlay(ep, gsc, this);
		Tessellator v5 = Tessellator.instance;
		{
			int ox = Minecraft.getMinecraft().displayWidth/(gsc*2)+12;
			int oy = Minecraft.getMinecraft().displayHeight/(gsc*2)-12;
			int x = ox;
			int y = oy;
			double t = (0.75*System.currentTimeMillis())%360D;
			int idx = 1+status.ordinal();
			int row = idx/8;
			int col = idx%8;
			double u = col/8D;
			double v = row/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			int s = 32;
			int dy = 18;
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/nodeoverlays.png");
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(x, y+s-dy, 0, u, dv);
			v5.addVertexWithUV(x+s, y+s-dy, 0, du, dv);
			v5.addVertexWithUV(x+s, y-dy, 0, du, v);
			v5.addVertexWithUV(x, y-dy, 0, u, v);
			v5.draw();

			int i = 0;
			s = 8;
			int d = s+2;

			/*
			if (!candidateNextAspects.isEmpty()) {
				ArrayList<Aspect> li = new ArrayList(candidateNextAspects);
				ReikaThaumHelper.sortAspectList(li);
				for (Aspect a : li) {
					if (a != activeAspect) {
						ResourceLocation loc = a.getImage();
						Minecraft.getMinecraft().renderEngine.bindTexture(loc);
						double offset = a.getTag().hashCode()*360D/Integer.MAX_VALUE;
						int ap = (int)(255*(0.625+0.375*Math.sin(Math.toRadians(offset+t))));
						if (ap > 0) {
							v5.startDrawingQuads();
							v5.setColorRGBA_I(a.getColor(), ap);
							v5.addVertexWithUV(x, y+s, 0, 0, 1);
							v5.addVertexWithUV(x+s, y+s, 0, 1, 1);
							v5.addVertexWithUV(x+s, y, 0, 1, 0);
							v5.addVertexWithUV(x, y, 0, 0, 0);
							v5.draw();
						}
						i++;
						x += d;
						if (x-ox >= d*8) {
							x = ox;
							y -= d;
						}
					}
				}
			}
			 */
		}
		if (activeAspect != null) {
			ResourceLocation loc = activeAspect.getImage();
			Minecraft.getMinecraft().renderEngine.bindTexture(loc);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(activeAspect.getColor());
			int s = 24;
			int ar = 8;
			int ox = Minecraft.getMinecraft().displayWidth/(gsc*2)+ar;
			int oy = Minecraft.getMinecraft().displayHeight/(gsc*2)+ar;
			v5.addVertexWithUV(ox, oy+s, 0, 0, 1);
			v5.addVertexWithUV(ox+s, oy+s, 0, 1, 1);
			v5.addVertexWithUV(ox+s, oy, 0, 1, 0);
			v5.addVertexWithUV(ox, oy, 0, 0, 0);
			v5.draw();
			int s2 = 55;
			int ds = s2-s;
			ReikaTextureHelper.bindTerrainTexture();
			int dx = ox-ds/2;
			int dy = oy-ds/2;

			if (activeAspect.isPrimal()) {
				GL11.glTranslated(dx, dy, 0);
				GL11.glTranslated(s2/2D, s2/2D, 0);
				GL11.glRotated((System.currentTimeMillis()/9D)%360D, 0, 0, 1);
				GL11.glTranslated(-s2/2D, -s2/2D, 0);
			}

			int x = 0;
			int y = 0;
			IIcon ico;

			if (activeAspect.isPrimal()) {
				int blend = activeAspect.getBlend();
				ico = blend == GL11.GL_ONE_MINUS_SRC_ALPHA ? ChromaIcons.ALPHAHOLE.getIcon() : ChromaIcons.WHITEHOLE.getIcon();
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, blend);
			}
			else {
				BlendMode.ADDITIVEDARK.apply();
				ico = ChromaIcons.ECLIPSEFLARE.getIcon();
				x = dx;
				y = dy;
			}
			v5.startDrawingQuads();
			v5.setColorOpaque_I(activeAspect.getColor());
			v5.addVertexWithUV((x + 0), (y + s2), 0, ico.getMinU(), ico.getMaxV());
			v5.addVertexWithUV((x + s2), (y + s2), 0, ico.getMaxU(), ico.getMaxV());
			v5.addVertexWithUV((x + s2), (y + 0), 0, ico.getMaxU(), ico.getMinV());
			v5.addVertexWithUV((x + 0), (y + 0), 0, ico.getMinU(), ico.getMinV());
			v5.draw();

			//ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(0, 0, ico, s2, s2);
		}
	}

	@Override
	public int getEnergy(CrystalElement e) {
		return storedEnergy.getValue(e);
	}

	@Override
	public ElementTagCompound getEnergy() {
		return storedEnergy.copy();
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return NEW_ASPECT_COST*2;
	}

	@Override
	public ElementTagCompound getRequestedTotal() {
		switch(status) {
			case NEWASPECT:
				return currentRequestSet != null ? currentRequestSet.copy().scale(NEW_ASPECT_COST) : null;
			case BRIGHTENING:
				return brightnessCost.copy();
			case HEALING:
				return typeCost.copy();
			case REFILLING:
				int amt = node.getAspectsBase().getAmount(activeAspect)-node.getAspects().getAmount(activeAspect);
				return activeAspect != null ? this.getTagValue(activeAspect).scale(amt) : null;
			default:
				return null;
		}
	}

	@Override
	public int getTicksExisted() {
		return (int)(age%Integer.MAX_VALUE);
	}

	public void debug(ArrayList<String> li) {
		li.add("Status: "+status);
		li.add("Age: "+age);
		li.add("Ticks Since Energy: "+ticksSinceEnergyInput);
		li.add("Active Aspect: "+(activeAspect != null ? activeAspect.getTag() : "None"));
		li.add("Requesting Element Set: "+currentRequestSet);
		li.add("Queued New Aspects: "+candidateNextAspects);
		li.add("Queued Fill Aspects: "+candidateRefillAspects);
		li.add("Energy: "+storedEnergy);
		li.add("Jar: "+isJarred);
	}

	public static enum NodeImprovementStatus {
		IDLE(),
		REFILLING(),
		BRIGHTENING(),
		HEALING(),
		NEWASPECT(),
		COMPLETE();

		private static final NodeImprovementStatus[] list = values();
	}

}
