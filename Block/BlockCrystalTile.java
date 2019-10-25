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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalTile extends BlockChromaTile {

	public BlockCrystalTile(Material mat) {
		super(mat);
		this.setLightLevel(1F);
		this.setResistance(6000);
		this.setHardness(4.5F);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 15;
	}

	public ArrayList<ItemStack> getPieces(World world, int x, int y, int z) {
		ArrayList<ItemStack> li = new ArrayList();
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null)
			return li;
		switch (c) {
			case GUARDIAN:
				if (ReikaRandomHelper.doWithChance(25))
					li.add(ChromaStacks.crystalStar);
				break;
			case DIMENSIONCORE:
				if (ReikaRandomHelper.doWithChance(25))
					li.add(ChromaStacks.crystalStar);
				if (ReikaRandomHelper.doWithChance(25))
					li.add(ChromaStacks.voidCore);
				if (ReikaRandomHelper.doWithChance(25))
					li.add(ChromaStacks.energyCore);
				if (ReikaRandomHelper.doWithChance(25))
					li.add(ChromaStacks.crystalFocus);
				if (ReikaRandomHelper.doWithChance(50))
					li.add(ChromaStacks.beaconDust);
				break;
			default:
				break;
		}
		return li;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getSizedBlockAABB(x, y, z, this.getSize(world, x, y, z));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getSizedBlockAABB(x, y, z, this.getSize(world, x, y, z));
	}

	public float getSize(IBlockAccess iba, int x, int y, int z) {
		switch (iba.getBlockMetadata(x, y, z)) {
			case 2:
				return 0.75F;
			default:
				return 1;
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return this.getIcon(s, iba.getBlockMetadata(x, y, z));
	}

	public boolean canHarvest(World world, EntityPlayer player, int x, int y, int z)
	{
		if (player.capabilities.isCreativeMode)
			return false;
		if (world.getBlock(x, y, z) != this)
			return false;
		ItemStack is = player.getCurrentEquippedItem();
		return true;//is != null && is.getItem() instanceof ItemPickaxe && EnchantmentHelper.getSilkTouchModifier(player);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return this.getPieces(world, x, y, z);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		if (this.canHarvest(world, player, x, y, z) && !world.isRemote) {
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null)
			return;
		if (c == ChromaTiles.PYLON) {

		}
		else {
			boolean silk = EnchantmentHelper.getSilkTouchModifier(ep);
			TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
			if (!te.isUnHarvestable()) {
				/*
				if (te instanceof TileEntityWeakRepeater && !((TileEntityWeakRepeater)te).hasRemainingLife())
					return;
				 */
				if (silk || !c.needsSilkTouch()) {
					ItemStack is = this.getHarvestedItemStack(world, x, y, z, meta, c);
					if (is == null) {
						throw new IllegalStateException("Block type "+c+" returned null ItemStack when silk touched!");
					}
					if (c.hasNBTVariants()) {
						NBTTagCompound nbt = new NBTTagCompound();
						((NBTTile)te).getTagsToWriteToStack(nbt);
						is.stackTagCompound = (NBTTagCompound)(!nbt.hasNoTags() ? nbt.copy() : null);
					}
					ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
				}
				else {
					ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getPieces(world, x, y, z));
				}
			}
		}
	}

	protected ItemStack getHarvestedItemStack(World world, int x, int y, int z, int meta, ChromaTiles c) {
		return c.getCraftedProduct();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int s) {
		if (Minecraft.getMinecraft().gameSettings.fancyGraphics)
			return true;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		int dx = x;//+dir.offsetX;
		int dy = y;//+dir.offsetY;
		int dz = z;//+dir.offsetZ;
		Block id = world.getBlock(dx, dy, dz);
		if (id == Blocks.air)
			return true;
		if (id == this)
			return false;
		return !id.isOpaqueCube();
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(meta) {
			case 0:
				return ChromaIcons.GUARDIANOUTER.getIcon();
			case 2:
				return ChromaIcons.TRANSPARENT.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}


}
