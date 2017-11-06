/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLinkedTilePlacer extends Item {

	public ItemLinkedTilePlacer(int tex) {
		super();
		maxStackSize = 64;
		this.setCreativeTab(ChromatiCraft.instance.isLocked() ? null : ChromatiCraft.tabChroma);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (!this.checkValidBounds(is, ep, world, x, y, z))
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, this.getBlock(), this.getMetadata(is.getItemDamage()), 3);
		}
		this.linkTile(world, x, y, z, is, ep);
		return true;
	}

	static void linkTile(World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.wool);
		LinkedTile te = (LinkedTile)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		if (is.stackTagCompound == null) {
			saveRiftLocation(world, x, y, z, is);
		}
		else {
			WorldLocation loc = getRiftLocation(is);
			if (loc != null && !loc.equals(world, x, y, z)) {
				TileEntity tile2 = loc.getTileEntity();
				if (tile2 instanceof LinkedTile) {
					LinkedTile te2 = (LinkedTile)tile2;
					te.linkTo(loc);
					te2.setPrimary(true);
					is.stackTagCompound = null;
				}
				else {
					saveRiftLocation(world, x, y, z, is);
				}
			}
			else {
				saveRiftLocation(world, x, y, z, is);
			}
		}
	}

	protected Block getBlock() {
		return ChromaBlocks.RIFT.getBlockInstance();
	}

	static WorldLocation getRiftLocation(ItemStack is) {
		return is.stackTagCompound != null ? WorldLocation.readFromNBT("loc", is.stackTagCompound) : null;
	}

	private static void saveRiftLocation(World world, int x, int y, int z, ItemStack is) {
		is.stackTagCompound = new NBTTagCompound();
		WorldLocation loc = new WorldLocation(world, x, y, z);
		loc.writeToNBT("loc", is.stackTagCompound);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			WorldLocation loc = this.getRiftLocation(is);
			if (loc != null)
				li.add("Linking to "+loc.toString());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 1; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName();// + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

}
