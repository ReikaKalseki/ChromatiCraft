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

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator.MazePath;

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

		return true;
	}

	public static class TileEntityStructureDataStorage extends TileEntity {

		private List<MazePath> paths;

		public void loadData(DimensionStructureGenerator gen) {
			switch(this.getType()) {
			case SHIFTMAZE:
				paths = ((ShiftMazeGenerator)gen).getPaths();
				break;
			case MUSIC:
				break;
			}
		}

		public StructureType getType() {
			return StructureType.list[this.getBlockMetadata()];
		}

	}

	public static enum StructureType {
		SHIFTMAZE(),
		MUSIC();

		private static final StructureType[] list = values();
	}

}
