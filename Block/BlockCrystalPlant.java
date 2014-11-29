/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.RotaryCraft.API.BlowableCrop;

public class BlockCrystalPlant extends Block implements BlowableCrop {

	private IIcon colorIcon;
	private IIcon fastIcon;
	private IIcon center;

	public BlockCrystalPlant(Material xMaterial) {
		super(Material.plants);
		this.setTickRandomly(true);
		this.setLightOpacity(0);
		this.setHardness(0);
		this.setResistance(1F);
		this.setStepSound(soundTypeGrass);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block par5)
	{
		super.onNeighborBlockChange(world, x, y, z, par5);
		if (!this.canBlockStay(world, x, y, z)) {
			this.die(world, x, y, z);
		}
	}

	private void die(World world, int x, int y, int z) {
		this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {

	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if (this.canBlockStay(world, x, y, z)) {
			if (random.nextInt(12) == 0) {
				TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
				te.grow();
			}
		}
		else {
			this.die(world, x, y, z);
		}
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)iba.getTileEntity(x, y, z);
		return te.emitsLight() ? 15 : 0;
	}

	@Override
	public int getRenderType() {
		return 1; //cross tex, render the "plant" part here
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityCrystalPlant();
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
	public int getRenderColor(int meta) {
		return super.getRenderColor(meta);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ChromaItems.SEED.getItemInstance();
	}

	@Override
	public int damageDropped(int meta) {
		return meta+16;
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {/*
		int l = 0;
		int i1 = 0;
		int j1 = 0;

		for (int k1 = -1; k1 <= 1; ++k1)
		{
			for (int l1 = -1; l1 <= 1; ++l1)
			{
				int i2 = iba.getBiomeGenForCoords(x + l1, z + k1).getBiomeGrassColor();
				l += (i2 & 16711680) >> 16;
			i1 += (i2 & 65280) >> 8;
			j1 += i2 & 255;
			}
		}

		return (l / 9 & 255) << 16 | (i1 / 9 & 255) << 8 | j1 / 9 & 255;*/
		return super.colorMultiplier(iba, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		//return Minecraft.getMinecraft().gameSettings.fancyGraphics ? colorIcon : fastIcon;
		return fastIcon;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		if (te.canHarvest()) {
			te.harvest();
		}
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		fastIcon = ico.registerIcon("chromaticraft:plant");
		colorIcon = ico.registerIcon("chromaticraft:plant_gray");
		center = ico.registerIcon("chromaticraft:crystal/bloom");
	}

	public IIcon getBulbIcon(ReikaDyeHelper color) {
		return center;//bulb[color.ordinal()];
	}

	@Override
	public boolean isReadyToHarvest(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == Blocks.grass)
			;//return false;
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		return te.canHarvest();
	}

	@Override
	public void setPostHarvest(World world, int x, int y, int z) {
		TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getTileEntity(x, y, z);
		te.harvest();
	}

	@Override
	public ArrayList<ItemStack> getHarvestProducts(World world, int x, int y, int z) {
		if (ChromaOptions.CRYSTALFARM.getState()) {
			ArrayList li = new ArrayList();
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(world.getBlockMetadata(x, y, z));
			li.add(shard);
			return li;
		}
		return null;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(ChromaBlocks.PLANT.getBlockInstance(), 1, meta);
	}

	@Override
	public float getHarvestingSpeed() {
		return 0.33F;
	}

}
