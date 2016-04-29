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

import java.util.ArrayList;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;


public class BlockAdjacencyUpgrade extends BlockCrystalTileNonCube {

	public BlockAdjacencyUpgrade(Material mat) {
		super(mat);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		double r = 0.3125;
		AxisAlignedBB box = this.getBlockAABB(x, y, z).contract(r, r, r);
		this.setBounds(box, x, y, z);
		return box;
	}


	@Override
	public IIcon getIcon(int s, int meta) {
		return ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public ArrayList<ItemStack> getPieces(World world, int x, int y, int z) {
		ArrayList<ItemStack> li = new ArrayList();
		int meta = world.getBlockMetadata(x, y, z);
		li.add(ChromaStacks.crystalStar);
		for (int i = 0; i < 4; i++) {
			li.add(ChromaStacks.getChargedShard(CrystalElement.elements[meta]));
		}
		return li;
	}

	private ItemStack getHarvestedItemStack(World world, int x, int y, int z, int meta, ChromaTiles c) {
		return ChromaItems.ADJACENCY.getStackOfMetadata(meta);
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		ChromaTiles m = ChromaTiles.getTileFromIDandMetadata(this, meta);
		ItemStack is = this.getHarvestedItemStack(world, target.blockX, target.blockY, target.blockZ, meta, m);
		NBTTagCompound nbt = new NBTTagCompound();
		((TileEntityAdjacencyUpgrade)world.getTileEntity(target.blockX, target.blockY, target.blockZ)).getTagsToWriteToStack(nbt);
		is.stackTagCompound = nbt.hasNoTags() ? null : (NBTTagCompound)nbt.copy();
		return is;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		ItemStack is = ChromaItems.ADJACENCY.getStackOfMetadata(acc.getMetadata());
		is.stackTagCompound = new NBTTagCompound();
		((NBTTile)acc.getTileEntity()).getTagsToWriteToStack(is.stackTagCompound);
		return is;
	}
}
