package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.OverlayColor;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.DragonAPICore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRayblendFloor extends BlockDimensionStructureTile {

	public BlockRayblendFloor(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRayblendFloor();
	}
	/*
	@Override
	public IIcon getIcon(int s, int meta) {
		return ChromaBlocks.SPECIALSHIELD.getBlockInstance().getIcon(s, BlockType.GLASS.metadata);
	}*/

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.rayblendFloorRender;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/rayblendfloor");
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntityRayblendFloor te = (TileEntityRayblendFloor)world.getTileEntity(x, y, z);
		OverlayColor e = te.getOverlayColor();
		return e != null ? e.getColor() : 0xffffff;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		TileEntityRayblendFloor te = (TileEntityRayblendFloor)world.getTileEntity(x, y, z);
		te.blockUpdate(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z) != this;
	}

	@Override
	protected boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack held = ep.getCurrentEquippedItem();
		Block bk = held != null ? Block.getBlockFromItem(held.getItem()) : null;
		if (bk instanceof CrystalTypeBlock) {
			CrystalElement e = CrystalElement.elements[held.getItemDamage()];
			TileEntityRayblendFloor te = (TileEntityRayblendFloor)world.getTileEntity(x, y, z);
			if (!te.allowsCrystalAt(x, z, e)) {
				ChromaSounds.ERROR.playSoundAtBlock(te);
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public static class TileEntityRayblendFloor extends StructureBlockTile<RayBlendGenerator> {

		private UUID puzzleID;
		private UUID gridID = DragonAPICore.Reika_UUID;
		private int tileX;
		private int tileZ;

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.RAYBLEND;
		}

		public boolean allowsCrystalAt(int x, int z, CrystalElement e) {
			RayBlendGenerator g = this.getGenerator();
			return g != null && g.allowsCrystalAt(worldObj, puzzleID, x, z, e);
		}

		public OverlayColor getOverlayColor() {
			RayBlendGenerator g = this.getGenerator();
			return g != null ? g.getCageColor(puzzleID, tileX, tileZ) : null;
		}

		private void blockUpdate(World world, int x, int y, int z) {
			RayBlendGenerator g = this.getGenerator();
			if (g == null)
				return;
			Block b = world.getBlock(x, y+1, z);
			int meta = world.getBlockMetadata(x, y+1, z);
			CrystalElement e = b instanceof CrystalTypeBlock ? CrystalElement.elements[meta] : null;
			if (e != null) {
				/*
				if (!g.allowsCrystalAt(puzzleID, x, z, e)) {
					ReikaItemHelper.dropItem(world, x+0.5, y+1.5, z+0.5, new ItemStack(b, 1, meta));
					world.setBlockToAir(x, y+1, z);
					ChromaSounds.ERROR.playSoundAtBlock(this);
					return;
				}*/
			}
			g.setCrystal(world, puzzleID, tileX, tileZ, e);
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void populate(UUID uid, UUID grid, int x, int z) {
			puzzleID = uid;
			gridID = grid;
			tileX = x;
			tileZ = z;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (puzzleID != null)
				NBT.setString("puzzle", puzzleID.toString());
			if (gridID != null)
				NBT.setString("grid", gridID.toString());
			NBT.setInteger("tileX", tileX);
			NBT.setInteger("tileZ", tileZ);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("puzzle"))
				puzzleID = UUID.fromString(NBT.getString("puzzle"));
			if (NBT.hasKey("grid"))
				gridID = UUID.fromString(NBT.getString("grid"));
			tileX = NBT.getInteger("tileX");
			tileZ = NBT.getInteger("tileZ");
		}

		public UUID getGridID() {
			return gridID;
		}

	}

}
