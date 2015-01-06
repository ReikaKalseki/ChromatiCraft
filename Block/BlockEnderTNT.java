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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.GuiController;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class BlockEnderTNT extends Block {

	private IIcon top;
	private IIcon side;
	private IIcon bottom;
	private IIcon active;

	public BlockEnderTNT(Material mat) {
		super(mat);
		blockHardness = 1;
		blockResistance = 5;
		stepSound = soundTypeStone;
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityEnderTNT();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(s) {
		case 0:
			return bottom;
		case 1:
			return top;
		default:
			return meta > 0 ? active : side;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		side = ico.registerIcon("chromaticraft:basic/enderbomb_side");
		top = ico.registerIcon("chromaticraft:basic/enderbomb_top");
		bottom = ico.registerIcon("chromaticraft:basic/enderbomb_bottom");
		active = ico.registerIcon("chromaticraft:basic/enderbomb_active");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
			((TileEntityEnderTNT)world.getTileEntity(x, y, z)).prime(100);
		else
			ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityEnderTNT)
			((TileEntityEnderTNT)te).testDetonation();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityEnderTNT)
			((TileEntityEnderTNT)te).testDetonation();
	}

	public static class TileEntityEnderTNT extends TileEntity implements GuiController {

		private WorldLocation target = null;
		private int countdown = -1;
		private int existed = 0;
		private String owner = null;
		private int fuse;

		public void testDetonation() {
			if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
				this.prime(200);
		}

		public void prime(int delay) {
			if (target != null && owner != null) {
				fuse = delay;
				countdown = fuse;
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 3);
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "game.tnt.primed");
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		@Override
		public void updateEntity() {
			if (existed == 0) {
				if (target == null)
					target = new WorldLocation(this);
				this.testDetonation();
			}
			existed++;
			if (countdown == 0) {
				this.detonate();
			}
			else if (countdown > 0) {
				countdown--;
			}

			if (countdown > 0) {
				ReikaParticleHelper.SMOKE.spawnAt(worldObj, xCoord+0.5, yCoord+1, zCoord+0.5);
				if (countdown%10 == 0 || (countdown <= fuse*2/5 && countdown%5 == 0) || countdown <= fuse/10) {
					float p = countdown <= fuse/10 ? 1.5F : countdown < fuse*2/5 ? 1.25F : countdown%20 == 0 ? 1F : 1.25F;
					ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 1, p);
				}
			}
		}

		private void detonate() {
			if (target != null && target.getWorld() != null && !target.getWorld().isRemote) {
				World w = target.dimensionID == worldObj.provider.dimensionId ? worldObj : target.getWorld();
				new FlyingBlocksExplosion(w, target.xCoord+0.5, target.yCoord+0.5, target.zCoord+0.5, 4).doExplosion();
				if (!worldObj.isRemote)
					worldObj.createExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 4, true);
				String s = owner+" has just detonated Ender TNT at "+target;
				ChromatiCraft.logger.log(s);
				ReikaPlayerAPI.notifyAdmins(s);
			}
		}

		public void setTarget(EntityPlayer ep, int dim, int x, int y, int z) {
			target = new WorldLocation(dim, x, y, z);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			owner = ep.getCommandSenderName();
		}

		public int getCountdown() {
			return countdown;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("tick", countdown);
			if (target != null)
				target.writeToNBT("loc", NBT);
			if (owner != null && !owner.isEmpty())
				NBT.setString("owner", owner);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			target = WorldLocation.readFromNBT("loc", NBT);
			countdown = NBT.getInteger("tick");
			owner = NBT.getString("owner");
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

		public WorldLocation getTarget() {
			return target;
		}

	}
}
