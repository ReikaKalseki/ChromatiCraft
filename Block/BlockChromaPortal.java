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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockChromaPortal extends Block {

	public BlockChromaPortal(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setResistance(50000);
		this.setBlockUnbreakable();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;//meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return /*meta == 1 ? */new TileEntityCrystalPortal()/* : null*/;
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
		return -1;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrystalPortal && ((TileEntityCrystalPortal)te).complete) {
			ReikaEntityHelper.transferEntityToDimension(e, ExtraChromaIDs.DIMID.getValue(), new ChromaTeleporter());
		}
		else {
			e.motionY = 1.5;
			e.addVelocity(ReikaRandomHelper.getRandomPlusMinus(0, 0.25), 0, ReikaRandomHelper.getRandomPlusMinus(0, 0.25));
			ChromaSounds.POWERDOWN.playSound(e);
		}
	}

	public static class TileEntityCrystalPortal extends TileEntity {

		private boolean complete;
		private int charge;
		public static final int MINCHARGE = 200;
		private int ticks = 0;

		@Override
		public void updateEntity() {
			if (ticks == 0)
				this.onFirstTick();
			ticks++;
			if (charge > 0 && charge < MINCHARGE) {
				charge++;
			}
			int pos = this.getPortalPosition();
			if (worldObj.isRemote) {
				if (pos == 5) {
					if (complete) {
						this.idleParticles();
						if (charge > 0) {
							this.chargingParticles();
							if (charge >= MINCHARGE) {
								this.activeParticles();
							}
						}
					}
				}
			}
			if (pos == 5) {
				if (ticks%20 == 0)
					this.validateStructure(worldObj, xCoord, yCoord, zCoord);
				if (complete && ticks%90 == 0) {
					ChromaSounds.PORTAL.playSoundAtBlock(this);
				}
			}
		}

		public int getTicks() {
			return ticks;
		}

		public int getCharge() {
			return charge;
		}

		public boolean isComplete() {
			return complete;
		}

		@SideOnly(Side.CLIENT)
		private void idleParticles() {
			/*
			if (worldObj.rand.nextInt(4) == 0) {
				Coordinate[] pos = new Coordinate[]{new Coordinate(-2, 5, -2), new Coordinate(2, 5, -2), new Coordinate(-2, 5, 2), new Coordinate(2, 5, 2)};
				for (int i = 0; i < pos.length; i++) {
					Coordinate c = pos[i];
					int x = xCoord+c.xCoord;
					int y = yCoord+c.yCoord;
					int z = zCoord+c.zCoord;
					if (worldObj.getBlock(x, y, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && worldObj.getBlockMetadata(x, y, z) == 5) {
						//EntityFX fx = new EntityBoltFX(worldObj, x+0.5, y+0.5, z+0.5, x+5, y, z+5);
						double px = x+worldObj.rand.nextDouble();
						double py = y+worldObj.rand.nextDouble();
						double pz = z+worldObj.rand.nextDouble();
						EntityBallLightningFX fx = new EntityBallLightningFX(worldObj, px, py, pz, CrystalElement.elements[ticks/8%16]);
						fx.noClip = false;
						double v = 0.125;
						double vx = v*-Math.signum(c.xCoord);
						double vy = -0.125;
						double vz = v*-Math.signum(c.zCoord);
						fx.motionX = vx;
						fx.motionY = vy;
						fx.motionZ = vz;
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
			}*/

			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			EntityBlurFX fx = new EntityBlurFX(worldObj, xCoord+0.5, yCoord+8.25, zCoord+0.5, vx, 0, vz).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		@SideOnly(Side.CLIENT)
		private void chargingParticles() {

		}

		@SideOnly(Side.CLIENT)
		private void activeParticles() {

		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		private void onFirstTick() {
			this.validateStructure(worldObj, xCoord, yCoord, zCoord);
		}

		public void validateStructure(World world, int x, int y, int z) {
			if (worldObj.isRemote)
				return;
			complete = ChromaStructures.getPortalStructure(world, x, y, z).matchInWorld();
			complete &= this.getEntities(world, x, y, z);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		private boolean getEntities(World world, int x, int y, int z) {
			int[][] pos = new int[][]{{-5, 5, -9}, {-5, 5, 9}, {5, 5, -9}, {5, 5, 9}, {-9, 5, -5}, {-9, 5, 5}, {9, 5, -5}, {9, 5, 5}};
			for (int i = 0; i < pos.length; i++) {
				int[] loc = pos[i];
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x+loc[0], y+loc[1], z+loc[2]);
				if (world.getEntitiesWithinAABB(EntityEnderCrystal.class, box).size() != 1)
					return false;
			}
			return true;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("built", complete);

			NBT.setInteger("charge", charge);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			complete = NBT.getBoolean("built");

			charge = NBT.getInteger("charge");
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;//super.shouldRenderInPass(pass);
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(8, 8, 8);
		}

		public int getPortalPosition() {
			World world = worldObj;
			int x = xCoord;
			int y = yCoord;
			int z = zCoord;
			if (world.getBlock(x-1, y, z) != ChromaBlocks.PORTAL.getBlockInstance()) {
				if (world.getBlock(x, y, z-1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 7;
				}
				else if (world.getBlock(x, y, z+1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 1;
				}
				else {
					return 4;
				}
			}
			else if (world.getBlock(x+1, y, z) != ChromaBlocks.PORTAL.getBlockInstance()) {
				if (world.getBlock(x, y, z-1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 9;
				}
				else if (world.getBlock(x, y, z+1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 3;
				}
				else {
					return 6;
				}
			}
			else {
				if (world.getBlock(x, y, z-1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 8;
				}
				else if (world.getBlock(x, y, z+1) != ChromaBlocks.PORTAL.getBlockInstance()) {
					return 2;
				}
				else {
					return 5;
				}
			}
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

	public static class ChromaTeleporter extends Teleporter {

		public ChromaTeleporter() {
			super(MinecraftServer.getServer().worldServerForDimension(ExtraChromaIDs.DIMID.getValue()));
		}

	}

}
