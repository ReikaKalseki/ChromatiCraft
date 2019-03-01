/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Water;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.Lock;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterFloor;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRotatingLock extends Block {

	public BlockRotatingLock(Material mat) {
		super(mat);

		this.setResistance(6000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityRotatingLock();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return meta == 0 ? blockIcon : ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/waterlock");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote && this.hasTileEntity(world.getBlockMetadata(x, y, z)))
			((TileEntityRotatingLock)world.getTileEntity(x, y, z)).startRotating(ep.isSneaking());
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block other) {
		super.onNeighborBlockChange(world, x, y, z, other);
		if (this.hasTileEntity(world.getBlockMetadata(x, y, z)))
			((TileEntityRotatingLock)world.getTileEntity(x, y, z)).updateHasFluidState();
	}

	public static class TileEntityRotatingLock extends StructureBlockTile<WaterPuzzleGenerator> {

		private ForgeDirection direction = ForgeDirection.EAST;
		private int level;
		private int lockX;
		private int lockY;
		private boolean isEndpoint;
		private boolean isCheckpoint;

		private Collection<ForgeDirection> openEndsAtZero = new HashSet();
		private FilledBlockArray lockState;

		private int rotatingAmount;

		@Override
		public void updateEntity() {
			if (rotatingAmount > 0) {
				rotatingAmount = Math.min(rotatingAmount+3, 90);
				if (rotatingAmount == 90) {
					rotatingAmount = 0;
					this.finishRotating(false);
				}
				else {
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			else if (rotatingAmount < 0) {
				rotatingAmount = Math.max(rotatingAmount-3, -90);
				if (rotatingAmount == -90) {
					rotatingAmount = 0;
					this.finishRotating(true);
				}
				else {
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}

		private void updateHasFluidState() {
			WaterPuzzleGenerator gen = this.getGenerator();
			if (gen != null) {
				WaterFloor f = gen.getLevel(level);
				if (f != null) {
					boolean fluid = worldObj.getBlock(xCoord, yCoord+1, zCoord) == ChromaBlocks.EVERFLUID.getBlockInstance();
					/*
					if (fluid) {
						TileEntityEverFluid te = (TileEntityEverFluid)worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
						fluid = te.uid == uid && te.getLevel() == level;
					}
					 */
					//ReikaJavaLibrary.pConsole(level+">"+fluid);
					f.updateFluid(worldObj, lockX, lockY, fluid);
				}
			}
		}

		public ForgeDirection getDirection() {
			return direction != null ? direction : ForgeDirection.EAST;
		}

		public void setData(ForgeDirection dir, int lvl, int x, int y, boolean check, boolean end, Collection<ForgeDirection> c) {
			direction = dir;
			level = lvl;
			lockX = x;
			lockY = y;
			openEndsAtZero = c;
			isCheckpoint = check;
			isEndpoint = end;
		}

		public int getRotationProgress() {
			return rotatingAmount;
		}

		public boolean isCheckpoint() {
			return isCheckpoint;
		}

		public boolean isEndpoint() {
			return isEndpoint;
		}

		private void startRotating(boolean reverse) {
			if (rotatingAmount != 0)
				return;
			if (worldObj.isRemote)
				return;
			rotatingAmount = reverse ? -1 : 1;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ChromaSounds.WATERLOCK.playSoundAtBlock(this, 2, 1);
			lockState = new FilledBlockArray(worldObj);
			for (int i = -2; i <= 2; i++) {
				for (int k = -2; k <= 2; k++) {
					if (worldObj.getBlock(xCoord+i, yCoord+1, zCoord+k) == ChromaBlocks.EVERFLUID.getBlockInstance()) {
						worldObj.setBlock(xCoord+i, yCoord+1, zCoord+k, Blocks.air);
					}
					if (i != 0 || k != 0) {
						//f.addBlockCoordinate(xCoord+i, yCoord, zCoord+k);
						lockState.addBlockCoordinate(xCoord+i, yCoord+1, zCoord+k);
					}
				}
			}

			for (int i = -2; i <= 2; i++) {
				for (int k = -2; k <= 2; k++) {
					if (i != 0 || k != 0) {
						Block b = worldObj.getBlock(xCoord+i, yCoord+1, zCoord+k);
						if (b == Blocks.air || b == ChromaBlocks.EVERFLUID.getBlockInstance()) {
							worldObj.setBlock(xCoord+i, yCoord+1, zCoord+k, this.getBlockType(), 1, 3);
						}
						//f.addBlockCoordinate(xCoord+i, yCoord, zCoord+k);
						lockState.addBlockCoordinate(xCoord+i, yCoord+1, zCoord+k);
					}
				}
			}
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		}

		private void finishRotating(boolean reverse) {
			if (worldObj.isRemote)
				return;
			direction = reverse ? ReikaDirectionHelper.getLeftBy90(direction) : ReikaDirectionHelper.getRightBy90(direction);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone, 2, 0);
			WaterPuzzleGenerator gen = this.getGenerator();
			if (gen != null) {
				WaterFloor f = gen.getLevel(level);
				if (f != null) {
					f.rotateLock(lockX, lockY, reverse);
				}
			}
			if (lockState == null) {
				Thread.dumpStack();
				return;
			}
			//if (gen == null || worldObj.provider.dimensionId == 0) { //debug testing
			lockState = (FilledBlockArray)lockState.rotate90Degrees(xCoord, zCoord, reverse);
			lockState.place();
			//}
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		}

		public HashSet<ForgeDirection> getSidesOfState(boolean open) {
			HashSet<ForgeDirection> set = new HashSet();
			WaterPuzzleGenerator gen = this.getGenerator();
			if (gen != null) {
				WaterFloor f = gen.getLevel(level);
				if (f != null) {
					Lock l = f.getLock(lockX, lockY);
					for (int i = 2; i < 6; i++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
						if (l.isDirectionOpen(dir) == open) {
							set.add(dir);
						}
					}
				}
			}
			return set;
		}

		@SideOnly(Side.CLIENT)
		public Collection<ForgeDirection> getOpenEndsForRender() {
			Collection<ForgeDirection> ret = new ArrayList();
			for (ForgeDirection dir : openEndsAtZero) {
				ForgeDirection ref = direction;
				while (ref != ForgeDirection.EAST) {
					dir = ReikaDirectionHelper.getLeftBy90(dir);
					ref = ReikaDirectionHelper.getRightBy90(ref);
				}
				ret.add(dir);
			}
			//ReikaJavaLibrary.pConsole(openEndsAtZero+" > "+ret, xCoord == -5035 && zCoord == -2992 && level == 0);
			return ret;
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.WATER;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("dir", this.getDirection().ordinal());
			NBT.setInteger("lvl", level);
			NBT.setInteger("lockX", lockX);
			NBT.setInteger("lockY", lockY);
			NBT.setBoolean("check", isCheckpoint);
			NBT.setBoolean("end", isEndpoint);

			NBTTagList li = new NBTTagList();
			for (ForgeDirection dir : openEndsAtZero) {
				li.appendTag(new NBTTagInt(dir.ordinal()));
			}
			NBT.setTag("ends", li);

			NBT.setInteger("rotation", rotatingAmount);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			direction = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("dir")];
			level = NBT.getInteger("lvl");
			lockX = NBT.getInteger("lockX");
			lockY = NBT.getInteger("lockY");
			isCheckpoint = NBT.getBoolean("check");
			isEndpoint = NBT.getBoolean("end");

			openEndsAtZero.clear();
			NBTTagList li = NBT.getTagList("ends", NBTTypes.INT.ID);
			for (Object o : li.tagList) {
				openEndsAtZero.add(ForgeDirection.VALID_DIRECTIONS[((NBTTagInt)o).func_150287_d()]);
			}

			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				rotatingAmount = NBT.getInteger("rotation");
			}
		}

		@Override
		public double getMaxRenderDistanceSquared() {
			return super.getMaxRenderDistanceSquared();//*4;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(this).expand(Lock.SIZE, 3, Lock.SIZE);
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return true;
		}

	}

}
