/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

@Deprecated
public class BlockLoreReader extends BlockContainer {

	public BlockLoreReader(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(2);
		this.setResistance(6000);
		//this.setLightLevel(1);
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLoreReader();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		//if (!ChromaItems.DATACRYSTAL.matchWith(ep.getCurrentEquippedItem()) && ((TileEntityLoreReader)world.getTileEntity(x, y, z)).hasCrystal() && ChromaStructures.getLoreReaderStructure(world, x, y, z).matchInWorld()) {
		//ep.openGui(ChromatiCraft.instance, ChromaGuis.LORE.ordinal(), world, x, y, z);
		//}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		TileEntityLoreReader te = (TileEntityLoreReader)world.getTileEntity(x, y, z);
		if (te.hasCrystal) {
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, ChromaItems.DATACRYSTAL.getStackOf());
		}
		super.breakBlock(world, x, y, z, b, meta);
	}

	public static class TileEntityLoreReader extends TileEntity {

		private boolean hasCrystal = false;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void addCrystal() {
			hasCrystal = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public boolean hasCrystal() {
			return hasCrystal;
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			hasCrystal = NBT.getBoolean("crystal");
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
			NBT.setBoolean("crystal", hasCrystal);
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

}
