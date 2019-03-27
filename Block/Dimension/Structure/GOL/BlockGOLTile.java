/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.GOL;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDimensionStructure;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.GOLGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class BlockGOLTile extends BlockDimensionStructure {

	private final IIcon[] icons = new IIcon[4];

	public BlockGOLTile(Material mat) {
		super(mat);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		if (iba.getBlockMetadata(x, y, z) == 3)
			return 15;
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof GOLTile && ((GOLTile)te).isActive() ? 15 : 6;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta < 2;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta < 2 ? new GOLTile() : null;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		if (!world.isRemote)
			this.toggle(world, x, y, z);
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote)
			this.toggle(world, x, y, z);
		return true;
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity e, float d) {
		if (!world.isRemote && e instanceof EntityPlayer && d > 1)
			this.toggle(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		((GOLTile)world.getTileEntity(x, y, z)).recalculate();
	}

	private void activate(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) >= 2)
			return;
		GOLTile te = (GOLTile)world.getTileEntity(x, y, z);
		te.setActive(true);
		//te.scheduleRecalculation(5);
	}

	private void toggle(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) >= 2)
			return;
		GOLTile te = (GOLTile)world.getTileEntity(x, y, z);
		if (te.getGenerator() == null) {
			ChromatiCraft.logger.logError("No structure for tile at "+x+", "+y+", "+z+", UID="+te.uid+"!?!");
			return;
		}
		if (te.isTicking) {
			ChromaSounds.ERROR.playSoundAtBlock(world, x, y, z);
			return;
		}
		boolean flag = true;
		if (te.isActive()) {
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.75F, 0.65F);
			te.getGenerator().deactivateTile(world, x, y, z);
			world.setBlockMetadataWithNotify(x, y+GOLGenerator.ROOM_HEIGHT, z, 2, 3);
		}
		else {
			flag = te.getGenerator().activateTile(world, x, y, z);
			if (flag)
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.75F, 0.75F);
			else
				ChromaSounds.ERROR.playSoundAtBlock(world, x, y, z);
		}
		if (flag)
			te.setActive(!te.isActive());
		//te.scheduleRecalculation(5);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/gol_off");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/gol_on");

		icons[2] = ico.registerIcon("chromaticraft:dimstruct/gol_mem_off");
		icons[3] = ico.registerIcon("chromaticraft:dimstruct/gol_mem_on");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (meta >= 2)
			return icons[meta];
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta >= 2)
			return icons[meta];
		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof GOLTile) {
			return ((GOLTile)te).isActive() ? icons[1] : icons[0];
		}
		else {
			return ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(BlockType.STONE.ordinal(), 0);
		}
	}

	public static class GOLTile extends StructureBlockTile<GOLGenerator> {

		private boolean defaultActive;
		private boolean isActive;
		private int numberNeighbors;
		private boolean isTicking;

		private static final int OFFSET = 1;
		private static final int RATE = 5;

		@Override
		public boolean canUpdate() {
			return true;//false;
		}

		/*
		private void scheduleRecalculation(int ticks) {
			worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlockType(), ticks);
		}
		 */

		public void activate() {
			isTicking = true;
		}

		public void reset() {
			this.setActive(defaultActive && false);
			isTicking = false;
		}

		@Override
		public void updateEntity() {
			if (isTicking) {
				int mod = (int)(worldObj.getTotalWorldTime()%RATE);
				if (mod == 0) {
					numberNeighbors = this.countNeighbors();
				}
				else if (mod == OFFSET) {
					this.recalculate();
				}
			}
		}

		private void recalculate() {
			if (isActive) {
				if (numberNeighbors < 2 || numberNeighbors > 3) {
					this.setActive(false);
				}
			}
			else if (numberNeighbors == 3) {
				this.setActive(true);
			}

		}

		public void initialize(boolean active) {
			defaultActive = active;
			isActive = active;
		}

		public boolean isActive() {
			return isActive;
		}

		private void setActive(boolean ac) {
			isActive = ac;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (ac) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord+GOLGenerator.ROOM_HEIGHT, zCoord, 3, 3);
			}
			//this.updateNeighbors();
		}

		private int countNeighbors() {
			int c = 0;
			boolean flag = xCoord == -1 && zCoord == 0;
			for (int a1 = -1; a1 <= 1; a1++) {
				for (int a2 = -1; a2 <= 1; a2++) {
					if (a1 != 0 || a2 != 0) {
						int dx = xCoord+a1;
						int dz = zCoord+a2;
						Block b = worldObj.getBlock(dx, yCoord, dz);
						if (b == ChromaBlocks.GOL.getBlockInstance()) {
							GOLTile te = (GOLTile)worldObj.getTileEntity(dx, yCoord, dz);
							if (te.isActive())
								c++;
						}
					}
				}
			}
			return c;
		}

		private void updateNeighbors() {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = xCoord+dir.offsetX;
				int dz = zCoord+dir.offsetY;
				Block b = worldObj.getBlock(dx, yCoord, dz);
				if (b == ChromaBlocks.GOL.getBlockInstance()) {
					GOLTile te = (GOLTile)worldObj.getTileEntity(dx, yCoord, dz);
					te.recalculate();
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("active", isActive);
			NBT.setBoolean("def", defaultActive);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isActive = NBT.getBoolean("active");
			defaultActive = NBT.getBoolean("def");
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.GOL;
		}

	}
}
