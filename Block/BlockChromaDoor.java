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

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;


@Strippable(value="mcp.mobius.waila.api.IWailaDataProvider")
public class BlockChromaDoor extends BlockContainer implements SemiUnbreakable, IWailaDataProvider {

	private final IIcon[] icons = new IIcon[2];

	public BlockChromaDoor(Material mat) {
		super(mat);

		this.setResistance(600000);
		//this.setHardness(3);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return (iba instanceof World && ((World)iba).provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) ? 0 : 12;
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityChromaDoor te = (TileEntityChromaDoor)world.getTileEntity(x, y, z);
		return te.isOwner(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityChromaDoor();
	}

	public static void setOpen(World world, int x, int y, int z, boolean open) {
		setOpen(world, x, y, z, open, 0);
	}

	public static void setOpen(World world, int x, int y, int z, boolean open, int delay) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityChromaDoor) {
			if (open)
				((TileEntityChromaDoor)te).open(delay);
			else
				((TileEntityChromaDoor)te).close();
		}
	}

	public static boolean isOpen(IBlockAccess iba, int x, int y, int z) {
		return getBitflag(iba, x, y, z, 1);
	}

	public static boolean dealDamage(IBlockAccess iba, int x, int y, int z) {
		return getBitflag(iba, x, y, z, 2);
	}

	public static boolean consumeKey(IBlockAccess iba, int x, int y, int z) {
		return getBitflag(iba, x, y, z, 4);
	}

	public static boolean stayOpen(IBlockAccess iba, int x, int y, int z) {
		return getBitflag(iba, x, y, z, 8);
	}

	/**
	 * 1 = open
	 * 2 = damage
	 * 4 = one-use
	 * 8 = stay-open
	 */
	private static boolean getBitflag(IBlockAccess iba, int x, int y, int z, int bit) {
		return (iba.getBlockMetadata(x, y, z) & bit) != 0;
	}

	public static int getMetadata(boolean open, boolean damage, boolean oneuse, boolean stay) {
		return (open ? 1 : 0) | (damage ? 2 : 0) | (oneuse ? 4 : 0) | (stay ? 8 : 0);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		//if (this.isOpen(world, x, y, z) && world.isBlockIndirectlyGettingPowered(x, y, z))
		//	((TileEntityChromaDoor)world.getTileEntity(x, y, z)).close();
		this.setBlockBoundsBasedOnState(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float in = 0.375F;
		float minx = in;
		float miny = in;
		float minz = in;
		float maxx = 1-in;
		float maxy = 1-in;
		float maxz = 1-in;

		if (this.connectToBlock(world, x, y+1, z, ForgeDirection.DOWN))
			maxy = 1;
		if (this.connectToBlock(world, x, y-1, z, ForgeDirection.UP))
			miny = 0;
		if (this.connectToBlock(world, x+1, y, z, ForgeDirection.WEST))
			maxx = 1;
		if (this.connectToBlock(world, x-1, y, z, ForgeDirection.EAST))
			minx = 0;
		if (this.connectToBlock(world, x, y, z+1, ForgeDirection.NORTH))
			maxz = 1;
		if (this.connectToBlock(world, x, y, z-1, ForgeDirection.SOUTH))
			minz = 0;

		this.setBlockBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	private boolean connectToBlock(IBlockAccess world, int x, int y, int z, ForgeDirection s) {
		Block b = world.getBlock(x, y, z);
		if (b == this)
			return true;
		if (b == ChromaBlocks.PYLON.getBlockInstance() || b == ChromaBlocks.HOVER.getBlockInstance())
			return false;
		if (b.isOpaqueCube() || b.getRenderType() == 0)
			return true;
		if (b instanceof BlockStructureShield)
			return true;
		if (b.isSideSolid(world, x, y, z, s))
			return true;
		/*
		String n = b.getClass().getName().toLowerCase(Locale.ENGLISH);
		if (n.contains("facade"))
			return true;
		if (n.contains("conduitbundle"))
			return true;
		if (n.contains("cover"))
			return true;
		if (n.contains("multipart"))
			return true;
		 */
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		if (e instanceof EntityPlayer)
			((TileEntityChromaDoor)world.getTileEntity(x, y, z)).setPlacer((EntityPlayer)e);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return this.isOpen(world, x, y, z) ? null : super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {

		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		if (world.getBlock(x, y, z) == this)
			((TileEntityChromaDoor)world.getTileEntity(x, y, z)).close();
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!this.isOpen(world, x, y, z)) {
			TileEntityChromaDoor te = (TileEntityChromaDoor)world.getTileEntity(x, y, z);
			/*
			if (te.autoOpen) {
				te.open(20);
			}
			else */if (this.dealDamage(world, x, y, z)) {
				e.attackEntityFrom(DamageSource.magic, 5F);
				ReikaEntityHelper.knockbackEntityFromPos(x+0.5, y+0.5, z+0.5, e, 2);
				e.addVelocity(0, 0.03125, 0);
				ChromaSounds.DISCHARGE.playSoundAtBlock(world, x, y, z, 0.5F, 2F);
			}
		}
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta%2];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:basic/door_closed");
		icons[1] = ico.registerIcon("chromaticraft:basic/door_open");
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		if (!super.shouldSideBeRendered(iba, x, y, z, s))
			return false;
		if (iba.getBlock(x, y, z) != this)
			return true;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
		BlockBounds box1 = BlockBounds.fromBlock(this, iba, x, y, z);
		BlockBounds box2 = BlockBounds.fromBlock(this, iba, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		return !box1.sharesSideSize(box2, dir);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return true;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		MovingObjectPosition mov = acc.getPosition();
		if (this.isClientSufficient(acc.getWorld(), mov.blockX, mov.blockY, mov.blockZ))
			return currenttip;
		else {
			for (int i = 0; i < currenttip.size(); i++) {

			}
		}
		 */
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntityChromaDoor te = (TileEntityChromaDoor)acc.getTileEntity();
		if (te.uid != null)
			currenttip.add("ID: "+te.uid);
		else
			currenttip.add("No ID");
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		MovingObjectPosition mov = acc.getPosition();
		if (this.isClientSufficient(acc.getWorld(), mov.blockX, mov.blockY, mov.blockZ))
			return currenttip;
		else {
			for (int i = 0; i < currenttip.size(); i++) {

			}
		}*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	public static class TileEntityChromaDoor extends TileEntity implements SneakPop {

		private UUID uid;
		private UUID placer;

		private int lastAutoOpenDuration = 20;
		private boolean autoOpen;

		@Override
		public void updateEntity() {
			boolean flag = false;
			if (!worldObj.isRemote && autoOpen && !isOpen(worldObj, xCoord, yCoord, zCoord)) {
				if (worldObj.getBlock(xCoord, yCoord-1, zCoord) != this.getBlockType() && worldObj.getBlock(xCoord-1, yCoord, zCoord) != this.getBlockType() && worldObj.getBlock(xCoord, yCoord, zCoord-1) != this.getBlockType()) {
					EntityPlayer ep = worldObj.func_152378_a(placer);
					if (ep != null) {
						double d = ep.getDistanceSq(xCoord+0.5, yCoord, zCoord+0.5);
						if (Math.abs(ep.posY-yCoord) < 1 && d < 9) {
							if (d < 2 || ReikaEntityHelper.isLookingAt(ep, xCoord+0.5, yCoord-1.5, zCoord+0.5) || ReikaEntityHelper.isLookingAt(ep, xCoord+0.5, yCoord+0.5, zCoord+0.5)) {
								lastAutoOpenDuration = Math.min(lastAutoOpenDuration+10, 200);
								this.open(lastAutoOpenDuration);
								flag = true;
							}
						}
					}
				}
			}
			if (!flag)
				lastAutoOpenDuration = Math.max(lastAutoOpenDuration-10, 20);
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		public boolean isOwner(EntityPlayer ep) {
			return ep.getUniqueID().equals(placer);
		}

		public boolean isOwned() {
			return placer != null;
		}

		public boolean canOpen(EntityPlayer ep, UUID uid) {
			return uid.equals(this.uid);
		}

		public void openClick() {
			this.open(50);
		}

		public void open(int delay) {
			this.setStates(true);
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 1, 2F);
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 1, 1F);
			if (delay > 0 && !BlockChromaDoor.stayOpen(worldObj, xCoord, yCoord, zCoord))
				worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlockType(), delay);
		}

		public void close() {
			this.setStates(false);
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 1, 0.5F);
		}

		private void setStates(boolean open) {
			for (Coordinate c : this.getDoorBlocks()) {
				if (matchUIDs(this, (TileEntityChromaDoor)c.getTileEntity(worldObj)))
					c.setBlockMetadata(worldObj, (c.getBlockMetadata(worldObj) & 0b1110) | (open ? 1 : 0));
			}
		}

		private Collection<Coordinate> getDoorBlocks() {
			StructuredBlockArray b = new StructuredBlockArray(worldObj);
			b.recursiveAddWithBounds(worldObj, xCoord, yCoord, zCoord, this.getBlockType(), xCoord-8, yCoord-8, zCoord-8, xCoord+8, yCoord+8, zCoord+8);
			return b.keySet();
		}

		private static boolean matchUIDs(TileEntityChromaDoor te1, TileEntityChromaDoor te2) {
			if (te1 == null || te2 == null) {
				ReikaJavaLibrary.pConsole("NULL TILE");
				return false;
			}
			if (te1.uid == te2.uid)
				return true;
			if (te1.uid == null || te2.uid == null)
				return false;
			return te1.uid.equals(te2.uid);
		}

		public void bindUUID(EntityPlayer ep, UUID id, int flags) {
			for (Coordinate c : this.getDoorBlocks()) {
				TileEntityChromaDoor te = (TileEntityChromaDoor)c.getTileEntity(worldObj);
				if (te == null) {
					te = new TileEntityChromaDoor();
					worldObj.setTileEntity(c.xCoord, c.yCoord, c.zCoord, te);
				}
				if (ep == null || te.isOwner(ep)) {
					te.uid = id;
					te.autoOpen = (flags & 1) > 0;
				}
			}
		}

		public void setPlacer(EntityPlayer ep) {
			placer = ep.getUniqueID();
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (uid != null)
				NBT.setString("uid", uid.toString());
			if (placer != null)
				NBT.setString("ep", placer.toString());

			NBT.setBoolean("auto", autoOpen);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("uid"))
				uid = UUID.fromString(NBT.getString("uid"));
			if (NBT.hasKey("ep"))
				placer = UUID.fromString(NBT.getString("ep"));

			autoOpen = NBT.getBoolean("auto");
		}

		@Override
		public void drop() {
			this.getBlockType().dropBlockAsItem(worldObj, xCoord, yCoord, zCoord, 0, 0);
			worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
		}

		@Override
		public boolean canDrop(EntityPlayer ep) {
			return this.isOwner(ep);
		}

		@Override
		public boolean allowMining(EntityPlayer ep) {
			return this.isOwner(ep);
		}

	}

}
