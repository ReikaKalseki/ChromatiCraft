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

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiner extends ChargedCrystalPowered {

	private boolean digging;

	private int range = 512;

	private int readX = 0;
	private int readY = 0;
	private int readZ = 0;

	private double particleX;
	private double particleY;
	private double particleVX;
	private double particleVY;

	private static int TICKSTEP = 2048;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.YELLOW, 50);
		required.addTag(CrystalElement.LIME, 30);
		required.addTag(CrystalElement.GRAY, 20);
		required.addTag(CrystalElement.BROWN, 40);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MINER;
	}

	public int getReadX() {
		return readX;
	}

	public int getReadY() {
		return readY;
	}

	public int getReadZ() {
		return readZ;
	}

	public int getRange() {
		return range;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		digging = true;
		if (!world.isRemote) {
			for (int i = 0; i < TICKSTEP && digging && this.hasEnergy(required); i++) {
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
				else if (this.isTieredResource(world, dx, dy, dz, id, meta2)) {
					this.dropTieredResource(world, x, y, z, dx, dy, dz, id, meta2);
				}
				this.useEnergy(required);
				this.updateReadPosition();
			}
		}
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	private void dropTieredResource(World world, int x, int y, int z, int dx,int dy, int dz, Block id, int meta2) {
		Collection<ItemStack> li = ((BlockTieredResource)id).getHarvestResources(world, dx, dy, dz, 10, this.getPlacer());
		this.dropItems(world, x, y, z, li);
		world.setBlock(dx, dy, dz, id.getMaterial().isSolid() ? Blocks.stone : Blocks.air);
	}

	private boolean isTieredResource(World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = this.getPlacer();
		return ep != null && id instanceof BlockTieredResource && ((BlockTieredResource)id).isPlayerSufficientTier(world, x, y, z, ep);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double px = x+particleX;
		double py = y+particleY;
		double pz = z;

		int color = CrystalElement.getBlendedColor(this.getTicksExisted(), 40);
		int red = ReikaColorAPI.getRedFromInteger(color);
		int green = ReikaColorAPI.getGreenFromInteger(color);
		int blue = ReikaColorAPI.getBlueFromInteger(color);

		EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1;
		px = x+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x;
		pz = z+particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+1-particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		px = x+1;
		pz = z+1-particleX;
		py = y+particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		pz = z+particleX;
		py = y+1-particleY;
		fx = new EntityBlurFX(world, px, py, pz).setScale(0.5F).setLife(40).setColor(red, green, blue);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		double d = 0.05;
		particleX += particleVX;
		particleY += particleVY;
		particleX = MathHelper.clamp_double(particleX, 0, 1);
		particleY = MathHelper.clamp_double(particleY, 0, 1);

		if (particleX == 1 && particleY == 0) {
			particleVX = 0;
			particleVY = d;
		}
		if (particleY == 1 && particleY == 1) {
			particleVX = -d;
			particleVY = 0;
		}
		if (particleX == 0 && particleY == 1) {
			particleVX = 0;
			particleVY = -d;
		}
		if (particleX == 0 && particleY == 0) {
			particleVX = d;
			particleVY = 0;
		}
	}

	private void dropBlock(World world, int x, int y, int z, int dx, int dy, int dz, Block id, int meta2) {
		if (this.silkTouch()) {
			this.dropItems(world, x, y, z, ReikaJavaLibrary.makeListFrom(new ItemStack(id, 1, meta2)));
		}
		else {
			this.dropItems(world, x, y, z, id.getDrops(world, dx, dy, dz, meta2, 0));
		}
		world.setBlock(dx, dy, dz, Blocks.stone);
	}

	private boolean silkTouch() {
		return ReikaItemHelper.matchStacks(inv[1], ChromaStacks.silkUpgrade);
	}

	private void dropItems(World world, int x, int y, int z, Collection<ItemStack> li) {
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
	/*
	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		silkTouch = NBT.getBoolean("silk");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("silk", silkTouch);
	}*/

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		switch(slot) {
		case 0:
			return ChromaItems.STORAGE.matchWith(is);
		case 1:
			return ReikaItemHelper.matchStacks(is, ChromaStacks.silkUpgrade);
		default:
			return false;
		}
	}

}
