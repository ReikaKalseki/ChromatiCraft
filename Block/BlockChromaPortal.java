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

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaTeleporter;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.World.Dimension.CheatingPreventionSystem;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockChromaPortal extends Block {

	private static final HashSet<Coordinate> portalCheck = new HashSet();

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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (HoldingChecks.MANIPULATOR.isHolding(ep)) {
			TileEntityCrystalPortal te = (TileEntityCrystalPortal)world.getTileEntity(x, y, z);
			if (te.ownedBy(ep)) {
				//world.addWeatherEffect(new EntityLightningBolt(world, x+0.5, y+0.5, z+0.5));
				//world.createExplosion(ep, x+0.5, y+0.5, z+0.5, 4, false);
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (world.getBlock(x+i, y, z+k) == Blocks.fire)
							world.setBlock(x+i, y, z+k, Blocks.air);
					}
				}
				BlockArray bk = new BlockArray();
				bk.recursiveAddWithBounds(world, x, y, z, this, x-16, y-8, z-16, x+16, y+8, z+16);
				for (Coordinate loc : bk.keySet()) {
					loc.setBlock(world, Blocks.air);
					ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this));
				}
				ChromaSounds.RIFT.playSoundAtBlock(te);
				ChromaSounds.POWERDOWN.playSoundAtBlock(te);
			}
		}
		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		boolean teleport = false;
		portalCheck.add(new Coordinate(x, y, z));
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dz = z+dir.offsetZ;
			if (world.getBlock(dx, y, dz) != this) {
				int ddx = x-dir.offsetX;
				int ddz = z-dir.offsetZ;
				if (world.getBlock(ddx, y, ddz) == this && !portalCheck.contains(new Coordinate(ddx, y, ddz))) {
					this.onEntityCollidedWithBlock(world, ddx, y, ddz, e);
					portalCheck.remove(new Coordinate(x, y, z));
					return;
				}
			}
		}
		portalCheck.remove(new Coordinate(x, y, z));
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityCrystalPortal && !world.isRemote) {
			TileEntityCrystalPortal te = (TileEntityCrystalPortal)tile;
			if (e instanceof EntityPlayer) {
				if (te.complete) {
					EntityPlayer ep = (EntityPlayer)e;
					if (te.canPlayerUse(ep)) {
						te.teleportPlayer(ep);
					}
					else {
						this.denyEntity(e);
					}
				}
				else {
					this.denyEntity(e);
				}
			}
			else if (e instanceof EntityItem) {
				EntityItem ei = (EntityItem)e;
				ItemStack is = ei.getEntityItem();
				if (ReikaItemHelper.matchStacks(is, ChromaStacks.bedrockloot) || ReikaItemHelper.matchStacks(is, ChromaStacks.bedrockloot2)) {
					te.addTuningEnergy(is);
					ei.setDead();
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

	private void denyEntity(Entity e) {
		e.motionY = 1.5;
		e.fallDistance = Math.max(e.fallDistance, 500);
		e.addVelocity(ReikaRandomHelper.getRandomPlusMinus(0, 0.25), 0, ReikaRandomHelper.getRandomPlusMinus(0, 0.25));
		e.velocityChanged = true;
		ChromaSounds.POWERDOWN.playSound(e);
	}

	public static boolean isPortalFunctional() {
		return ChunkProviderChroma.areGeneratorsReady() && ChromatiCraft.instance.isDimensionLoadable();
	}

	public static class TileEntityCrystalPortal extends TileEntity {

		private boolean complete;
		private int charge;
		public final int MINCHARGE = 300;
		private int ticks = 0;
		private UUID placerUUID;

		private int tuning = 0;

		@Override
		public void updateEntity() {
			if (ticks == 0)
				this.onFirstTick();
			ticks++;

			if (DragonAPICore.debugtest) {
				//ChromaStructures.getPortalStructure(worldObj, xCoord, yCoord, zCoord, false).place();
				//DragonAPICore.debugtest = false;
			}

			if (complete) {
				if (charge < MINCHARGE || !ChunkProviderChroma.areGeneratorsReady()) {
					charge++;
					if (worldObj.isRemote)
						this.chargingParticles();
				}
			}
			else {
				charge = 0;
			}
			int pos = this.getPortalPosition(worldObj, xCoord, yCoord, zCoord);
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
				if (complete) {
					if (ticks%90 == 0)
						ChromaSounds.PORTAL.playSoundAtBlock(this);
					if (tuning > 0)
						if (DragonAPICore.rand.nextInt(400) == 0)
							tuning--;
				}
			}
		}

		private void teleportPlayer(EntityPlayer ep) {
			CheatingPreventionSystem.instance.preJoin(ep);
			int dim = this.getTargetDimension();
			DimensionTuningManager.instance.tunePlayer(ep, tuning);
			ReikaEntityHelper.transferEntityToDimension(ep, dim, new ChromaTeleporter(dim));
			if (ProgressStage.DIMENSION.stepPlayerTo(ep)) {
				ReikaSoundHelper.broadcastSound(ChromaSounds.GOTODIM, 1, 1);
			}
			else {
				ChromaSounds.GOTODIM.playSoundAtBlockNoAttenuation(this, 1, 1, 32);
				ReikaSoundHelper.playSound(ChromaSounds.GOTODIM, ep.worldObj, 0, 1024, 0, 1, 1, false);
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DIMSOUND.ordinal(), PacketTarget.allPlayers);
			}
			tuning *= 0.4;
		}

		public void addTuningEnergy(ItemStack is) {
			boolean tier2 = ReikaItemHelper.matchStacks(is, ChromaStacks.bedrockloot2);
			int amt = (int)((tier2 ? 150 : 1)*Math.pow(is.stackSize, tier2 ? 0.85 : 0.5));
			tuning += amt;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public boolean ownedBy(EntityPlayer ep) {
			return placerUUID == null || placerUUID.equals(ep.getUniqueID());
		}

		public int getTargetDimension() {
			return this.getBlockMetadata() == 15 ? 0 : ExtraChromaIDs.DIMID.getValue();
		}

		public boolean canPlayerUse(EntityPlayer ep) {
			return isPortalFunctional() && charge >= MINCHARGE && ProgressionManager.instance.playerHasPrerequisites(ep, ProgressStage.DIMENSION);
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
			int l = ReikaRandomHelper.getRandomPlusMinus(80, 40);
			EntityBlurFX fx = new EntityCCBlurFX(worldObj, px, yCoord+1.25, pz, 0, 0, 0).setGravity(g).setLife(l).setColor(color);
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
			EntityBlurFX fx = new EntityCCBlurFX(worldObj, xCoord+0.5, yCoord+8.25, zCoord+0.5, vx, 0, vz).setGravity(g);
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
			boolean last = complete;
			if (this.getBlockMetadata() == 15) {
				complete = true;
			}
			else {
				ChromaStructures.PORTAL.getStructure().resetToDefaults();
				complete = ChromaStructures.PORTAL.getArray(world, x, y, z).matchInWorld();
				complete &= this.getEntities(world, x, y, z);
			}
			if (last != complete)
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
			NBT.setInteger("tuning", tuning);

			if (placerUUID != null) {
				NBT.setString("owner", placerUUID.toString());
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			complete = NBT.getBoolean("built");

			charge = NBT.getInteger("charge");
			tuning = NBT.getInteger("tuning");

			String s = NBT.getString("owner");
			if (s != null && !s.isEmpty()) {
				placerUUID = UUID.fromString(s);
			}
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

		public int getPortalPosition(IBlockAccess world, int x, int y, int z) {
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

		public int getTuning() {
			return tuning;
		}

	}

}
