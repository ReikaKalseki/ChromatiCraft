/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.TapeStage;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class BlockPistonController extends BlockDimensionStructureTile {

	private IIcon fireFront;
	private IIcon playFront;

	public BlockPistonController(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(meta) {
			case 0:
				return new TilePistonCycler();
			case 1:
				return new TilePistonTrigger();
			default:
				return null;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/piston_control");
		fireFront = ico.registerIcon("chromaticraft:dimstruct/piston_control_fire");
		playFront = ico.registerIcon("chromaticraft:dimstruct/piston_control_play");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int side) {
		TilePistonController te = (TilePistonController)iba.getTileEntity(x, y, z);
		if (te.getFacing().ordinal() != side)
			return blockIcon;
		switch(iba.getBlockMetadata(x, y, z)) {
			case 0:
				return playFront;
			case 1:
				return fireFront;
			default:
				return blockIcon;
		}
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote) {
			TilePistonController te = (TilePistonController)world.getTileEntity(x, y, z);
			if (te.getFacing().ordinal() == s)
				te.onRightClick();
		}
		return true;
	}

	public static class TilePistonCycler extends TilePistonController {

		private static final int DELAY = 70;

		private boolean isPlaying;
		private int tickUntilNextDoor = DELAY;

		@Override
		public void updateEntity() {
			//ReikaJavaLibrary.pConsole(this.getStage().doorCount, yCoord == 87);
			if (isPlaying) {
				if (tickUntilNextDoor > 0) {
					tickUntilNextDoor--;
				}
				else {
					//ReikaJavaLibrary.pConsole("Attempting cycle of "+currentDoor);
					if (this.getStage().cycle(worldObj)) {
						currentDoor++;
						//ReikaJavaLibrary.pConsole("Cycled to "+currentDoor);
						if (currentDoor == this.getStage().doorCount)
							tickUntilNextDoor = DELAY;
						if (currentDoor >= this.getStage().doorCount) {
							if (currentDoor >= this.getStage().getTotalLength()) {
								isPlaying = false;
								//ReikaJavaLibrary.pConsole("Done");
							}
							else {
								//ReikaJavaLibrary.pConsole("Returning...");
							}
						}
						else {
							//ReikaJavaLibrary.pConsole("Firing "+currentDoor);
							this.fire();
							tickUntilNextDoor = DELAY;
						}
					}
				}
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		protected void onRightClick() {
			currentDoor = 0;
			isPlaying = true;
			this.fire();
			tickUntilNextDoor = DELAY;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
		}

		private TilePistonTrigger getTrigger() {
			ForgeDirection dir = ReikaDirectionHelper.getRightBy90(this.getFacing());
			return (TilePistonTrigger)worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		}

	}

	public static class TilePistonTrigger extends TilePistonController {

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
		}

		@Override
		protected void onRightClick() {
			this.fire();
		}

	}

	public static abstract class TilePistonController extends StructureBlockTile<PistonTapeGenerator> {

		private ForgeDirection facing;
		private int stageIndex;
		protected int currentDoor;

		public ForgeDirection getFacing() {
			return facing != null ? facing : ForgeDirection.UNKNOWN;
		}

		public final TapeStage getStage() {
			return this.getGenerator().getStage(stageIndex);
		}

		protected abstract void onRightClick();

		public void setData(int stage, ForgeDirection dir) {
			stageIndex = stage;
			facing = dir;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (facing != null)
				NBT.setInteger("face", facing.ordinal());

			NBT.setInteger("stage", stageIndex);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("face"))
				facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];

			stageIndex = NBT.getInteger("stage");
		}

		protected final void fire() {
			this.getStage().fireEmitters(worldObj, currentDoor);
		}

		@Override
		public final DimensionStructureType getType() {
			return DimensionStructureType.PISTONTAPE;
		}

	}

}
