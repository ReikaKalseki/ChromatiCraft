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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecoPlant extends BlockChromaTile implements IPlantable {

	private final IIcon[] front_icons = new IIcon[16];
	private final IIcon[] back_icons = new IIcon[16];

	public BlockDecoPlant(Material xMaterial) {
		super(xMaterial);
		stepSound = soundTypeGrass;
		blockHardness = 0;
		blockResistance = 0.5F;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		switch(world.getBlockMetadata(x, y, z)) {
			case 0:
				return 15;
			case 1:
				return 6;
			case 4:
				return 12;
			default:
				return 0;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public final boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block par5)
	{
		super.onNeighborBlockChange(world, x, y, z, par5);
		this.checkFlowerChange(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		this.checkFlowerChange(world, x, y, z);
	}

	private final void checkFlowerChange(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 3);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return ((TileEntityMagicPlant)world.getTileEntity(x, y, z)).isPlantable(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		switch(world.getBlockMetadata(x, y, z)) {
			case 0: {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.1875);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.1875);
				double ry = y+0.25+r.nextDouble()*0.75;
				ReikaParticleHelper.FLAME.spawnAt(world, rx, ry, rz);
				break;
			}
			case 1: {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.1875*2);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.1875*2);
				double ry = y+0.25+r.nextDouble()*0.75+0.5;
				ReikaParticleHelper.ENCHANTMENT.spawnAt(world, rx, ry, rz);
				break;
			}
			case 4: {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.1875*2);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.1875*2);
				double ry = y+0.25+r.nextDouble()*0.75;
				ReikaParticleHelper.spawnColoredParticleAt(world, rx, ry, rz, 1, 0, 0);
				break;
			}
			case 5: {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.5);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.5);
				double ry = y+r.nextDouble();
				ReikaParticleHelper.spawnColoredParticleAt(world, rx, ry, rz, 1, 1, 0);
				break;
			}
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return ReikaJavaLibrary.makeListFrom(ChromaTiles.getTileFromIDandMetadata(this, meta).getCraftedProduct());
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.plantRender2;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ChromaClient.plant2.renderPass = pass;
		return pass <= 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public IIcon getOverlay(int meta) {
		return front_icons[meta];
	}

	public IIcon getBacking(int meta) {
		return back_icons[meta];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.getBacking(meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			if (ChromaTiles.getTileFromIDandMetadata(this, i) != null) {
				front_icons[i] = ico.registerIcon("chromaticraft:plant/decoplant_"+i+"_front");
				back_icons[i] = ico.registerIcon("chromaticraft:plant/decoplant_"+i+"_back");
			}
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
			case 0:
				return EnumPlantType.Water;
			case 2:
				return EnumPlantType.Cave;
			case 4:
				return EnumPlantType.Crop;
			default:
				return EnumPlantType.Plains;
		}
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
			case 0:
				if (e instanceof EntityLivingBase)
					e.setFire(2);
				break;
			case 1:
				if (e instanceof EntityLivingBase)
					((EntityLivingBase)e).addPotionEffect(new PotionEffect(ChromatiCraft.betterRegen.id, 20, 0));
				break;
			case 5:
				e.attackEntityFrom(DamageSource.cactus, 1);
				if (e instanceof EntityLivingBase)
					((EntityLivingBase)e).addPotionEffect(new PotionEffect(Potion.hunger.id, 20, 1));
				break;
			default:
				break;
		}
	}

}
