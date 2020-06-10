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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

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
				this.sendParticle(e, e.posX, -50, e.posZ, TYPES[type], false);
				return false;
			}

			WorldLocation loc = DungeonGenerator.instance.getNearestZone(TYPES[type], world, e.posX, e.posZ, RANGE);
			if (loc != null) {
				double dist = loc.getDistanceTo(e);
				if (dist <= FUZZ) {
					double px = ReikaRandomHelper.getRandomPlusMinus(e.posX, FUZZ);
					double py = ReikaRandomHelper.getRandomPlusMinus(e.posY, FUZZ);
					double pz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, FUZZ);
					this.sendParticle(e, px, py, pz, TYPES[type], true);
				}
				else {
					double px = ReikaRandomHelper.getRandomPlusMinus(loc.xCoord+0.5, FUZZ);
					double py = ReikaRandomHelper.getRandomPlusMinus(loc.yCoord+0.5, FUZZ);
					double pz = ReikaRandomHelper.getRandomPlusMinus(loc.zCoord+0.5, FUZZ);
					this.sendParticle(e, px, py, pz, TYPES[type], false);
				}
			}
		}
		return true;
	}

	private void sendParticle(EntityPlayer ep, double sx, double sy, double sz, ChromaStructures s, boolean close) {
		if (ep instanceof EntityPlayerMP) {
			PacketTarget pt = new PacketTarget.PlayerTarget((EntityPlayerMP)ep);
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTFIND.ordinal(), ep.worldObj, sx, sy, sz, pt, s.ordinal(), close ? 1 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doHeldFX(EntityPlayer ep, double sx, double sy, double sz, ChromaStructures s, boolean close) {
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.0625, -ep.rotationPitch, ep.rotationYawHead+90+60);
		double px = ep.posX+xyz[0];
		double py = ep.posY+xyz[1];
		double pz = ep.posZ+xyz[2];
		double dx = sx-px;
		double dy = sy-py;
		double dz = sz-pz;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.03125/2;
		double vx = dx/dd*v;
		double vy = dy/dd*v;
		double vz = dz/dd*v;
		float sc = 0.125F+rand.nextFloat()*0.25F;
		int l = 10;
		int c = COLORS[s.ordinal()];
		EntityBlurFX fx = new EntityBlurFX(ep.worldObj, px, py, pz, vx, vy, vz);
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
		fx.setLife(10).setScale(sc).setColor(c);
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
	protected CrystalElement getColor() {
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
	protected int getChargeState(float frac) {
		return frac > 0.1 ? 1 : 0;
	}

	@Override
	protected int getChargeConsumptionRate(EntityPlayer e, World world, ItemStack is) {
		return 1;
	}

}
