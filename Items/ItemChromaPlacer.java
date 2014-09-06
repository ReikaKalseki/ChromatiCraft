/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.TileEntityGuardianStone;
import Reika.DragonAPI.Interfaces.SidePlacedTile;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChromaPlacer extends Item {

	public ItemChromaPlacer(int tex) {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
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
		ChromaTiles m = ChromaTiles.TEList[is.getItemDamage()];
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, m.getBlock(), m.getBlockMetadata(), 3);
		}
		Material mat = m.getBlock().getMaterial();
		Block b = m.getBlock();
		if (b == ChromaBlocks.TILECRYSTAL.getBlockInstance() || b == ChromaBlocks.TILECRYSTALNONCUBE.getBlockInstance()) {
			ReikaSoundHelper.playPlaceSound(world, x, y, z, m.getBlock());
		}
		else {
			if (mat == Material.iron) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.iron_block);
			}
			else if (mat == Material.rock) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
			}
			else if (mat == Material.plants) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.grass);
			}
			else {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.wool);
			}
		}
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		if (m.canBeVertical())
			te.setBlockMetadata(ChromaAux.get6SidedMetadataFromPlayerLook(ep));
		else
			te.setBlockMetadata(ChromaAux.get4SidedMetadataFromPlayerLook(ep));
		if (m.isSidePlaced()) {
			((SidePlacedTile)te).placeOnSide(side);
		}
		/*
		if (m == ChromaTiles.ACCELERATOR) {
			((TileEntityAccelerator)te).setTier(is);
		}*/
		if (te instanceof NBTTile && is.stackTagCompound != null) {
			((NBTTile)te).setDataFromItemStackTag(is);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles c = ChromaTiles.TEList[i];
			if (c.isAvailableInCreativeInventory()) {
				ItemStack item = new ItemStack(par1, 1, i);
				if (c == ChromaTiles.ACCELERATOR) {
					for (int k = 0; k <= TileEntityAccelerator.MAX_TIER; k++) {
						ItemStack item2 = item.copy();
						item2.stackTagCompound = new NBTTagCompound();
						item2.stackTagCompound.setInteger("tier", k);
						par3List.add(item2);
					}
				}
				else {
					par3List.add(item);
				}
			}
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
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		ChromaTiles r = ChromaTiles.TEList[is.getItemDamage()];
		if (r == ChromaTiles.GUARDIAN) {
			li.add(String.format("Protects a radius-%d area", TileEntityGuardianStone.RANGE));
		}
		if (r == ChromaTiles.ACCELERATOR && is.stackTagCompound != null) {
			li.add("Accelerates time by "+(TileEntityAccelerator.getAccelFromTier(is.stackTagCompound.getInteger("tier"))+1)+"x");
			li.add("for TileEntities adjacent to it.");
			long max = TileEntityAccelerator.MAX_LAG/1000000;
			li.add(EnumChatFormatting.GOLD+"Admin Note:"+EnumChatFormatting.WHITE+" Will not cause more than "+max+"ms lag.");
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}


}
