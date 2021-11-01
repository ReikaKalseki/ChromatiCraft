/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Locks;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomBig;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomEntry;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomFence;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomHouse;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomRecurse;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomSpiral;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomTriple;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomWhite;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLockKey extends Block {

	private long lastPlace = -1;

	public static enum LockChannel {

		ENTRY("Eniro", 3, LocksRoomEntry.class),
		BIG("Besar", 4, LocksRoomBig.class),
		WHITE("Sapheda", 3, LocksRoomWhite.class),
		TRIPLE("Yerraki", 4, LocksRoomTriple.class),
		RECURSION("Saiki", 5, LocksRoomRecurse.class),
		FENCE("Bera", 4, LocksRoomFence.class),
		HOUSE("Kuka", 5, LocksRoomHouse.class),
		SPIRAL("Karkace", 5, LocksRoomSpiral.class),
		//PIPE("Rura", 5, LocksRoomPipe.class),
		//COMPLEX("Jatila", 15, LocksRoomComplex.class),
		//LAYER("Shar", 4, LocksRoomLayer.class),
		//END("Ipari", 5, LocksRoomEnding.class),
		;

		public final String name;
		public final int numberKeys;
		private final Class genClass;

		private LockChannel(String s, int key, Class c) {
			name = s;
			numberKeys = key;
			genClass = c;
		}

		public static final LockChannel[] lockList = values();

		public LockLevel genRoom(LocksGenerator g) throws Exception {
			Constructor<LockLevel> c = genClass.getDeclaredConstructor(g.getClass());
			c.setAccessible(true);
			return c.newInstance(g);
		}
	}

	public BlockLockKey(Material mat) {
		super(mat);
		this.setHardness(0.15F);
		this.setCreativeTab(DragonAPICore.isReikasComputer() ? ChromatiCraft.tabChromaGen : null);
		this.setLightLevel(1);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityLockKey();
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityLockKey te = (TileEntityLockKey)world.getTileEntity(x, y, z);
		if (te.delegate != null) {
			TileEntity te2 = te.delegate.getTileEntity(world);
			if (te2 instanceof TileEntityStructControl) {
				if (((TileEntityStructControl)te2).isInaccessible(world, x, y, z, te, ep))
					return -1;
			}
		}
		return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	//Metadata corresponds to "structure index"
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/key");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
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
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int n = 2+r.nextInt(2);
		for (int i = 0; i < n; i++) {
			int l = 10+r.nextInt(30);
			float s = (float)ReikaRandomHelper.getRandomBetween(1, 1.5);
			EntityFX fx = new EntitySparkleFX(world, x+r.nextDouble(), y+r.nextDouble(), z+r.nextDouble(), 0, 0, 0).setLife(l).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		//this.openLocks(world, x, y, z);
	}

	private void openLocks(World world, int x, int y, int z) {
		if (world.isRemote)
			return;

		if (lastPlace == world.getTotalWorldTime())
			return;
		lastPlace = world.getTotalWorldTime();

		LocksGenerator gen = this.getGenerator(world, x, y, z);
		if (gen == null)
			return;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			int m = world.getBlockMetadata(dx, dy, dz);
			int ch = world.getBlockMetadata(x, y, z);
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				gen.openColor(CrystalElement.elements[m], world, ch);
				break;
			}
			else if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && m%8 == BlockType.CLOAK.metadata%8) {
				gen.markOpenGate(world, ch);
				break;
			}
		}
	}

	private void closeLocks(World world, int x, int y, int z, int meta2) {
		LocksGenerator gen = this.getGenerator(world, x, y, z);
		if (gen == null)
			return;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			int m = world.getBlockMetadata(dx, dy, dz);
			int ch = meta2;
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				gen.closeColor(CrystalElement.elements[m], world, ch);
				break;
			}
			else if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && m%8 == BlockType.CLOAK.metadata%8) {
				gen.markClosedGate(world, ch);
				break;
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b2, int meta2) {
		if (!world.isRemote) {
			this.closeLocks(world, x, y, z, meta2);
			TileEntityLockKey te = (TileEntityLockKey)world.getTileEntity(x, y, z);
			if (te.delegate != null) {
				TileEntity te2 = te.delegate.getTileEntity(world);
				if (te2 instanceof TileEntityStructControl) {
					((TileEntityStructControl)te2).onDelegatedTileRemoved(world, x, y, z, te);
				}
			}
		}
		super.breakBlock(world, x, y, z, b2, meta2);
	}

	private static LocksGenerator getGenerator(World world, int x, int y, int z) {
		return (LocksGenerator)DimensionStructureType.LOCKS.getGenerator(((TileEntityLockKey)world.getTileEntity(x, y, z)).uid);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
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
		if (world.isRemote)
			return;
		TileEntityLockKey te = (TileEntityLockKey)world.getTileEntity(x, y, z);
		if (te == null || (te.uid == null && te.delegate == null))
			return;
		ItemStack is = new ItemStack(this, 1, meta);
		is.stackTagCompound = new NBTTagCompound();
		if (te.uid != null)
			is.stackTagCompound.setString("uid", te.uid.toString());
		if (te.delegate != null)
			te.delegate.writeToNBT("delegate", is.stackTagCompound);
		ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase elb, ItemStack is) {
		if (world.isRemote)
			return;
		if (is.stackTagCompound == null)
			return;
		TileEntityLockKey te = (TileEntityLockKey)world.getTileEntity(x, y, z);
		if (is.stackTagCompound.hasKey("uid"))
			te.uid = UUID.fromString(is.stackTagCompound.getString("uid"));
		te.delegate = Coordinate.readFromNBT("delegate", is.stackTagCompound);

		this.openLocks(world, x, y, z);
		if (te.delegate != null) {
			TileEntity te2 = te.delegate.getTileEntity(world);
			if (te2 instanceof TileEntityStructControl) {
				((TileEntityStructControl)te2).onDelegatedTileAdded(world, x, y, z, te);
			}
		}
	}

	/*
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderColor(int meta) {
		return CrystalElement.elements[meta].getColor();
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}
	 */

	public static class TileEntityLockKey extends StructureBlockTile<LocksGenerator> implements InteractionDelegateTile {

		private Coordinate delegate;

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.LOCKS;
		}

		@Override
		public void setDelegate(Coordinate c) {
			delegate = c;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (delegate != null) {
				delegate.writeToNBT("delegate", NBT);
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			delegate = Coordinate.readFromNBT("delegate", NBT);
		}

	}
}
