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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey.KeyIO;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.TapeStage;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;

public class BlockPistonController extends BlockDimensionStructureTile {

	private IIcon fireFront;
	private IIcon playFront;
	private IIcon stepFront;

	public BlockPistonController(Material mat) {
		super(mat);
		this.setLightLevel(12);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(meta) {
			case 0:
				return new TilePistonPlayback();
			case 1:
				return new TilePistonTrigger();
			case 2:
				return new TilePistonCycler();
			case 3:
				return new TilePistonDisplay();
			default:
				return null;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/piston_control");
		fireFront = ico.registerIcon("chromaticraft:dimstruct/piston_control_fire");
		playFront = ico.registerIcon("chromaticraft:dimstruct/musicmemory_front"); // piston_control_play
		stepFront = ico.registerIcon("chromaticraft:dimstruct/piston_control_step");
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
			case 2:
				return stepFront;
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

	public static class TilePistonPlayback extends TilePistonController {

		private static final int DELAY = 70;

		private boolean isPlaying;
		private int tickUntilNextDoor = DELAY;

		@Override
		public boolean canUpdate() {
			return true;
		}

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
						this.setDoor(currentDoor+1);
						//ReikaJavaLibrary.pConsole("Cycled to "+currentDoor);
						if (currentDoor == this.getStage().doorCount)
							tickUntilNextDoor = DELAY;
						if (currentDoor >= this.getStage().doorCount) {
							if (currentDoor >= this.getStage().getTotalLength()) {
								this.setPlaying(false);
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
		protected void onRightClick() {
			this.setDoor(0);
			this.setPlaying(true);
			this.fire();
			tickUntilNextDoor = DELAY;
		}

		private void setPlaying(boolean play) {
			isPlaying = play;
			this.getDisplay().setActive(play);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
		}

		private TilePistonDisplay getDisplay() {
			ForgeDirection dir = ReikaDirectionHelper.getLeftBy90(this.getFacing());
			return (TilePistonDisplay)worldObj.getTileEntity(xCoord+dir.offsetX*2, yCoord+dir.offsetY-1, zCoord+dir.offsetZ*2);
		}

		@Override
		protected void setDoor(int door) {
			super.setDoor(door);
			this.getDisplay().setDoor(door);
		}

	}

	public static class TilePistonCycler extends TilePistonController {

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
			this.setDoor(currentDoor+1);
			TilePistonController te = (TilePistonController)worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
			te.setDoor(currentDoor);
			this.getStage().cycle(worldObj);
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

	public static class TilePistonDisplay extends TilePistonController {

		private List<DoorKey> display = new ArrayList();
		private boolean active;

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
			NBT.setBoolean("active", active);
			ReikaNBTHelper.writeCollectionToNBT(display, NBT, "display", KeyIO.instance);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			active = NBT.getBoolean("active");
			ReikaNBTHelper.readCollectionFromNBT(display, NBT, "display", KeyIO.instance);
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(this).expand(3, 3, 3);
		}

		@Override
		protected void onRightClick() {
			if (!worldObj.isRemote)
				this.read();
		}

		public void read() {
			TapeStage s = this.getStage();
			if (s != null) {
				display = new ArrayList(s.getDisplayList());
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		public List<DoorKey> getDisplayList() {
			return display;
		}

		public int getActiveDoor() {
			return currentDoor-1;
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (!worldObj.isRemote && display.isEmpty()) {
				this.read();
			}
		}

		private void setActive(boolean flag) {
			active = flag;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public boolean isActive() {
			return active;
		}

	}

	public static abstract class TilePistonController extends StructureBlockTile<PistonTapeGenerator> {

		private ForgeDirection facing;
		private int stageIndex;
		protected int currentDoor;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public ForgeDirection getFacing() {
			return facing != null ? facing : ForgeDirection.UNKNOWN;
		}

		public final TapeStage getStage() {
			return this.getGenerator().getStage(stageIndex);
		}

		protected abstract void onRightClick();

		public final void setData(int stage, ForgeDirection dir) {
			stageIndex = stage;
			facing = dir;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (facing != null)
				NBT.setInteger("face", facing.ordinal());

			NBT.setInteger("stage", stageIndex);
			NBT.setInteger("door", currentDoor);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("face"))
				facing = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("face")];

			stageIndex = NBT.getInteger("stage");
			currentDoor = NBT.getInteger("door");
		}

		protected final void fire() {
			if (this.getGenerator() == null)
				return;
			this.getStage().fireEmitters(worldObj, currentDoor);
		}

		@Override
		public final DimensionStructureType getType() {
			return DimensionStructureType.PISTONTAPE;
		}

		protected void setDoor(int door) {
			currentDoor = door;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

}
