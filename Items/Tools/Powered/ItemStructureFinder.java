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

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator.StructureSeekData;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemStructureFinder extends ItemPoweredChromaTool {

	private static final double RANGE = 512;
	private static final double FUZZ = 96;
	private static ChromaStructures[] TYPES;
	private static final int[] COLORS = new int[ChromaStructures.structureList.length];

	static {
		COLORS[ChromaStructures.BURROW.ordinal()] = 0x79B783;
		COLORS[ChromaStructures.CAVERN.ordinal()] = 0x8FA3B5;
		COLORS[ChromaStructures.OCEAN.ordinal()] = 0x60C4C4;
		COLORS[ChromaStructures.DESERT.ordinal()] = 0xBF655D;
		COLORS[ChromaStructures.SNOWSTRUCT.ordinal()] = 0xf050c0;
		COLORS[ChromaStructures.BIOMEFRAG.ordinal()] = 0x22aaff;
	}

	private static final Random rand = new Random();

	public static int getColor(ChromaStructures s) {
		return COLORS[s.ordinal()];
	}

	public ItemStructureFinder(int index) {
		super(index);
	}

	@Override
	public boolean doTick(ItemStack is, World world, EntityPlayer e, boolean held) {
		if (!world.isRemote) {
			if (is.stackTagCompound == null) {
				is.stackTagCompound = new NBTTagCompound();
			}

			int type = this.getStructureType(is);
			if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
				this.sendParticle(e, e.posX, -50, e.posZ, TYPES[type], false, true);
				return false;
			}

			ChromaStructures s = TYPES[type];
			double r = RANGE*this.getSightRangeModifier(s);
			boolean debug = ReikaObfuscationHelper.isDeObfEnvironment() && KeyWatcher.instance.isKeyDown(e, Key.LCTRL) && ReikaPlayerAPI.isReika(e);
			if (debug)
				r = 5000;
			StructureSeekData loc = DungeonGenerator.instance.getNearestRealStructure(s, (WorldServer)world, e.posX, e.posZ, r, false);
			if (loc != null) {
				double dist = loc.location.getDistanceTo(e);
				double fz = debug ? 0 : FUZZ;
				if (dist <= fz) {
					double px = ReikaRandomHelper.getRandomPlusMinus(e.posX, fz);
					double py = ReikaRandomHelper.getRandomPlusMinus(e.posY, fz);
					double pz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, fz);
					this.sendParticle(e, px, py, pz, TYPES[type], true, loc.isKnownSuccess);
				}
				else {
					double px = ReikaRandomHelper.getRandomPlusMinus(loc.location.xCoord+0.5, fz);
					double py = ReikaRandomHelper.getRandomPlusMinus(e.posY, fz);
					double pz = ReikaRandomHelper.getRandomPlusMinus(loc.location.zCoord+0.5, fz);
					this.sendParticle(e, px, py, pz, TYPES[type], false, loc.isKnownSuccess);
				}
			}
		}
		return true;
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

	private void sendParticle(EntityPlayer ep, double sx, double sy, double sz, ChromaStructures s, boolean close, boolean genned) {
		if (ep instanceof EntityPlayerMP) {
			PacketTarget pt = new PacketTarget.PlayerTarget((EntityPlayerMP)ep);
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTFIND.ordinal(), ep.worldObj, sx, sy, sz, pt, s.ordinal(), close ? 1 : 0, genned ? 1 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doHeldFX(EntityPlayer ep, double sx, double sy, double sz, ChromaStructures s, boolean close, boolean genned) {
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.0625, -ep.rotationPitch, ep.rotationYawHead+90+60);
		double px = ep.posX+xyz[0];
		double py = ep.posY+xyz[1];
		double pz = ep.posZ+xyz[2];
		double dx = sx-px;
		double dy = sy-py;
		double dz = sz-pz;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.03125/2;
		float sc = 0.125F+rand.nextFloat()*0.25F;
		int l = 10;
		int c = COLORS[s.ordinal()];
		if (!genned) {
			if (ep.getRNG().nextInt(3) == 0)
				return;
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F);
			l = 15;
			sc *= 1.25;
			v *= 0.67;
		}
		double vx = dx/dd*v;
		double vy = dy/dd*v;
		double vz = dz/dd*v;
		EntityCCBlurFX fx = new EntityCCBlurFX(ep.worldObj, px, py, pz, vx, vy, vz);
		if (close) {
			l *= 2;
			sc *= 0.5;
			fx.setIcon(ChromaIcons.FADE_RAY);
			double d = rand.nextDouble()*360;
			MathExpression e = new PeriodicExpression().addWave(0.5, 16, d).normalize();
			fx.setColorController(new FlashColorController(e, c, 0xffffff));
			fx.motionX *= 0.375;
			fx.motionY *= 0.375;
			fx.motionZ *= 0.375;
		}
		if (!genned && !close)
			fx.setIcon(ChromaIcons.FADE_GENTLE);
		fx.setLife(l).setScale(sc).setColor(c);
		if (sy == -50) {
			fx.setLife(40+rand.nextInt(21));
			fx.setColliding();
			fx.motionY *= 0.7;
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private int getStructureType(ItemStack is) {
		if (TYPES == null)
			TYPES = DungeonGenerator.instance.getStructureTypes().toArray(new ChromaStructures[DungeonGenerator.instance.getStructureTypes().size()]);
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
		}
		int type = is.stackTagCompound != null ? is.stackTagCompound.getInteger("type") : 0;
		return type;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		super.addInformation(is, ep, li, vb);
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			int type = this.getStructureType(is);
			li.add(String.format("Type: "+TYPES[type]));
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		int type = this.getStructureType(is);
		type = (type+1)%TYPES.length;
		is.stackTagCompound.setInteger("type", type);
		return is;
	}

	@Override
	protected boolean isActivated(EntityPlayer e, ItemStack is, boolean held) {
		return held;
	}

	@Override
	public CrystalElement getColor(ItemStack is) {
		return CrystalElement.BLACK;
	}

	@Override
	public int getMaxCharge() {
		return 36000; //half hour
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
