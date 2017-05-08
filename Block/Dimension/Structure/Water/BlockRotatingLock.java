package Reika.ChromatiCraft.Block.Dimension.Structure.Water;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterFloor;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class BlockRotatingLock extends BlockContainer {

	public BlockRotatingLock(Material mat) {
		super(mat);

		this.setResistance(6000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRotatingLock();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		((TileEntityRotatingLock)world.getTileEntity(x, y, z)).rotate();
		return true;
	}

	public static class TileEntityRotatingLock extends StructureBlockTile<WaterPuzzleGenerator> {

		private ForgeDirection direction = ForgeDirection.EAST;
		private int level;
		private int lockX;
		private int lockY;

		public ForgeDirection getDirection() {
			return direction != null ? direction : ForgeDirection.EAST;
		}

		public void setData(ForgeDirection dir, int lvl, int x, int y) {
			direction = dir;
			level = lvl;
			lockX = x;
			lockY = y;
		}

		public void rotate() {
			direction = ReikaDirectionHelper.getRightBy90(direction);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.stone);
			WaterPuzzleGenerator gen = this.getGenerator();
			if (gen != null) {
				WaterFloor f = gen.getLevel(level);
				if (f != null) {
					f.rotateLock(lockX, lockY);
				}
			}
			if (gen == null || worldObj.provider.dimensionId == 0) { //debug testing
				FilledBlockArray f = new FilledBlockArray(worldObj);
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (worldObj.getBlock(xCoord+i, yCoord+1, zCoord+k) == ChromaBlocks.EVERFLUID.getBlockInstance()) {
							worldObj.setBlock(xCoord+i, yCoord+1, zCoord+k, Blocks.air);
						}
						if (i != 0 || k != 0) {
							//f.addBlockCoordinate(xCoord+i, yCoord, zCoord+k);
							f.addBlockCoordinate(xCoord+i, yCoord+1, zCoord+k);
						}
					}
				}
				f = (FilledBlockArray)f.rotate90Degrees(xCoord, zCoord, false);
				f.place();
			}
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
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
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			direction = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("dir")];
			level = NBT.getInteger("lvl");
			lockX = NBT.getInteger("lockX");
			lockY = NBT.getInteger("lockY");
		}

	}

}
