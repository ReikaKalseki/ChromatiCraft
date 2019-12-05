/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverInventoryBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

//Infuses shards with activated chroma
@Deprecated
public class TileEntityAuraInfuser_Old1 extends FluidReceiverInventoryBase implements ItemOnRightClick {

	private CrystalElement color;
	private int berries;
	public static final int BERRY_UNIT = 24;
	public static final int DURATION = 20;
	private int progressTimer = 0;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		/*
		if (!world.isRemote) {
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y+1, z, x+1, y+1.25, z+1);
			List<EntityItem> li = world.getEntitiesWithinAABB(EntityItem.class, box);

			for(EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				if (ChromaItems.BERRY.matchWith(is)) {
					if (color == null || color.ordinal() == is.getItemDamage()%16) {
						color = CrystalElement.elements[is.getItemDamage()%16];
						int amt = Math.min(is.stackSize, BERRY_UNIT-berries);
						berries += amt;
						if (amt >= is.stackSize)
							ei.setDead();
						else
							is.stackSize -= amt;
					}
				}
			}
		}*/

		//ReikaJavaLibrary.pConsole(color+":"+berries, Side.SERVER);
		//ReikaJavaLibrary.pConsole(progressTimer, Side.SERVER);

		/*
		if (this.getTicksExisted()%1 == 0) {
			double r = 3;
			for (int n = 0; n < 6; n++) {
				for (int i = 0; i < 360; i += 60) {
					int a = this.getTicksExisted()+i;
					double px = x+0.5+r*Math.sin(Math.toRadians(a));
					double py = y+0.125+n*0.0625+ReikaRandomHelper.getRandomPlusMinus(0, 0.05);
					double pz = z+0.5+r*Math.cos(Math.toRadians(a));
					int red = n < 2 ? 255 : 0;
					int green = n < 4 ? 255 : 0;
					int blue = 255;
					EntityBlurFX fx = new EntityBlurFX(world, px, py, pz, 0, 0, 0).setColor(red, green, blue).setScale(0.5F);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}*/

		if (this.canProcess()) {
			if (progressTimer < DURATION) {
				progressTimer++;
				//if (!tank.isEmpty())
				//	tank.removeLiquid(25);
			}
			else {
				this.process();
				progressTimer = 0;
			}
			this.syncAllData(true);
		}
		else if (ChromaItems.BERRY.matchWith(inv[0]) && tank.isFull() && tank.getActualFluid().equals(FluidRegistry.getFluid("chroma"))) {
			if (progressTimer < DURATION) {
				progressTimer++;
			}
			else {
				color = CrystalElement.elements[inv[0].getItemDamage()];
				berries++;
				inv[0] = null;
				progressTimer = 0;
			}
			this.syncAllData(true);
		}
		else {
			progressTimer = 0;
		}
	}

	public int getColor() {
		return color != null ? ReikaColorAPI.mixColors(color.getColor(), 0xffffff, berries/(float)BERRY_UNIT) : 0xffffff;
	}

	public int getProgressScaled(int a) {
		return a * progressTimer / DURATION;
	}

	public int getProgress() {
		return progressTimer;
	}

	private boolean canProcess() {
		if (!ChromaItems.SHARD.matchWith(inv[0]))
			return false;
		if (inv[0].getItemDamage() >= 16)
			return false;
		boolean hasFluid = tank.isFull() && tank.getActualFluid().equals(FluidRegistry.getFluid("chroma"));
		if (!hasFluid && progressTimer == 0)
			return false;
		//hasfluid &= tank.getNBTInt("color") == inv[0].getItemDamage()%16;
		boolean hasenergy = berries >= BERRY_UNIT && color == CrystalElement.elements[inv[0].getItemDamage()];//energy.getValue(e) >= BERRY_UNIT;
		return hasenergy;
	}
	/*
	public void addFluid(CrystalElement e, int amt) {
		if (tank.isEmpty()) {
			tank.addLiquid(amt, FluidRegistry.getFluid("active chroma"));
			tank.setNBTInt("color", e.ordinal());
		}
		else if (tank.getNBTInt("color") == e.ordinal()) {

		}
	}*/

	private void process() {
		inv[0].setItemDamage(inv[0].getItemDamage()+16);
		tank.removeLiquid(tank.getCapacity());
		berries = 0;
		color = null;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack is, int j) {
		return ChromaItems.SHARD.matchWith(is) && is.getItemDamage() >= 16 && is.getItemDamage() < 32;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (progressTimer > 0)
			return false;
		boolean shard = ChromaItems.SHARD.matchWith(is) && is.getItemDamage() < 16;
		boolean berry = ChromaItems.BERRY.matchWith(is) && (color == null || is.getItemDamage() == color.ordinal()) && berries < BERRY_UNIT;
		return shard || berry;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INFUSER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getCapacity() {
		return 500;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("chroma");
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return from.offsetY == 0 && progressTimer == 0 && (!(ChromaItems.SHARD.matchWith(inv[0]) && inv[0].getItemDamage() >= 16));
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		int e = NBT.getInteger("color");
		color = e >= 0 ? CrystalElement.elements[e] : null;
		berries = NBT.getInteger("berries");
		progressTimer = NBT.getInteger("progress");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color != null ? color.ordinal() : -1);
		NBT.setInteger("berries", berries);
		NBT.setInteger("progress", progressTimer);
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (item != null && !this.isItemValidForSlot(0, item))
			return item;
		ItemStack ret = inv[0];
		inv[0] = item != null ? ReikaItemHelper.getSizedItemStack(item, 1) : null;
		if (ret == null && item != null && item.stackSize > 1)
			ret = ReikaItemHelper.getSizedItemStack(item, item.stackSize-1);
		//if (item != null && item.stackSize > 1)
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ReikaItemHelper.getSizedItemStack(item, item.stackSize-1));
		return ret;
	}

}
