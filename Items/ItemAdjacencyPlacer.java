/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.ISize;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = {"com.bioxx.tfc.api.Interfaces.ISize"})
public class ItemAdjacencyPlacer extends Item implements ISize {

	public ItemAdjacencyPlacer(int tex) {
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
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else {
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, ChromaBlocks.ADJACENCY.getBlockInstance(), is.getItemDamage(), 3);
		}

		ReikaSoundHelper.playPlaceSound(world, x, y, z, ChromaBlocks.ADJACENCY.getBlockInstance());

		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		if (te instanceof NBTTile && is.stackTagCompound != null) {
			((NBTTile)te).setDataFromItemStackTag(is);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 16; i++) {
			if (AdjacencyUpgrades.upgrades[i].isImplemented()) {
				ItemStack item = new ItemStack(par1, 1, i);
				for (int k = 0; k < TileEntityAdjacencyUpgrade.MAX_TIER; k++) {
					ItemStack item2 = item.copy();
					item2.stackTagCompound = new NBTTagCompound();
					item2.stackTagCompound.setInteger("tier", k);
					par3List.add(item2);
				}
			}
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return y > 0 && y < world.provider.getHeight()-1;
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
		if (is.stackTagCompound != null) {
			li.add(AdjacencyUpgrades.upgrades[is.getItemDamage()].getDesc(is.stackTagCompound.getInteger("tier")));
			if (is.getItemDamage() == CrystalElement.LIGHTBLUE.ordinal()) {
				long max = TileEntityAccelerator.MAX_LAG/1000000;
				li.add(EnumChatFormatting.GOLD+"Admin Note:"+EnumChatFormatting.WHITE+" Will not cause more than "+max+"ms lag.");
			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		return ir.getMultiValuedName(is.getItemDamage());
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumSize getSize(ItemStack is) {
		return EnumSize.SMALL;
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumWeight getWeight(ItemStack is) {
		return EnumWeight.LIGHT;
	}

	@Override
	@ModDependent(ModList.TFC)
	public EnumItemReach getReach(ItemStack is) {
		return EnumItemReach.SHORT;
	}

	@Override
	@ModDependent(ModList.TFC)
	public boolean canStack() {
		return true;
	}


}
