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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
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
		return /*meta == 1 ? */new TileEntityPortal()/* : null*/;
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
		if (te instanceof TileEntityPortal && ((TileEntityPortal)te).complete) {
			ReikaEntityHelper.transferEntityToDimension(e, ExtraChromaIDs.DIMID.getValue(), new ChromaTeleporter());
		}
		else {
			e.motionY = 1.5;
			e.addVelocity(ReikaRandomHelper.getRandomPlusMinus(0, 0.25), 0, ReikaRandomHelper.getRandomPlusMinus(0, 0.25));
			ChromaSounds.POWERDOWN.playSound(e);
		}
	}

	public static class TileEntityPortal extends TileEntity {

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
			if (worldObj.isRemote) {
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

		@SideOnly(Side.CLIENT)
		private void idleParticles() {
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
			}
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
			complete = ChromaStructures.getPortalStructure(world, x, y, z).matchInWorld();
			complete &= this.getFluid(world, x, y, z);
			complete &= this.getEntities(world, x, y, z);
		}

		private boolean getFluid(World world, int x, int y, int z) {
			return false;
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

			complete = NBT.getBoolean("built");
		}

	}

	public static class ChromaTeleporter extends Teleporter {

		public ChromaTeleporter() {
			super(MinecraftServer.getServer().worldServerForDimension(ExtraChromaIDs.DIMID.getValue()));
		}

	}

}
