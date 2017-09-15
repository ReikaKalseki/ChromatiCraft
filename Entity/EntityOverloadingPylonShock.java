package Reika.ChromatiCraft.Entity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalFuse;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityOverloadingPylonShock extends InertEntity implements IEntityAdditionalSpawnData {

	private CrystalElement color;
	private final ArrayList<DecimalPosition> path;
	private Coordinate target;
	private int damageFactor;

	public EntityOverloadingPylonShock(World world) {
		super(world);
		path = new ArrayList();
		target = null;
	}

	public EntityOverloadingPylonShock(World world, TileEntityCrystalPylon te, ArrayList<Coordinate> path, double speed, int dmg) {
		super(world);

		this.path = new ArrayList();
		damageFactor = dmg;
		color = te.getColor();
		for (int i = 0; i < path.size()-1; i++) {
			Coordinate c1 = path.get(i);
			Coordinate c2 = path.get(i+1);
			for (int t = 0; t < speed; t++) {
				this.path.add(DecimalPosition.interpolate(c1.xCoord+0.5, c1.yCoord+0.5, c1.zCoord+0.5, c2.xCoord+0.5, c2.yCoord+0.5, c2.zCoord+0.5, t/speed));
			}
		}
		target = path.get(path.size()-1);
		this.setLocationAndAngles(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, 0, 0);
	}

	public static double getRandomSpeed() {
		return ReikaRandomHelper.getRandomBetween(7D, 15D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!path.isEmpty()) {
			DecimalPosition pos = path.get(Math.min(ticksExisted, path.size()-1));
			this.setLocationAndAngles(pos.xCoord, pos.yCoord, pos.zCoord, 0, 0);
		}

		if (worldObj.isRemote) {
			this.doParticles(worldObj, posX, posY, posZ);
		}
		else {
			if (ticksExisted >= path.size()) {
				this.setDead();
				worldObj.newExplosion(this, posX, posY, posZ, 5, true, false);
				if (rand.nextInt(8/damageFactor) == 0) {
					TileEntity te = target.getTileEntity(worldObj);
					if (te instanceof TileEntityCrystalPylon) {
						if (rand.nextInt(8/damageFactor) == 0)
							((TileEntityCrystalPylon)te).destabilize();
					}
					else if (te instanceof CrystalFuse) {
						((CrystalFuse)te).overload(color);
					}
					else {
						ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, target.xCoord, target.yCoord, target.zCoord, null, true, true);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, double x, double y, double z) {
		EntityBlurFX fx = new EntityBlurFX(world, x, y, z);
		fx.setAlphaFading().setRapidExpand().setScale(32).setColor(color.getColor()).setLife(12);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(color.ordinal());
		buf.writeInt(path.size());
		for (DecimalPosition p : path) {
			p.writeToBuf(buf);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		color = CrystalElement.elements[buf.readInt()];
		int len = buf.readInt();
		path.clear();
		for (int i = 0; i < len; i++) {
			DecimalPosition p = DecimalPosition.readFromBuf(buf);
			path.add(p);
		}
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		color = CrystalElement.elements[tag.getInteger("color")];
		path.clear();
		NBTTagList li = tag.getTagList("path", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			path.add(DecimalPosition.readTag((NBTTagCompound)o));
		}
		target = Coordinate.readFromNBT("tgt", tag);
		damageFactor = tag.getInteger("dmg");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("color", color.ordinal());
		NBTTagList li = new NBTTagList();
		for (DecimalPosition p : path) {
			li.appendTag(p.writeToTag());
		}
		tag.setTag("path", li);
		target.writeToNBT("tgt", tag);
		tag.setInteger("dmg", damageFactor);
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

}
