/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;

public class BlockChromaPortal extends Block {

	public BlockChromaPortal(Material mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 1 ? new TileEntityPortal() : null;
	}

	public static class TileEntityPortal extends TileEntity {

		private boolean complete;

		public void validateStructure(World world, int x, int y, int z) {
			complete = ChromaStructures.getPortalStructure(world, x, y, z).matchInWorld();
			complete &= this.getEntities(world, x, y, z);
		}

		private boolean getEntities(World world, int x, int y, int z) {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("built", complete);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			NBT.setBoolean("built", complete);
		}

	}

}
