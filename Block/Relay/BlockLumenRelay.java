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

import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value="mcp.mobius.waila.api.IWailaDataProvider")
public class BlockLumenRelay extends BlockRelayBase implements IWailaDataProvider {

	private final IIcon[][] icons = new IIcon[6][6];

	public BlockLumenRelay(Material mat) {
		super(mat);
	}

	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z).isSideSolid(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
	}

	public void setSide(World world, int x, int y, int z, int side) {
		world.setBlockMetadataWithNotify(x, y, z, side, 3);
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/relay");
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, meta)) {
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
			ItemStack is = ChromaBlocks.RELAY.getStackOfMetadata(te.isMulti ? 16 : te.color.ordinal());
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float xmin = 0;
		float ymin = 0;
		float zmin = 0;
		float xmax = 1;
		float ymax = 1;
		float zmax = 1;
		float h = 0.875F;
		float w = 0.125F;
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
	/*
	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 6; i++) { //metas (dirs)
			for (int k = 0; k < 6; k++) { //sides
				if (i == k) { //top face
					icons[i][k] = ico.registerIcon("chromaticraft:crystal/crystal_32");
				}
				else if (i%2 == 0 ? k == i+1 : k == i-1) { //bottom face
					icons[i][k] = ico.registerIcon("chromaticraft:pylon/block_0");
				}
				else {
					icons[i][k] = ico.registerIcon("chromaticraft:basic/relay_side_"+i);
				}
			}
		}
	}
	 */

	/*
	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 6; i++) { //metas (dirs)
			for (int k = 0; k < 6; k++) { //sides
				if (i == k) { //top face
					icons[i][k] = ico.registerIcon("chromaticraft:crystal/crystal_32");
				}
				else if (i%2 == 0 ? k == i+1 : k == i-1) { //bottom face
					icons[i][k] = ico.registerIcon("chromaticraft:pylon/block_0");
				}
				else {
					icons[i][k] = ico.registerIcon("chromaticraft:basic/relay_side_"+i);
				}
			}
		}
	}
	 */

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		te.isMulti = is.getItemDamage() == 16;
		te.color = te.isMulti ? CrystalElement.WHITE : CrystalElement.elements[is.getItemDamage()];
		te.setInput(ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z)].getOpposite());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		CrystalElement e = te.isMulti() ? CrystalElement.randomElement() : te.getColor();
		double h = 0.25;
		if (dir.offsetX+dir.offsetY+dir.offsetZ < 0)
			h = h-0.125;
		double dx = x+0.5+dir.offsetX*h;
		double dy = y+0.5+dir.offsetY*h;
		double dz = z+0.5+dir.offsetZ*h;
		EntityFX fx = new EntityCenterBlurFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityLumenRelay();
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest) {
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlockToAir(x, y, z);
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (ep.capabilities.isCreativeMode)
			return false;
		return true;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		if (te != null) {
			ItemStack is = ChromaBlocks.RELAY.getStackOfMetadata(te.isMulti ? 16 : te.color.ordinal());
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		}
	}

	public static class TileEntityLumenRelay extends TileRelayBase {

		private CrystalElement color = CrystalElement.WHITE;
		private boolean isMulti = false;
		//private int energy = 0;

		@Override
		public boolean canTransmit(CrystalElement e) {
			return isMulti || e == color;
		}

		public boolean isMulti() {
			return isMulti;
		}

		public CrystalElement getColor() {
			return color;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("multi", isMulti);
			NBT.setInteger("color", color.ordinal());
			//NBT.setInteger("energy", energy);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isMulti = NBT.getBoolean("multi");
			color = CrystalElement.elements[NBT.getInteger("color")];
			//energy = NBT.getInteger("energy");
		}

	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		TileEntityLumenRelay te = (TileEntityLumenRelay)acc.getTileEntity();
		int meta = te.isMulti ? 16 : te.color.ordinal();
		return ChromaBlocks.RELAY.getStackOfMetadata(meta);
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP ep, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
