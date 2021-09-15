/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.ShiftMaze;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.BlockDimensionStructure;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockShiftLock extends BlockDimensionStructure implements IWailaDataProvider {

	private IIcon[] icons = new IIcon[2];

	public BlockShiftLock(Material mat) {
		super(mat);
	}

	public static enum Passability {
		CLOSED(),
		OPEN(),
		EAST_CLOSED(),
		EAST_OPEN(),
		WEST_CLOSED(),
		WEST_OPEN(),
		SOUTH_CLOSED(),
		SOUTH_OPEN(),
		NORTH_CLOSED(),
		NORTH_OPEN(),
		CLOSED_HIDDEN(),
		EAST_HIDDEN(),
		WEST_HIDDEN(),
		SOUTH_HIDDEN(),
		NORTH_HIDDEN(),
		BREAKABLE();

		public static final Passability[] list = values();

		public boolean useOpenTexture() {
			switch(this) {
				case CLOSED:
				case EAST_CLOSED:
				case WEST_CLOSED:
				case SOUTH_CLOSED:
				case NORTH_CLOSED:
				case CLOSED_HIDDEN:
					return false;
				default:
					return true;
			}
		}

		public boolean isPassable(ForgeDirection side) {
			switch(this) {
				default:
					return false;
				case OPEN:
					return true;
				case EAST_HIDDEN:
				case WEST_HIDDEN:
					return side.offsetX != 0;
				case EAST_OPEN:
					return side == ForgeDirection.EAST;
				case NORTH_OPEN:
					return side == ForgeDirection.NORTH;
				case NORTH_HIDDEN:
				case SOUTH_HIDDEN:
					return side.offsetZ != 0;
				case SOUTH_OPEN:
					return side == ForgeDirection.SOUTH;
				case WEST_OPEN:
					return side == ForgeDirection.WEST;
			}
		}

		public boolean isOmniPassable() {
			switch(this) {
				case OPEN:
					return true;
				default:
					return false;
			}
		}

		public boolean isDisguised(int side) {
			switch(this) {
				case CLOSED_HIDDEN:
					return true;
				case EAST_HIDDEN:
					return side != ForgeDirection.WEST.ordinal();
				case WEST_HIDDEN:
					return side != ForgeDirection.EAST.ordinal();
				case SOUTH_HIDDEN:
					return side != ForgeDirection.NORTH.ordinal();
				case NORTH_HIDDEN:
					return side != ForgeDirection.SOUTH.ordinal();
				case EAST_OPEN:
				case EAST_CLOSED:
					return side != ForgeDirection.EAST.ordinal();
				case WEST_OPEN:
				case WEST_CLOSED:
					return side != ForgeDirection.WEST.ordinal();
				case SOUTH_OPEN:
				case SOUTH_CLOSED:
					return side != ForgeDirection.SOUTH.ordinal();
				case NORTH_OPEN:
				case NORTH_CLOSED:
					return side != ForgeDirection.NORTH.ordinal();
				default:
					return false;
			}
		}

		public static Passability getDirectionalPassability(ForgeDirection dir, boolean open) {
			switch(dir) {
				case EAST:
					return open ? EAST_OPEN : EAST_CLOSED;
				case WEST:
					return open ? WEST_OPEN : WEST_CLOSED;
				case SOUTH:
					return open ? SOUTH_OPEN : SOUTH_CLOSED;
				case NORTH:
					return open ? NORTH_OPEN : NORTH_CLOSED;
				default:
					return null;
			}
		}

		public static Passability getHiddenPassability(ForgeDirection dir) {
			switch(dir) {
				case EAST:
					return EAST_HIDDEN;
				case WEST:
					return WEST_HIDDEN;
				case SOUTH:
					return SOUTH_HIDDEN;
				case NORTH:
					return NORTH_HIDDEN;
				default:
					return null;
			}
		}
	}

	/*
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	 */
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta == Passability.BREAKABLE.ordinal() ? 1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/shiftlock-closed");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/shiftlock-open");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		Passability p = Passability.list[meta];
		if (p.isDisguised(s))
			return ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(0, BlockType.STONE.ordinal());
		return icons[p.useOpenTexture() ? 1 : 0];
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && ReikaItemHelper.matchStackWithBlock(is, this))
			return false;
		world.markBlockForUpdate(x, y, z);
		return true;
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
		return 0;//ChromatiCraft.proxy.shiftLockRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List li, Entity e) {
		if (e == null || e.boundingBox == null)
			return;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		if (e.boundingBox.intersectsWith(box)) //entity inside block space, you can go through
			return;

		double d = 0.125;
		Passability p = Passability.list[world.getBlockMetadata(x, y, z)];

		if (p.isOmniPassable())
			return;

		if (p.isPassable(ForgeDirection.DOWN))
			box.minY += d;
		if (p.isPassable(ForgeDirection.UP))
			box.maxY -= d;
		if (p.isPassable(ForgeDirection.EAST))
			box.maxX -= d;
		if (p.isPassable(ForgeDirection.WEST))
			box.minX += d;
		if (p.isPassable(ForgeDirection.NORTH))
			box.minZ += d;
		if (p.isPassable(ForgeDirection.SOUTH))
			box.maxZ -= d;

		if (box != null && box.intersectsWith(mask)) {
			li.add(box);
		}
	}

	@Override
	public int damageDropped(int meta) { //for pick block
		return meta;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		Passability p = Passability.list[world.getBlockMetadata(x, y, z)];
		return !p.isPassable(side) && p.isDisguised(side.ordinal());
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1 ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int dx, int dy, int dz, int s) {
		return super.shouldSideBeRendered(iba, dx, dy, dz, s) && iba.getBlock(dx, dy, dz) != this;
	}

	public static void setOpen(World world, int x, int y, int z, boolean open) {
		if (world.getBlock(x, y, z) != ChromaBlocks.SHIFTLOCK.getBlockInstance())
			return;
		Passability p = Passability.list[world.getBlockMetadata(x, y, z)];
		Passability put = p;
		switch(p) {
			case CLOSED:
				put = Passability.OPEN;
				break;
			case OPEN:
				put = Passability.CLOSED;
				break;
			case EAST_CLOSED:
				put = Passability.EAST_OPEN;
				break;
			case EAST_OPEN:
				put = Passability.EAST_CLOSED;
				break;
			case NORTH_CLOSED:
				put = Passability.NORTH_OPEN;
				break;
			case NORTH_OPEN:
				put = Passability.NORTH_CLOSED;
				break;
			case SOUTH_CLOSED:
				put = Passability.SOUTH_OPEN;
				break;
			case SOUTH_OPEN:
				put = Passability.SOUTH_CLOSED;
				break;
			case WEST_CLOSED:
				put = Passability.WEST_OPEN;
				break;
			case WEST_OPEN:
				put = Passability.WEST_CLOSED;
				break;
			default:
				break;
		}
		if (put != p) {
			world.setBlockMetadataWithNotify(x, y, z, put.ordinal(), 3);
			world.markBlockForUpdate(x, y, z);
			ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.stone);
		}
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			if (Passability.list[acc.getMetadata()].isDisguised(mov.sideHit))
				return ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.STONE.metadata);
		}
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
