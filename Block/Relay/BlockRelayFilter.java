/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Relay;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;


public class BlockRelayFilter extends BlockContainer {

	public BlockRelayFilter(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(0.5F);
		this.setResistance(5);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRelayFilter();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/relayfilter");
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		ReikaParticleHelper.spawnColoredParticlesWithOutset(world, x, y, z, 1, 1, 1, 8, 0.0625);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
	}

	public static class TileEntityRelayFilter extends TileEntity implements GuiController {

		private boolean[] filter = ReikaArrayHelper.getTrueArray(16);

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			filter = ReikaArrayHelper.booleanFromBitflags(NBT.getInteger("filter"), 16);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("filter", ReikaArrayHelper.booleanToBitflags(filter));
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

		public boolean canTransmit(CrystalElement e) {
			return filter[e.ordinal()];
		}

		public boolean[] getFilter() {
			return Arrays.copyOf(filter, filter.length);
		}

		public void setFlag(CrystalElement e, boolean allow) {
			filter[e.ordinal()] = allow;
			//ReikaJavaLibrary.pConsole(Arrays.toString(filter));
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

}
