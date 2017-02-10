package Reika.ChromatiCraft.Items.Tools;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.DungeonGenerator;
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


public class ItemStructureFinder extends ItemChromaTool {

	private static final double RANGE = 512;
	private static final double FUZZ = 96;
	private static Structures[] TYPES;
	private static final int[] COLORS = new int[Structures.structureList.length];

	static {
		COLORS[Structures.BURROW.ordinal()] = 0x79B783;
		COLORS[Structures.CAVERN.ordinal()] = 0x8FA3B5;
		COLORS[Structures.OCEAN.ordinal()] = 0x60C4C4;
		COLORS[Structures.DESERT.ordinal()] = 0xBF655D;
	}

	private static final Random rand = new Random();

	public ItemStructureFinder(int index) {
		super(index);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (held && e instanceof EntityPlayer && !world.isRemote) {
			if (is.stackTagCompound == null) {
				is.stackTagCompound = new NBTTagCompound();
			}

			int type = this.getStructureType(is);
			WorldLocation loc = DungeonGenerator.instance.getNearestStructure(TYPES[type], world, e.posX, e.posY, e.posZ, RANGE);
			if (loc != null) {
				double dist = loc.getDistanceTo(e);
				if (dist <= FUZZ) {
					double px = ReikaRandomHelper.getRandomPlusMinus(e.posX, FUZZ);
					double py = ReikaRandomHelper.getRandomPlusMinus(e.posY, FUZZ);
					double pz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, FUZZ);
					this.sendParticle((EntityPlayer)e, px, py, pz, TYPES[type], true);
				}
				else {
					double px = ReikaRandomHelper.getRandomPlusMinus(loc.xCoord+0.5, FUZZ);
					double py = ReikaRandomHelper.getRandomPlusMinus(loc.yCoord+0.5, FUZZ);
					double pz = ReikaRandomHelper.getRandomPlusMinus(loc.zCoord+0.5, FUZZ);
					this.sendParticle((EntityPlayer)e, px, py, pz, TYPES[type], false);
				}
			}
		}
	}

	private void sendParticle(EntityPlayer ep, double sx, double sy, double sz, Structures s, boolean close) {
		if (ep instanceof EntityPlayerMP) {
			PacketTarget pt = new PacketTarget.PlayerTarget((EntityPlayerMP)ep);
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.STRUCTFIND.ordinal(), ep.worldObj, sx, sy, sz, pt, s.ordinal(), close ? 1 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doHeldFX(EntityPlayer ep, double sx, double sy, double sz, Structures s, boolean close) {
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
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private int getStructureType(ItemStack is) {
		if (TYPES == null)
			TYPES = DungeonGenerator.instance.getStructureTypes().toArray(new Structures[DungeonGenerator.instance.getStructureTypes().size()]);
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
		}
		int type = is.stackTagCompound != null ? is.stackTagCompound.getInteger("type") : 0;
		return type;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
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

}
