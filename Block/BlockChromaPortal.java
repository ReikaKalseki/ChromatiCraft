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
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class BlockChromaPortal extends Block {

	public BlockChromaPortal(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setResistance(50000);
		this.setBlockUnbreakable();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 1 ? new TileEntityPortal() : null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (true || te instanceof TileEntityPortal && ((TileEntityPortal)te).complete) {
			ReikaEntityHelper.transferEntityToDimension(e, ExtraChromaIDs.DIMID.getValue(), new ChromaTeleporter());
		}
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

	public static class ChromaTeleporter extends Teleporter {

		public ChromaTeleporter() {
			super(MinecraftServer.getServer().worldServerForDimension(ExtraChromaIDs.DIMID.getValue()));
		}

	}

}
