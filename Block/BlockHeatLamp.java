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

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockAttachableMini;
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
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler.SmelteryWrapper;
import Reika.DragonAPI.ModInteract.Power.ReikaRailCraftHelper;
import Reika.DragonAPI.ModInteract.Power.ReikaRailCraftHelper.FireboxWrapper;
import Reika.ReactorCraft.Auxiliary.ReactorCoreTE;
import Reika.RotaryCraft.API.Interfaces.BasicTemperatureMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHeatLamp extends BlockAttachableMini {

	private IIcon coldIcon;

	public BlockHeatLamp(Material mat) {
		super(mat);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		li.add(ChromaBlocks.HEATLAMP.getStackOfMetadata(0));
		li.add(ChromaBlocks.HEATLAMP.getStackOfMetadata(8));
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ((BlockTieredOre)ChromaBlocks.TIEREDORE.getBlockInstance()).getGeodeIcon(6);
		coldIcon = ico.registerIcon("chromaticraft:coldlamp");
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return isCold(iba, x, y, z) ? coldIcon : super.getIcon(iba, x, y, z, s);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return meta >= 8 ? coldIcon : blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r) {
		CrystalElement e = isCold(world, x, y, z) ? CrystalElement.WHITE : CrystalElement.ORANGE;
		EntityFX fx = new EntityCenterBlurFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		if (r.nextInt(4) == 0) {
			fx = new EntityLaserFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
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
	public int getColor(IBlockAccess iba, int x, int y, int z) {
		return this.isCold(iba, x, y, z) ? 0x90b0ff : 0xffaa00;
	}

	private static boolean isCold(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z) >= 8;
	}

	public static class TileEntityHeatLamp extends TileEntity implements GuiController {

		public int temperature;
		public static final int MAXTEMP = 615;
		public static final int MAXTEMP_COLD = 15;
		public static final int MINTEMP = 20;
		public static final int MINTEMP_COLD = -60;

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			temperature = Math.max(this.isCold() ? MINTEMP_COLD : MINTEMP, Math.min(this.isCold() ? MAXTEMP_COLD : MAXTEMP, temperature));
			ForgeDirection dir = ((BlockHeatLamp)this.getBlockType()).getSide(worldObj, xCoord, yCoord, zCoord).getOpposite();
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if (te instanceof ThermalTile) {
				//((ThermalTile)te).setTemperature(((ThermalTile) te).getTemperature()+(int)Math.signum(temperature-((ThermalTile) te).getTemperature()));
				ThermalTile tl = (ThermalTile)te;
				if (this.canHeat(tl)) {
					if (this.isCold() ? temperature < tl.getTemperature() : temperature > tl.getTemperature()) {
						tl.setTemperature(tl.getTemperature()+(this.isCold() ? -1 : 1));
						if (ModList.ROTARYCRAFT.isLoaded() && te instanceof BasicTemperatureMachine)
							((BasicTemperatureMachine)te).resetAmbientTemperatureTimer();
					}
				}
				else {
					((BlockHeatLamp)this.getBlockType()).drop(worldObj, xCoord, yCoord, zCoord);
				}
			}
			else if (!this.isCold() && te instanceof TileEntityFurnace) {
				TileEntityFurnace tf = (TileEntityFurnace)te;
				double c = Math.min(1, 1.25*temperature/1000D);
				if (ReikaRandomHelper.doWithChance(c)) {
					if (tf.furnaceBurnTime == 0 && ReikaRandomHelper.doWithChance(c)) {
						tf.furnaceBurnTime = 20;
					}
					te.updateEntity();
				}
			}
			else if (!this.isCold() && TinkerSmelteryHandler.isSmelteryController(te)) {
				SmelteryWrapper s = new SmelteryWrapper(te);
				s.fuelLevel = 4000;
				s.meltPower = temperature*1500/MAXTEMP; //that puts max heat lamp at 1500
				s.write(te);
				//TinkerSmelteryHandler.tick(te, temperature*1500/MAXTEMP);
			}
			else if (!this.isCold() && ReikaRailCraftHelper.isFirebox(te)) {
				te = MultiblockControllerFinder.instance.getController(te);
				FireboxWrapper s = new FireboxWrapper(te);
				double temp = 80+25*Math.sin(worldObj.getTotalWorldTime()/350D); //not enough to be sustainable, but enough to prevent total blackout
				s.temperature = Math.max(s.temperature, temp);
				s.write(te);
			}
		}

		private boolean isCold() {
			return BlockHeatLamp.isCold(worldObj, xCoord, yCoord, zCoord);
		}

		private boolean canHeat(ThermalTile tl) {
			if (ModList.ROTARYCRAFT.isLoaded() && this.isRotaryHeatTile(tl))
				return false;
			return !ModList.REACTORCRAFT.isLoaded() || !this.isReactorTile(tl);
		}

		@ModDependent(ModList.ROTARYCRAFT)
		private boolean isRotaryHeatTile(ThermalTile tl) {
			return tl instanceof TemperatureTE && !((TemperatureTE)tl).allowExternalHeating();
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
