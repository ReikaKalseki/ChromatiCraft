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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiner extends TileEntityChromaticBase {

	private boolean digging;

	private int range = 512;

	private int readX = 0;
	private int readY = 0;
	private int readZ = 0;

	private static final int len = 2;
	private final double[] particleX = new double[len];
	private final double[] particleY = new double[len];
	private final double[] particleZ = new double[len];
	private final int[] particleIndex;
	private static final double[][] coords = new double[6][3];

	private static int TICKSTEP = 256;

	static {
		coords[1][0] = 1;

		coords[2][0] = 1;
		coords[2][1] = 1;

		coords[3][0] = 1;
		coords[3][1] = 1;
		coords[3][2] = 1;

		coords[4][1] = 1;
		coords[4][2] = 1;

		coords[5][2] = 1;
	}

	public TileEntityMiner() {
		particleIndex = new int[len];
		for (int i = 0; i < len; i++) {
			int idx = i*coords.length/len;
			particleIndex[i] = idx;
			particleX[i] = coords[idx][0];
			particleY[i] = coords[idx][1];
			particleZ[i] = coords[idx][2];
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MINER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			for (int i = 0; i < TICKSTEP*8 && digging; i++) {
				int dx = x+readX;
				int dy = readY;
				int dz = z+readZ;
				ReikaWorldHelper.forceGenAndPopulate(world, dx, dy, dz, meta);
				Block id = world.getBlock(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				//ReikaJavaLibrary.pConsole(readX+":"+dx+", "+dy+", "+readZ+":"+dz+" > "+ores.getSize(), Side.SERVER);
				if (ReikaBlockHelper.isOre(id, meta2)) {
					//ores.addBlockCoordinate(dx, dy, dz);
					this.dropBlock(world, x, y, z, dx, dy, dz, id, meta2);
				}
				this.updateReadPosition();
			}
		}
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		for (int i = 0; i < particleIndex.length; i++) {
			double px = x+particleX[i];
			double py = y+particleY[i];
			double pz = z+particleZ[i];

			if (this.getTicksExisted()%2 == 0) {
				EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(50);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);

				px = x+1-particleX[i];
				//py = y+1-particleY[i];
				//pz = z+1-particleZ[i];
				fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(50);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			double d = 0.05;
			double tx = coords[particleIndex[i]][0];
			double ty = coords[particleIndex[i]][1];
			double tz = coords[particleIndex[i]][2];
			double vx = Math.signum(tx-particleX[i])*d;
			double vy = Math.signum(ty-particleY[i])*d;
			double vz = Math.signum(tz-particleZ[i])*d;
			particleX[i] += vx;
			particleY[i] += vy;
			particleZ[i] += vz;
			particleX[i] = MathHelper.clamp_double(particleX[i], 0, 1);
			particleY[i] = MathHelper.clamp_double(particleY[i], 0, 1);
			particleZ[i] = MathHelper.clamp_double(particleZ[i], 0, 1);
			boolean step = particleX[i] == tx && particleY[i] == ty && particleZ[i] == tz;
			if (step)
				particleIndex[i] = (particleIndex[i]+1)%coords.length;
		}
	}

	private void dropBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		ArrayList<ItemStack> li = id.getDrops(world, dx, dy, dz, meta2, 0);
		for (ItemStack is : li) {
			boolean flag = true;
			for (int i = 0; i < 6 && flag; i++) {
				TileEntity te = this.getAdjacentTileEntity(dirs[i]);
				if (te instanceof IInventory) {
					if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
						flag = false;
				}
			}
			if (flag)
				ReikaItemHelper.dropItem(world, x+0.5, y+1.5, z+0.5, is);
		}
		world.setBlock(dx, dy, dz, Blocks.stone);
	}

	private void updateReadPosition() {
		boolean flag1 = false;
		boolean flag2 = false;
		readX++;
		if (readX > range) {
			readX = -range;
			flag1 = true;
		}
		if (flag1) {
			readZ++;
			//ReikaJavaLibrary.pConsole(readY+" > "+readZ+":"+range+" > "+ores.getSize(), Side.SERVER);
			if (readZ > range) {
				readZ = -range;
				flag2 = true;
			}
			if (flag2) {
				readY++;
			}
		}
		if (readY >= worldObj.getActualHeight())
			digging = false;
	}

	public void triggerCalculation() {
		digging = true;
		readX = -range;
		readY = 1;
		readZ = -range;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
