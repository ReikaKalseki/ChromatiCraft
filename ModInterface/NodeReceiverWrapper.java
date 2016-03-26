/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.NotifiedNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.WrapperTile;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper.EffectType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class NodeReceiverWrapper implements CrystalReceiver, NotifiedNetworkTile, WrapperTile {

	private static final int DELAY = 600;
	private static final int MIN_DELAY = 200;

	private static final int NEW_ASPECT_COST = 240000;

	private static final float LOSS_FACTOR = 0.6F;

	private static final String LOGGER_ID = "chromanodes";

	private static final Random rand = new Random();

	private final INode node;
	public final WorldLocation location;
	private final UUID uid = UUID.randomUUID();

	private int fulltick = MIN_DELAY;
	private int tick = DELAY;

	private final ElementTagCompound baseVis = new ElementTagCompound();

	private final WeightedRandom<Aspect> newAspectWeight = new WeightedRandom();

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_ID);
	}

	NodeReceiverWrapper(INode n) {
		node = n;
		location = new WorldLocation((TileEntity)n);

		AspectList al = n.getAspectsBase();

		for (Aspect a : Aspect.aspects.values()) {
			if (!al.aspects.containsKey(a)) {
				double wt = a.isPrimal() ? 100 : 10/ReikaThaumHelper.decompose(a).size();
				newAspectWeight.addEntry(a, wt);
			}
		}

		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			ElementTagCompound tag = this.getTagValue(a).scale(amt);
			baseVis.addTag(tag);
		}

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
		return ChromaAspectManager.instance.getElementCost(a, 1);
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return baseVis.contains(e);
	}

	@Override
	public void cachePosition() {}

	@Override
	public void removeFromCache() {}

	@Override
	public double getDistanceSqTo(double x, double y, double z) {
		return location.getSquareDistanceTo(x, y, z);
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
		return 50;
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
		if (!this.isConductingElement(e))
			return 0;
		Collection<Aspect> li = ChromaAspectManager.instance.getAspects(e, true);
		AspectList al = new AspectList();
		for (Aspect a : li) {
			if (node.getAspectsBase().aspects.containsKey(a)) {
				al.add(a, (int)Math.ceil(amt*LOSS_FACTOR/baseVis.getValue(e)));
			}
		}
		//ReikaJavaLibrary.pConsole(e+":"+amt+" @ "+baseVis.getValue(e)+" > "+ReikaThaumHelper.aspectsToString(al));
		//ReikaJavaLibrary.pConsole(ReikaThaumHelper.aspectsToString(node.getAspectsBase()));
		this.recharge(al);
		tick = 0;
		return this.isFull(e) ? 0 : amt;
	}

	private boolean isFull(CrystalElement e) {
		Collection<Aspect> li = ChromaAspectManager.instance.getAspects(e, true);
		for (Aspect a : li) {
			if (!this.isFull(a))
				return false;
		}
		return true;
	}

	private boolean isFull(Aspect a) {
		return node.getAspects().getAmount(a) >= node.getAspectsBase().getAmount(a);
	}

	private void recharge(AspectList al) {
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.aspects.get(a);
			int space = node.getAspectsBase().getAmount(a)-node.getAspects().getAmount(a);
			node.addToContainer(a, Math.min(amt, space));
		}

		if (rand.nextInt(20) == 0)
			this.playSound("thaumcraft:runicShieldCharge", 1, 0.5F);

		if (rand.nextInt(240) == 0)
			this.healNode();

		ModularLogger.instance.log(LOGGER_ID, "Node "+location+" recharge");

		location.triggerBlockUpdate(false);
	}

	public void tick() {
		tick++;
		fulltick++;

		if (rand.nextInt(240) == 0) {
			this.playSound("thaumcraft:zap");
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CHARGINGNODE.ordinal(), (TileEntity)node, 32);
		}

		if (rand.nextInt(1600) == 0)
			this.healNode();

		if (tick >= DELAY && fulltick >= MIN_DELAY) {
			ElementTagCompound req = new ElementTagCompound();
			for (Aspect a : node.getAspectsBase().aspects.keySet()) {
				if (a == null) //WHY, AZANOR!?
					continue;
				int space = node.getAspectsBase().getAmount(a)-node.getAspects().getAmount(a);
				//if (space > 0)
				//	ReikaJavaLibrary.pConsole(a.getName()+":"+space+":"+this.getTagValue(a).scale(space*space));
				req.addTag(this.getTagValue(a).scale(space*space));
			}
			req.scale(1.25F/LOSS_FACTOR);
			boolean flag = false;
			for (CrystalElement e : req.elementSet()) {
				ModularLogger.instance.log(LOGGER_ID, "Node "+location+" requesting "+req.getValue(e)+" of "+e.displayName);
				flag |= CrystalNetworker.instance.makeRequest(this, e, req.getValue(e), this.getReceiveRange());
			}
			fulltick = 0;
			if (flag) {
				this.playSound("thaumcraft:craftstart");
				tick = 0;
			}
		}

		if (rand.nextInt(8000) == 0 && !newAspectWeight.isEmpty()) {
			AspectList al = node.getAspectsBase();
			Aspect a = newAspectWeight.getRandomEntry();
			newAspectWeight.remove(a);
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" attempting to gain aspect '"+a.getName()+"'");
			if (this.tryToAddAspect(a, al)) {
				ModularLogger.instance.log(LOGGER_ID, "Node "+location+" gained aspect '"+a.getName()+"'");
			}
			location.triggerBlockUpdate(false);
		}
	}

	private boolean tryToAddAspect(Aspect a, AspectList base) {
		ElementTagCompound tag = this.getTagValue(a);
		boolean flag = true;
		for (CrystalElement e : tag.elementSet()) {
			flag &= CrystalNetworker.instance.findSourceWithX(this, e, NEW_ASPECT_COST, this.getReceiveRange(), true) != null;
		}
		if (flag) {
			base.add(a, 1);
			node.addToContainer(a, 1);
			baseVis.addTag(this.getTagValue(a));
			this.playSound("thaumcraft:hhon");
			this.playSound("thaumcraft:hhoff");
			ReikaPacketHelper.sendStringPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.NEWASPECTNODE.ordinal(), (TileEntity)node, 32, a.getName().toLowerCase(Locale.ENGLISH));
			return true;
		}
		return false;
	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {
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

	private void healNode() {
		ChromaSounds.CAST.playSoundAtBlock(location);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.HEALNODE.ordinal(), (TileEntity)node, 32);

		if (rand.nextInt(60) == 0) {
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

			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" brightness change");
		}

		if (rand.nextInt(240) == 0) {
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

			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" type change");
		}

		AspectList al = node.getAspectsBase();
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			int newamt = Math.min(720, Math.max(amt+1, (int)(amt+rand.nextDouble()*Math.sqrt(amt))));//(int)(amt*(1+rand.nextFloat()/5F));
			al.merge(a, newamt);
			ModularLogger.instance.log(LOGGER_ID, "Node "+location+" aspect '"+a.getName()+"' cap increased to "+newamt);
		}
		//ReikaJavaLibrary.pConsole(a.getName()+" from "+amt+" to "+newamt);

		location.triggerBlockUpdate(false);
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
			EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, vx, 0, vz).setColor(color).setGravity(gv).setScale(2).setRapidExpand();
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
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(wrap.node.getAspects().aspects.keySet());
			double v = 0.125+rand.nextDouble()*0.25;
			double rx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double ry = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double rz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			int color = a.getColor();
			EntityBlurFX fx = new EntityBlurFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), rx, ry, rz).setColor(color);
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
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(wrap.node.getAspects().aspects.keySet());
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
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(wrap.node.getAspects().aspects.keySet());
			int color = a.getColor();
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy+d, dz).setColor(color).setScale(2.5F-2*(float)Math.abs(d)).setRapidExpand().setLife(20);
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

	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENDGAME;
	}

}
