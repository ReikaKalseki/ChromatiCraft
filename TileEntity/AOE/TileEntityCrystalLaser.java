/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalLaser extends InventoriedRelayPowered {

	private static final HashSet<CrystalElement> localEffects = new HashSet();
	private static final HashSet<CrystalElement> aabbEffects = new HashSet();

	static {
		localEffects.add(CrystalElement.GREEN);
		localEffects.add(CrystalElement.ORANGE);

		aabbEffects.add(CrystalElement.RED);
		aabbEffects.add(CrystalElement.BLACK);
		aabbEffects.add(CrystalElement.MAGENTA);
		aabbEffects.add(CrystalElement.LIME);
	}

	private int range;
	private StepTimer rangeTimer = new StepTimer(20);

	public static final int MAX_RANGE = 128;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LASER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		ForgeDirection dir = this.getFacing();

		rangeTimer.update();
		if (rangeTimer.checkCap()) {
			range = this.updateRange(dir);
		}

		if (this.isActive()) {
			if (!world.isRemote)
				this.applyEffects(world, x, y, z, dir);

			if (world.isRemote)
				this.spawnParticles(world, x, y, z, dir);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, ForgeDirection dir) {
		int c = 4+4*Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(c) == 0) {
			this.spawnParticle(world, x, y, z, dir);
		}
	}

	private int updateRange(ForgeDirection dir) {
		if (!this.isActive())
			return 0;
		int energy = this.getEnergy(this.getColor());
		int max = (int)Math.min(Math.sqrt(energy/4), MAX_RANGE);
		for (int i = 1; i <= max; i++) {
			int dx = xCoord+dir.offsetX*i;
			int dy = yCoord+dir.offsetY*i;
			int dz = zCoord+dir.offsetZ*i;
			Block b = worldObj.getBlock(dx, dy, dz);
			if (b != Blocks.air && b.isOpaqueCube())
				return i;
		}
		return max;
	}

	public boolean isActive() {
		return ChromaItems.LENS.matchWith(inv[0]) && this.getEnergy(this.getColor()) > 0;
	}

	private void applyEffects(World world, int x, int y, int z, ForgeDirection dir) {
		int r = this.getRange();
		CrystalElement color = this.getColor();
		if (aabbEffects.contains(color)) {
			AxisAlignedBB box = this.getAABB(r);
			List<Entity> li = world.getEntitiesWithinAABB(Entity.class, box);
			switch(color) {
			case LIME:
				double vmax = 1;
				for (Entity e : li) {
					if (Math.abs(e.motionX) < vmax)
						e.motionX += this.getFacing().offsetX*0.0625;
					if (Math.abs(e.motionY) < vmax)
						e.motionY += this.getFacing().offsetY*0.0625;
					if (Math.abs(e.motionZ) < vmax)
						e.motionZ += this.getFacing().offsetZ*0.0625;
				}
				break;
			case RED:
				for (Entity e : li) {
					if (e instanceof EntityLivingBase)
						((EntityLivingBase)e).addPotionEffect(new PotionEffect(Potion.resistance.id, 100, 1));
				}
				break;
			case BLACK:
				for (Entity e : li) {
					if (e instanceof EntityPlayer) {
						EntityPlayer ep = (EntityPlayer)e;

					}
				}
				break;
			default:
				break;
			}
		}
		if (localEffects.contains(color)) {
			for (int i = 1; i <= r; i++) {
				int dx = x+dir.offsetX*i;
				int dy = y+dir.offsetY*i;
				int dz = z+dir.offsetZ*i;
				switch(color) {
				case BLUE: //light beam?
					break;
				case GREEN:
					Block b = world.getBlock(dx, dy, dz);
					b.updateTick(world, dx, dy, dz, rand);
					break;
				case ORANGE:
					ReikaWorldHelper.temperatureEnvironment(world, dx, dy, dz, 400);
					break;
				default:
					break;
				}
			}
		}
		this.drainEnergy(color, this.getRange());
	}

	public AxisAlignedBB getAABB(int range) {
		int minx = 0;
		int miny = 0;
		int minz = 0;
		int maxx = 0;
		int maxy = 0;
		int maxz = 0;

		switch (this.getFacing()) {
		case WEST:
			minx = xCoord-range-1;
			maxx = xCoord;
			miny = yCoord;
			maxy = yCoord+1;
			minz = zCoord;
			maxz = zCoord+1;
			break;
		case EAST:
			minx = xCoord+1;
			maxx = xCoord+range+1;
			miny = yCoord;
			maxy = yCoord+1;
			minz = zCoord;
			maxz = zCoord+1;
			break;
		case SOUTH:
			maxz = zCoord+range+1;
			minz = zCoord+1;
			miny = yCoord;
			maxy = yCoord+1;
			minx = xCoord;
			maxx = xCoord+1;
			break;
		case NORTH:
			maxz = zCoord;
			minz = zCoord-range-1;
			miny = yCoord;
			maxy = yCoord+1;
			minx = xCoord;
			maxx = xCoord+1;
			break;
		case UP:
			minz = zCoord;
			maxz = zCoord+1;
			miny = yCoord+1;
			maxy = yCoord+range+1;
			minx = xCoord;
			maxx = xCoord+1;
			break;
		case DOWN:
			minz = zCoord;
			maxz = zCoord+1;
			maxy = yCoord;
			miny = yCoord-range;
			minx = xCoord;
			maxx = xCoord+1;
			break;
		case UNKNOWN:
			break;
		}
		return AxisAlignedBB.getBoundingBox(minx, miny, minz, maxx, maxy, maxz).expand(0.0, 0.0, 0.0);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle(World world, int x, int y, int z, ForgeDirection dir) {
		int num = 1+this.getRange()/32;
		for (int i = 0; i < num; i++) {
			double r = rand.nextDouble()*this.getRange();
			double rx = x+0.5+r*dir.offsetX;
			double ry = y+0.5+r*dir.offsetY;
			double rz = z+0.5+r*dir.offsetZ;
			double px = ReikaRandomHelper.getRandomPlusMinus(rx, 0.25);
			double py = ReikaRandomHelper.getRandomPlusMinus(ry, 0.25);
			double pz = ReikaRandomHelper.getRandomPlusMinus(rz, 0.25);
			EntityLaserFX fx = new EntityLaserFX(this.getColor(), world, px, py, pz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			//ParticleEngine.instance.addEffect(world, fx);
		}
	}

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
		case 0:
			return ForgeDirection.WEST;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.SOUTH;
		case 4:
			return ForgeDirection.UP;
		case 5:
			return ForgeDirection.DOWN;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public CrystalElement getColor() {
		return inv[0] != null && ChromaItems.LENS.matchWith(inv[0]) ? CrystalElement.elements[inv[0].getItemDamage()] : null;
	}

	public int getRange() {
		return range;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == 0;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public ItemStack swapLens(ItemStack is) {
		ItemStack ret = inv[0] != null ? inv[0].copy() : null;
		inv[0] = is != null ? is.copy() : null;
		return ret;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ChromaItems.LENS.matchWith(is);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		int r = this.getRange();
		ForgeDirection f = this.getFacing();
		int fx = f.offsetX;
		int fy = f.offsetY;
		int fz = f.offsetZ;
		return r > 0 ? ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(fx*r, fy*r, fz*r) : super.getRenderBoundingBox();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		range = NBT.getInteger("range");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("range", range);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 65536;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return e == this.getColor();
	}

	@Override
	protected ElementTagCompound getRequiredEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		if (this.getColor() != null)
			tag.addValueToColor(this.getColor(), 1);
		return tag;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}

}
