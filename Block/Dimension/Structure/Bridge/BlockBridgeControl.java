/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Bridge;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;


public class BlockBridgeControl extends BlockDimensionStructureTile {

	private final IIcon[] icons = new IIcon[4];

	public BlockBridgeControl(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileBridgeControl();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		((TileBridgeControl)world.getTileEntity(x, y, z)).updateState(world, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (s == 1)
			meta += 2;
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/bridgekey_off");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/bridgekey_on");
		icons[2] = ico.registerIcon("chromaticraft:dimstruct/bridgekey_off_top");
		icons[3] = ico.registerIcon("chromaticraft:dimstruct/bridgekey_on_top");
	}

	public static class TileBridgeControl extends StructureBlockTile<BridgeGenerator> {

		private UUID switchID;
		private boolean isActive;

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.BRIDGE;
		}

		public void updateState(World world, int x, int y, int z) {
			isActive = world.isBlockIndirectlyGettingPowered(x, y, z);
			BridgeGenerator gen = this.getGenerator();
			if (gen != null) {
				gen.updateControl(world, switchID, isActive);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setString("key", switchID.toString());
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			switchID = UUID.fromString(NBT.getString("key"));
		}

		public void setData(UUID structure, UUID key) {
			uid = structure;
			switchID = key;
		}

	}
}
