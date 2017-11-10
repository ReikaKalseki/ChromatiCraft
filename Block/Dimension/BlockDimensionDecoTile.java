/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.DecoType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBounds;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDimensionDecoTile extends BlockDimensionDeco {

	private static final IIcon[][] icons = new IIcon[2][16];

	public static enum DimDecoTileTypes implements DecoType {
		FIREJET("aurajet"),
		GLOWCRACKS("glowcracks");

		public static DimDecoTileTypes[] list = values();

		private final String itemTex;
		private IIcon icon;

		private DimDecoTileTypes(String s) {
			itemTex = s;
		}

		public ItemStack getItem() {
			return new ItemStack(ChromaBlocks.DIMGENTILE.getBlockInstance(), 1, this.ordinal());
		}

		public boolean hasBlockRender() {
			return false;
		}

		public boolean needsRandomTick() {
			switch(this) {
				case FIREJET:
					return true;
				default:
					return false;
			}
		}

		public boolean isCollideable() {
			switch(this) {
				case FIREJET:
					return true;
				default:
					return false;
			}
		}

		public IIcon getItemIcon() {
			return icon;
		}

		public List<IIcon> getIcons(int pass) {
			return pass == 0 ? ReikaJavaLibrary.makeListFrom(icons[1][this.ordinal()]) : new ArrayList();
		}

		public BlockBounds getBounds() {
			switch(this) {
				case GLOWCRACKS:
					return BlockBounds.block().cut(ForgeDirection.UP, 0.999);
				default:
					return BlockBounds.block();
			}
		}

		public boolean isMineable() {
			return this == FIREJET;
		}
	}

	public BlockDimensionDecoTile(Material mat) {
		super(mat);
		this.setResistance(50000);
		//this.setBlockUnbreakable();
		this.setHardness(10);
	}

	@Override
	public boolean isMineable(int meta) {
		return DimDecoTileTypes.list[meta].isMineable();
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		return ProgressStage.CTM.isPlayerAtStage(ep) && DimDecoTileTypes.list[world.getBlockMetadata(x, y, z)].isMineable() ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == DimDecoTileTypes.GLOWCRACKS.ordinal() ? new TileGlowingCracks() : new DimensionDecoTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (DimDecoTileTypes.list[world.getBlockMetadata(x, y, z)].needsRandomTick()) {
			DimensionDecoTile te = (DimensionDecoTile)world.getTileEntity(x, y, z);
			te.activate();
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world)+rand.nextInt(1+this.tickRate(world)));
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	@Override
	public int tickRate(World world) {
		return 400;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0][meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < DimDecoTileTypes.list.length; i++) {
			icons[0][i] = ico.registerIcon("chromaticraft:dimgen2/underlay_"+i);
			icons[1][i] = ico.registerIcon("chromaticraft:dimgen2/overlay_"+i);
			DimDecoTileTypes.list[i].icon = ico.registerIcon("chromaticraft:dimgen/"+DimDecoTileTypes.list[i].itemTex);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (DimDecoTileTypes.list[meta].isCollideable()) {
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
		else {
			DimDecoTileTypes.list[meta].getBounds().copyToBlock(this);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return DimDecoTileTypes.list[meta].isCollideable() ? ReikaAABBHelper.getBlockAABB(x, y, z) : null;
	}

	public static class TileGlowingCracks extends DimensionDecoTile {

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(this).expand(4, 0, 4);
		}

		@Override
		public double getMaxRenderDistanceSquared() {
			return 65536;
		}

	}

	public static class DimensionDecoTile extends TileEntity {

		private static final Random rand = new Random();

		private int tick = 0;

		public DimensionDecoTile() {

		}

		public void activate() {
			tick = 100+rand.nextInt(1200);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "fire.ignite");
		}

		@Override
		public void updateEntity() {
			if (tick > 0) {
				int meta = this.getBlockMetadata();

				switch(DimDecoTileTypes.list[meta]) {
					case FIREJET:
						break;
					case GLOWCRACKS:
						break;
				}

				if (worldObj.isRemote)
					this.spawnParticles(DimDecoTileTypes.list[meta]);

				tick--;
			}

		}

		@SideOnly(Side.CLIENT)
		private void spawnParticles(DimDecoTileTypes t) {
			switch(t) {
				case FIREJET: {
					if (worldObj.rand.nextBoolean()) {
						double vy = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.01);
						int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)((DragonAPICore.getLaunchTime()+(xCoord+yCoord+zCoord)*8)%360));
						ChromaIcons ico = ChromaIcons.FADE;
						switch(Math.abs(System.identityHashCode(this)%6)) {
							case 0:
								break;
							case 1:
								ico = ChromaIcons.FADE_RAY;
								break;
							case 2:
								ico = ChromaIcons.CLOUDGROUP;
								break;
							case 3:
								ico = ChromaIcons.TRIDOT;
								break;
							case 4:
								ico = ChromaIcons.BIGFLARE;
								break;
							case 5:
								ico = ChromaIcons.NODE2;
								break;
						}
						EntityFX fx = new EntityBlurFX(worldObj, xCoord+0.5, yCoord+0.9, zCoord+0.5, 0, vy, 0).setRapidExpand().setScale(4).setColor(c).setIcon(ico);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				}
				case GLOWCRACKS:
					break;
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
			NBT.setInteger("tick", tick);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			tick = NBT.getInteger("tick");
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
		}

	}

}
