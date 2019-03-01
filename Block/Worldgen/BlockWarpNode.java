/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Magic.WarpNetwork;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class BlockWarpNode extends BlockContainer {

	public static final double ANGLE_TOLERANCE = 15;

	public BlockWarpNode(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(6000000);
		this.setBlockUnbreakable();
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote && ChromaItems.TOOL.matchWith(ep.getCurrentEquippedItem())) {
			TileEntityWarpNode te = (TileEntityWarpNode)world.getTileEntity(x, y, z);
			te.open();
			ChromaSounds.USE.playSoundAtBlock(te);
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)e;
			long last = ep.getEntityData().getLong("lastWarpNode");
			if (world.getTotalWorldTime()-last > 200) {
				double vx = ep.posX-ep.prevPosX;
				double vz = ep.posZ-ep.prevPosZ;
				if (vx != 0 || vz != 0) {
					double ang = ReikaPhysicsHelper.cartesianToPolar(vx, 0, vz)[2];
					//ReikaJavaLibrary.pConsole(vx+" , "+vz+" > "+ang);
					WorldLocation loc = WarpNetwork.instance.getLink(new WorldLocation(world, x, y, z), ang, ANGLE_TOLERANCE);
					if (loc != null) {
						this.teleportPlayer(ep, loc);
						ep.getEntityData().setLong("lastWarpNode", world.getTotalWorldTime());
					}
				}
			}
		}
	}

	private void teleportPlayer(EntityPlayerMP ep, WorldLocation loc) {
		ep.setPositionAndUpdate(loc.xCoord+0.5, loc.yCoord+0.5, loc.zCoord+0.5);
		ep.motionX += ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
		ep.motionY += ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
		ep.motionZ += ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
		ep.velocityChanged = true;
		if (!ep.capabilities.isCreativeMode && !ProgressStage.LINK.isPlayerAtStage(ep) && ep.getRNG().nextInt(ProgressStage.USEENERGY.isPlayerAtStage(ep) ? 2 : ProgressStage.ALLCOLORS.isPlayerAtStage(ep) ? 3 : 6) > 0) {
			ep.attackEntityFrom(DamageSource.magic, 1+ep.getRNG().nextInt(8));
		}
		if (!ep.worldObj.isRemote) {
			ProgressStage.WARPNODE.stepPlayerTo(ep);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWarpNode();
	}

	public static class TileEntityWarpNode extends TileEntity {

		private boolean isOpen;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void open() {
			isOpen = true;
			WarpNetwork.instance.addLocation(new WorldLocation(this));
		}

		public boolean isOpen() {
			return isOpen;
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

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("open", isOpen);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isOpen = NBT.getBoolean("open");

		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass == 1;
		}

		@Override
		public double getMaxRenderDistanceSquared() {
			return 512*512;
		}

	}

}
