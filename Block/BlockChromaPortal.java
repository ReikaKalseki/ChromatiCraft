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
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
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
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
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
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityCrystalPortal && !world.isRemote) {
			TileEntityCrystalPortal te = (TileEntityCrystalPortal)tile;
			if (e instanceof EntityPlayer) {
				if (te.complete) {
					EntityPlayer ep = (EntityPlayer)e;
					if (te.canPlayerUse(ep)) {
						int dim = te.getTargetDimension();
						ReikaEntityHelper.transferEntityToDimension(e, dim, new ChromaTeleporter(dim));
						ProgressStage.DIMENSION.stepPlayerTo(ep);
						ReikaSoundHelper.broadcastSound(ChromaSounds.GOTODIM, ChromatiCraft.packetChannel, 1, 1);
					}
					else {
						this.denyEntity(e);
					}
				}
				else {
					this.denyEntity(e);
				}
			}
		}
	}

	private void denyEntity(Entity e) {
		e.motionY = 1.5;
		e.fallDistance = Math.max(e.fallDistance, 500);
		e.addVelocity(ReikaRandomHelper.getRandomPlusMinus(0, 0.25), 0, ReikaRandomHelper.getRandomPlusMinus(0, 0.25));
		ChromaSounds.POWERDOWN.playSound(e);
	}

	public static class TileEntityCrystalPortal extends TileEntity {

		private boolean complete;
		private int charge;
		public final int MINCHARGE = 300;
		private int ticks = 0;

		@Override
		public void updateEntity() {
			if (ticks == 0)
				this.onFirstTick();
			ticks++;
			if (complete) {
				if (charge < MINCHARGE || !ChunkProviderChroma.areStructuresReady()) {
					charge++;
					if (worldObj.isRemote)
						this.chargingParticles();
				}
			}
			else {
				charge = 0;
			}
			int pos = this.getPortalPosition();
			if (worldObj.isRemote) {
				if (pos == 5 && this.isFull9x9()) {
					if (worldObj.isRemote)
						this.idleParticles();
					if (complete) {
						if (charge >= MINCHARGE) {
							if (worldObj.isRemote)
								this.activeParticles();
						}
					}
				}
			}
			if (pos == 5 && this.isFull9x9()) {
				if (ticks%20 == 0)
					this.validateStructure(worldObj, xCoord, yCoord, zCoord);
				if (complete && ticks%90 == 0) {
					ChromaSounds.PORTAL.playSoundAtBlock(this);
				}
			}
		}

		public int getTargetDimension() {
			return this.getBlockMetadata() == 15 ? 0 : ExtraChromaIDs.DIMID.getValue();
		}

		public boolean canPlayerUse(EntityPlayer ep) {
			return ChunkProviderChroma.areStructuresReady() && charge >= MINCHARGE && ProgressionManager.instance.playerHasPrerequisites(ep, ProgressStage.DIMENSION);
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
			double px = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, 1.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, 1.5);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			int color = CrystalElement.getBlendedColor(ticks, 40);
			int red = ReikaColorAPI.getRed(color);
			int green = ReikaColorAPI.getGreen(color);
			int blue = ReikaColorAPI.getBlue(color);
			int l = ReikaRandomHelper.getRandomPlusMinus(80, 40);
			EntityBlurFX fx = new EntityBlurFX(worldObj, px, yCoord+1.25, pz, 0, 0, 0).setGravity(g).setLife(l).setColor(red, green, blue);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		@SideOnly(Side.CLIENT)
		private void chargingParticles() {
			if (worldObj.rand.nextInt(4) == 0) {
				int dx = worldObj.rand.nextBoolean() ? 3 : -3;
				int dz = worldObj.rand.nextBoolean() ? 3 : -3;
				int x = xCoord+dx;
				int y = yCoord+5;
				int z = zCoord+dz;
				if (worldObj.getBlock(x, y, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance() && worldObj.getBlockMetadata(x, y, z) == 5) {
					//EntityFX fx = new EntityBoltFX(worldObj, x+0.5, y+0.5, z+0.5, x+5, y, z+5);
					double px = x+worldObj.rand.nextDouble();
					double py = y+worldObj.rand.nextDouble();
					double pz = z+worldObj.rand.nextDouble();
					EntityBallLightningFX fx = new EntityBallLightningFX(worldObj, px, py, pz, CrystalElement.elements[ticks/8%16]);
					fx.noClip = false;
					double v = 0.125;
					double vx = v*-Math.signum(dx);
					double vy = -0.125;
					double vz = v*-Math.signum(dz);
					fx.motionX = vx;
					fx.motionY = vy;
					fx.motionZ = vz;
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private void activeParticles() {
			CrystalElement e = CrystalElement.elements[ticks/8%16];
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			EntityBlurFX fx = new EntityBlurFX(worldObj, xCoord+0.5, yCoord+8.25, zCoord+0.5, vx, 0, vz).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);


			//---------------------------
			int dx = worldObj.rand.nextBoolean() ? 3 : -3;
			int dz = worldObj.rand.nextBoolean() ? 3 : -3;
			double x = xCoord+dx+worldObj.rand.nextDouble();
			double y = yCoord+5+worldObj.rand.nextDouble();
			double z = zCoord+dz+worldObj.rand.nextDouble();
			double v = 0.0625;
			vx = x < xCoord ? v : -v;
			vz = z < zCoord ? v : -v;
			if (worldObj.rand.nextBoolean())
				vx = 0;
			else
				vz = 0;

			EntityFX fx2 = new EntityCenterBlurFX(e, worldObj, x, y, z, vx, 0, vz).setScale(2).setNoSlowdown();
			fx2.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);


			//----------------------------------
			dx = worldObj.rand.nextBoolean() ? 7 : -7;
			dz = worldObj.rand.nextBoolean() ? 7 : -7;
			if (worldObj.rand.nextBoolean())
				dx += Math.signum(dx)*-4;
			else
				dz += Math.signum(dz)*-4;
			x = xCoord+dx+worldObj.rand.nextDouble();
			y = yCoord+5+worldObj.rand.nextDouble();
			z = zCoord+dz+worldObj.rand.nextDouble();
			v = 0.0625;
			vx = x < xCoord ? v : -v;
			vz = z < zCoord ? v : -v;
			if (worldObj.rand.nextBoolean())
				vx = 0;
			else
				vz = 0;

			boolean longAxis = (Math.abs(dx) == 7 && vx != 0) || (Math.abs(dz) == 7 && vz != 0);
			int l = !longAxis ? 100 : 60;

			EntityRuneFX fx3 = new EntityRuneFX(worldObj, x, y, z, vx, 0, vz, e).setScale(2).setLife(l);
			fx3.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
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
			if (this.getBlockMetadata() == 15) {
				complete = true;
			}
			else {
				complete = ChromaStructures.getPortalStructure(world, x, y, z).matchInWorld();
				complete &= this.getEntities(world, x, y, z);
			}
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

		public boolean isFull9x9() {
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (worldObj.getBlock(xCoord+i, yCoord, zCoord+k) != ChromaBlocks.PORTAL.getBlockInstance()) {
						return false;
					}
				}
			}
			return true;
		}

	}

	public static class ChromaTeleporter extends Teleporter {

		private ChromaTeleporter() {
			this(ExtraChromaIDs.DIMID.getValue());
		}

		public ChromaTeleporter(int dim) {
			super(MinecraftServer.getServer().worldServerForDimension(dim));
		}

		@Override
		public void placeInPortal(Entity e, double x, double y, double z, float facing) {
			e.setLocationAndAngles(0, 1024, 0, 0, 0);
			this.placeInExistingPortal(e, x, y, z, facing);
		}

		@Override
		public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float facing) {
			return true;
		}

		private void makeReturnPortal(World world, int x, int y, int z) {

		}

		@Override
		public boolean makePortal(Entity e) { //NOOP - custom worldgen for return portal
			return false;//super.makePortal(e);
		}

	}

}
