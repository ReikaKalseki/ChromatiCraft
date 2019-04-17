package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

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
		CrystalElement e = te.getOverlayColor();
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

	public static class TileEntityRayblendFloor extends StructureBlockTile<RayBlendGenerator> {

		private UUID puzzleID;
		private UUID gridID = DragonAPICore.Reika_UUID;
		private int tileX;
		private int tileZ;

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.RAYBLEND;
		}

		public CrystalElement getOverlayColor() {
			RayBlendGenerator g = this.getGenerator();
			return g != null ? g.getCageColor(puzzleID, tileX, tileZ) : null;
		}

		private void blockUpdate(World world, int x, int y, int z) {
			RayBlendGenerator g = this.getGenerator();
			if (g == null)
				return;
			CrystalElement e = world.getBlock(x, y+1, z) instanceof CrystalTypeBlock ? CrystalElement.elements[world.getBlockMetadata(x, y+1, z)] : null;
			if (e != null) {
				if (!g.allowsCrystalAt(puzzleID, x, z, e)) {
					ReikaWorldHelper.dropAndDestroyBlockAt(world, x, y+1, z, null, true, true);
					ChromaSounds.ERROR.playSoundAtBlock(this);
					return;
				}
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
