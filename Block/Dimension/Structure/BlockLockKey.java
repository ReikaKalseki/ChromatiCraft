/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.lang.reflect.Constructor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomBig;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomEntry;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomFence;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomHouse;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomRecurse;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomSpiral;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomTriple;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksRoomWhite;

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
		this.setHardness(0.1875F);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
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
	public void onBlockAdded(World world, int x, int y, int z) {

		if (world.isRemote)
			return;

		if (lastPlace == world.getTotalWorldTime())
			return;
		lastPlace = world.getTotalWorldTime();

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			int m = world.getBlockMetadata(dx, dy, dz);
			int ch = world.getBlockMetadata(x, y, z);
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				BlockColoredLock.openColor(CrystalElement.elements[m], world, ch);
				break;
			}
			else if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && m%8 == BlockType.CLOAK.metadata%8) {
				BlockColoredLock.markOpenGate(world, ch);
				break;
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b2, int meta2) {
		super.breakBlock(world, x, y, z, b2, meta2);

		if (world.isRemote)
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
				BlockColoredLock.closeColor(CrystalElement.elements[m], world, ch);
				break;
			}
			else if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() && m%8 == BlockType.CLOAK.metadata%8) {
				BlockColoredLock.markClosedGate(world, ch);
				break;
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
}
