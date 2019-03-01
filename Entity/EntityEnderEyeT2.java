package Reika.ChromatiCraft.Entity;

import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ElementEncodedNumber;
import Reika.ChromatiCraft.Auxiliary.ElementEncodedNumber.EncodedPosition;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;


public final class EntityEnderEyeT2 extends EntityEnderEye implements IEntityAdditionalSpawnData {

	public static final int FUZZ = 440;
	public static final double DEVIATION_CHANCE = 0.08;

	public static final int ADDITIONAL_LIFE = 60;
	public static final double RANGE = 36;//vanilla is 12

	private UUID owner;
	private int deviateTime;
	private int deviationTick;

	private EncodedPosition colorData;

	private Interpolation spiralColor;
	private static final Interpolation distanceColor = new Interpolation(true);

	private double finalX;
	private double finalZ;
	private double spawnX;
	private double spawnZ;
	private double totalDistance;

	static {
		distanceColor.addPoint(0, 0x606060);
		distanceColor.addPoint(FUZZ, 75);
		distanceColor.addPoint(6000, 180);
		distanceColor.addPoint(0, 0xffffff);
	}

	public EntityEnderEyeT2(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityEnderEyeT2(World world) {
		super(world);
	}

	@Override
	public void moveTowards(double x, int y, double z) {
		finalX = x;
		finalZ = z;
		spawnX = posX;
		spawnZ = posZ;

		totalDistance = this.setTarget(x, y, z);

		despawnTimer = 0-ADDITIONAL_LIFE;
		shatterOrDrop = false; //always die so never drops vanilla eye ([/rhyme])

		if (totalDistance > FUZZ) {
			int offset = new Random(System.identityHashCode(worldObj)+worldObj.getTotalWorldTime()/24000).nextInt(16);
			ElementEncodedNumber ex = new ElementEncodedNumber(MathHelper.floor_double(finalX), offset);
			ElementEncodedNumber ez = new ElementEncodedNumber(MathHelper.floor_double(finalZ), offset);
			colorData = new EncodedPosition(offset, ex, ez);
		}

		deviateTime = ReikaRandomHelper.doWithChance(DEVIATION_CHANCE) ? 10+rand.nextInt(80+ADDITIONAL_LIFE-40-10) : -1;
	}

	private double setTarget(double x, double y, double z) {
		double dx = x - posX;
		double dz = z - posZ;
		double d = ReikaMathLibrary.py3d(dx, 0, dz);
		if (d > RANGE) {
			targetX = posX + dx / d * RANGE;
			targetZ = posZ + dz / d * RANGE;
			targetY = posY + 8.0D;
		}
		else {
			targetX = x;
			targetY = y;
			targetZ = z;
		}
		return d;
	}

	/** Only called server side, so it clears the entity and creates a new one */
	private void deviate() {
		Towers t = LoreManager.instance.getNearestTower(worldObj, posX, posZ);
		if (t != null) {
			int x;
			int y;
			int z;
			Coordinate c = t.getGeneratedLocation();
			if (c != null) {
				x = c.xCoord;
				y = c.yCoord+18;
				z = c.zCoord;
			}
			else {
				x = t.getRootPosition().chunkXPos;
				z = t.getRootPosition().chunkZPos;
				y = 96;
			}
			if (deviationTick > 30) {
				NBTTagCompound data = new NBTTagCompound();
				this.writeEntityToNBT(data);
				EntityEnderEyeT2 repl = EntityEnderEyeT2.create(worldObj, posX, posY, posZ, data);
				repl.moveTowards(x, y, z);
				repl.deviateTime = -1;
				worldObj.spawnEntityInWorld(repl);
				this.setDead();
			}
			else {
				deviationTick++;
				double dx = x-posX;
				double dz = z-posZ;
				double d = ReikaMathLibrary.py3d(dx, 0, dz);
				double f = deviationTick/30D;
				//motionX += dx/d*f;
				//motionZ += dz/d*f;
				double tx = f*(x-posX)+(1-f)*(finalX-posX);
				double tz = f*(z-posZ)+(1-f)*(finalZ-posZ);
				this.setTarget(tx, targetY, tz);
			}
		}
	}

	@Override
	public void onUpdate() {
		if (worldObj.isRemote) {
			this.doParticles();
		}
		else if (ticksExisted%8 == 0) {
			this.sync();
		}

		boolean flag = deviateTime > 0 && ticksExisted >= deviateTime;
		super.onUpdate();
		if (flag) {
			if (!worldObj.isRemote)
				this.deviate();
			motionX *= 0.85;
			motionY *= 0.85;
			motionZ *= 0.85;
		}
		else {
			motionX *= 0.7;
			motionY *= 0.7;
			motionZ *= 0.7;
		}
		velocityChanged = true;
		if (despawnTimer > 80 && !worldObj.isRemote) {
			ItemStack is = ChromaItems.ENDEREYE.getStackOf();
			is.stackTagCompound = new NBTTagCompound();
			this.writeEntityToNBT(is.stackTagCompound);
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, is));
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles() {
		if (ticksExisted >= 6) {
			int n = ReikaRandomHelper.getRandomBetween(2, 6);
			double r = 0.125;
			for (int i = 0; i < n; i++) {
				double dx = ReikaRandomHelper.getRandomPlusMinus(posX, r);
				double dy = ReikaRandomHelper.getRandomPlusMinus(posY, r);
				double dz = ReikaRandomHelper.getRandomPlusMinus(posZ, r);
				int l = 80-ticksExisted+DragonAPICore.rand.nextInt(40);
				float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 3);
				int c = ReikaColorAPI.getModifiedHue(0xff0000, ReikaRandomHelper.getRandomBetween(75, 180));
				if (rand.nextInt(6) > 0)
					c = (int)distanceColor.getValue(totalDistance);
				EntityBlurFX fx = new EntityBlurFX(worldObj, dx, dy, dz).setLife(l).setScale(s).setColor(c);
				fx.setRapidExpand().setAlphaFading().setIcon(ChromaIcons.FADE_GENTLE);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (colorData != null && spiralColor != null && (deviateTime < 0 || deviateTime > ticksExisted)) {
			int t = ticksExisted;
			int c = (int)spiralColor.getValue(t);
			//ReikaJavaLibrary.pConsole(c+" @ "+t);
			double lf = 80+ADDITIONAL_LIFE;
			double ang = t*360D/lf*2;
			double r = 3;
			double l = t/lf;
			Vec3 vec = ReikaVectorHelper.getPointAroundVector(Vec3.createVectorHelper(motionX, motionY, motionZ), r, ang);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX+vec.xCoord, posY+vec.yCoord, posZ+vec.zCoord);
			float s = 7.5F;
			fx.setLife(180).setScale(s).setColor(c);
			fx.setRapidExpand().setAlphaFading().setIcon(ChromaIcons.FADE_BASICBLEND).setBasicBlend();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public boolean isOwner(EntityPlayer ep) {
		return ep.getPersistentID().equals(owner);
	}

	public EntityPlayer getOwner(World world) {
		return owner != null ? world.func_152378_a(owner) : null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		if (owner != null)
			tag.setString("owner", owner.toString());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		if (tag.hasKey("owner"))
			owner = UUID.fromString(tag.getString("owner"));
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeDouble(finalX);
		buf.writeDouble(finalZ);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnZ);
		buf.writeInt(deviateTime);
		if (colorData != null) {
			buf.writeInt(colorData.totalLength());
			colorData.writeData(buf);
		}
		else {
			buf.writeInt(-1);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		finalX = buf.readDouble();
		finalZ = buf.readDouble();
		spawnX = buf.readDouble();
		spawnZ = buf.readDouble();
		deviateTime = buf.readInt();
		totalDistance = ReikaMathLibrary.py3d(finalX-spawnX, 0, finalZ-spawnZ);
		int amt = buf.readInt();
		if (amt != -1) {
			colorData = EncodedPosition.readData(buf);
			spiralColor = new Interpolation(true);
			for (int i = 0; i < colorData.totalLength(); i++) {
				CrystalElement e = colorData.getColor(i);
				int lf = 80+ADDITIONAL_LIFE;
				int t = lf*i/amt;
				int t2 = lf*(i+1)/amt;
				int cr = 3;//2;
				int c = colorData.isVariableChange(i) ? 0x22aaff : colorData.isPartOfNegative(i) ? 0x000000 : 0xffffff;
				//ReikaJavaLibrary.pConsole(i+" > "+e+" > "+t+"-"+t2);
				spiralColor.addPoint(t, c);
				spiralColor.addPoint(t+cr, e.getColor());
				spiralColor.addPoint(t2-cr, e.getColor());
				spiralColor.addPoint(t2, c);
			}
		}
	}

	public void sync() {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENDEREYESYNC.ordinal(), new PacketTarget.RadiusTarget(this, 200), this.getEntityId(), ticksExisted);
	}

	@SideOnly(Side.CLIENT)
	public void doSync(int t) {
		ticksExisted = t;
	}

	public static EntityEnderEyeT2 create(World world, double x, double y, double z, NBTTagCompound tag) {
		EntityEnderEyeT2 eye = new EntityEnderEyeT2(world, x, y, z);
		eye.readEntityFromNBT(tag);
		return eye;
	}

}
