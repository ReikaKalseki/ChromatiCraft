/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverInventoryBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAuraInfuser2 extends FluidReceiverInventoryBase {

	private InertItem[] items = new InertItem[this.getSizeInventory()];

	private int sprays = 4;
	private int thetat[] = new int[sprays];
	private int phit[] = new int[sprays];
	private int theta[] = new int[sprays];
	private int phi[] = new int[sprays];

	private int craftingTick = 200;

	private boolean isCollecting() {
		return ReikaMathLibrary.isValueInsideBounds(0, 40, craftingTick);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted() == 0) {
			for (int i = 0; i < 16; i++) {
				inv[i] = ChromaStacks.getShard(CrystalElement.elements[i]);
			}
		}

		if (craftingTick > 0) {
			this.onCraftingTick(world, x, y, z);
		}
	}

	private void onCraftingTick(World world, int x, int y, int z) {
		for (int i = 0; i < 16; i++) {
			InertItem ir = items[i];
			if (ir != null) {
				double ang = Math.toRadians(i*22.5+this.getTicksExisted()%360);
				double r = 5*Math.cos(ang)*Math.sin(ang);
				if (this.isCollecting()) {
					r *= craftingTick/40D;
				}
				double ix = r*Math.sin(ang);
				double iz = r*Math.cos(ang);
				double iy = 0.5+0.5*Math.cos(0.1*this.getTicksExisted()*0.002*ReikaMathLibrary.py3d(ix, 0, iz));
				ir.setPosition(ix, iy, iz);
			}
		}

		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		craftingTick--;

		if (craftingTick == 0) {

		}
	}

	@Override
	public void markDirty() {
		super.markDirty();

		for (int i = 0; i < 16; i++) {
			ItemStack is = inv[i];
			if (!ReikaItemHelper.matchStacks(is, items[i] != null ? items[i].getEntityItem() : null)) {
				InertItem ir = inv[i] != null ? new InertItem(worldObj, is) : null;
				items[i] = ir;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		Fluid f = tank.getActualFluid();

		for (int i = 0; i < sprays; i++) {
			if (phi[i] < phit[i]) {
				phi[i]++;
			}
			else if (phi[i] > phit[i]) {
				phi[i]--;
			}
			else {
				phit[i] = rand.nextInt(360);
			}

			if (theta[i] < thetat[i]) {
				theta[i]++;
			}
			else if (theta[i] > thetat[i]) {
				theta[i]--;
			}
			else {
				thetat[i] = 30+rand.nextInt(120);
			}

			//ReikaJavaLibrary.pConsole(Arrays.toString(phi)+":"+Arrays.toString(theta));

			double v = 0.1;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta[i], phi[i]);

			//double ang = Math.toRadians(this.getTicksExisted()*2%360);
			double r = 0.5;
			double px = x+0.5;//+r*Math.sin(ang);
			double py = y+1;
			double pz = z+0.5;//+r*Math.cos(ang);
			double vx = xyz[0];//v*Math.cos(ang);
			double vy = xyz[1];//0.2;
			double vz = xyz[2];//v*Math.sin(ang);
			EntityChromaFluidFX fx = new EntityChromaFluidFX(CrystalElement.WHITE, world, px, py, pz, vx, vy, vz).setScale(1.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canExtractItem(int side, ItemStack is, int slot) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 16;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ChromaItems.SHARD.matchWith(is) && is.getItemDamage() == slot;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INFUSER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public EntityItem getItem(int i) {
		return items[i];
	}

	@Override
	public int getCapacity() {
		return 4000;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("chroma");
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return from != ForgeDirection.UP;
	}

}
