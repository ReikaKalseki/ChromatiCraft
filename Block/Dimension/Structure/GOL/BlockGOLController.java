/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.GOL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.GOL.BlockGOLTile.GOLTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.GOLGenerator;

public class BlockGOLController extends BlockContainer {

	private final IIcon[] icons = new IIcon[3];

	public BlockGOLController(Material mat) {
		super(mat);
		this.setBlockUnbreakable();
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new GOLController();
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		if (!world.isRemote)
			this.activate(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote)
			this.activate(world, x, y, z);
		return true;
	}

	private void activate(World world, int x, int y, int z) {
		GOLController te = (GOLController)world.getTileEntity(x, y, z);
		if (te.isActive)
			te.reset();
		else
			te.activate();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/gol_control_play");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/gol_control_stop");
		icons[2] = ico.registerIcon("chromaticraft:dimstruct/gol_control_end");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? icons[2] : icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		if (s <= 1)
			return icons[2];
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof GOLController && ((GOLController)te).isActive) {
			return icons[1];
		}
		return super.getIcon(world, x, y, z, s);
	}

	public static class GOLController extends StructureBlockTile<GOLGenerator> {

		private int minX;
		private int maxX;
		private int minZ;
		private int maxZ;
		private int floorY;

		private boolean isActive;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void initialize(int x1, int x2, int z1, int z2, int y) {
			minX = x1;
			maxX = x2;
			minZ = z1;
			maxZ = z2;
			floorY = y;
		}

		private void activate() {
			isActive = true;
			//ReikaJavaLibrary.pConsole("activate");
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = worldObj.getBlock(x, floorY, z);
					if (b == ChromaBlocks.GOL.getBlockInstance()) {
						GOLTile te = (GOLTile)worldObj.getTileEntity(x, floorY, z);
						te.activate();
					}
				}
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		private void reset() {
			//ReikaJavaLibrary.pConsole("reset");
			this.getGenerator().checkConditions(worldObj);
			isActive = false;
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = worldObj.getBlock(x, floorY, z);
					if (b == ChromaBlocks.GOL.getBlockInstance()) {
						GOLTile te = (GOLTile)worldObj.getTileEntity(x, floorY, z);
						te.reset();

						worldObj.setBlockMetadataWithNotify(x, floorY+GOLGenerator.ROOM_HEIGHT, z, 2, 3);
					}
				}
			}
			this.getGenerator().clearTiles();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("minx", minX);
			NBT.setInteger("maxx", maxX);
			NBT.setInteger("minz", minZ);
			NBT.setInteger("maxz", maxZ);
			NBT.setInteger("posy", floorY);

			NBT.setBoolean("active", isActive);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			minX = NBT.getInteger("minx");
			maxX = NBT.getInteger("maxx");
			minZ = NBT.getInteger("minz");
			maxZ = NBT.getInteger("maxz");
			floorY = NBT.getInteger("posy");

			isActive = NBT.getBoolean("active");
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.GOL;
		}

	}
}
