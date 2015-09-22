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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;

public class BlockStructureDataStorage extends BlockContainer {

	private final IIcon[] icons = new IIcon[2];

	public BlockStructureDataStorage(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityStructureDataStorage();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/dimdata");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/dimdata_side");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? icons[0] : icons[1];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote) {
			((TileEntityStructureDataStorage)world.getTileEntity(x, y, z)).onRightClick(ep, s);
		}
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
			if (data != null)
				data.onInteract(worldObj, xCoord, yCoord, zCoord, ep, s);
		}

	}

}
