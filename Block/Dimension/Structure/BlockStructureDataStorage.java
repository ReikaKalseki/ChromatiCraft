/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;

public class BlockStructureDataStorage extends BlockContainer {

	public BlockStructureDataStorage(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityStructureDataStorage();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		((TileEntityStructureDataStorage)world.getTileEntity(x, y, z)).onRightClick(ep, s);
		return true;
	}

	public static class TileEntityStructureDataStorage extends TileEntity {

		private StructureData data;

		public void loadData(DimensionStructureGenerator gen) {
			data = gen.createDataStorage();
			if (data != null)
				data.load();
		}

		protected void onRightClick(EntityPlayer ep, int s) {
			data.onInteract(worldObj, xCoord, yCoord, zCoord, ep, s);
		}

	}

}
