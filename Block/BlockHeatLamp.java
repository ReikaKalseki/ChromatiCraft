/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.ThermalTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.RotaryCraft.API.Interfaces.BasicTemperatureMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHeatLamp extends Block implements SidedBlock {

	public BlockHeatLamp(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(0);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof TileEntityHeatLamp ? (ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(0xffaa00, 7) : 7) : 0;
	}

	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z).isSideSolid(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
	}

	public void setSide(World world, int x, int y, int z, int side) {
		world.setBlockMetadataWithNotify(x, y, z, side, 3);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, meta)) {
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			TileEntityHeatLamp te = (TileEntityHeatLamp)world.getTileEntity(x, y, z);
			this.drop(world, x, y, z);
		}
	}

	private static void drop(World world, int x, int y, int z) {
		ItemStack is = new ItemStack(ChromaBlocks.HEATLAMP.getBlockInstance());
		ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		world.setBlock(x, y, z, Blocks.air);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float xmin = 0;
		float ymin = 0;
		float zmin = 0;
		float xmax = 1;
		float ymax = 1;
		float zmax = 1;
		float h = 0.25F;
		float w = 0.25F;
		switch(ForgeDirection.VALID_DIRECTIONS[iba.getBlockMetadata(x, y, z)]) {
			case WEST:
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				xmin = 1-h;
				break;
			case EAST:
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				xmax = h;
				break;
			case NORTH:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				zmin = 1-h;
				break;
			case SOUTH:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				zmax = h;
				break;
			case UP:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymax = h;
				break;
			case DOWN:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 1-h;
				break;
			default:
				break;
		}
		this.setBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
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
		return 0;//ChromatiCraft.proxy.relayRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;//1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return super.canRenderInPass(pass);//true;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ((BlockTieredOre)ChromaBlocks.TIEREDORE.getBlockInstance()).getGeodeIcon(6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		TileEntityHeatLamp te = (TileEntityHeatLamp)world.getTileEntity(x, y, z);
		double dx = x+0.5-dir.offsetX*0.3125;
		double dy = y+0.5-dir.offsetY*0.3125;
		double dz = z+0.5-dir.offsetZ*0.3125;

		double w = 0.1875;
		double h = 0.125;

		if (Math.abs(dir.offsetX) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, h);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, w);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, w);
		}
		else if (Math.abs(dir.offsetY) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, w);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, h);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, w);
		}
		else if (Math.abs(dir.offsetZ) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, w);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, w);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, h);
		}

		EntityFX fx = new EntityCenterBlurFX(CrystalElement.ORANGE, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		if (r.nextInt(4) == 0) {
			fx = new EntityLaserFX(CrystalElement.ORANGE, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityHeatLamp();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public Item getItemDropped(int dmg, Random r, int fortune) {
		return super.getItemDropped(dmg, r, fortune);
	}

	public static class TileEntityHeatLamp extends TileEntity implements GuiController {

		public int temperature;
		public static final int MAXTEMP = 615;

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			temperature = Math.max(0, Math.min(MAXTEMP, temperature));
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta].getOpposite();
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if (te instanceof ThermalTile) {
				//((ThermalTile)te).setTemperature(((ThermalTile) te).getTemperature()+(int)Math.signum(temperature-((ThermalTile) te).getTemperature()));
				ThermalTile tl = (ThermalTile)te;
				if (this.canHeat(tl)) {
					if (temperature > tl.getTemperature()) {
						tl.setTemperature(tl.getTemperature()+1);
						if (ModList.ROTARYCRAFT.isLoaded() && te instanceof BasicTemperatureMachine)
							((BasicTemperatureMachine)te).resetAmbientTemperatureTimer();
					}
				}
				else {
					drop(worldObj, xCoord, yCoord, zCoord);
				}
			}
			else if (te instanceof TileEntityFurnace) {
				TileEntityFurnace tf = (TileEntityFurnace)te;
				double c = Math.min(1, 1.25*temperature/1000D);
				if (ReikaRandomHelper.doWithChance(c)) {
					if (tf.furnaceBurnTime == 0 && ReikaRandomHelper.doWithChance(c)) {
						tf.furnaceBurnTime = 20;
					}
					te.updateEntity();
				}
			}
		}

		private boolean canHeat(ThermalTile tl) {
			return !ModList.REACTORCRAFT.isLoaded() || !this.isReactorTile(tl);
		}

		@ModDependent(ModList.REACTORCRAFT)
		private boolean isReactorTile(ThermalTile tl) {
			return tl instanceof ReactorCoreTE;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("temperature", temperature);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			temperature = NBT.getInteger("temperature");
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
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		}

	}

}
