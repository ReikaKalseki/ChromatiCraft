package Reika.ChromatiCraft.Entity;

import java.util.UUID;

import Reika.ChromatiCraft.Items.ItemT2EnderEye;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public final class EntityEnderEyeT2 extends EntityEnderEye implements IEntityAdditionalSpawnData {

	public static final int ADDITIONAL_LIFE = 60;
	public static final double RANGE = 36;//vanilla is 12

	private UUID owner;

	private CrystalElement[] colorData;

	private Interpolation spiralColor;
	private Interpolation distanceColor;

	private double finalX;
	private double finalZ;
	private double spawnX;
	private double spawnZ;
	private double totalDistance;

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

		double d2 = x - posX;
		double d3 = z - posZ;
		float f = MathHelper.sqrt_double(d2 * d2 + d3 * d3);
		totalDistance = f;

		if (f > RANGE) {
			targetX = posX + d2 / f * RANGE;
			targetZ = posZ + d3 / f * RANGE;
			targetY = posY + 8.0D;
		}
		else {
			targetX = x;
			targetY = y;
			targetZ = z;
		}

		despawnTimer = 0-ADDITIONAL_LIFE;
		shatterOrDrop = false; //always die so never drops vanilla eye ([/rhyme])
	}

	@Override
	public void onUpdate() {
		if (worldObj.isRemote) {
			this.doParticles();
		}
		super.onUpdate();
		motionX *= 0.7;
		motionY *= 0.7;
		motionZ *= 0.7;
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
				int l = 80-despawnTimer+DragonAPICore.rand.nextInt(40);
				float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 3);
				int c = ReikaColorAPI.getModifiedHue(0xff0000, ReikaRandomHelper.getRandomBetween(75, 180));
				if (rand.nextInt(6) > 0)
					c = (int)distanceColor.getValue(totalDistance);
				EntityBlurFX fx = new EntityBlurFX(worldObj, dx, dy, dz).setLife(l).setScale(s).setColor(c);
				fx.setRapidExpand().setAlphaFading().setIcon(ChromaIcons.FADE_GENTLE);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		if (colorData != null && spiralColor != null) {
			int t = ticksExisted;//this.despawnTimer;
			int c = (int)spiralColor.getValue(t);
			double lf = 80+ADDITIONAL_LIFE;
			double ang = t*360D/lf*2;
			double r = 3;
			double l = t/lf;
			Vec3 vec = ReikaVectorHelper.getPointAroundVector(Vec3.createVectorHelper(motionX, motionY, motionZ), r, ang);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX+vec.xCoord, posY+vec.yCoord, posZ+vec.zCoord);
			float s = 7.5F;
			fx.setLife(180).setScale(s).setColor(c);
			fx.setRapidExpand().setAlphaFading().setIcon(ChromaIcons.FADE_GENTLE);
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

	public void setColorKey(CrystalElement[] key) {
		colorData = key;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeDouble(finalX);
		buf.writeDouble(finalZ);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnZ);
		if (colorData != null) {
			buf.writeInt(colorData.length);
			for (int i = 0; i < colorData.length; i++) {
				buf.writeInt(colorData[i].ordinal());
			}
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
		totalDistance = ReikaMathLibrary.py3d(finalX-spawnX, 0, finalZ-spawnZ);
		distanceColor = new Interpolation(true);
		distanceColor.addPoint(0, 0x606060);
		distanceColor.addPoint(ItemT2EnderEye.FUZZ, 75);
		distanceColor.addPoint(6000, 180);
		distanceColor.addPoint(0, 0xffffff);
		int amt = buf.readInt();
		if (amt != -1) {
			spiralColor = new Interpolation(true);
			colorData = new CrystalElement[amt];
			for (int i = 0; i < amt; i++) {
				colorData[i] = CrystalElement.elements[buf.readInt()];
				int lf = 80+ADDITIONAL_LIFE;
				int t = lf*i/amt;
				int t2 = lf*(i+1)/amt;
				int cr = 3;//2;
				spiralColor.addPoint(t, 0xffffff);
				spiralColor.addPoint(t+cr, colorData[i].getColor());
				spiralColor.addPoint(t2-cr, colorData[i].getColor());
				spiralColor.addPoint(t2, 0xffffff);
			}
		}
	}

}
