package Reika.ChromatiCraft.Block.Dimension;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockStructureDataStorage extends BlockContainer {

	public BlockStructureDataStorage(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityStructureDataStorage();
	}

	public static class TileEntityStructureDataStorage extends TileEntity {

	}

	public static enum StructureType {
		SHIFTMAZE(),
		LOCKS();
	}

}
