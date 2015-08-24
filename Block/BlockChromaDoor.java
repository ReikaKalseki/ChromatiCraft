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

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;


public class BlockChromaDoor extends BlockContainer implements SemiUnbreakable {

	private final IIcon[] icons = new IIcon[2];

	public BlockChromaDoor(Material mat) {
		super(mat);

		this.setResistance(600000);
		this.setHardness(3);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return (iba instanceof World && ((World)iba).provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) ? 0 : 12;
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		return ((TileEntityChromaDoor)world.getTileEntity(x, y, z)).isOwner(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityChromaDoor();
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

	/**
	 * 1 = open
	 * 2 = damage
	 * 4 = one-use
	 * 8 = ?
	 */
	private static boolean getBitflag(IBlockAccess iba, int x, int y, int z, int bit) {
		return (iba.getBlockMetadata(x, y, z) & bit) == 1;
	}

	public static int getMetadata(boolean open, boolean damage, boolean oneuse, boolean blank) {
		return (open ? 1 : 0) | (damage ? 2 : 0) | (oneuse ? 4 : 0) | (blank ? 8 : 0);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		//if (this.isOpen(world, x, y, z) && world.isBlockIndirectlyGettingPowered(x, y, z))
		//	((TileEntityChromaDoor)world.getTileEntity(x, y, z)).close();
		this.setBlockBoundsBasedOnState(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float in = 0.375F;
		float minx = in;
		float miny = in;
		float minz = in;
		float maxx = 1-in;
		float maxy = 1-in;
		float maxz = 1-in;

		if (this.connectToBlock(iba.getBlock(x, y+1, z)))
			maxy = 1;
		if (this.connectToBlock(iba.getBlock(x, y-1, z)))
			miny = 0;
		if (this.connectToBlock(iba.getBlock(x+1, y, z)))
			maxx = 1;
		if (this.connectToBlock(iba.getBlock(x-1, y, z)))
			minx = 0;
		if (this.connectToBlock(iba.getBlock(x, y, z+1)))
			maxz = 1;
		if (this.connectToBlock(iba.getBlock(x, y, z-1)))
			minz = 0;

		this.setBlockBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	private boolean connectToBlock(Block b) {
		return b == this || b.isOpaqueCube() || b.getRenderType() == 0 || b instanceof BlockStructureShield;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		if (e instanceof EntityPlayer)
			((TileEntityChromaDoor)world.getTileEntity(x, y, z)).setPlacer((EntityPlayer)e);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return this.isOpen(world, x, y, z) ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
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
		if (!this.isOpen(world, x, y, z) && this.dealDamage(world, x, y, z)) {
			e.attackEntityFrom(DamageSource.magic, 5F);
			ReikaEntityHelper.knockbackEntityFromPos(x+0.5, y+0.5, z+0.5, e, 2);
			e.addVelocity(0, 0.03125, 0);
			ChromaSounds.DISCHARGE.playSoundAtBlock(world, x, y, z, 0.5F, 2F);
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
		return super.shouldSideBeRendered(iba, x, y, z, s) && iba.getBlock(x, y, z) != this;
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

	public static class TileEntityChromaDoor extends TileEntity {

		private UUID uid;
		private UUID placer;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public boolean isOwner(EntityPlayer ep) {
			return ep.getUniqueID().equals(placer);
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
			if (delay > 0)
				worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlockType(), delay);
		}

		public void close() {
			this.setStates(false);
			ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 1, 0.5F);
		}

		private void setStates(boolean open) {
			StructuredBlockArray b = new StructuredBlockArray(worldObj);
			b.recursiveAddWithBounds(worldObj, xCoord, yCoord, zCoord, this.getBlockType(), xCoord-8, yCoord-8, zCoord-8, xCoord+8, yCoord+8, zCoord+8);
			for (Coordinate c : b.keySet()) {
				if (matchUIDs(this, (TileEntityChromaDoor)c.getTileEntity(worldObj)))
					c.setBlockMetadata(worldObj, open ? 1 : 0);
			}
		}

		private static boolean matchUIDs(TileEntityChromaDoor te1, TileEntityChromaDoor te2) {
			if (te1.uid == te2.uid)
				return true;
			if (te1.uid == null || te2.uid == null)
				return false;
			return te1.uid.equals(te2.uid);
		}

		public void bindUUID(UUID id) {
			uid = id;
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
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("uid"))
				uid = UUID.fromString(NBT.getString("uid"));
			if (NBT.hasKey("ep"))
				placer = UUID.fromString(NBT.getString("ep"));
		}

	}

}
