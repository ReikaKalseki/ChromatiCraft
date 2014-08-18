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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCrystalBrewer extends Block {

	private IIcon top;
	private IIcon side;
	private IIcon bottom;
	private float w = 0.75F;

	public BlockCrystalBrewer(Material mat) {
		super(mat);
		//this.setBlockBounds(0.5F-w/2, 0, 0.5F-w/2, 0.5F+w/2, 0.875F, 0.5F+w/2);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityCrystalBrewer();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9)
	{
		ep.openGui(ChromatiCraft.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		TileEntity tileentity = world.getTileEntity(x, y, z);
		Random rand = new Random();

		if (tileentity instanceof TileEntityCrystalBrewer)
		{
			TileEntityCrystalBrewer tileentitybrewingstand = (TileEntityCrystalBrewer)tileentity;

			for (int j1 = 0; j1 < tileentitybrewingstand.getSizeInventory(); ++j1)
			{
				ItemStack itemstack = tileentitybrewingstand.getStackInSlot(j1);

				if (itemstack != null)
				{
					float f = rand.nextFloat() * 0.8F + 0.1F;
					float f1 = rand.nextFloat() * 0.8F + 0.1F;
					float f2 = rand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0)
					{
						int k1 = rand.nextInt(21) + 10;

						if (k1 > itemstack.stackSize)
						{
							k1 = itemstack.stackSize;
						}

						itemstack.stackSize -= k1;
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float)rand.nextGaussian() * f3;
						entityitem.motionY = (float)rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)rand.nextGaussian() * f3;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico)
	{
		top = ico.registerIcon("ChromatiCraft:brewer_top");
		side = ico.registerIcon("ChromatiCraft:brewer_side");
		bottom = ico.registerIcon("ChromatiCraft:brewer_bottom");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (s == 0)
			return bottom;
		if (s == 1)
			return top;
		return side;
	}


}
